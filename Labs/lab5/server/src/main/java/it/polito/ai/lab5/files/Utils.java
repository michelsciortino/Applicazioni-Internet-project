package it.polito.ai.lab5.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<File> readFile(final File folder) throws IOException {
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                readFile(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
                files.add(fileEntry);
            }
        }
        return files;
    }
}
