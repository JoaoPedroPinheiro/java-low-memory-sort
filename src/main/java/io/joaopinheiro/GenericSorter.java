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

public class GenericSorter {

    public static final int CHUNK_LENGTH;
    public static final String TMP_DIRECTORY = "tmp\\";
    public static final String CHUNK_GENERAL_NAME = "sorted_chunk_";
    /*
     * (Rudimentary) Heuristic to guess acceptable Chunk Size
     */
    static{
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory();
        CHUNK_LENGTH = (int) memory/(4*10);
    }

    /**
     * This method reads from source, sorts, and then writes to destination
     *
     * @param source
     * @param destination
     */
    public static void sort (Reader source, Writer destination) throws IOException{

        Path dirPathObj = Paths.get(TMP_DIRECTORY);
        Files.createDirectories(dirPathObj);

        List<ChunkEntry> chunkList = createSortedChunks(source);
        unifyChunks(destination, chunkList);
        cleanup();
    }

    /**
     * This methods receives a {@link Reader}, consumes chunks of {@code int} from it,
     * writes each chunk to a separate file, and stores the info about each chunk.
     *
     * @param source The {@link Reader} pointing to the source we want to read and sort
     */
   private static List<ChunkEntry> createSortedChunks(Reader source){

        int chunkNumber = 0;
        boolean consumed = false;
        List<ChunkEntry> chunkList = new ArrayList<>();
        List<Integer> chunk;
        String chunkPath;


        try (BufferedReader reader = new BufferedReader(source)) {

            while(!consumed) {
                //Read file and sort chunk
                chunk = new ArrayList<>();
                StringBuilder string = new StringBuilder();
                for (int i = 0; i < CHUNK_LENGTH; i++) {
                    string.append(reader.readLine());
                    if (string.toString().equals("null")) {
                        consumed = true;
                        break;
                    } else {
                        chunk.add(Integer.parseInt(string.toString()));
                    }
                    string.setLength(0);
                }
                if(chunk.isEmpty()){
                    break;
                }
                Collections.sort(chunk);

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
            System.out.println("There was an error reading the file.");
        }
    }

    private static ChunkEntry storeChunk(List<Integer> values, String path){
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
