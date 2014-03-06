package com.cerner.training.mapreduce;

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
import java.util.List;
import java.util.Map;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.mapred.FsInput;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.compress.utils.IOUtils;
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
import com.cerner.training.PersonPair;
import com.cerner.training.internal.GeneratedData;

public class FriendsJobTest {

    private static Path peoplePath = new Path("/users/testing/people.avro");
    private static Path morePeoplePath = new Path("/users/testing/more_people.avro");

    private static FileSystem fs;

    private static Map<String, PersonPair> calculatedPairs = new HashMap<String, PersonPair>();

    private static byte[] peopleBytes;
    private static byte[] morePeopleBytes;

    private FriendsJob job;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ClusterTests.startTest();

        fs = FileSystem.get(ClusterTests.getConf());
        peopleBytes = peopleToBytes(GeneratedData.people.values());
        morePeopleBytes = peopleToBytes(GeneratedData.morePeople.values());

        for (Person person1 : GeneratedData.people.values()) {
            for (Person person2 : GeneratedData.people.values()) {
                if (person1.getName().equals(person2.getName()))
                    break;
                String key = getKey(person1, person2);
                if (!calculatedPairs.containsKey(key)) {
                    List<String> friends = new ArrayList<String>(person1.getFriends());
                    friends.retainAll(person2.getFriends());
                    calculatedPairs.put(
                            key,
                            PersonPair.newBuilder().setPersonName1(person1.getName()).setPersonName2(person2.getName())
                                    .setCommonFriends(friends).build());
                }
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
        writeBytes(morePeoplePath, morePeopleBytes);

        job = new FriendsJob();
        job.setConf(ClusterTests.getConf());
    }

    private void writeBytes(Path path, byte[] bytes) throws IOException {
        FSDataOutputStream out = fs.create(path, true);
        IOUtils.copy(new ByteArrayInputStream(bytes), out);
        out.close();
    }

    @After
    public void after() throws IOException {
        fs.delete(peoplePath, true);
        fs.delete(morePeoplePath, true);

        // The output of the FriendsJob
        fs.delete(new Path("friends"), true);
    }

    @Test
    public void runWithMissingData() throws Exception {
        assertThat(run("doesNotExist"), is(1));
    }

    @Test
    public void runWithPeopleData() throws Exception {
        assertThat(run(peoplePath), is(0));
        List<PersonPair> pairs = readJobOutput();
        assertIsCalculatedPersonPairs(pairs);
    }

    @Test
    public void runWithMorePeopleData() throws Exception {
        assertThat(run(morePeoplePath), is(1));
    }

    private int run(Path path) throws Exception {
        return run(path.toString());
    }

    private int run(String path) throws Exception {
        return job.run(new String[] { path });
    }

    private List<PersonPair> readJobOutput() throws IOException {
        List<PersonPair> pairs = new ArrayList<PersonPair>();

        // FriendsJob should create a 'friends' directory with the output
        Path output = new Path("friends");
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

        DatumReader<PersonPair> reader = new SpecificDatumReader<PersonPair>();
        FileReader<PersonPair> fileReader = DataFileReader.openReader(input, reader);

        for (PersonPair pair : fileReader) {
            pairs.add(pair);
        }

        return pairs;
    }

    private void assertIsCalculatedPersonPairs(List<PersonPair> pairs) {
        for (PersonPair pair : pairs) {
            String key = getKey(pair.getPersonName1(), pair.getPersonName2());
            assertTrue(calculatedPairs.containsKey(key));
            PersonPair calculatedPair = calculatedPairs.get(key);
            assertThat(pair.getCommonFriends().size(), is(calculatedPair.getCommonFriends().size()));
            assertTrue(pair.getCommonFriends().containsAll(calculatedPair.getCommonFriends()));
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
