package io.joaopinheiro;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class IntegerInputSorterTest {

    @Test
    public void sortTest() throws Exception {

        //Set CHUNK_LENGTH to a smaller value
        Field chunk_size = IntegerInputSorter.class.getField("CHUNK_LENGTH");
        chunk_size.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(chunk_size, chunk_size.getModifiers() & ~Modifier.FINAL);

        chunk_size.set(null, 50);

        List<Integer> values = new ArrayList<>();
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < 5000; i++) {
            int current = ThreadLocalRandom.current().nextInt(-1000, 1000);
            values.add(current);
            string.append(current);
            string.append(System.lineSeparator());
        }

        StringWriter writer = new StringWriter();
        IntegerInputSorter.sort(new StringReader(string.toString()), writer);
        BufferedReader resultReader = new BufferedReader(new StringReader(writer.toString()));

        Collections.sort(values);
        for (int val : values) {
            assertEquals(val, Integer.parseInt(resultReader.readLine()));
        }
    }
}