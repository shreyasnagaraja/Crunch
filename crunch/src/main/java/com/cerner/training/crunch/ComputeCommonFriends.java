package com.cerner.training.crunch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

import com.cerner.training.Person;
import com.cerner.training.PersonPair;

/**
 * A {@link DoFn} that computes the common friends between the {@link Person} objects and emits a {@link PersonPair} as the result
 */
public class ComputeCommonFriends extends DoFn<Pair<Pair<String, String>, Iterable<Person>>, PersonPair> {

    private static final long serialVersionUID = 4746139934413202837L;

    @Override
    public void process(Pair<Pair<String, String>, Iterable<Person>> values, Emitter<PersonPair> emitter) {
        Iterator<Person> iterator = values.second().iterator();

        // We copy the value the iterator gives us since the iterator re-uses the same object overwriting the value
        Person person1 = Person.newBuilder(iterator.next()).build();

        // In case we see invalid data where person A is lists B as a friend but B does not list A as a friend
        if (!iterator.hasNext())
            return;

        // We copy the value the iterator gives us since the iterator re-uses the same object overwriting the value
        Person person2 = Person.newBuilder(iterator.next()).build();

        // Calculate the friends they have in common using both friends lists
        List<String> commonFriends = getCommonFriends(person1, person2);

        // Emit the result as a PersonPair object with both person names and the common friends list
        PersonPair pair = PersonPair.newBuilder().setPersonName1(person1.getName()).setPersonName2(person2.getName())
                .setCommonFriends(commonFriends).build();

        emitter.emit(pair);
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
    private List<String> getCommonFriends(Person person1, Person person2) {
        List<String> commonFriends = new ArrayList<String>(person1.getFriends());

        // Java's Collection has a convenient method for taking the intersection of two collections with a method called
        // #retainAll(Collection). This will leave only the values that the two collections have in common in commonFriends
        commonFriends.retainAll(person2.getFriends());

        return commonFriends;
    }

}