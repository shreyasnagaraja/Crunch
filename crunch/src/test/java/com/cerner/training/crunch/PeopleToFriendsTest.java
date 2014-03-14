package com.cerner.training.crunch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
public class PeopleToFriendsTest {
    
    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";
    
    @Mock
    private Emitter<Pair<Pair<String, String>, Person>> emitter;
    
    @Captor
    private ArgumentCaptor<Pair<Pair<String, String>, Person>> argumentCaptor;
    
    private PeopleToFriends doFn = new PeopleToFriends();
    
    @Test
    public void personWithNoFriends() {
        doFn.process(getPerson(PERSON_A), emitter);
        verify(emitter, never()).emit(argumentCaptor.capture());
    }
    
    @Test
    public void singleMapValue() {
        doFn.process(getPerson(PERSON_A, PERSON_B, PERSON_C), emitter);
        verify(emitter, times(2)).emit(argumentCaptor.capture());
        
        List<Pair<Pair<String, String>, Person>> values = argumentCaptor.getAllValues();
        assertThat(values.size(), is(2));
        
        assertThat(values.get(0).first().first(), is(PERSON_A));
        assertThat(values.get(0).first().second(), is(PERSON_B));
        
        Person person1 = values.get(0).second();
        
        assertThat(person1.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person1.getName(), is(PERSON_A));
        
        assertThat(values.get(1).first().first(), is(PERSON_A));
        assertThat(values.get(1).first().second(), is(PERSON_C));
        
        Person person2 = values.get(1).second();
        
        assertThat(person2.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person2.getName(), is(PERSON_A));
    }
    
    @Test
    public void manyMapValues() {
        doFn.process(getPerson(PERSON_A, PERSON_B, PERSON_C), emitter);
        doFn.process(getPerson(PERSON_B, PERSON_A, PERSON_C), emitter);
        doFn.process(getPerson(PERSON_C, PERSON_A, PERSON_B), emitter);
        
        verify(emitter, times(6)).emit(argumentCaptor.capture());
        
        List<Pair<Pair<String, String>, Person>> values = argumentCaptor.getAllValues();
        assertThat(values.size(), is(6));
        
        Person person1 = values.get(0).second();
        
        assertThat(person1.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person1.getName(), is(PERSON_A));
        
        Person person2 = values.get(1).second();
        
        assertThat(person2.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person2.getName(), is(PERSON_A));
        
        Person person3 = values.get(2).second();
        
        assertThat(person3.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_C })));
        assertThat(person3.getName(), is(PERSON_B));
        
        Person person4 = values.get(3).second();
        
        assertThat(person4.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_C })));
        assertThat(person4.getName(), is(PERSON_B));
        
        Person person5 = values.get(4).second();
        
        assertThat(person5.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_B })));
        assertThat(person5.getName(), is(PERSON_C));
        
        Person person6 = values.get(5).second();
        
        assertThat(person6.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_B })));
        assertThat(person6.getName(), is(PERSON_C));
        
        // Key 0 : AB
        // Key 1 : AC
        // Key 2 : BA
        // Key 3 : BC
        // Key 4 : CA
        // Key 5 : CB
        
        // Here we verify that keys made of the same two people (names) produce the same key (i.e. AB = BA)
        assertThat(values.get(0).first(), is(values.get(2).first()));
        assertThat(values.get(1).first(), is(values.get(4).first()));
        assertThat(values.get(3).first(), is(values.get(5).first()));
    }
    
    private Person getPerson(String name, String ... friends) {
        List<String> friendsList = new ArrayList<String>();
        if(friends != null)
            friendsList = Arrays.asList(friends);
        return Person.newBuilder().setName(name).setFriends(friendsList).build();
    }

}
