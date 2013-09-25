package com.cerner.cdh.examples.crunchExample;

import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;
import org.junit.Test;
import org.mockito.Mockito;

public class StringSplitFunctionTest {

    /**
     * A test that demonstrate a DoFn through unit Tests.
     */
    @Test
    public void testSplitDoFn() {

        String stringToSplit = "Hello:World";

        // Mocking the Emitter
        Emitter<Pair<String, String>> emitter = Mockito.mock(Emitter.class);
        StringSplitFunction cfn = new StringSplitFunction();
        cfn.process(stringToSplit, emitter);

        // Verifying weather the split happened and the emitter was called once
        Mockito.verify(emitter, Mockito.times(1)).emit(Pair.of("Hello", "World"));

    }
}
