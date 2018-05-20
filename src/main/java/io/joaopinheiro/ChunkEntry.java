package io.joaopinheiro;

import java.io.*;

/**
 * Auxiliary class to handle chunks of integers stored in disk.
 *
 * Keep calling {@link ChunkEntry#hasNext()} and {@link ChunkEntry#getNextInt()} to get all the values in the chunk.
 *
 * After {@link ChunkEntry#hasNext()} returns {@code false}, call {@link #close()} to close the underlying
 * Resource
 *
 * @author Joao Pedro Pinheiro
 */
public class ChunkEntry implements Comparable<ChunkEntry> {

    private int nextInt;
    private BufferedReader bufferedReader;

    public ChunkEntry(Reader chunkSource){
        bufferedReader = new BufferedReader(chunkSource);
        this.hasNext();
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
    public void close(){
        try {
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("There was an error closing the file: " +e.getLocalizedMessage());
        }
    }

    @Override
    public int compareTo(ChunkEntry e){
        return Integer.compare(nextInt, e.nextInt);
    }
}
