package com.cerner.training.mapreduce;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.training.Person;

@RunWith(MockitoJUnitRunner.class)
public class FriendsMapperTest {
    
    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";
    
    private static FriendsMapper mapper = new FriendsMapper();
    
    @Mock
    private Context context;
    
    @Captor
    ArgumentCaptor<Text> keyArgumentCaptor;
    
    @Captor
    private ArgumentCaptor<AvroValue<Person>> valueArgumentCaptor;
    
    @Test
    public void personWithNoFriends() throws IOException, InterruptedException {
        mapper.map(getPerson(PERSON_A), NullWritable.get(), context);
        verify(context, never()).write(anyObject(), anyObject());
    }
    
    @Test
    public void singleMapValue() throws IOException, InterruptedException {
        mapper.map(getPerson(PERSON_A, PERSON_B, PERSON_C), NullWritable.get(), context);
        
        verify(context, times(2)).write(keyArgumentCaptor.capture(), valueArgumentCaptor.capture());
        
        List<AvroValue<Person>> values = valueArgumentCaptor.getAllValues();
        assertThat(values.size(), is(2));
        
        Person person1 = values.get(0).datum();
        
        assertThat(person1.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person1.getName(), is(PERSON_A));
        
        Person person2 = values.get(1).datum();
        
        assertThat(person2.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person2.getName(), is(PERSON_A));
        
        List<Text> keys = keyArgumentCaptor.getAllValues();
        assertThat(keys.size(), is(2));
        
        assertTrue(keys.get(0).toString().contains(PERSON_A));
        assertTrue(keys.get(0).toString().contains(PERSON_B));
        
        assertTrue(keys.get(1).toString().contains(PERSON_A));
        assertTrue(keys.get(1).toString().contains(PERSON_C));
    }
    
    @Test
    public void manyMapValues() throws IOException, InterruptedException {
        mapper.map(getPerson(PERSON_A, PERSON_B, PERSON_C), NullWritable.get(), context);
        mapper.map(getPerson(PERSON_B, PERSON_A, PERSON_C), NullWritable.get(), context);
        mapper.map(getPerson(PERSON_C, PERSON_A, PERSON_B), NullWritable.get(), context);
        
        verify(context, times(6)).write(keyArgumentCaptor.capture(), valueArgumentCaptor.capture());
        
        List<AvroValue<Person>> values = valueArgumentCaptor.getAllValues();
        assertThat(values.size(), is(6));
        
        Person person1 = values.get(0).datum();
        
        assertThat(person1.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person1.getName(), is(PERSON_A));
        
        Person person2 = values.get(1).datum();
        
        assertThat(person2.getFriends(), is(Arrays.asList(new String[] { PERSON_B, PERSON_C })));
        assertThat(person2.getName(), is(PERSON_A));
        
        Person person3 = values.get(2).datum();
        
        assertThat(person3.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_C })));
        assertThat(person3.getName(), is(PERSON_B));
        
        Person person4 = values.get(3).datum();
        
        assertThat(person4.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_C })));
        assertThat(person4.getName(), is(PERSON_B));
        
        Person person5 = values.get(4).datum();
        
        assertThat(person5.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_B })));
        assertThat(person5.getName(), is(PERSON_C));
        
        Person person6 = values.get(5).datum();
        
        assertThat(person6.getFriends(), is(Arrays.asList(new String[] { PERSON_A, PERSON_B })));
        assertThat(person6.getName(), is(PERSON_C));
        
        List<Text> keys = keyArgumentCaptor.getAllValues();
        assertThat(keys.size(), is(6));
        
        // Key 0 : AB
        // Key 1 : AC
        // Key 2 : BA
        // Key 3 : BC
        // Key 4 : CA
        // Key 5 : CB
        
        // Here we verify that keys made of the same two people (names) produce the same key (i.e. AB = BA)
        assertThat(keys.get(0), is(keys.get(2)));
        assertThat(keys.get(1), is(keys.get(4)));
        assertThat(keys.get(3), is(keys.get(5)));
    }
    
    private AvroKey<Person> getPerson(String name, String ... friends) {
        List<String> friendsList = new ArrayList<String>();
        if(friends != null)
            friendsList = Arrays.asList(friends);
        return new AvroKey<Person>(Person.newBuilder().setName(name).setFriends(friendsList).build());
    }

}
