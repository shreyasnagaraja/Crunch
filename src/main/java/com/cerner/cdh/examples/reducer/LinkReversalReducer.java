package com.cerner.cdh.examples.reducer;

import java.io.IOException;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Text;

/**
 * Simple Reducer implementation that takes a docId as key and a list of inlinks as values and writes them to HBase
 * 
 * */
public class LinkReversalReducer extends TableReducer<Text, Text, ImmutableBytesWritable> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        // TODO: Create a inlinks String from the Iterable values

        // TODO: Obtain the docId from the Key

        // TODO: Create a Put with the docId and add the specified column and value to this Put operation.

        // TODO: Perform a context write

    }
}
