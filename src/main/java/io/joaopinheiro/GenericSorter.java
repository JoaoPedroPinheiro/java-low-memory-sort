package io.joaopinheiro;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class with method to order an input and write it to the output
 *
 * @author Joao Pedro Pinheiro
 */

public class GenericSorter {

    public static final int CHUNK_SIZE = 10000;
    public static final String TMP_DIRECTORY = "tmp\\";
    public static final String CHUNK_GENERAL_NAME = "sorted_chunk_";

    /**
     * This method reads from source, sorts, and then writes to destination
     *
     * @param source
     * @param destination
     */
    public static void sort(Reader source, Writer destination){

        List<ChunkEntry> chunkList = createSortedChunks(source);
        unifyChunks(destination, chunkList);
        cleanup();
    }

    /**
     * This methods receives a Reader, consumes it in chunks,
     * writes each chunk to a separate file, and stores the info about each chunk.
     *
     * @param source The Reader pointing to the source we want to read and sort
     */
    private static List<ChunkEntry> createSortedChunks(Reader source){
        int[] chunk;
        int chunkNumber = 0;
        boolean eofFound = false;
        List<ChunkEntry> chunkList = new ArrayList<>();
        String chunkPath;

        try (BufferedReader reader = new BufferedReader(source)) {

            while(!eofFound) {
                //Read file and sort chunk
                chunk = new int[CHUNK_SIZE];
                for (int i = 0; i < CHUNK_SIZE; i++) {
                    String val = reader.readLine();
                    if (val == null) {
                        eofFound = true;
                        break;
                    } else {
                        chunk[i] = Integer.parseInt(val);
                    }
                }
                Arrays.sort(chunk);

                //Create new Chunk File and write sorted values to a tmp_file
                chunkNumber++;
                chunkPath = TMP_DIRECTORY + CHUNK_GENERAL_NAME + chunkNumber;
                chunkList.add(storeChunk(chunk, chunkPath));
            }
            return chunkList;

        } catch (IOException | NumberFormatException e){
            System.out.println("There was an error reading the file: " + e.getLocalizedMessage());
            return null;
        }
    }

    private static void unifyChunks(Writer destination,List<ChunkEntry> chunkList){

        try(BufferedWriter writer = new BufferedWriter(destination)){

            while(!chunkList.isEmpty()) {
                //Sort the list
                Collections.sort(chunkList);

                //take first entry
                ChunkEntry entry = chunkList.get(0);

                //write to new file
                writer.write(Integer.toString(entry.getNextInt()));
                writer.newLine();

                if(!entry.hasNext()){
                    //returning false means there is no next value
                    entry.close();
                    chunkList.remove(entry);
                }

            }

        } catch (IOException e){
            System.out.println("There was an error reading the file. Exiting.");
        }
    }

    private static ChunkEntry storeChunk(int[] values, String path){
        //Write the values to disk
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){

            for (int val : values) {
                writer.write(Integer.toString(val));
                writer.newLine();
            }
        } catch (IOException e){
            System.out.println("There was an error writing the file : " + e.getLocalizedMessage());
        }

        //Create the ChunkEntry and return it
        try{
            FileReader reader = new FileReader(path);
            return new ChunkEntry(reader);
        }catch (FileNotFoundException e){
            System.out.println("There was an error reading the file : " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Cleans up the tmp directory
     */
    private static void cleanup(){
        Path toBeDeleted = Paths.get(TMP_DIRECTORY);

        try {
            Files.walk(toBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
