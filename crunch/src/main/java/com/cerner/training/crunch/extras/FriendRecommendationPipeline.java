package com.cerner.training.crunch.extras;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.FsInput;
import org.apache.crunch.PCollection;
import org.apache.crunch.PGroupedTable;
import org.apache.crunch.PTable;
import org.apache.crunch.Pair;
import org.apache.crunch.Pipeline;
import org.apache.crunch.PipelineResult;
import org.apache.crunch.Source;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.From;
import org.apache.crunch.io.To;
import org.apache.crunch.types.avro.Avros;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.GlobFilter;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cerner.training.Person;
import com.cerner.training.crunch.PeopleToFriends;

/**
 * This is the class responsible for building and running the Crunch pipeline used to calculate recommended friends between
 * {@link Person people}. When complete the results will be in the 'recommended_friends' directory
 */
public class FriendRecommendationPipeline extends Configured implements Tool {

    /**
     * Runs the map/reduce job
     * 
     * @param args
     *            arguments supplied to the job
     * @throws Exception
     *             if there is an issue running the map/reduce job
     */
    public static void main(String[] args) throws Exception {
        // Here we use hadoop's ToolRunner to give us generic hadoop options like -conf and automatically load hadoop config files
        // on the classpath
        System.exit(ToolRunner.run(new FriendRecommendationPipeline(), args));
    }

    public int run(String[] args) throws Exception {
        // Verify that 1 argument was provided
        if (args.length != 1) {
            // Send a message to user that the arguments was incorrect and quit
            System.out.println("Incorrect number of arguments. Expected 1 argument [input path]");
            return 1;
        }

        String inputFile = args[0];
        Path inputPath = new Path(inputFile);

        // Retrieve the Configuration object that ToolRunner built for us
        Configuration conf = getConf();

        // The FileSystem.get(Configuration) method retrieves a cached FileSystem instance so we shouldn't close it
        FileSystem fs = FileSystem.get(conf);

        // Verify the input path given to us exists in HDFS
        if (!fs.exists(inputPath)) {
            System.out.println("The file [" + inputFile + "] does not exist in HDFS."
                    + " You may not have copied the data into HDFS or mis-typed the file name");
            return 1;
        }

        // This is the directory we will use for the output of the map/reduce job
        Path outputDirectory = new Path("recommended_friends");

        // Ensure the output directory does not exist so there are no problems with the map/reduce job writes to this directory
        if (fs.exists(outputDirectory)) {
            System.out.println("The output directory [" + outputDirectory + "] exists, purging for next run");
            if (!fs.delete(outputDirectory, true)) {
                System.out.println("Unable to delete the output directory. "
                        + "Delete the directory manually using 'hdfs dfs' before proceeding");
                return 1;
            }
        }

        // Build our pipeline from the MRPipeline object
        Pipeline pipeline = new MRPipeline(FriendRecommendationPipeline.class, conf);

        // Build a source from our input path which should be composed of Person avro records
        Source<Person> source = From.avroFile(inputPath, Person.class);

        // Read in the people from the file
        PCollection<Person> people = pipeline.read(source);

        // Build a PTable from the list of people where the key is a String composed of two Person objects that are friends
        PTable<Pair<String, String>, Person> peopleTable = people.parallelDo(
                "Transform Person objects to a table of friend names and Person object key/values", new PeopleToFriends(),
                Avros.tableOf(Avros.pairs(Avros.strings(), Avros.strings()), Avros.records(Person.class)));

        // Group the values by key so that we collect each pair of Person objects that are friends
        PGroupedTable<Pair<String, String>, Person> groupedPeopleTable = peopleTable.groupByKey();

        // Compute the raw recommendations for each pair of friends
        PTable<String, Person> rawRecommendedFriends = groupedPeopleTable.parallelDo(
                "Calculate the recommendation for each friend pair", new FriendsToRawRecommendations(),
                Avros.tableOf(Avros.strings(), Avros.records(Person.class)));

        // Here we do a second group by key since the table of raw recommended friends can produce 0-many recommendations for a
        // single person, each as their own key/value. So here we group them together so we can combine them
        PCollection<Person> recommendedFriends = rawRecommendedFriends.groupByKey().parallelDo(
                "Combine all the recommendations for each unique person", new RawRecommendationsToRecommendations(),
                Avros.records(Person.class));

        // Write these Person objects to an avro file in our output directory
        pipeline.write(recommendedFriends, To.avroFile(outputDirectory));

        // Signal to the pipeline we are done planning
        PipelineResult result = pipeline.done();

        // Verify the pipeline was successful
        if (!result.succeeded()) {
            // The pipeline failed so notify the user and quite
            System.out.println("The pipeline failed!");
            return 1;
        }

        // The pipeline was successful so notify the user
        System.out.println("The pipeline was successful!");

        // Print the results to the user. In map/reduce jobs this is not normally done since results can be
        // very large (GB, TB, or even PB in size), but for lab purposes it is helpful to easily see the results and the data set
        // should be small
        printResults(outputDirectory);

        return 0;
    }

    /**
     * Prints the {@link Person} avro data in the output directory
     * 
     * @param outputDirectory
     *            the output directory of the pipeline
     * @throws IOException
     *             if there is an issue reading from the filesystem
     */
    private void printResults(Path outputDirectory) throws IOException {
        FileSystem fs = FileSystem.get(getConf());

        // Find the .avro file with our results in our output directory
        List<Path> outputFiles = new ArrayList<Path>();
        for (FileStatus fileStatus : fs.listStatus(outputDirectory, new GlobFilter("*.avro"))) {
            outputFiles.add(fileStatus.getPath());
        }

        // This implies that we could not find a .avro file in the output directory. This should likely never happen but code
        // defensively just in case
        if (outputFiles.isEmpty()) {
            System.out.println("Unable to determine output file(s)");
        }

        // Read the avro file(s) and print the results to the user
        for (Path outputFile : outputFiles) {
            SeekableInput input = new FsInput(outputFile, getConf());

            DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
            FileReader<GenericRecord> fileReader = DataFileReader.openReader(input, reader);

            for (GenericRecord datum : fileReader) {
                System.out.println("The person [" + datum.get("name") + "] should be friends with ...");
                System.out.println("  " + datum.get("friends"));
            }
        }

    }

}