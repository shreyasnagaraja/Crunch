package com.cerner.training.crunch;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

import com.cerner.training.Person;

/**
 * A {@link DoFn} that converts a collection of {@link Person} objects into a set of String/{@link Person} key values for each
 * friend of the {@link Person}. A key/value is generated for each {@link Person} and each friend of that {@link Person}. The key is
 * then created from the name of the {@link Person} and a friend of that {@link Person}.
 */
public class PeopleToFriends extends DoFn<Person, Pair<Pair<String, String>, Person>> {

    private static final long serialVersionUID = 1333386114323342990L;

    @Override
    public void process(Person person, Emitter<Pair<Pair<String, String>, Person>> emitter) {
        // For each friend of this person we write a key/value, where the key is the name of the person and the friend while
        // the value is the Person object
        for (String friend : person.getFriends()) {
            Pair<String, String> key = getKey(person, friend);
            emitter.emit(Pair.of(key, person));
        }
    }

    /**
     * Generates a key from the given person names
     * 
     * @param person
     *            the person we are processing
     * @param friend
     *            the name of the person's friend
     * @return a key from the given person and friend name
     */
    private static Pair<String, String> getKey(Person person, String friend) {
        // Special care has to be taken into account when building this key so that the key (A, B) = (B, A). We do this by comparing
        // the names and writing their names in sorted order (i.e. (A, B) = AB, (B, A) = AB)
        if (person.getName().compareTo(friend) <= 0)
            return Pair.of(person.getName(), friend);

        return Pair.of(friend, person.getName());
    }

}
