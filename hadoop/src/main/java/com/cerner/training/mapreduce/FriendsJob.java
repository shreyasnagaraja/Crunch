package com.cerner.training.mapreduce;

import java.io.FileNotFoundException;
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
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.GlobFilter;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.cerner.training.Person;
import com.cerner.training.PersonPair;

/**
 * A class used to build and submit the job used to calculate common friends between people. When complete the results will be in
 * the 'friends' directory.
 */
public class FriendsJob extends Configured implements Tool {

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
        System.exit(ToolRunner.run(new FriendsJob(), args));
    }

    @Override
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

        // Create a new Job with the Configuration loaded in by ToolRunner
        Job job = new Job(conf);

        // Identify the jar we want the job to include when running the job on the task trackers. We do this by identifying a class
        // that exists within that jar which is this class itself
        job.setJarByClass(FriendsJob.class);

        // Give a name to our job (This is the name shown in the JobTracker)
        job.setJobName("Friends Job");

        // Set the input of the job to the path given to us
        FileInputFormat.setInputPaths(job, inputPath);

        // Set the output of the job to the directory we chose
        FileOutputFormat.setOutputPath(job, outputDirectory);

        // Set our input format to be of AvroKeyInputFormat. This implies the mapper KEYIN will be the avro object and the value
        // will be NullWritable. This is a wrapper to make any avro objects writables, a requirement for map/reduce for
        // serialization purposes
        job.setInputFormatClass(AvroKeyInputFormat.class);

        // This tells the AvroKeyInputFormat the avro object we will be using
        AvroJob.setInputKeySchema(job, Person.getClassSchema());

        // Set the job's mapper class to the mapper we wrote
        job.setMapperClass(FriendsMapper.class);

        // Sets the mapper's output key class to Text, which can be thought of as String
        job.setMapOutputKeyClass(Text.class);

        // Sets the mapper's output value to be our Person avro object
        AvroJob.setMapOutputValueSchema(job, Person.getClassSchema());

        // Sets the job's reducer to the reducer we wrote
        job.setReducerClass(FriendsReducer.class);

        // Sets the job's output format to be AvroKeyOutputFormat. This is the same as AvroKeyInputFormat and is simply a wrapper
        // for avro objects
        job.setOutputFormatClass(AvroKeyOutputFormat.class);

        // Tells AvroKeyOutputFormat to write PersonPair objects
        AvroJob.setOutputKeySchema(job, PersonPair.getClassSchema());

        // Submit the job and wait for its completion. The true argument tells it we do want verbose system information when
        // running the job. The method returns a boolean indicating if the job was successful
        if (!job.waitForCompletion(true)) {
            // The job failed so tell the user and quit
            System.out.println("Job failed!");
            return 1;
        }

        // The job succeeded so tell the user
        System.out.println("Job completed successfully!");

        // Print the results of the job. In map/reduce jobs this is not normally done since results can be
        // very large (GB, TB, or even PB in size), but for lab purposes it is helpful to easily see the results and the data set
        // should be small (5 records or PersonPair objects)
        printResults(outputDirectory);

        return 0;
    }

    private void printResults(Path outputDirectory) throws FileNotFoundException, IOException {
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
                System.out.println("The friends [" + datum.get("personName1") + "] and [" + datum.get("personName2")
                        + "] have the following friends in common ...");
                System.out.println("  " + datum.get("commonFriends"));
            }
        }

    }

}
