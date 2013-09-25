package com.cerner.cdh.examples;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

import com.cerner.cdh.examples.mapper.LinkReversalMapper;
import com.cerner.cdh.examples.reducer.LinkReversalReducer;

/**
 * Main class containing the mapper and reducer for wikipedia graph reversal
 * 
 * */
public class LinkReversalMain {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        if (args.length != 1) {
            printUsage();
            return;
        }

        String tableName = args[0];

        // Step 1: Start by setting up the HBase configuration
        Configuration config = HBaseConfiguration.create();

        // Step 2: Setup the job to execute with the given config and a name
        Job job = new Job(config, "Reversing wikipedia graph links");

        // Step 3: Setup the main class for the job that contains the mapper and the reducer
        job.setJarByClass(LinkReversalMain.class); // class that contains mapper and reducer

        Scan scan = new Scan();
        scan.setCaching(500); // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false); // don't set to true for MR jobs

        // Step 4: Setup the Table Mapper Job.Make sure the passed job is carrying all necessary HBase configuration
        TableMapReduceUtil.initTableMapperJob(tableName, // input table
                scan, // Scan instance
                LinkReversalMapper.class, // mapper class
                Text.class, // mapper output key
                Text.class, // mapper output value
                job);

        // Step 5: Setup the Table Reducer Job.
        TableMapReduceUtil.initTableReducerJob(tableName, // output table
                LinkReversalReducer.class, // reducer class
                job);

        // Step 6: Add the HBase dependency jars as well as jars for any of the configured job classes to the job configuration
        TableMapReduceUtil.addDependencyJars(job);

        job.setNumReduceTasks(1); // at least one, adjust as required

        // Step 7: Execute the job and wait for its completion
        boolean b = job.waitForCompletion(true);

        if (!b) {
            throw new IOException("Error executing job!");
        }
    }

    private static void printUsage() {
        System.out.println("command <HBASE TABLE NAME>");
    }
}
