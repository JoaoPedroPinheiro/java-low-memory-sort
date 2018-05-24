package io.joaopinheiro;

import java.io.*;

/**
 * Main class. Gets the target file as a parameter and sorts it
 *
 * @author Joao Pedro Pinheiro
 */
public class FileSorter {

    public static void main(String[] args){
        String sourceFilePath = args[0];
        String destinationFilePath = args[1];

        if(sourceFilePath == null) {
            throw new IllegalArgumentException("Please provide the target file as an argument.");
        }
        if(destinationFilePath == null) {
            throw new IllegalArgumentException("Please provide the destination file as an argument.");
        }

        try{
            Reader source = new FileReader(sourceFilePath);
            Writer destination = new FileWriter(destinationFilePath);

            new IntegerInputSorter(source, destination).sort();

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
