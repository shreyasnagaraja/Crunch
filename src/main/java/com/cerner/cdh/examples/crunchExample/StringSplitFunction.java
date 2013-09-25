package com.cerner.cdh.examples.crunchExample;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

/**
 * A DoFn allows implementations to emit multiple values for a single input
 * 
 */
public class StringSplitFunction extends DoFn<String, Pair<String, String>> {

    /**
     * The serialVersionUID.
     * 
     * More info on this can be found at http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html
     * 
     */
    private static final long serialVersionUID = 8512055247284202128L;

    // TODO: This DoFn should split the input lines on ":" and emit the pair.
    // Run the StringSplitFunctionTest after completion to see if its correctly implemented
    @Override
    public void process(String line, Emitter<Pair<String, String>> emitter) {

    }
}
