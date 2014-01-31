# MAPREDUCE-101 


## Introduction


The purpose of this project is to help individuals with the following:

* Understand the different components,configuration and functionality that can be performed inside of the Hadoop MapReduce framework.
* Write map and reduce functions.
* Write and execute a runnable crunch job that implements a full pipeline.


## Assignment Overview



### 1-HDFS To HBase Mapper 

**Goal**:  Understand the different aspects of the Hadoop MapReduce framework by importing data from HDFS to an HBASE Table.

**Success Criteria**:

* Successfully execute all steps of the assignment.
* Verify the results by scanning the HBase table.


### 2-Link Reversal

**Goal**: Create a MapReduce job that will read the 'outlinks' column from the HBase table generated in the previous assignment and create an 'inlinks' column by reversing the links in the graph.

**Success Criteria**:

* Write the map and reduce function as instructed in the code.
* Ensure code changes pass all Junit tests.
* Successfully Build and execute.
* Verify the results by scanning the table.

### 3-Crunch

[Getting Started Guide For Crunch](https://wiki.ucern.com/display/pophealth/Getting+Started+with+Apache+Crunch).

**Goal**:  Create a runnable Crunch job that implements the full pipeline of HDFS->HBase + Link Reversal in a single pipeline.

**Success Criteria**:

* Complete TODOs as instructed in the code.
* Ensure code changes pass all Junit tests.
* Successfully Build and execute the job.
* Verify the results by scanning the table.


## Prerequisite Installation

This project requires the following to be installed:

* Git
* Apache Maven
* Java
* Vagrant
* Virtual Box



### Git

_Windows_

http://msysgit.github.io/

http://geekswithblogs.net/renso/archive/2009/10/21/how-to-set-the-windows-path-in-windows-7.aspx

_Linux/Mac_

https://wiki.ucern.com/display/pophealth/Git+Reference#GitReference-GitInstallation



### VirtualBox

* [Download virtual box](https://www.virtualbox.org/wiki/Downloads)



### Vagrant

* [Follow the instructions for installation of Vagrant](http://docs.vagrantup.com/v2/installation/index.html)


## Environment Setup

### Clone the Project

Select a directory and download the project from github

`git clone http://github.cerner.com/CDH/mapreduce-101.git`


### Acquire Vagrant Box

The VagrantFile in this repository has all of the connection information needed to download, import and start the vagrant box. The operation done using the Vagrant file is large, so it is best to do it on a Cerner connection.

On the command line navigate into the clone directory and execute:

`vagrant up`

It will then start to import the box and start it up. This will take awhile so be patient.

Once the box has started successfully you should be able to access the environment with the following command:

`vagrant ssh`

Get sudo access by doing:

 `sudo su -` 
 
Once your inside the box, the project will be available under the `/vagrant` directory.


## Assignment

### 1-HDFS To HBase Mapper

#### Find Active Hadoop Namenode 


If `http://bdatadevhadoopmstr01.northamerica.cerner.net:50070` is active 

then **source_hostname** =  bdatadevhadoopmstr01

Otherwise, `http://bdatadevhadoopmstr02.northamerica.cerner.net:50070` should be active

and **source_hostname** = bdatadevhadoopmstr02



#### Load Data to HDFS   

* Create a wikidump directory on hdfs:

`hadoop fs -mkdir wikidump`


* Use the Hadoop distributed copy tool to copy the data we need from the HDFS in the source cluster, using the active **source_hostname** defined above, to our HDFS:

`hadoop distcp hdfs://<source_hostname>.northamerica.cerner.net:8020/wikidump/links hdfs://0.0.0.0:8020/wikidump`



* Confirm input data is successfully copied by following: 

`http://localhost:50075/browseDirectory.jsp?dir=%2Fwikidump&namenodeInfoPort=50070&nnaddr=localhost:8020`


#### Create an HBase Table


* Create 2 tables `WIKIDUMP` and `crunchExample` having family name "RAW" using the HBase Shell:

`hbase shell createTable.rb` 


* List the tables created from the Hadoop node:

`hbase shell`


* List all of the HBase tables:

`list`

(NOTE: You will see a third table TEST_TABLE which is created as a part of vagrant file. This can be ignored).


* Display the table structure of the HBase table:

`describe '<HBase table name>'`


#### HDFS TO HBase Mapper - Example


* Load the wikipedia data needed for this example onto the HDFS directory.

* Make sure a HBase table exists where you wish to run this example to store the final output.

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table WIKIDUMP, the command to run the example would be of the following form:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.HDFSToHBaseMain /wikidump/titles-sorted.txt /wikidump/links-simple-sorted.txt WIKIDUMP`

The output from a mapreduce job will contain several lines. However it should also contain the end status of your job which should be a SUCCESS for a successful job.

It should also contain a link (http://localhost:50030/jobtracker.jsp) to a webui where you can track the status of your job and view the errors.

* The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP".


#### Scanning The Table 

* On the Hadoop node execute:

`hbase shell`

Then using the simple

`scan '<hbase table name>'`

or to follow the example above:

`scan 'WIKIDUMP'`

This will scan the whole table and show you the contents of each row.


### 2-Link Reversal


In order to run this assignment you must first make sure all the **TODO's** are complete in: 

`LinkReversalMapper.java`  _/src/main/java/com/cerner/cdh/examples/mapper_

`LinkReversalReducer.java` _/src/main/java/com/cerner/cdh/examples/reducer_


and corresponding **Junit tests** pass inside:

LinkReversalMapperTest.java  _/src/test/java/com/cerner/cdh/examples/mapper_
LinkReversalReducerTest.java  _/src/test/java/com/cerner/cdh/examples/reducer_


* Build your project using the following command :

   `mvn clean install -DskipTests=true`
   
(NOTE: You are doing a skipTest here because if you plan to build the entire project your build will fail assuming
you haven't completed Assignment 3)

(NOTE: The jar will be generated in the *target/* folder (e.g. ~/target/mapreduce-101-1.0-SNAPSHOT.jar) )


* Make sure the HBase table exists and is correctly populated on the node you wish to run.

* Run the link reversal using the WIKIDUMP table generated in Assignment 1:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.LinkReversalMain WIKIDUMP`

* The results can be seen by doing a scan of the resulting HBase table "WIKIDUMP".



### 3-Crunch

In order to run this assignment you must first make sure all the **TODO's** are complete in: 

[x] CrunchExample.java  _/src/main/java/com/cerner/cdh/examples/crunchExample_


[x] CreatePutFn.java  _/src/main/java/com/cerner/cdh/examples/crunchExample_


[x] StringSplitFunction.java _/src/main/java/com/cerner/cdh/examples/crunchExample_


and corresponding **Junit tests** pass inside:


[x] CreatePutFnTest.java  _/src/test/java/com/cerner/cdh/examples/crunchExample_


[x] StringSplitFunctionTest.java  _/src/test/java/com/cerner/cdh/examples/crunchExample_



* Build your project using the following command :

   `mvn clean install`
    
* Load the wikipedia data needed for this example onto the HDFS directory.

* Make sure a HBase table exists where you wish to run this example to store the final output.

* To run the example enter the following command on the Hadoop node:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.CrunchExample <title_file_path>  <links_file_path>  <hbase_table_name>`

For example if the inputs have been loaded onto the "/wikidump/links" on HDFS and there exists a HBase Table crunchExample, the command to run the example would be of the following form:

`hadoop jar mapreduce-examples-1.0-SNAPSHOT.jar com.cerner.cdh.examples.CrunchExample /wikidump/titles-sorted.txt /wikidump/links-simple-sorted.txt crunchExample`

* The results can be seen by doing a scan of the resulting HBase table "crunchExample". 


## Notes

### Further Documentation on basic Hadoop commands like listing out directories, showing/killing submitted jobs, downloading data from HDFS directory to the local directory can all be found in the following link:

 http://hadoop.apache.org/docs/r2.0.3-alpha/hadoop-project-dist/hadoop-common/CommandsManual.html


### Further Documentation on other basic HBase commands can be found in the following links :

http://wiki.apache.org/hadoop/Hbase/Shell

http://learnhbase.wordpress.com/2013/03/02/hbase-shell-commands/


### The Data
Most of the examples in this project run over data extracted from [Wikipedia](http://www.wikipedia.org/). The data is available at the following Namenodes depending on whichever is active

i) http://bdatadevhadoopmstr01.northamerica.cerner.net:50070

ii) http://bdatadevhadoopmstr02.northamerica.cerner.net:50070

### Ports to remember

* 50030 - JobTracker

  The jobtracker‘s UI (http://localhost:50030) is commonly used to look at running jobs, and, especially, to find the causes of failed jobs.
  More Information on the JobTracker can be found [here](http://wiki.apache.org/hadoop/JobTracker)

* 50060 - TaskTracker
 
  A TaskTracker (http://localhost:50060) is a node in the cluster that accepts tasks. The Tasktrackers UI shows the running tasks.More Information
  on the TastTracker can be found [here](http://wiki.apache.org/hadoop/TaskTracker)

* 60010 - HBase Master WebUI 

  The HBase Master server is responsible for monitoring all RegionServer instances in the cluster, and is the interface for all metadata changes.
  More information on HMaster and Region servers can be found [here](http://hbase.apache.org/book/master.html) 

* 50070 - NameNode

  A NameNode(http://localhost:50070) is basically responsible for maintaining the Hadoop filesystem metadata. More information can be found [here] --  (http://wiki.apache.org/hadoop/NameNode)
   
## Presentation

The presentation to accompany this lab is available [here](https://docs.google.com/presentation/d/1JHmELsppdqb4BaaHUzle3lVF_HouB4CE5NvjYBX64D0/edit?usp=sharing).
