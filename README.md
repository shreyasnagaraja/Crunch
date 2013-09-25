MAPREDUCE
==================

Introduction
-------------



Building
---------

The project is configured to be easily built using Maven version 3.0.4.  After cloning the repository the project can be easily built with the following command:

`mvn clean package`

The jar generated in the *target/* folder (e.g. ~/target/mapreduce-examples-1.0-SNAPSHOT.jar) can then be copied to a Hadoop node for execution of the jobs.

The Data
---------

Most of the examples in this project run over data extracted from [Wikipedia](http://www.wikipedia.org/). The data is available as a part of this project.


HDFS To HBase Mapper - Example
-------------------------------

This serves as an example of how to import data from HDFS to an HBase table. For this specific example, we import the wikipedia dump lying in HDFS into an HBase table for futher processing on it.

To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.HDFSToHBaseMain <title_file_path> <links_file_path> <hbase_table_name>`

Load the wikipedia data needed for this example onto the HDFS directory.

Make sure a HBase table exists where you wish to run this example to store the final output.

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table WIKIDUMP, the command to run the example would be of the following form:

hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.HDFSToHBaseMain /wikidump/links/titles-sorted.txt /wikidump/links/links-simple-sorted.txt WIKIDUMP

The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP". 


Link Reversal - Assignment
--------------------------

The basic idea behind the Link Reversal Assignment is to create a mapreduce job that will read the 'outlinks' column from the HBase table and create an 'inlinks' column by reversing the links in the graph

 For this specific assignment, we will try to use the HBase table generated above and reverse all the links in the given wikipedia graph.

To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.LinkReversalMain <hbase_table_name>`

So, in this case it would be:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.LinkReversalMain WIKIDUMP`

The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP".

Crunch Example - Assignment
----------------------------

What is Crunch ?

Running on top of Hadoop MapReduce, the Apache Crunch™ library is a simple Java API for tasks like joining and data aggregation that are tedious to implement on plain MapReduce.

Crunch Documentation - http://crunch.apache.org/index.html


The basic idea behind the Crunch Assignment is to create a runnable crunch job that implements the full pipeline of HDFS->HBase + Link Reversal in a single pipeline.

To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.CrunchExample <title_file_path>  <links_file_path>  <hbase_table_name>`

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table OUTPUT, the command to run the example would be of the following form:

hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.HDFSToHBaseMain /wikidump/links/titles-sorted.txt /wikidump/links/links-simple-sorted.txt OUTPUT

The results can be seen by doing a scan of the resulting HBase table "OUTPUT". 
