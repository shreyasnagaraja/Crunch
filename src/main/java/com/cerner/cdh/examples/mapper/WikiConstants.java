package com.cerner.cdh.examples.mapper;

import org.apache.hadoop.hbase.util.Bytes;

public class WikiConstants {

    // Constants representing the column family in which all Wikipedia data will be written.
    public static final String COLUMN_FAMILY = "RAW";
    public static final byte[] COLUMN_FAMILY_BYTES = Bytes.toBytes(COLUMN_FAMILY);

    // Constants representing the qualifier for storing document titles
    public static final String TITLE_COLUMN_QUALIFIER = "title";
    public static final byte[] TITLE_COLUMN_QUALIFIER_BYTES = Bytes.toBytes(TITLE_COLUMN_QUALIFIER);

    // Constants representing the qualifier for storing the ids of the outgoing links from a document.
    public static final String OUTLINKS_COLUMN_QUALIFIER = "outlinks";
    public static final byte[] OUTLINKS_COLUMN_QUALIFIER_BYTES = Bytes.toBytes(OUTLINKS_COLUMN_QUALIFIER);

    // Constants representing the qualifier for storing the ids of the documents linking to the document.
    public static final String INLINKS_COLUMN_QUALIFIER = "inlinks";
    public static final byte[] INLINKS_COLUMN_QUALIFIER_BYTES = Bytes.toBytes(INLINKS_COLUMN_QUALIFIER);
}
