# MAPREDUCE-101 


## Introduction

MapReduce is a great framework for efficiently processing large volumes and variety of data.  This course builds upon a basic understanding of
MapReduce to give participants a hands on experience working with MapReduce and introduce them to a higher order processing framework for composing their jobs.

The purpose of this project is to help individuals with the following:

* Review the different components, configuration and functionality that can be performed inside of the Hadoop MapReduce framework.
* Be able to launch and execute a MapReduce job.
* Cover basic troubleshooting techniques for MapReduce
* Understand the basic concepts of Apache Crunch for composing MapReduce jobs
* Write and execute a runnable crunch job that implements a full pipeline.

The class builds upon tutorials that are already available to focus on concepts specific to Cerner and how we solve Big Data processing problems.  The **Prerequisites** were 
selected to introduce participants to Hadoop & MapReduce.  Please complete the **Prerequisites** prior to arriving to class.  The content of the course will build upon concepts presented in the **Prerequisites** and participants will be lost or left behind if they have not adequately prepared.

## Prerequisites

The prereqs were selected because they provide a nice introduction to Hadoop's Architecture as well as to the concepts of MapReduce.  They are
self-study courses but some portions can be skipped as they are not relevant to how Cerner is using Hadoop.  Please review each prereq to see which sections
you are expected to perform.

### Udacity & Cloudera

Cloudera offers the first four sections of its training for free on [Udacity](http://www.cloudera.com/content/cloudera/en/training/courses/udacity/mapreduce.html).
All of the sections are useful and should be reviewed by participants.  Participants can pay for the additional sections (at their own expense) but it is not necessary.

The Udacity training focuses on Hadoop's Streaming API and Python for interacting with the cluster.  Currently Cerner typically does not use the Streaming
API or Python for MapReduce processing.  Participants should feel free to attempt the lessons and coding operations but should not be blocked by a lack of knowledge
about Python.

### Big Data University

To compensate Cloudera and Udacity's focus on Python, it is also suggested participants take the Big Data University's
[Introduction to MapReduce Programming](http://bigdatauniversity.com/bdu-wp/bdu-course/introduction-to-mapreduce-programming/).  For this course please audit
the following sections:

* Lesson 1: Introduction to MapReduce
* Lesson 2: MapReduce Programming

Do not attempt *Lesson 3: MapReduce Programming Using BigInsights* as BigInsights is not currently used at Cerner and not relevant to general Hadoop & MapReduce knowledge.
The purpose of Big Data University's course is to introduce participants to the Java APIs that developers will typically be using at Cerner.

Completing the labs is not necessary but participants can feel free to do so if they choose.

### Device Setup

The Udacity and Big Data University documentation will include any instructions on how to setup your device to complete their labs.  Please follow those instructions as you attempt the pre-requisites before the class begins.  The VM we use for class will differ from the pre-reqs as this will utilize an updated version of the cluster.

During the actual course you will also be completing labs over the material covered.  Complete the following instructions prior to arriving at the class.

In order to complete either lab the following software must be installed,

 * [VirtualBox](https://www.virtualbox.org/wiki/Downloads)

Once the software has been installed download the following [file](http://repo.release.cerner.corp/nexus/content/repositories/vagrant/com/cerner/virtualbox/cloudera-training-vm-4.2.1/1.0.0/cloudera-training-vm-4.2.1-1.0.0-virtualbox.zip).

Once the file has finished downloading,

 * Unzip the archive. Note that on Windows machines you may need to use 7-zip or WinZip, due to a bug in Windows' built-in unzip program.
 * Launch VirtualBox
 * Create a new machine. Specify that it's a Linux -- RedHat 64-bit machine
 * Allocate at least 1024MB of RAM to the machine (more is better)
 * Specify that you'll use an existing virtual hard drive file, and navigate to the Cloudera-Training-VM-4.2.1.p.vmdk file which you unzipped above

You should now be able to create and launch the virtual machine.

**NOTE** : It will take several minutes to start up the first time. Subsequent startups will be much faster.

## Agenda

* [Hadoop & MapReduce Refresher presentation](https://docs.google.com/presentation/d/1sGhf_nZPlfWHjNPFSgOwKf208_67oC21ZeORNQXVo88/edit#slide=id.p)
* [Hadoop & MapReduce Refresher lab](hadoop/README.md)
* [Introduction to Processing with Crunch](https://docs.google.com/presentation/d/1TnLU5ZaigrR7R4Fkj55zWfFJ1Xcl9FvKaL4h0B9a6-o/edit?usp=sharing)
* [Crunch Processing Lab](crunch/README.md)
