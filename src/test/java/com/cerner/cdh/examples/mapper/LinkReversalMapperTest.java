package com.cerner.cdh.examples.mapper;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Test;
import org.mockito.Mockito;

public class LinkReversalMapperTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testLinkReversalMapper() throws Exception {

        // Creating an example ImmutableBytesWritable row and a Result value to pass into the mapper.
        ImmutableBytesWritable row = new ImmutableBytesWritable(Bytes.toBytes("docId"));

        KeyValue values = new KeyValue(Bytes.toBytes("docId"), WikiConstants.COLUMN_FAMILY_BYTES,
                WikiConstants.OUTLINKS_COLUMN_QUALIFIER_BYTES, Bytes.toBytes("value"));
        KeyValue[] valueArray = { values };
        Result value = new Result(valueArray);

        // Mocking the Context class
        Context context = Mockito.mock(Context.class);

        LinkReversalMapper mapper = new LinkReversalMapper();
        mapper.map(row, value, context);
        Mockito.verify(context, Mockito.times(1)).write(new Text("value"), new Text("docId"));

    }

}