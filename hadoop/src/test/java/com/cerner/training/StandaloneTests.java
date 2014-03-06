package com.cerner.training;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cerner.training.internal.GeneratedDataTest;
import com.cerner.training.mapreduce.FriendsMapperTest;
import com.cerner.training.mapreduce.FriendsReducerTest;

/**
 * Suite of tests that can be run independently of any clusters or services.
 * 
 * IMPORTANT: New tests added to this project <em>not</em> requiring clusters or services should be added to this suite so they will
 * be executed as part of the build.
 */
@RunWith(Suite.class)
@SuiteClasses({
        // com.cerner.training.internal
        GeneratedDataTest.class,
        
        // com.cerner.training.mapreduce
        FriendsMapperTest.class, FriendsReducerTest.class
 })
public class StandaloneTests {

}
