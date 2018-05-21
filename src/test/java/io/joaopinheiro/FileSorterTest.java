package io.joaopinheiro;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileSorterTest {

    @Test
    public void createLargeFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("big_file"))){

            for (int i=0; i < 15000000; i++) {
                writer.write(Integer.toString(i));
                writer.newLine();
            }
        } catch (IOException e){
            System.out.println("There was an error writing the file : " + e.getLocalizedMessage());
        }
    }
}
