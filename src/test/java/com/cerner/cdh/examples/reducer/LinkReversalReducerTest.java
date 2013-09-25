package com.cerner.cdh.examples.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class LinkReversalReducerTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testLinkReversalReducer() throws IOException,
			InterruptedException {

		Text key = new Text("key");
		Context context = Mockito.mock(Context.class);
		Iterable<Text> values = generateText();

		// Step-1 : Create a inlinks String from the Iterable values
		StringBuilder inlinks = new StringBuilder();

		for (Text value : values) {
			inlinks.append(value);
			inlinks.append(" ");
		}

		LinkReversalReducer reducer = new LinkReversalReducer();

		// Step-2: Obtain the docId from the Key
		byte[] docIdBytes = Bytes.toBytes(key.toString());

		// Step-3: Create a Put with the docId and add the specified column and
		// value to this Put operation.
		byte[] argument1 = Bytes.toBytes("family");
		byte[] argument2 = Bytes.toBytes("qualifier");
		byte[] argument3 = Bytes.toBytes(inlinks.toString().trim());

		Put put = new Put(docIdBytes);
		put.add(argument1, argument2, argument3);

		reducer.reduce(key, values, context);

		// Step-4 : Do a context write
		Mockito.verify(context).write(Matchers.anyObject(), Matchers.anyObject());

	}

	private List<Text> generateText() {
		Text value = new Text("AB");
		List<Text> texts = new ArrayList<Text>();
		texts.add(value);
		return texts;
	}

}