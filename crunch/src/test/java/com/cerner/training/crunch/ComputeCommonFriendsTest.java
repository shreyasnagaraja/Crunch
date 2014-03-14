package com.cerner.training.crunch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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
import com.cerner.training.PersonPair;

@RunWith(MockitoJUnitRunner.class)
public class ComputeCommonFriendsTest {
    
    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";
    
    @Mock
    private Emitter<PersonPair> emitter;
    
    @Captor
    private ArgumentCaptor<PersonPair> argumentCaptor;
    
    private ComputeCommonFriends doFn = new ComputeCommonFriends();
    
    @Test
    public void processSingleValue() {
        doFn.process(getValue(getPerson(PERSON_A, PERSON_B, PERSON_C)), emitter);
        verify(emitter, times(0)).emit(argumentCaptor.capture());
    }
    
    @Test
    public void processTwoValues() {
        doFn.process(getValue(getPerson(PERSON_A, PERSON_B, PERSON_C), getPerson(PERSON_B, PERSON_A, PERSON_C)), emitter);
        verify(emitter).emit(argumentCaptor.capture());
        
        PersonPair pair = argumentCaptor.getValue();
        assertThat(pair.getPersonName1(), is(PERSON_A));
        assertThat(pair.getPersonName2(), is(PERSON_B));
        assertThat(pair.getCommonFriends().size(), is(1));
        assertTrue(pair.getCommonFriends().contains(PERSON_C));
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
