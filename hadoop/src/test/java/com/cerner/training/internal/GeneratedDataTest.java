package com.cerner.training.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.cerner.training.Person;

public class GeneratedDataTest {
    
    @Test
    public void peopleGeneratedData() {
        // There should be some data
        assertTrue(GeneratedData.people.values().size() > 0);
        
        for(Person person : GeneratedData.people.values()) {
            
            // Every person in our generated dataset should have friends
            assertTrue(person.getFriends().size() > 0);
            
            for(String friend : person.getFriends()) {
                // For every person -> friend that opposite relationship should exist (i.e. friend -> person)
                assertTrue(GeneratedData.people.get(friend).getFriends().contains(person.getName()));
            }
        }
    }
    
    @Test
    public void morePeopleGeneratedData() {
        // There should be some data
        assertTrue(GeneratedData.morePeople.values().size() > 0);
        
        boolean missingFriendConnection = false;
        for(Person person : GeneratedData.morePeople.values()) {
            for(String friend : person.getFriends()) {
                if(!GeneratedData.morePeople.get(friend).getFriends().contains(person.getName())) {
                    missingFriendConnection = true;
                    break;
                }
            }
        }
        
        // There should be at least one case where we are missing a friend connection
        assertTrue(missingFriendConnection);
    }
    
    @Test
    public void writeGeneratedData() throws IOException {
        TemporaryFolder tmp = new TemporaryFolder();
        tmp.create();
        
        File file = new File(tmp.getRoot().getAbsoluteFile() + "/people.avro");
        GeneratedData.write(GeneratedData.people.values(), file);
        
        List<Person> writtenPeople = read(file);
        assertThat(GeneratedData.people.size(), is(writtenPeople.size()));
        assertTrue(writtenPeople.containsAll(GeneratedData.people.values()));
        
        tmp.delete();
    }
    
    private List<Person> read(File file) throws IOException {
        List<Person> people = new ArrayList<Person>();
        
        DatumReader<Person> reader = new SpecificDatumReader<Person>();
        FileReader<Person> fileReader = DataFileReader.openReader(file, reader);
        
        for(Person person : fileReader){
            people.add(person);
        }
        
        return people;
    }

}
