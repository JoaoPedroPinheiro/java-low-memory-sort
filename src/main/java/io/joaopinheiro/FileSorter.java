package io.joaopinheiro;

import java.io.*;

/**
 * Main class. Gets the target file as a parameter and sorts it
 *
 * @author Joao Pedro Pinheiro
 */
public class FileSorter {


    public static void main(String[] args){
        String filePath = args[0];
        if(filePath == null) {
            throw new IllegalArgumentException("Please provide the target file as an argument.");
        }

        try{
            Reader source = new FileReader(filePath);
            Writer destination = new FileWriter("sorted_"+filePath);

            GenericSorter.sort(source, destination);

        } catch (FileNotFoundException e){
            System.out.println("The File was not found: " + e.getLocalizedMessage());
            System.exit(1);
        } catch (IOException e){
            System.out.println("There was an error: " + e.getLocalizedMessage());
            System.exit(1);
        }
        System.out.println("Your file is sorted");
    }




}
