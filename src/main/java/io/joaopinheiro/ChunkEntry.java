package io.joaopinheiro;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Auxiliary class to handle chunks of integers stored in disk. To use, first call {@link ChunkEntry#initialize()}.
 * {@link ChunkEntry#getNextInt()} will return null until {@link ChunkEntry#initialize()} is called.
 *
 * Keep calling {@link ChunkEntry#hasNext()} and {@link ChunkEntry#getNextInt()} to get all the values in the chunk.
 *
 * After {@link ChunkEntry#hasNext()} returns {@code false}, call {@link #closeAndDelete()} to close the underlying
 * Resource and delete the representation on disk.
 *
 * @author Joao Pedro Pinheiro
 */
public class ChunkEntry implements Comparable<ChunkEntry> {

    private int nextInt;
    private final String path;
    private BufferedReader bufferedReader;


    public ChunkEntry(String path, int[] values){
        this.path = path;
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){

            for (int val : values) {
                writer.write(Integer.toString(val));
                writer.newLine();
            }
        } catch (IOException e){
            System.out.println("There was an error reading the file : " + e.getLocalizedMessage());
        }
    }

    /**
     * Initializes the underlying Resource and makes this ChunkEntry ready to be used.
     */
    public void initialize(){
        if(bufferedReader != null){
            System.out.println("ChunkEntry is already initialized.");
            return;
        }

        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            this.hasNext();
        } catch (FileNotFoundException e){
            System.out.println("Internal Error : ");
            System.out.println(e.getLocalizedMessage());
        }
    }


    public int getNextInt(){
        return nextInt;
    }

    /**
     * Tries to read the next Integer in the underlying file.
     *
     * @return {@code true} if there is a next value, or {@code false} if EOF is reached.
     */
    public boolean hasNext(){
        try {
            nextInt = Integer.parseInt(bufferedReader.readLine());
        } catch (IOException e) {
            System.out.println("There was an error reading the file. Exiting.");
        } catch (NumberFormatException e){
            //Will be thrown when the readLine returns null, and signals the end of the file.
            return false;
        }
        return true;
    }

    /**
     * Closed the underlying resource and deletes the file, since it is no longer needed;
     */
    public void closeAndDelete(){
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
