package com.cerner.training;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cerner.training.crunch.CommonFriendsPipelineTest;
import com.cerner.training.crunch.extras.FriendRecommendationPipelineTest;

/**
 * Suite of tests requiring an internal set of clusters/services.
 * 
 * IMPORTANT: New tests added to this project requiring a set of clusters/services should be added to this suite so they will be
 * executed as part of the build.
 */
@RunWith(Suite.class)
@SuiteClasses({
    // com.cerner.training.crunch
    CommonFriendsPipelineTest.class,
    
    // com.cerner.training.crunch.extras
    FriendRecommendationPipelineTest.class
})
public class ClusterTests {
    
    private static MiniDFSCluster cluster;
    private static Configuration conf;
    
    private static boolean runAsSuite = false;
    
    @BeforeClass
    public static void startSuite() throws Exception {
        runAsSuite = true;
        startHDFS();
    }

    @AfterClass
    public static void endSuite() throws Exception {
        stopHDFS();
    }

    public static void startTest() throws Exception {
        if (!runAsSuite) {
            startHDFS();
        }
    }

    public static void endTest() throws Exception {
        if (!runAsSuite) {
            stopHDFS();
        }
    }
    
    public static Configuration getConf() {
        return new Configuration(conf);
    }
    
    private static void startHDFS() throws Exception {
        Configuration startingConf = new Configuration();

        File baseDir = new File("target/hdfs");
        startingConf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
        
        // Run Map/Reduce tests in process.
        startingConf.set("mapreduce.jobtracker.address", "local");

        MiniDFSCluster.Builder clusterBuilder = new MiniDFSCluster.Builder(startingConf);
        cluster = clusterBuilder.build();

        conf = cluster.getConfiguration(0);
    }
    
    private static void stopHDFS() throws Exception {
        cluster.shutdown();
    }

}