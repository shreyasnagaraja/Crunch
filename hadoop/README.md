Hadoop & MapReduce Refresher Lab
================================

The purpose of this lab is to :

 * Review the Hadoop Map/Reduce API with an example map/reduce job
 * Learn how to build and run a map/reduce job
 * Learn how to troubleshoot a map/reduce job when errors occur

But first lets consider a possible map/reduce problem and how we might solve it,

Friends in Common
-----------------

Suppose we have a list of people and their known friends and we want to know for every pair of friends what friends they have in common. 
So a list of friends might look like this,

    [Person] : [Friend1, Friend2, ...]
    [A] : [B, C, D]
    [B] : [A, C, D, E]
    [C] : [A, B, D, E]
    [D] : [A, B, C, E]
    [E] : [B, C, D]

Since the list is small enough we can calculate this on paper,

    [Person1, Person2] : [CommonFriend1, CommonFriend2, ...]
    [A, B] : [C, D]
    [A, C] : [B, D]
    [A, D] : [B, C]
    [B, C] : [A, D, E]
    [B, D] : [A, C, E]
    [B, E] : [C, D]
    [C, D] : [A, B, E]
    [C, E] : [B, D]
    [D, E] : [B, C]

So lets talk about how we might implement this in map/reduce. The hard part of the problem is getting the data for every friend 
pair together. Since the dataset could be too large to hold in memory we can't actually build every friend pair in the mapper. 
So instead what we could do is create a key such that `key(AB) = key(BA)` in the mapper for every person and every friend 
in that person's list. We then write the firends list to the key made up of the person and the friend to the reducer. In the 
reducer we should now end up with two friend lists for every key making the calculation simple. Lets walk through the solution,

First our data would be read into our mapper,

    [A] : [B, C, D]
    [B] : [A, C, D, E]
    [C] : [A, B, D, E]
    [D] : [A, B, C, E]
    [E] : [B, C, D]

Where each line would be an argument to the mapper. For every friend in the list of friends, the mapper will output zero to many 
key-value pairs. The key will be the person along with the friend. The value will be the list of friends. The key will be sorted 
so that the friends are in order, causing all pairs of friends to go to the same reducer. Let's just do it and see if you can 
see the pattern.

    map([A] : [B, C, D]) ->
        [A, B] : [B, C, D]
        [A, C] : [B, C, D]
        [A, D] : [B, C, D]

    map([B] : [A, C, D, E]) -> (Note that A comes before B in the key)
        [A, B] : [A, C, D, E]
        [B, C] : [A, C, D, E]
        [B, D] : [A, C, D, E]
        [B, E] : [A, C, D, E]

    map([C] : [A, B, D, E]) ->
        [A, C] : [A, B, D, E]
        [B, C] : [A, B, D, E]
        [C, D] : [A, B, D, E]
        [C, E] : [A, B, D, E]

    map([D] : [A, B, C, E]) ->
        [A, D] : [A, B, C, E]
        [B, D] : [A, B, C, E]
        [C, D] : [A, B, C, E]
        [D, E] : [A, B, C, E]

    map([E] : [B, C, D]) ->
        [B, E] : [B, C, D]
        [C, E] : [B, C, D]
        [D, E] : [B, C, D]

Before we send these key-value pairs to the reducers, we group them by their keys and get,

    [A, B] : [B, C, D], [A, C, D, E]
    [A, C] : [B, C, D], [A, B, D, E]
    [A, D] : [B, C, D], [A, B, C, E]
    [B, C] : [A, C, D, E], [A, B, D, E]
    [B, D] : [A, C, D, E], [A, B, C, E]
    [B, E] : [A, C, D, E], [B, C, D]
    [C, D] : [A, B, D, E], [A, B, C, E]
    [C, E] : [A, B, D, E], [B, C, D]
    [D, E] : [A, B, C, E], [B, C, D]

Each line will be passed as an argument to a reducer. The reduce function will simply intersect the lists of values and 
output the same key with the result of the intersection. For example, `reduce([A, B] : [B, C, D], [A, C, D, E])` will output 
`([A, B] : [C, D])` and means that friends A and B have C and D as common friends.

