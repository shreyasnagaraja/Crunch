package com.cerner.training.crunch.extras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

import com.cerner.training.Person;

/**
 * Transforms a table of String (person and friend name) -> {@link Person people} key/values to String (person name) ->
 * {@link Person} with recommended friends
 */
public class FriendsToRawRecommendations extends DoFn<Pair<Pair<String, String>, Iterable<Person>>, Pair<String, Person>> {

    private static final long serialVersionUID = -1736879605507191490L;

    @Override
    public void process(Pair<Pair<String, String>, Iterable<Person>> values, Emitter<Pair<String, Person>> emitter) {
        Iterator<Person> iterator = values.second().iterator();

        // We copy the value the iterator gives us since the iterator re-uses the same object overwriting the value
        Person person1 = Person.newBuilder(iterator.next()).build();

        // In case we see invalid data where person A is listed B as a friend but B does not list A as a friend
        if (!iterator.hasNext())
            return;

        // We copy the value the iterator gives us since the iterator re-uses the same object overwriting the value
        Person person2 = Person.newBuilder(iterator.next()).build();

        List<String> person1RecommendedFriends = getOuterFriends(person1, person2);

        // Only emit data if we have friends to recommend
        if (!person1RecommendedFriends.isEmpty()) {
            emitter.emit(Pair.of(person1.getName(),
                    Person.newBuilder().setName(person1.getName()).setFriends(person1RecommendedFriends).build()));
        }

        List<String> person2RecommendedFriends = getOuterFriends(person2, person1);

        // Only emit data if we have friends to recommend
        if (!person2RecommendedFriends.isEmpty()) {
            emitter.emit(Pair.of(person2.getName(),
                    Person.newBuilder().setName(person2.getName()).setFriends(person2RecommendedFriends).build()));
        }
    }

    private List<String> getOuterFriends(Person a, Person b) {
        List<String> recommendedFriends = new ArrayList<String>(b.getFriends());

        // Removes A from B's list of friends so we don't recommend people to be friends with themselves
        recommendedFriends.remove(a.getName());
        
        // Removes any of A's friends that were also B's friends
        recommendedFriends.removeAll(a.getFriends());

        return recommendedFriends;
    }

}
