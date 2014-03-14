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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.GlobFilter;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cerner.training.Person;

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
        
        // TODO: Start adding your pipeline here

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