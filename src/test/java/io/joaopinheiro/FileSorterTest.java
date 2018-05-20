package io.joaopinheiro;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;


public class FileSorterTest {


    private static final int TEST_SIZE = 100000;
    private static int[] vals = new int[TEST_SIZE];
    private static final String FILE_PATH = "int_list_file.txt";

    @BeforeAll
    public static void init(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            int current;
            for(int i = 0; i < TEST_SIZE; i++){
                current = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE );
                vals[i] = current;
                writer.write(Integer.toString(current));
                writer.newLine();
            }
        }catch (IOException e){
            //Should not happen
            System.out.println("There was an error reading the file. Exiting.");
        }

        Arrays.sort(vals);
    }

    @AfterAll
    public static void finish()throws IOException{
        Files.delete(Paths.get(FILE_PATH));
    }


    @Test
    public void createFile(){
        FileSorter.createSortedChunks(FILE_PATH);
        FileSorter.unifyChunks("sorted_"+FILE_PATH);
    }



}