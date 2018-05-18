package io.joaopinheiro;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main class. Gets the target file as a parameter goes from there
 *
 * @authot Joao Pedro Pinheiro
 */
public class FileSorter {

    public static void main(String[] args){
        String filePath = args[0];

        if(filePath == null){
            throw new IllegalArgumentException("Please provide the target file as an argument.");
        } else try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

        } catch (FileNotFoundException e) {
            System.out.println("The file was not found.");
            System.exit(1);
        } catch (IOException e){

        }


    }
}
