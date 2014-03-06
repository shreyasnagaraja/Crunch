package com.cerner.training.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.cerner.training.Person;
import com.cerner.training.PersonPair;

/**
 * The reducer class for our {@link FriendsJob} used to calculate the common friends between people. Here we expect to see two
 * values for each key. Each value should be a {@link Person} object of a pair of friends. We calculate the friends they
 * have in common by taking an intersection of their friends lists. Then we write the intersected list out as a {@link PersonPair} object
 * with both person names.
 */
public class FriendsReducer extends Reducer<Text, AvroValue<Person>, AvroKey<PersonPair>, NullWritable> {

    @Override
    public void reduce(Text key, Iterable<AvroValue<Person>> values, Context context) throws IOException, InterruptedException {
        // Read in the two expected values. Each value should have the friends list of one person
        Iterator<AvroValue<Person>> iterator = values.iterator();
        Person person1 = Person.newBuilder(iterator.next().datum()).build();
        Person person2 = Person.newBuilder(iterator.next().datum()).build();

        // Calculate the friends they have in common using both friends lists
        List<String> commonFriends = getCommonFriends(person1, person2);

        // Write out the result as a PersonPair object with both person names and the common friends list
        PersonPair finalPair = PersonPair.newBuilder().setPersonName1(person1.getName()).setPersonName2(person2.getName())
                .setCommonFriends(commonFriends).build();

        context.write(new AvroKey<PersonPair>(finalPair), NullWritable.get());
    }

    /**
     * Retrieves the list of friends the two {@link Person} objects have in common
     * 
     * @param person1
     *            the {@link Person} object for a person
     * @param person2
     *            the {@link Person} object for another person
     * @return the list of friends the two {@link Person} objects have in common
     */
    private static List<String> getCommonFriends(Person person1, Person person2) {
        List<String> commonFriends = new ArrayList<String>(person1.getFriends());

        // Java's Collection has a convenient method for taking the intersection of two collections with a method called
        // #retainAll(Collection). This will leave only the values that the two collections have in common in commonFriends
        commonFriends.retainAll(person2.getFriends());

        return commonFriends;
    }

}
