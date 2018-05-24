package io.joaopinheiro;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class with method to sort an input containing {@code int} separated by newlines
 * and write it to the given output
 *
 * Uses temporary files written to disk to avoid running out of memory
 *
 * @author Joao Pedro Pinheiro
 */

public class IntegerInputSorter {

    public static final int MAX_CHUNK_LENGTH;
    public static final int MAX_NUMBER_OF_CHUNKS;
    public static final String TMP_DIRECTORY = "tmp\\";
    public static final String CHUNK_GENERAL_NAME = "sorted_chunk_";
    public static final String INTERMEDIATE_CHUNK_NAME = "intermediate_chunk";

    private Reader source;
    private Writer destination;


    private List<ChunkEntry> chunkList;

    /*
     * (Rudimentary) Heuristic to guess acceptable Chunk Size and Maximum number of Chunks
     */
    static{
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory();
        MAX_CHUNK_LENGTH = (int) memory/(4*10);
        MAX_NUMBER_OF_CHUNKS = (int) memory/ChunkEntry.CHUNK_ENTRY_SIZE*10;
    }

    public IntegerInputSorter(Reader source, Writer destination){
        this.source = source;
        this.destination = destination;
        chunkList = new ArrayList<>();
    }

    /**
     * This method reads from source, sorts, and then writes to destination
     */
    public void sort() throws IOException{

        Path dirPathObj = Paths.get(TMP_DIRECTORY);
        Files.createDirectories(dirPathObj);

        boolean complete = false;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(source)) {

        while(!complete){
           complete = createSortedChunks(reader);
           if(complete) {
               unifyChunks(destination);
           } else {
               /*
                If the file has not been completely consumed, unify the existing
                chunks into a larger chunk, keep a reference, and continue
                */

               unifyChunks(new FileWriter(TMP_DIRECTORY + INTERMEDIATE_CHUNK_NAME + count));

               ChunkEntry intermediateUnified = new ChunkEntry(new FileReader(TMP_DIRECTORY + INTERMEDIATE_CHUNK_NAME + count));
               chunkList.add(intermediateUnified);
               count++;
           }
        }
        } catch (IOException | NumberFormatException e){
            System.out.println("There was an error reading the file: " + e.getLocalizedMessage());
        }
        cleanup();
    }

    /**
     * This methods receives a {@link Reader}, consumes chunks of {@code int} from it,
     * writes each chunk to a separate file, and stores the info about each chunk.
     *
     * @param source The {@link Reader} pointing to the source we want to read and sort
     * @return {@code true} if the input has been completely consumed, false otherwise
     */
   private boolean createSortedChunks(BufferedReader source){

        int chunkNumber = 0;
        boolean consumed = false;
        List<Integer> chunk;
        String chunkPath;

        try {
            while (!consumed) {
                //Read file and sort chunk
                chunk = new ArrayList<>();
                StringBuilder string = new StringBuilder();

                for (int i = 0; i < MAX_CHUNK_LENGTH; i++) {
                    string.append(source.readLine());
                    if (string.toString().equals("null")) {
                        consumed = true;
                        break;
                    } else {
                        chunk.add(Integer.parseInt(string.toString()));
                    }
                    string.setLength(0);
                }

                if (chunk.isEmpty()) {
                    break;
                }
                Collections.sort(chunk);

                //Create new Chunk File and write sorted values to a tmp_file
                chunkNumber++;
                chunkPath = TMP_DIRECTORY + CHUNK_GENERAL_NAME + chunkNumber;
                chunkList.add(storeChunk(chunk, chunkPath));

                if (chunkList.size() == MAX_NUMBER_OF_CHUNKS) {
                    return false;
                }
            }
        } catch (IOException e){
            System.out.println("There was an error reading the file: " + e.getLocalizedMessage());
        }
        return true;


    }

   private void unifyChunks(Writer destination){

        try(BufferedWriter writer = new BufferedWriter(destination)){

            while(!chunkList.isEmpty()) {
                //Sort the list
                Collections.sort(chunkList);

                //take first entry
                ChunkEntry entry = chunkList.get(0);

                //write to new file
//                writer.append(Integer.toString(entry.getNextInt()));
                writer.write(Integer.toString(entry.getNextInt()));
                writer.newLine();

                if(!entry.readNextIfAvailable()){
                    //returning false means there is no next value
                    entry.close();
                    chunkList.remove(entry);
                }
            }

        } catch (IOException e){
            System.out.println("There was an error reading the file.");
        }
    }

    private ChunkEntry storeChunk(List<Integer> values, String path){
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
