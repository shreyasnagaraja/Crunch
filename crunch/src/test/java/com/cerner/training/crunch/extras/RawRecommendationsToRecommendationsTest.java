package com.cerner.training.crunch.extras;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.crunch.Emitter;
import org.apache.crunch.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.training.Person;

@RunWith(MockitoJUnitRunner.class)
public class RawRecommendationsToRecommendationsTest {

    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";
    private static final String PERSON_D = "D";

    @Mock
    private Emitter<Person> emitter;

    @Captor
    private ArgumentCaptor<Person> argumentCaptor;

    private RawRecommendationsToRecommendations doFn = new RawRecommendationsToRecommendations();

    @Test
    public void singleValue() {
        doFn.process(getValue(getPerson(PERSON_A, PERSON_B, PERSON_C)), emitter);

        verify(emitter).emit(argumentCaptor.capture());

        Person person = argumentCaptor.getValue();

        assertThat(person.getName(), is(PERSON_A));
        assertThat(person.getFriends().size(), is(2));
        assertTrue(person.getFriends().contains(PERSON_B));
        assertTrue(person.getFriends().contains(PERSON_C));
    }

    @Test
    public void manyValues() {
        doFn.process(
                getValue(getPerson(PERSON_A, PERSON_B, PERSON_C), getPerson(PERSON_A, PERSON_B),
                        getPerson(PERSON_A, PERSON_D, PERSON_C)), emitter);

        verify(emitter).emit(argumentCaptor.capture());

        Person person = argumentCaptor.getValue();

        assertThat(person.getName(), is(PERSON_A));
        assertThat(person.getFriends().size(), is(3));
        assertTrue(person.getFriends().contains(PERSON_B));
        assertTrue(person.getFriends().contains(PERSON_C));
        assertTrue(person.getFriends().contains(PERSON_D));
    }

    private static Pair<String, Iterable<Person>> getValue(Person... people) {
        return Pair.of(people[0].getName(), getIterable(people));
    }

    private static Iterable<Person> getIterable(Person... people) {
        return Arrays.asList(people);
    }

    private static Person getPerson(String person, String... friends) {
        List<String> friendsList = new ArrayList<String>();
        if (friends != null)
            friendsList = Arrays.asList(friends);
        return Person.newBuilder().setName(person).setFriends(friendsList).build();
    }

}
