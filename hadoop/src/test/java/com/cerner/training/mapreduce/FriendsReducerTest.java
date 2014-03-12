package com.cerner.training.mapreduce;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.training.Person;
import com.cerner.training.PersonPair;

@RunWith(MockitoJUnitRunner.class)
public class FriendsReducerTest {

    private static final String PERSON_A = "A";
    private static final String PERSON_B = "B";
    private static final String PERSON_C = "C";

    private static FriendsReducer reducer = new FriendsReducer();

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<AvroKey<PersonPair>> argumentCaptor;

    @Test(expected = NoSuchElementException.class)
    public void reduceOnlyOneValue() throws IOException, InterruptedException {
        // This is our failure/bug case that they have to fix
        // The reducer doesn't actually look at or care about the key, we only use that to do group by key
        reducer.reduce(new Text(), getIterable(getPerson(PERSON_A, PERSON_B, PERSON_C)), context);
    }

    @Test
    public void reduceTwoValues() throws IOException, InterruptedException {
        // The reducer doesn't actually look at or care about the key, we only use that to do group by key
        reducer.reduce(new Text(),
                getIterable(getPerson(PERSON_A, PERSON_B, PERSON_C), getPerson(PERSON_B, PERSON_A, PERSON_C)),
                context);
        verify(context).write(argumentCaptor.capture(), anyObject());
        PersonPair pair = argumentCaptor.getValue().datum();
        assertThat(pair.getPersonName1(), is(PERSON_A));
        assertThat(pair.getPersonName2(), is(PERSON_B));
        assertThat(pair.getCommonFriends().size(), is(1));
        assertThat(pair.getCommonFriends().get(0), is(PERSON_C));
    }

    private Iterable<AvroValue<Person>> getIterable(AvroValue<Person>... people) {
        return Arrays.asList(people);
    }

    private AvroValue<Person> getPerson(String person, String... friends) {
        List<String> friendsList = new ArrayList<String>();
        if (friends != null)
            friendsList = Arrays.asList(friends);
        return new AvroValue<Person>(Person.newBuilder().setName(person).setFriends(friendsList).build());
    }

}
