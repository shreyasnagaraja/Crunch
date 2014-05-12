Crunch Processing Lab
=====================

The purpose of this lab is to :

 * Learn the Crunch API
 * Build and run a Crunch pipeline

Lab
---

For this lab we are going to use the same 'Friends in Common' problem that we saw in the 
[refresher lab](../hadoop/README.md).

The pipeline should follow the same algorithm we used in the map/reduce job to calculate the friends in common and 
look like this,

![Friends in Common Pipeline](assets/friends_in_common_pipeline.png?raw=true)

### Clone the Project

If you haven't done so already, clone the project.

Open a terminal and clone the project using git.

    git clone http://github.cerner.com/CDH/mapreduce-101.git

Then navigate to the `crunch` project,

    cd mapreduce-101/crunch

### Setup the Environment

Again for this lab we will be using Eclipse to view and modify the project. Eclipse is already installed in the
VM but there is still some setup to get it to understand our Maven project.

First open Eclipse (if you haven't already). Then go to `File -> Import` and select `Existing Projects into Workspace`. 
In this dialog box find the `mapreduce-101/hadoop` directory. This should show that it will import a project called 
`hadoop-map-reduce-101`. Select `Finish` to import the project.

If you already added the `M2_REPO` environment variable from the last lab you can skip this step and your environment 
should be setup.

If for some reason you don't have the `M2_REPO` setup follow these instructions.

Although we imported the project Eclipse will not be able to find all of our dependencies so we need to tell it where 
to find our local Maven repo. Select the `crunch-101` project and go to `Project -> Properties`. From there 
select `Java Build Path -> Add Variable`. In this box select `Configure Variables -> New`. The name of the 
variable is `M2_REPO` and the value should be `/home/training/.m2/repository`. Once you have added this variable
Eclipse will ask if you want to rebuild so click `Yes`.

Your environment should now be setup.

### Write a Pipeline

Before we starting writing our pipeline make sure you take a look at the crunch [doc](http://crunch.apache.org/) and 
[javadoc](http://crunch.apache.org/apidocs/0.8.2/). You will likely need these to help you complete your pipeline.

We have provided a stubbed pipeline class for you to start with here `src/main/java/com/cerner/training/crunch/CommonFriendsPipeline.java`. 
This should provide you with everything you need but the crunch pipeline and processing.

Once you think you have the pipeline complete, feel free to try building and testing the pipeline.

### Building and Testing the Project

From within the `crunch` directory in the mapreduce-101 repository run the following command
to build the project,

    mvn clean install

This should build the Java jar used to run the map/reduce job. Once this is complete you can 
find this jar at `target/crunch-101-1.0-SNAPSHOT.jar`.

Just like before, building the project will also generate some test data to play with at 
`target/people.avro`. This is the same data as before and is provided in case the original 
data is lost.

If you did remove the old data copy this data over to HDFS,

    hdfs dfs -copyFromLocal target/people.avro people.avro

Then we can run the Crunch pipeline using the following command,

    hadoop jar target/crunch-101-1.0-SNAPSHOT.jar com.cerner.training.crunch.CommonFriendsPipeline people.avro

This should kick off the pipeline and you should see output that looks like this,

    2014-03-10 14:14:07,016 INFO org.apache.crunch.io.impl.FileTargetImpl: Will write output files to new path: friends
    2014-03-10 14:14:07,415 INFO org.apache.crunch.impl.mr.collect.PGroupedTableImpl: Setting num reduce tasks to 1
    2014-03-10 14:14:07,514 WARN org.apache.hadoop.mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    2014-03-10 14:14:07,960 WARN org.apache.hadoop.conf.Configuration: dfs.block.size is deprecated. Instead, use dfs.blocksize
    2014-03-10 14:14:07,965 INFO org.apache.hadoop.mapreduce.lib.input.FileInputFormat: Total input paths to process : 1
    2014-03-10 14:14:07,988 INFO com.hadoop.compression.lzo.GPLNativeCodeLoader: Loaded native gpl library
    2014-03-10 14:14:07,992 INFO com.hadoop.compression.lzo.LzoCodec: Successfully loaded & initialized native-lzo library [hadoop-lzo rev c7d54fffe5a853c437ee23413ba71fc6af23c91d]
    2014-03-10 14:14:07,992 INFO org.apache.hadoop.io.compress.LzoCodec: Bridging org.apache.hadoop.io.compress.LzoCodec to com.hadoop.compression.lzo.LzoCodec.
    2014-03-10 14:14:08,439 INFO org.apache.crunch.hadoop.mapreduce.lib.jobcontrol.CrunchControlledJob: Running job "com.cerner.training.crunch.CommonFriendsPipeline: Avro(people.avro)+S0+GBK+S1+Avro(friends) (1/1)"
    The pipeline was successful!
    The friends [captain america] and [hulk] have the following friends in common ...
      [thor]
    The friends [captain america] and [thor] have the following friends in common ...
      [hulk]
    The friends [iron man] and [hulk] have the following friends in common ...
      [thor]
    The friends [hulk] and [thor] have the following friends in common ...
      [iron man, captain america]
    The friends [iron man] and [thor] have the following friends in common ...
      [hulk]
    The friends [loki] and [thor] have the following friends in common ...
      []

If you see any errors use the job tracker's logs to help debug your pipeline.

**NOTE** : Normally you would test your Crunch pipeline by writing unit and integration tests but we test our
pipeline by running it on this local cluster for test purposes.

If you have completed this assignment you have finished all of the required sections of the Crunch Processing Lab. 
There is an extra assignment if you are interested or have finished the lab quickly and have extra time.

### Extra Assignment

This assignment is not required for completion of the Crunch Processing Lab and is only for those 
who are interested or completed the first part of this lab quickly.

Here we introduce a variation on our 'Friends in Common' problem.

#### Friend Recommendation

Another problem in the friends space is friend recommendation. If friend `A` has friends `B` and friend `B` 
has friends `A, C` then  `B` should recommend `C` to `A` and `A` has no recommendations for `B`. 
So we should recommend any of our friends to our friends if they are not already friends or all of my 
friends should be friends with each other.

So if we look at our original data set,

    [Person] : [Friend1, Friend2, ...]
    [A] : [B, C, D]
    [B] : [A, C, D, E]
    [C] : [A, B, D, E]
    [D] : [A, B, C, E]
    [E] : [B, C, D]

The results would be,

    [A] : [E]
    [B] : []
    [C] : []
    [D] : []
    [E] : [A]

Here we leave the implementation up to you.

#### Writing the Friend Recommendation Pipeline

Again we have provided a stubbed pipeline class for you to start with at 
`src/main/java/com/cerner/training/crunch/extras/FriendRecommendationPipeline.java`.

Once you think you have completed the pipeline feel free to move onto building and testing the project.

#### Building and Testing the Project

To test the pipeline build the project,

    mvn clean install

and run the following command,

    hadoop jar target/crunch-101-1.0-SNAPSHOT.jar com.cerner.training.crunch.extras.FriendRecommendationPipeline people.avro

This should kick off the pipeline and you should see output that looks something like this,

    2014-03-10 14:23:26,955 INFO org.apache.crunch.io.impl.FileTargetImpl: Will write output files to new path: recommended_friends
    2014-03-10 14:23:27,282 INFO org.apache.crunch.impl.mr.collect.PGroupedTableImpl: Setting num reduce tasks to 1
    2014-03-10 14:23:27,353 INFO org.apache.crunch.impl.mr.collect.PGroupedTableImpl: Setting num reduce tasks to 1
    2014-03-10 14:23:27,450 WARN org.apache.hadoop.mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    2014-03-10 14:23:28,282 WARN org.apache.hadoop.conf.Configuration: dfs.block.size is deprecated. Instead, use dfs.blocksize
    2014-03-10 14:23:28,287 INFO org.apache.hadoop.mapreduce.lib.input.FileInputFormat: Total input paths to process : 1
    2014-03-10 14:23:28,311 INFO com.hadoop.compression.lzo.GPLNativeCodeLoader: Loaded native gpl library
    2014-03-10 14:23:28,314 INFO com.hadoop.compression.lzo.LzoCodec: Successfully loaded & initialized native-lzo library [hadoop-lzo rev c7d54fffe5a853c437ee23413ba71fc6af23c91d]
    2014-03-10 14:23:28,315 INFO org.apache.hadoop.io.compress.LzoCodec: Bridging org.apache.hadoop.io.compress.LzoCodec to com.hadoop.compression.lzo.LzoCodec.
    2014-03-10 14:23:28,708 INFO org.apache.crunch.hadoop.mapreduce.lib.jobcontrol.CrunchControlledJob: Running job "com.cerner.training.crunch.extras.FriendRecommendationPipeline: Avro(people.avro)+S0+GBK+S1+Avro(/tmp/crunch-25063506/p1) (2/2)"
    2014-03-10 14:23:54,395 WARN org.apache.hadoop.mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    2014-03-10 14:23:54,735 INFO org.apache.hadoop.mapreduce.lib.input.FileInputFormat: Total input paths to process : 1
    2014-03-10 14:23:54,969 INFO org.apache.crunch.hadoop.mapreduce.lib.jobcontrol.CrunchControlledJob: Running job "com.cerner.training.crunch.extras.FriendRecommendationPipeline: Avro(/tmp/crunch-25063506/p1)+GBK+S2+Avro(recommended_fri... (1/2)"
    The pipeline was successful!
    The person [captain america] should be friends with ...
      [loki, iron man]
    The person [hulk] should be friends with ...
      [loki]
    The person [iron man] should be friends with ...
      [loki, captain america]
    The person [loki] should be friends with ...
      [captain america, iron man, hulk]

If you see any errors use the job tracker's logs to help debug your pipeline.

If you have completed this assignment you have completed all of the Crunch Processing Lab.

### Additional Ideas

Here are some additional ideas for those who want to try more.

 * In the friend recommendation problem include a score which would indicate the number of times a friend was recommended
 * Figure out a way to recommend friends for those who do not have any friends