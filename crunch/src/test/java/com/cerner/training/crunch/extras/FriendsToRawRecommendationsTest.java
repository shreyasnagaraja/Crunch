package com.cerner.training.crunch.extras;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

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
public class FriendsToRawRecommendationsTest {
    
    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";
    private static final String PERSON_D = "D";
    
    @Mock
    private Emitter<Pair<String, Person>> emitter;
    
    @Captor
    private ArgumentCaptor<Pair<String, Person>> argumentCaptor;
    
    private FriendsToRawRecommendations doFn = new FriendsToRawRecommendations();
    
    @Test
    public void processSingleValue() {
        doFn.process(getValue(getPerson(PERSON_A, PERSON_B, PERSON_C)), emitter);
        verify(emitter, never()).emit(argumentCaptor.capture());
    }
    
    @Test
    public void processTwoValues() {
        doFn.process(getValue(getPerson(PERSON_A, PERSON_B, PERSON_C), getPerson(PERSON_B, PERSON_A, PERSON_D)), emitter);
        verify(emitter, times(2)).emit(argumentCaptor.capture());
        
        List<Pair<String, Person>> people = argumentCaptor.getAllValues();
        assertThat(people.size(), is(2));
        
        assertThat(people.get(0).first(), is(PERSON_A));
        assertThat(people.get(0).second().getName(), is(PERSON_A));
        assertThat(people.get(0).second().getFriends().size(), is(1));
        assertTrue(people.get(0).second().getFriends().contains(PERSON_D));
        
        assertThat(people.get(1).first(), is(PERSON_B));
        assertThat(people.get(1).second().getName(), is(PERSON_B));
        assertThat(people.get(1).second().getFriends().size(), is(1));
        assertTrue(people.get(1).second().getFriends().contains(PERSON_C));
    }
    
    private static Pair<Pair<String, String>, Iterable<Person>> getValue(Person ... people) {
        // The DoFn doesn't actually care about the key, we only use that to do group by key
        return Pair.of(Pair.of("", ""), getIterable(people));
    }
    
    private static Iterable<Person> getIterable(Person ... people) {
        return Arrays.asList(people);
    }
    
    private static Person getPerson(String person, String ... friends) {
        List<String> friendsList = new ArrayList<String>();
        if (friends != null)
            friendsList = Arrays.asList(friends);
        return Person.newBuilder().setName(person).setFriends(friendsList).build();
    }

}
