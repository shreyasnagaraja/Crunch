package com.cerner.training.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cerner.training.Person;

/**
 * Class used internally to generate data for the lab
 */
public class GeneratedData {

    /**
     * Some generated data for the lab
     */
    public static Map<String, Person> people = new HashMap<String, Person>();

    static {
        // A person cannot a comma in their name otherwise CSV parsing would fail
        areFriends("thor", "loki");
        areFriends("thor", "hulk");
        areFriends("thor", "captain america");
        areFriends("thor", "iron man");
        areFriends("iron man", "hulk");
        areFriends("captain america", "hulk");
    }

    /**
     * Writes both datasets to the target/ directory
     * 
     * @param args
     *            any arguments
     * @throws Exception
     *             if there is an issue writing the files
     */
    public static void main(String[] args) throws Exception {
        write(people.values(), new File("target/people.csv"));
    }

    /**
     * Writes the given collection of {@link Person} objects to the given {@link File} in CSV format
     * 
     * @param people
     *            the collection of {@link Person} objects
     * @param file
     *            the {@link File} to write the csv data to
     * @throws IOException
     *             if there is an issue writing the file
     */
    static void write(Collection<Person> people, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        
        // CSV format is [NAME],[FRIEND_1],[FRIEND_2],...,[FRIEND_N]
        
        for(Person person : people) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(person.getName());
            
            if(!person.getFriends().isEmpty())
                builder.append(",");
            
            boolean first = true;
            for(String friend : person.getFriends()) {
                if(!first) {
                    builder.append(",");
                }
                else{
                	first = false;
                }
                builder.append(friend);
            }
            
            writer.println(builder.toString());
        }
        
        writer.close();
    }

    /**
     * Convenient method for creating our {@link Person} dataset where we define the relationship between two people
     * 
     * @param person1
     *            the name of a person
     * @param person2
     *            the name of another person
     */
    private static void areFriends(String person1, String person2) {

        if (!people.containsKey(person1)) {
            people.put(person1, Person.newBuilder().setName(person1).setFriends(new ArrayList<String>()).build());
        }
        
        if (!people.containsKey(person2)) {
            people.put(person2, Person.newBuilder().setName(person2).setFriends(new ArrayList<String>()).build());
        }

        people.get(person1).getFriends().add(person2);
        people.get(person2).getFriends().add(person1);

    }

}