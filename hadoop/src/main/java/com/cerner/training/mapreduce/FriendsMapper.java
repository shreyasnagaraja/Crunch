package com.cerner.training.mapreduce;

import java.io.IOException;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.cerner.training.Person;

/**
 * The mapper class for our {@link FriendsJob} used to calculate the common friends between people. Here we are reading in the list
 * of people ({@link Person} avro objects) and writing 0 to many key values. The key we are writing is the name of the
 * {@link Person} and the name of one of the {@link Person}'s friends. The value is the list of friends for that {@link Person}.
 */
public class FriendsMapper extends Mapper<AvroKey<Person>, NullWritable, Text, AvroValue<Person>> {

    @Override
    public void map(AvroKey<Person> key, NullWritable value, Context context) throws IOException, InterruptedException {
        Person person = key.datum();

        // For each friend of this person we write a key/value, where the key is the name of the person and the friend while
        // the value is a Person object with the person's name as well as the person's friend list
        for (String friend : key.datum().getFriends()) {
            context.write(generateKey(person.getName(), friend), new AvroValue<Person>(person));
        }
    }

    /**
     * Generates a key from the given person names
     * 
     * @param personName1
     *            the name of the first person
     * @param personName2
     *            the name of the second person
     * @return a key from the given person names
     */
    private static Text generateKey(String personName1, String personName2) {
        // Special care has to be taken into account when building this key so that the key (A, B) = (B, A). We do this by comparing
        // the names and writing their names in sorted order (i.e. (A, B) = AB, (B, A) = AB)
        if (personName1.compareTo(personName2) <= 0)
            return new Text(personName1 + " - " + personName2);

        return new Text(personName2 + " - " + personName1);
    }

}
