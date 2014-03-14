package com.cerner.training;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cerner.training.crunch.ComputeCommonFriendsTest;
import com.cerner.training.crunch.PeopleToFriendsTest;
import com.cerner.training.crunch.extras.FriendsToRawRecommendationsTest;
import com.cerner.training.crunch.extras.RawRecommendationsToRecommendationsTest;
import com.cerner.training.internal.GeneratedDataTest;

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

        // com.cerner.training.crunch
        PeopleToFriendsTest.class, ComputeCommonFriendsTest.class,

        // com.cerner.training.crunch.extras
        FriendsToRawRecommendationsTest.class, RawRecommendationsToRecommendationsTest.class })
public class StandaloneTests {

}