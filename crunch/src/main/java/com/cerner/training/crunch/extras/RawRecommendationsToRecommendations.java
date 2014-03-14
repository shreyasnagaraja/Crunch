package com.cerner.training.crunch.extras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;

import com.cerner.training.Person;

/**
 * Takes the raw recommendations String (person name) -> {@link Person}s and merges them to produce a single value for each key.
 */
public class RawRecommendationsToRecommendations extends DoFn<Pair<String, Iterable<Person>>, Person> {

    private static final long serialVersionUID = -8656477333916223173L;

    @Override
    public void process(Pair<String, Iterable<Person>> values, Emitter<Person> emitter) {
        Set<String> recommendedFriends = new HashSet<String>();

        String personName = values.first();
        Iterator<Person> iterator = values.second().iterator();

        while (iterator.hasNext()) {
            recommendedFriends.addAll(iterator.next().getFriends());
        }

        emitter.emit(Person.newBuilder().setName(personName).setFriends(new ArrayList<String>(recommendedFriends)).build());
    }

}