The result after reduction is,

    [A, B] : [C, D]
    [A, C] : [B, D]
    [A, D] : [B, C]
    [B, C] : [A, D, E]
    [B, D] : [A, C, E]
    [B, E] : [C, D]
    [C, D] : [A, B, E]
    [C, E] : [B, D]
    [D, E] : [B, C]

If you had trouble following all of that here is an image of the whole process,

![Friends in Common](assets/friends_in_common.png?raw=true)

Refresher Lab
-------------

The Refresher Lab provides a map/reduce implementation for the `Friends in Common` problem. Before we can begin
lets open the VM and clone the project. The entire lab will be done in the VM and all instructions and commands 
should be executed from within the VM.

### Clone the Project

Open a terminal and clone the project using git.

    git clone http://github.cerner.com/CDH/mapreduce-101.git

Then navigate to the `hadoop` project,

    cd mapreduce-101/hadoop

### Setup the Environment

For this lab we will be using Eclipse to view and modify the project. Eclipse is already installed in the
VM but there is still some setup to get it to understand our Maven project.

First we need to generate the Eclipse files required to import the project. So run the following command 
in the `mapreduce-101/hadoop` directory,

    mvn eclipse:eclipse

Next open Eclipse (if you haven't already). Then go to `File -> Import` and select `Existing Projects into Workspace`. 
In this dialog box find the `mapreduce-101/hadoop` directory. This should show that it will import a project called 
`hadoop-map-reduce-101`. Select `Finish` to import the project.

Although we imported the project Eclipse will not be able to find all of our dependencies so we need to tell it where 
to find our local Maven repo. Select the `hadoop-map-reduce-101` project and go to `Project -> Properties`. From there 
select `Java Build Path -> Add Variable`. In this box select `Configure Variables -> New`. The name of the 
variable is `M2_REPO` and the value should be `/home/training/.m2/repository`. Once you have added this variable
Eclipse will ask if you want to rebuild so click `Yes`.

Your environment should now be setup.

### Look Over the Implementation

Now lets start by taking a look at the implementation of this job. The code contains numerous comments which should 
making reading easier. Here are the files that you should look over,

 * `src/main/avro/person.avdl` : This is the file used to define our Avro records. There are two records (Person and PersonPair) which represent the input and output respectively.
 * `src/main/java/com/cerner/training/mapreduce/FriendsJob.java` : This class is responsible for building and running the map/reduce job
 * `src/main/java/com/cerner/training/mapreduce/FriendsMapper.java` : This class is our map/reduce Mapper class
 * `src/main/java/com/cerner/training/mapreduce/FriendsReducer.java` : This class is our map/reduce Reducer class

Once you feel that you understand the implementation lets move on to building the project.

### Building the Project Job

The project uses Maven for its build engine. Go back to your terminal and run the following command in the 
project directory,

    mvn clean package

This should build the Java jar used to run the map/reduce job. Once this is complete you can find this jar 
at `target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar`. 

In addition to creating this jar it also creates some test data for us to play with at `target/people.avro`. 
Let's move this data onto HDFS so we can use it as an input to our map/reduce job.

**NOTE** : You can view the avro data with the following command `java -jar target/tools/avro-tools-1.7.4.jar tojson target/people.avro`. 
This is an avro tool that can be used to print avro data as JSON.

### Copy the Data to HDFS

To copy the data to HDFS we use the `hdfs dfs` commands. The following command should copy the data to HDFS,

    hdfs dfs -copyFromLocal target/people.avro people.avro

We can verify that the data was copied successfully by using the following command,

    hdfs dfs -ls

Now that our data is on HDFS lets run our job.

**NOTE** : More information about the `hdfs dfs` command can be found 
[here](http://hadoop.apache.org/docs/r2.3.0/hadoop-project-dist/hadoop-common/CommandsManual.html#fs).

### Running the Map/Reduce Job

In order to run the job we use the `hadoop jar` command. The following command will run our map/reduce job,

    hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar com.cerner.training.mapreduce.FriendsJob people.avro

Here is the command dissected,

 * `hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar` : Runs our Java jar with the installed Hadoop classpath (hadoop jars, config, etc...)
 * `com.cerner.training.mapreduce.FriendsJob` : The name of the java class that contains our main method which will run our map/reduce job
 * `people.avro` : The argument our job expects which is the path to the avro input data

**NOTE** : More information about the `hadoop jar` command can be found [here](http://hadoop.apache.org/docs/r2.3.0/hadoop-project-dist/hadoop-common/CommandsManual.html#jar).

When the command is run you should likely see this,

    [training@cern-ho020419-t hadoop]$ hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar com.cerner.training.mapreduce.FriendsJob people.avro
    14/03/06 21:34:56 WARN mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    14/03/06 21:34:57 INFO input.FileInputFormat: Total input paths to process : 1

While the job is running lets go the JobTracker web ui and view the running job. Open Firefox and select the `Hadoop JobTracker` tab (http://localhost:50030). You should likely
see a job under the `Running Jobs` category with the name `Friends Job`. Click on the job id to see more details about the job. From the job view we can see,

 * The progress of the job
 * How many tasks were created for both the map and reduce phases
 * The counters for the job which contains stats about the job like number of bytes read/written, number of map input records and number of reduce input groups
 * The tasks for both map and reduce

Once the job is complete you should see this output in your terminal,

    Job completed successfully!
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

You have just successfully run a map/reduce job! For the purposes of the lab we print the results. Normally for most 
map/reduce jobs you would not do this since the results could be gigabytes or even terabytes in size.

### Running the Map/Reduce Job with Additional Data

Now that we have run the map/reduce job lets run the job again but with additional data. When we built the project it created the 
test data `target/people.avro` that we used to run our job. It also created additional data at `target/more_people.avro`. Copy 
this file into HDFS,

    hdfs dfs -copyFromLocal target/more_people.avro more_people.avro

Now lets run the job with this data,

    hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar com.cerner.training.mapreduce.FriendsJob more_people.avro

Once the job is complete you should likely see this output in your terminal,

    [training@cern-ho020419-t hadoop]$ hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar com.cerner.training.mapreduce.FriendsJob more_people.avro
    14/03/06 21:51:57 WARN mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    14/03/06 21:51:59 INFO input.FileInputFormat: Total input paths to process : 1
    Job failed!

Hmm, it seems we had a problem with that last job run so lets try to figure out what happened.

### Debugging the Map/Reduce Job

If you are running a map/reduce job and it fails to complete successfully one of the best places to start is the JobTracker web ui. So open Firefox and get
back to the JobTracker page. There you should see a category for `Failed Jobs` and you should see our last `Friends Job` run in there. Click on the 
job id for the failed job. Here we can see all the tasks that were run and it should show which tasks were `Complete` or `Killed`. You should see 
that our reduce task was killed in that run, so select that task. In this view we should be able to see any errors that it might have seen 
while running. You should likely see this error,

    java.util.NoSuchElementException: iterate past last value
	    at org.apache.hadoop.mapreduce.task.ReduceContextImpl$ValueIterator.next(ReduceContextImpl.java:187)
	    at com.cerner.training.mapreduce.FriendsReducer.reduce(FriendsReducer.java:30)
	    at com.cerner.training.mapreduce.FriendsReducer.reduce(FriendsReducer.java:23)
	    ...

Well, it seems like our reducer class may have a bug in it.

### Fixing the bug

So lets open our editor to our reducer class (`src/main/java/com/cerner/training/mapreduce/FriendsReducer.java`) and try to figure out
what could be causing this exception and how we might fix it.

Once you think you have fixed the issue try re-running the job.

If you fix the job your output should look like this,

    [training@cern-ho020419-t hadoop]$ hadoop jar target/hadoop-map-reduce-101-1.0-SNAPSHOT.jar com.cerner.training.mapreduce.FriendsJob more_people.avro
    14/03/06 22:06:48 WARN mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
    14/03/06 22:06:49 INFO input.FileInputFormat: Total input paths to process : 1
    Job completed successfully!
    The friends [captain america] and [hulk] have the following friends in common ...
      [thor]
    The friends [captain america] and [thor] have the following friends in common ...
      [hulk]
    The friends [hulk] and [thor] have the following friends in common ...
      [iron man, captain america]
    The friends [loki] and [thor] have the following friends in common ...
      []

If you have fixed the issue you have finished the lab.