package com.cerner.training.crunch;

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
import org.apache.crunch.types.PType;
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
import com.cerner.training.PersonPair;

/**
 * This is the class responsible for building and running the our Crunch pipeline used to calculate common friends between
 * {@link Person people}. When complete the results will be in the 'friends' directory
 */
public class CommonFriendsPipeline extends Configured implements Tool {

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
        System.exit(ToolRunner.run(new CommonFriendsPipeline(), args));
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
        Path outputDirectory = new Path("friends");

        // Ensure the output directory does not exist so there are no problems with the map/reduce job writes to this directory
        if (fs.exists(outputDirectory)) {
            System.out.println("The output directory [" + outputDirectory + "] exists, purging for next run");
            if (!fs.delete(outputDirectory, true)) {
                System.out.println("Unable to delete the output directory. "
                        + "Delete the directory manually using 'hdfs dfs' before proceeding");
                return 1;
            }
        }

        // Here is a link to the crunch javadoc you should be using, http://crunch.apache.org/apidocs/0.8.2/

        // Build our pipeline
        Pipeline pipeline = null;

        // TODO Read the data from the given inputPath. You should use the From crunch class
        // (http://crunch.apache.org/apidocs/0.8.2/org/apache/crunch/io/From.html) for the source you will need. Keep in mind the
        // inputPath is a path to an avro file of Person objects.

        // TODO Add the necessary logic to process all of our data

        // NOTE: Any DoFns/MapFns/etc should be written as their own class or do not use anonymous inner classes. This makes them
        // easier to unit test and you don't have to worry about the driver class implementing serializable. For more
        // information read this (http://crunch.apache.org/user-guide.html#dovsmap)

        // NOTE: For any PTypes or PTableTypes use the Avros crunch class
        // (http://crunch.apache.org/apidocs/0.8.2/org/apache/crunch/types/avro/Avros.html). You can even build PTypes for avro
        // records like this,
        PType<Person> personPType = Avros.records(Person.class);
        PType<PersonPair> personPairPType = Avros.records(PersonPair.class);

        // TODO Write the data to the outputDirectory. Take a look at the To crunch class
        // (http://crunch.apache.org/apidocs/0.8.2/org/apache/crunch/io/To.html) for the target you will need. Keep in mind we
        // should be writing out an avro file made of PersonPair objects

        // Signal to the pipeline we are done planning and should begin running the pipeline
        PipelineResult result = null;

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
        // should be small (5 records or PersonPair objects)
        printResults(fs, outputDirectory);

        return 0;
    }

    /**
     * Prints the {@link PersonPair} avro file in the output directory
     * 
     * @param fs
     *            the file system object to use to read the output data
     * @param outputDirectory
     *            the output directory of the pipeline
     * @throws IOException
     *             if there is an issue reading from the filesystem
     */
    private void printResults(FileSystem fs, Path outputDirectory) throws IOException {

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
                System.out.println("The friends [" + datum.get("personName1") + "] and [" + datum.get("personName2")
                        + "] have the following friends in common ...");
                System.out.println("  " + datum.get("commonFriends"));
            }
        }

    }

}
