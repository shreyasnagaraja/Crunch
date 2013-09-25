package com.cerner.cdh.examples;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import com.cerner.cdh.examples.mapper.WikiDocLinksMapper;
import com.cerner.cdh.examples.mapper.WikiDocTitlesMapper;

/**
 * Main class containing the mapper and reducer for importing data from HDFS to HBase
 * 
 * */
public class HDFSToHBaseMain {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        if (args.length != 3) {
            // wrong number of args provided
            printUsage();
            return;
        }

        String titlesFilePath = args[0];
        String linksFilePath = args[1];
        String tableName = args[2];

        // Step 1: Start by setting up the HBase configuration
        Configuration conf = HBaseConfiguration.create();

        // Step 2: Setup the job to execute with the given config and a name
        Job job = new Job(conf, "Importing wikipedia data from HDFS to HBase table " + tableName);

        // Step 3: Setup the main class for the job that contains the mapper and the reducer
        job.setJarByClass(HDFSToHBaseMain.class);

        // Step 4: Add different input paths along with their mapper implementations
        MultipleInputs.addInputPath(job, new Path(titlesFilePath), TextInputFormat.class, WikiDocTitlesMapper.class);

        MultipleInputs.addInputPath(job, new Path(linksFilePath), TextInputFormat.class, WikiDocLinksMapper.class);

        // Step 5: Set output key class
        job.setOutputKeyClass(ImmutableBytesWritable.class);

        // Step 6: Set output value class
        job.setOutputValueClass(Writable.class);

        // Step 7: Set table output format. Since we are writing to an HBase table, this will be TableOutputFormat
        job.setOutputFormatClass(TableOutputFormat.class);

        // Step 8: Set the HBase table name
        job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);

        // Step 9: No reducer is needed. So set to zero
        job.setNumReduceTasks(0);

        // Step 10: Call add dependency jars which will distribute any dependencies to Distributed Cache to that the tasks can be
        // executed on each node.
        // Note that calling TableMapReduceUtil contains a number of helpful methods for performing MapReduce jobs on HBase.
        TableMapReduceUtil.addDependencyJars(job);

        // Step 11: Execute the job and wait for its completion
        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("Error executing job!");
        }
    }

    private static void printUsage() {
        System.out.println("command <TITLES FILES PATH> <LINK FILES PATH> <HBASE TABLE NAME>");
    }
}
