package io.joaopinheiro;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.io.*;

/**
 * Auxiliary class to handle chunks of integers stored in disk.
 * <p></p>
 *
 * Constructor takes a {@link Reader} that points to the source of the chunk
 *
 * <p></p>
 *
 * Call {@link ChunkEntry#readNextIfAvailable()} and {@link ChunkEntry#getNextInt()} to get all the values in the chunk.
 *
 * After {@link ChunkEntry#readNextIfAvailable()} returns {@code false}, call {@link #close()} to close the Resource
 *
 * @author Joao Pedro Pinheiro
 */
public class ChunkEntry implements Comparable<ChunkEntry> {

    /** Memory use dby each ChunkEntry. Calculated using {@link ObjectSizeCalculator}*/
    public static final int CHUNK_ENTRY_SIZE = 25360;

    private int nextInt;
    private BufferedReader bufferedReader;



    /**
     * Constructs a new ChunkEntry. Uses the {@link Reader} to build a {@link BufferedReader}
     * and tries to read the first {@code int} of the stream.
     *
     * @param chunkSource {@link Reader} pointing to the location of the chunk
     */
    public ChunkEntry(Reader chunkSource){
        bufferedReader = new BufferedReader(chunkSource);
        this.readNextIfAvailable();
    }

    public int getNextInt(){
        return nextInt;
    }

    /**
     * Tries to read the next {@code int} from the stream.
     *
     * @return {@code true} if there is a next value, or {@code false} if end of the stream is reached.
     */
    public boolean readNextIfAvailable(){
        try {
            nextInt = Integer.parseInt(bufferedReader.readLine());
        } catch (IOException e) {
            System.out.println("There was an error reading the file. Exiting.");
        } catch (NumberFormatException e){
            //Will be thrown when the readLine returns null, and signals the end of the stream.
            return false;
        }
        return true;
    }

    /**
     * Closed the underlying {@link BufferedReader}. This also closes
     * the {@link Reader} so no further action is needed
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
