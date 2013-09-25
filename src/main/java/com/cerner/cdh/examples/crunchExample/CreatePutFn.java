package com.cerner.cdh.examples.crunchExample;

import org.apache.crunch.MapFn;
import org.apache.crunch.Pair;
import org.apache.hadoop.hbase.client.Put;

/**
 * This class extends the MapFn class.It Performs a MapFn that creates a Put. This MapFn is used by a PTable which is groupedby
 * keys.
 * 
 * 
 * 
 * 
 */
public class CreatePutFn extends MapFn<Pair<String, Iterable<String>>, Put> {
    private static final long serialVersionUID = 8881913416626789157L;

    // TODO: Creates a new Put from COLUMN_FAMILY = {@link WikiConstants#COLUMN_FAMILY_BYTES}, COLUMN_QUALIFIER =
    // {@link WikiConstants#INLINKS_COLUMN_QUALIFIER_BYTES} and the values associated with each key to the Put.
    // Run the CreatePutFnTest after completion to see if it has been correctly implemented.
    @Override
    public Put map(Pair<String, Iterable<String>> input) {

        return null;
    }

}
