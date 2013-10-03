MAPREDUCE-101
==================

Introduction
-------------

The purpose of this project is to help individuals with the following:

* Understanding the different components,configuration and functionality that can be performed inside of the Hadoop MapReduce framework.
* Writing map and reduce functions.
* Writing and executing a runnable crunch job that implements a full pipeline.

Prerequisites
--------------
This project assumes the following are already installed:

* Git
* Apache Maven
* Java
* Vagrant
* Virtual Box
* ruby

Installing Git
---------------

* Download and install a git client
* Add git bin directory to the windows PATH variable

http://geekswithblogs.net/renso/archive/2009/10/21/how-to-set-the-windows-path-in-windows-7.aspx

Installing Vagrant
-------------------

* [Follow the instructions for installation of Vagrant](http://docs.vagrantup.com/v2/installation/index.html)

Installing VirtualBox
----------------------

* [Download virtual box](https://www.virtualbox.org/wiki/Downloads)

Installing Ruby
-----------------

* [Follow the instructions for installation of ruby](http://rubyinstaller.org/)

The Data
---------

Most of the examples in this project run over data extracted from [Wikipedia](http://www.wikipedia.org/). The data is available at http://cernbdatahadoopd03.northamerica.cerner.net:50075/browseDirectory.jsp?dir=%2Fwikidump&namenodeInfoPort=50070&nnaddr=10.162.123.102:8020 as a part of this project.

Course of Study
-----------------

The following is the course of study for this lab

## HDFS To HBase Mapper - Example

### Objective

The main purpose of this example is to help individuals understand the different aspects of a Hadoop MapReduce framework. This example shows how data 
can be imported from HDFS to an HBASE Table.

For this specific example, we import the wikipedia dump lying in HDFS into an HBase table for futher processing on it.

## Assignment-1: Link Reversal

### Objective

The basic idea behind the Link Reversal Assignment is to create a mapreduce job that will read the 'outlinks' column from the HBase table 
and create an 'inlinks' column by reversing the links in the graph.

For this specific assignment, the users can use the HBase table generated above and reverse all the links in the given wikipedia graph.

The following must be accomplished inorder to successfully complete this assignment

* Write the Map and reduce function by following the TODO's commented out in the code.
* Refer the Junits for help and ensure that it passes on successful completion of code.
* Successfully Build and execute the assignment.
* Verify the results by scanning the table.

## Assignment-2: Crunch Example

What is Crunch ?

Running on top of Hadoop MapReduce, the Apache Crunch™ library is a simple Java API for tasks like joining and data aggregation that are tedious to implement on plain MapReduce.

[Getting Started Guide For Crunch](https://wiki.ucern.com/display/pophealth/Getting+Started+with+Apache+Crunch).

### Objective

The basic idea behind the Crunch Assignment is to create a runnable crunch job that implements the full pipeline of HDFS->HBase + Link Reversal in a single pipeline.

The following must be accomplished inorder to successfully complete this assignment

* Follow the different TODO's commented out in the code to complete the assignment.
* Refer the Junits for help and ensure that all the junits pass on successful completion of code.
* Successfully Build and execute the job.
* Verify the results by scanning the table

Running the project
--------------------

In order to run the projects first make sure all the **TODO's** in the code have been completed and all the **Junit tests** pass.

The Steps involved in running the different jobs are as follows:

## Obtain Clone

Select a directory and download the project from github

`git clone http://github.cerner.com/CDH/mapreduce-101.git`

## Building

The project is configured to be easily built using Maven version 3.0.4.  

`mvn clean package`

The jar generated in the *target/* folder (e.g. ~/target/mapreduce-examples-1.0-SNAPSHOT.jar) can then be copied to a Hadoop node for execution of the jobs.

## Acquire Vagrant Box

The VagrantFile in this repository has all of the connection information needed to download, import and start the vagrant box. The Vagrant file is large so it is best to do it on a Cerner connection.

On the command line navigate into the clone directory and execute:

`vagrant up`

It will then start to import the box and start it up. This will take awhile so be patient.

Once the box has started successfully you should be able to access the environment with the following command:

`vagrant ssh`

Get sudo access by doing a `sudo su -` once your inside the box.

The project will be available under the `/Vagrant` directory.

Once you have successfully completed your code you can run each of the examples and assignments in your vagrant box.

## Loading Data to HDFS

The following steps are to be followed to load the input data to HDFS

* Create a wikidump directory on hdfs . This can be done using the command:

`hadoop fs -mkdir wikidump`

The hadoop distributed copy tool can be used to copy the data we need from the HDFS in the source cluster to our HDFS.The following command can be used to perform that function:

`hadoop distcp hdfs://cernbdatahadoopd01.northamerica.cerner.net:8020/wikidump/links hdfs://0.0.0.0:8020/wikidump`

Make sure the source namenode is an active node . You can do this by going to `http://cernbdatahadoop01.northamerica.cerner.net:50070`. If it is in standby mode use 

`hdfs://cernbdatahadoop02.northamerica.cerner.net:8020/wikidump/links` as your source.

You can verify if the input data is successfully copied by going to  

`http://localhost:50075/browseDirectory.jsp?dir=%2Fwikidump&namenodeInfoPort=50070&nnaddr=localhost:8020`

## Creating a HBase Table

Perform the following steps to create a HBase Table

* Download and install Jruby

`curl -O http://repo1.maven.org/maven2/org/jruby/jruby-complete/1.7.0/jruby-complete-1.7.0.jar`

* Set the class Path

`export CLASSPATH=` java -jar jruby-complete-1.7.0.jar -e "puts Dir.glob('{.,build,lib}/*.jar').join(':')" `

* Now run the createTable script with HBase

`HBase org.jruby.Main createTable.rb` 

This will create 2 tables `WIKIDUMP` and `crunchExample` having family name "RAW".

* To see the table structure do the following:

On the Hadoop node execute:

`hbase shell`

Then using the simple

`describe '<hbase table name>'`

This will display a description of the named table.

## Scanning The Table 

On the Hadoop node execute:

`hbase shell`

Then using the simple

`scan '<hbase table name>'`

or to follow the example above:

`scan 'WIKIDUMP'`

This will scan the whole table and show you the contents of each row.

Further Documentation on other basic HBase commands can be found in the following links :

http://wiki.apache.org/hadoop/Hbase/Shell

http://learnhbase.wordpress.com/2013/03/02/hbase-shell-commands/

### OUTPUT

The output from a mapreduce job will contain several lines. However it should also contain the end status of your job which should be a SUCCESS for a successful job.

It should also contain a link (http://localhost:50060/tasktracker.jsp) to a webui where you can track the status of your job and view the errors.

## HDFS TO HBase Mapper - Example

* Load the wikipedia data needed for this example onto the HDFS directory.

* Make sure a HBase table exists where you wish to run this example to store the final output.

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table WIKIDUMP, the command to run the example would be of the following form:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.HDFSToHBaseMain /wikidump/links/titles-sorted.txt /wikidump/links/links-simple-sorted.txt WIKIDUMP`

* The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP".

## Assignment-1: Link Reversal

For this specific assignment, we will try to use the HBase table generated by the above example.

* Make sure the HBase table exists and is correctly populated on the node you wish to run .

* To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.LinkReversalMain <hbase_table_name>`

So, in this case it would be:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.LinkReversalMain WIKIDUMP`

* The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP".

## Assignment-2: Crunch Example

* Load the wikipedia data needed for this example onto the HDFS directory.

* Make sure a HBase table exists where you wish to run this example to store the final output.

* To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.CrunchExample <title_file_path>  <links_file_path>  <hbase_table_name>`

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table crunchExample, the command to run the example would be of the following form:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.CrunchExample /wikidump/links/titles-sorted.txt /wikidump/links/links-simple-sorted.txt crunchExample`

* The results can be seen by doing a scan of the resulting HBase table "crunchExample". 


Further Documentation on basic hadoop commands like listing out directories, showing/killing submitted jobs , downloading data from 
HDFS directory to the local directory can all be found in the following link:

 `http://hadoop.apache.org/docs/r2.0.3-alpha/hadoop-project-dist/hadoop-common/CommandsManual.html`










 
