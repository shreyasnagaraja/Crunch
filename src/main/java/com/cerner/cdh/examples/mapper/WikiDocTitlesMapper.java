package com.cerner.cdh.examples.mapper;

import static com.cerner.cdh.examples.mapper.WikiConstants.COLUMN_FAMILY_BYTES;
import static com.cerner.cdh.examples.mapper.WikiConstants.TITLE_COLUMN_QUALIFIER_BYTES;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Simple Mapper implementation that reads text dumps of wikipedia from HDFS, extracts the doc id and title from them and writes to
 * HBase
 * 
 * */
public class WikiDocTitlesMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Writable> {

    @Override
    protected void map(LongWritable key, Text value,
            org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, ImmutableBytesWritable, Writable>.Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        String[] splits = line.split(":");

        String docId = splits[0];
        String title = splits[1];

        byte[] docIdBytes = Bytes.toBytes(docId);

        Put put = new Put(docIdBytes);

        put.add(COLUMN_FAMILY_BYTES, TITLE_COLUMN_QUALIFIER_BYTES, Bytes.toBytes(title.trim()));

        // write the doc id and the outlinks bytes
        context.write(new ImmutableBytesWritable(docIdBytes), put);
    }
}
