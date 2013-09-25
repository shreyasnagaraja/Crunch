package com.cerner.cdh.examples.crunchExample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.crunch.Pair;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import com.cerner.cdh.examples.mapper.WikiConstants;

public class CreatePutFnTest {

    /**
     * A test that demonstrate a MapFn through unit Tests.
     */
    @Test
    public void testSimplePut() throws Exception {

        String key = "key";
        Iterable<String> values = generateData(10);
        Pair<String, Iterable<String>> pairs = new Pair<String, Iterable<String>>(key, values);
        CreatePutFn fn = new CreatePutFn();

        assertEquals("key", (Bytes.toString(fn.map(pairs).getRow())));

        // assert to check if any value between 1-10 exists in the put.
        assertTrue(fn.map(pairs).has(WikiConstants.COLUMN_FAMILY_BYTES, WikiConstants.INLINKS_COLUMN_QUALIFIER_BYTES,
                Bytes.toBytes("4")));

    }

    /**
     * A method that creates and returns a collection of Strings.
     */
    private Collection<String> generateData(int count) {
        Collection<String> values = new LinkedList<String>();

        for (int i = 0; i < count; i++) {
            values.add(Integer.toString(i));
        }
        return values;
    }

}
