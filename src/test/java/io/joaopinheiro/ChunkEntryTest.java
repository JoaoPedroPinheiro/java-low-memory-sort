package io.joaopinheiro;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ChunkEntryTest {

    @Test
    public void chunkEntryTest(){
        List<Integer> values = new ArrayList<>();
        StringBuilder string = new StringBuilder();
        for(int i =1; i < 101; i++){
            values.add(i);
            string.append(i);
            string.append(System.lineSeparator());
        }

        ChunkEntry entry = new ChunkEntry(new StringReader(string.toString()));

        for(Integer val: values){
            assertEquals(val.intValue(), entry.getNextInt());
            entry.hasNext();
        }

        assertFalse(entry.hasNext());

    }

}