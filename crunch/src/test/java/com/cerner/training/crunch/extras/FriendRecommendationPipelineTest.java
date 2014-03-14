package com.cerner.training.crunch.extras;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.FsInput;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cerner.training.ClusterTests;
import com.cerner.training.Person;
import com.cerner.training.internal.GeneratedData;

public class FriendRecommendationPipelineTest {

    private static Path peoplePath = new Path("/users/testing/people.avro");

    private static FileSystem fs;

    private static Map<String, Person> calculatedPairs = new HashMap<String, Person>();

    private static byte[] peopleBytes;

    private FriendRecommendationPipeline pipeline;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ClusterTests.startTest();

        fs = FileSystem.get(ClusterTests.getConf());
        peopleBytes = peopleToBytes(GeneratedData.people.values());

        for (Person person1 : GeneratedData.people.values()) {
            for (Person person2 : GeneratedData.people.values()) {
                if (person1.getName().equals(person2.getName()))
                    continue;
                
                String key = person1.getName();
                
                if (!calculatedPairs.containsKey(key)) {
                    calculatedPairs.put(key, Person.newBuilder().setName(key).setFriends(new ArrayList<String>()).build());
                }
                
                Set<String> recommendedFriends = new HashSet<String>(calculatedPairs.get(key).getFriends());
                List<String> person2RecommendedFriends = new ArrayList<String>(person2.getFriends());
                person2RecommendedFriends.remove(person1.getName());
                person2RecommendedFriends.removeAll(person1.getFriends());
                recommendedFriends.addAll(person2RecommendedFriends);
                
                calculatedPairs.get(key).setFriends(new ArrayList<String>(recommendedFriends));
            }
        }
    }

    private static byte[] peopleToBytes(Collection<Person> people) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReflectDatumWriter<Person> reflectDatumWriter = new ReflectDatumWriter<Person>(Person.getClassSchema());
        DataFileWriter<Person> writer = new DataFileWriter<Person>(reflectDatumWriter)
                .create(Person.getClassSchema(), outputStream);

        for (Person person : people) {
            writer.append(person);
        }

        writer.close();

        return outputStream.toByteArray();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        ClusterTests.endTest();
    }

    @Before
    public void before() throws IOException {
        writeBytes(peoplePath, peopleBytes);

        pipeline = new FriendRecommendationPipeline();
        pipeline.setConf(ClusterTests.getConf());
    }

    private void writeBytes(Path path, byte[] bytes) throws IOException {
        FSDataOutputStream out = fs.create(path, true);
        IOUtils.copy(new ByteArrayInputStream(bytes), out);
        out.close();
    }

    @After
    public void after() throws IOException {
        fs.delete(peoplePath, true);

        // The output of the FriendRecommendationPipeline
        fs.delete(new Path("recommended_friends"), true);
    }

    @Test
    public void runWithMissingData() throws Exception {
        assertThat(run("doesNotExist"), is(1));
    }

    @Test
    public void runWithPeopleData() throws Exception {
        assertThat(run(peoplePath), is(0));
        List<Person> recommendations = readJobOutput();
        assertIsRecommendedPeople(recommendations);
    }

    private int run(Path path) throws Exception {
        return run(path.toString());
    }

    private int run(String path) throws Exception {
        return pipeline.run(new String[] { path });
    }

    private List<Person> readJobOutput() throws IOException {
        List<Person> pairs = new ArrayList<Person>();

        // FriendsJob should create a 'friends' directory with the output
        Path output = new Path("recommended_friends");
        assertTrue(fs.exists(output));

        Path avroFile = null;
        for (FileStatus fileStatus : fs.listStatus(output)) {
            if (fileStatus.getPath().getName().endsWith(".avro")) {
                avroFile = fileStatus.getPath();
                break;
            }
        }

        assertThat(avroFile, is(notNullValue()));

        SeekableInput input = new FsInput(avroFile, ClusterTests.getConf());

        DatumReader<Person> reader = new SpecificDatumReader<Person>();
        FileReader<Person> fileReader = DataFileReader.openReader(input, reader);

        for (Person pair : fileReader) {
            pairs.add(pair);
        }

        return pairs;
    }

    private void assertIsRecommendedPeople(List<Person> people) {
        for (Person person : people) {
            assertTrue(calculatedPairs.containsKey(person.getName()));
            Person calculatedPerson = calculatedPairs.get(person.getName());
            assertThat(person.getFriends().size(), is(calculatedPerson.getFriends().size()));
            assertTrue(person.getFriends().containsAll(calculatedPerson.getFriends()));
        }
    }

    private static String getKey(Person person1, Person person2) {
        return getKey(person1.getName(), person2.getName());
    }

    private static String getKey(String personName1, String personName2) {
        if (personName1.compareTo(personName2) <= 0)
            return personName1 + " - " + personName2;

        return personName2 + " - " + personName1;
    }

}
