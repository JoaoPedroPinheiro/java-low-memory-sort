package io.joaopinheiro;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Main class. Gets the target file as a parameter goes from there
 *
 * @author Joao Pedro Pinheiro
 */
public class FileSorter {

    public static final int CHUNK_SIZE = 10000;
    private static List<ChunkEntry> chunkList = new ArrayList<>();

    public static void main(String[] args){
        String filePath = args[0];
        if(filePath == null) {
            throw new IllegalArgumentException("Please provide the target file as an argument.");
        }

        createSortedChunks(filePath);
        unifyChunks("sorted_" + filePath);

        System.out.println("Your file is sorted");
    }

    /**
     * This methods receives a File Path as a String, reads the file in chunks,
     * writes each chunk to a separate file, and store the info about each chunk.
     *
     * @param path The path to the file we want to read
     */
    public static void createSortedChunks(String path){
        int[] chunk;
        int chunkNumber = 0;
        boolean eofFound = false;

        String chunkPath;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

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
                chunkPath = "tmp\\chunk_"+chunkNumber;
                chunkList.add(new ChunkEntry(chunkPath, chunk));
            }

        } catch (FileNotFoundException e) {
            System.out.println("The file was not found.");
            System.exit(1);
        } catch (IOException e){
            System.out.println("There was an error reading the file. Exiting.");
        } catch (NumberFormatException e){
            System.out.println("There was an error reading the file: " + e.getLocalizedMessage());
        }
    }

    public static void unifyChunks(String destination){
        //Initialize all chunks
        for(ChunkEntry entry: chunkList)
            entry.initialize();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(destination))){

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
                    chunkList.remove(entry);
                }

            }

        } catch (IOException e){
            System.out.println("There was an error reading the file. Exiting.");
        }


    }
}
