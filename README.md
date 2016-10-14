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

Cloudera offers the first four sections of its training for free on [Udacity](https://www.udacity.com/course/intro-to-hadoop-and-mapreduce--ud617).
All of the sections are useful and should be reviewed by participants.  Participants can pay for the additional sections (at their own expense) but it is not necessary.

The Udacity training focuses on Hadoop's Streaming API and Python for interacting with the cluster.  Currently Cerner typically does not use the Streaming
API or Python for MapReduce processing.  Participants should feel free to attempt the lessons and coding operations but should not be blocked by a lack of knowledge
about Python.

These videos are also available on [Youtube as the following playlist](https://www.youtube.com/watch?v=DEQNknALf_8&list=PLAwxTw4SYaPkXJ6LAV96gH8yxIfGaN3H-).


### YouTube Videos

There are a number of great Youtube videos and playlists that also give a great introduction to the basics of the HDFS architecture and MapReduce.

* [Hadoop - Just the Basics for Big Data Rookies](https://www.youtube.com/watch?v=xYnS9PQRXTg)
* [Introduction to MapReduce](https://www.youtube.com/watch?v=1rKnf3MmSJA&list=PLkp40uss1kSJ5TL379zpHnYgnXimMuR2w)

Watching these videos ahead of class will give you a good introduction to the basics we will review the basics in class but preparing ahead of time will allow us to go deeper into the subject matter or allow you to come with more detailed questions.


### Device Setup

The Udacity documentation will include any instructions on how to setup your device to complete their labs.  Please follow those instructions as you attempt the pre-requisites before the class begins.  The VM we use for class will differ from the pre-reqs as this will utilize an updated version of the cluster.

During the actual course you will also be completing labs over the material covered.  Complete the following instructions prior to arriving at the class.

In order to complete either lab the following software must be installed,

 * [Git](http://git-scm.com/book/en/Getting-Started-Installing-Git)
 * [VirtualBox](https://www.virtualbox.org/wiki/Downloads) Check this [documentation](https://wiki.ucern.com/display/OPSINFRA/Enterprise+Chef+SDK) for information on versions.
 * [Vagrant](http://www.vagrantup.com/downloads.html)

Once you have installed the above software, open a terminal or command line and do the following,

    git clone http://github.cerner.com/CDH/mapreduce-101.git
    cd mapreduce-101
    vagrant up

These commands will clone the `mapreduce-101` repository onto your local machine. Then you will issue the `vagrant up` command
which will bring up the VM.

**NOTE** : It will take several minutes (~10m) for the whole process to finish and for all services to start up on VM.

In addition the `mapreduce-101` repository on your host machine will be synced with your VM so changes made in either will
be visible in the other.

## Links

* [Map/Reduce presentation](https://docs.google.com/presentation/d/1Ljnyb0l88NCSF1GrWhPwnhXr_oZxdRM7cSMpZ-E2O28)
* [Hadoop & MapReduce Refresher lab](hadoop/README.md)
* [Crunch Presentation & Processing Lab](crunch/README.md)

### Archive

These are some of the older presentations that we've used in the past that can be reviewed stand alone if desired.

* [Introduction to Processing with Crunch](https://docs.google.com/presentation/d/1TnLU5ZaigrR7R4Fkj55zWfFJ1Xcl9FvKaL4h0B9a6-o/edit?usp=sharing)
