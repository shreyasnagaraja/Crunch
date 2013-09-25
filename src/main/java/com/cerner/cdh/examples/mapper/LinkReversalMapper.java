package com.cerner.cdh.examples.mapper;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;

/**
 * Simple mapper implementation that reverses links in a graph. for a graph where A -> B, A -> C and A -> D, the mapper will emit
 * <B,A>, <C,A>, <D,A> as intermediate tuples
 * 
 * */
public class LinkReversalMapper extends TableMapper<Text, Text> {

    @Override
    protected void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {

        // TODO: Get the Document Id which will act as our key from row

        String key = null;

        // TODO: Obtain the KeyValue from the Result for the
        // {@link com.cerner.cdh.examples.mapper.WikiConstants#OUTLINKS_COLUMN_QUALIFIER_BYTES}

        // TODO: Get the outlinks from the KeyValue obtained

        String outlinks = null;

        // TODO: Split on whitespace and iterate over the splits and do a context write on each split with the key.

    }
}