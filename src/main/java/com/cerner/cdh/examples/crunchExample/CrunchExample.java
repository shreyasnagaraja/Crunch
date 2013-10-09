package com.cerner.cdh.examples.crunchExample;

import org.apache.crunch.PCollection;
import org.apache.crunch.PGroupedTable;
import org.apache.crunch.PTable;
import org.apache.crunch.Pipeline;
import org.apache.crunch.PipelineExecution.Status;
import org.apache.crunch.PipelineResult;
import org.apache.hadoop.hbase.client.Put;

/**
 * This class represents a simple crunch job which combines the the full pipeline of HDFS->HBase + Link Reversal in a single
 * pipeline
 * 
 * <p>
 * This class contains multiple aspects of processing using Crunch
 * </p>
 * 
 * <ul>
 * <li>Individual Processing Step Logic</li>
 * <li>Building of the Processing Pipeline</li>
 * <li>Running of the Processing Pipeline</li>
 * <li>Command to launch the Processing Pipeline</li>
 * </ul>
 * 
 * <p>
 * For further API reference and developer documentation,see the crunch javadoc
 * 
 * http://crunch.apache.org/apidocs/0.7.0/
 * </p>
 * 
 */

public class CrunchExample {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            // wrong number of args provided
            printUsage();
            return;
        }

        String titlesFilePath = args[0];
        String linksFilePath = args[1];
        String tableName = args[2];

        // TODO: Create a Crunch Pipeline that will be used for processing the data. 
        
        // Consult the Crunch javadoc (http://crunch.apache.org/apidocs/0.7.0/org/apache/crunch/Pipeline.html) for more info on instantiating a pipeline.

        Pipeline pipeline = null;

        // TODO: Read the input links file which is a text file into a PCollection.

        PCollection<String> lines = null;

        // TODO: We can observe from the input text file that we should perform splits on ':'(Since the format is of form
        // docId:link). Perform a DoFn that splits the lines on ":" and emit the pair you get.Populate the null value appropriately

        // A DoFn allows implementations to emit multiple values for a single input. A parallelDo will apply the DoFunction to the
        // elements of the PCollection and returns a PTable. Refer to the Apache Crunch javadoc for parallelDo.

        PTable<String, String> links = lines.parallelDo("split links", new StringSplitFunction(), null);

        // TODO: Repeat the same STEP-2 and STEP-3 for the title files

        PTable<String, String> titles = null;

        // TODO: Perform a union operation on the 2 PTable processed by the 2 different DoFn.

        PTable<String, String> combinedTable = null;

        // TODO: Group the Combined table by keys

        PGroupedTable<String, String> groupedTable = null;

        // TODO: Perform a MapFn that creates a Put.

        // A MapFn is a special case of DoFn in which case a single input will only produce a single output. The parallelDo will
        // apply the MapFn to the elements of the PTable and returns a PCollection of puts.Populate the null value appropriately

        PCollection<Put> createPut = groupedTable.parallelDo("createPut", new CreatePutFn(), null);

        // TODO: Write the createdPut to a HBase Table Use ToHBase API of crunch to write to the Table

        // TODO: Run the Pipeline

        PipelineResult result = pipeline.run();
 
        System.out.println("Pipeline Status :" + result.status);

        if (!pipeline.run().succeeded())
            System.out.println("There's a failure in your crunch job");
    }

    private static void printUsage() {
        System.out.println("command <TITLES FILES PATH> <LINK FILES PATH> <HBASE TABLE NAME>");
    }

}
