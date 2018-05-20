package io.joaopinheiro;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.joaopinheiro.GenericSorter.CHUNK_GENERAL_NAME;
import static org.junit.jupiter.api.Assertions.*;

class GenericSorterTest {

    @BeforeAll
    public static void init() throws IOException{
        Path filePath = Paths.get(GenericSorter.TMP_DIRECTORY);
        Files.createDirectories(filePath);
        assertTrue(Files.exists(filePath));
    }

    @AfterAll
    public static void cleanup(){
        Path filePath = Paths.get(GenericSorter.TMP_DIRECTORY);
        assertTrue(Files.exists(filePath));

        GenericSorter.cleanup();
        assertFalse(Files.exists(filePath));
    }

    @Test
    public void storeChunkTest(){
        List<Integer> values = new ArrayList<>();
        StringBuilder string = new StringBuilder();
        for(int i =1; i < 101; i++){
            values.add(i);
            string.append(i);
            string.append(System.lineSeparator());
        }

        ChunkEntry entry = new ChunkEntry(new StringReader(string.toString()));
        ChunkEntry storedChunk = GenericSorter.storeChunk(values, GenericSorter.TMP_DIRECTORY+CHUNK_GENERAL_NAME+1);

        do{
            assertEquals(entry.getNextInt(), storedChunk.getNextInt());
        } while (entry.hasNext() && storedChunk.hasNext());

        entry.close();
        storedChunk.close();
    }

}