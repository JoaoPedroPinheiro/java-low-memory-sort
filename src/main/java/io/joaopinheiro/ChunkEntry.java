package io.joaopinheiro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChunkEntry implements Comparable<ChunkEntry> {

    private int nextInt;
    private String path;
    private BufferedReader bufferedReader;


    public ChunkEntry(String path){
        this.path = path;
    }

    public void initialize(){
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            this.readNext();
        } catch (FileNotFoundException e){
            System.out.println("Internal Error : ");
            System.out.println(e.getLocalizedMessage());
        } catch (NumberFormatException e){
            System.out.println("There was an error reading the file:");
            System.out.println(e.getLocalizedMessage());
        }
    }


    public int getNextInt(){
        return nextInt;
    }

    public void readNext(){
        try {
            nextInt = Integer.parseInt(bufferedReader.readLine());
        } catch (IOException e) {
            System.out.println("There was an error reading the file. Exiting.");
        }
    }

    public void close(){
        try {
            bufferedReader.close();
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            System.out.println("There was an error closing the file.");
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Override
    public int compareTo(ChunkEntry e){
        return Integer.compare(nextInt, e.nextInt);
    }
}
