package it.polito.ai.labs.lab2.lab2;

import it.polito.ai.labs.lab2.lab2.files.LinesDeserializer;
import it.polito.ai.labs.lab2.lab2.models.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootApplication
public class Lab2Application {
    @Autowired
    LinesDeserializer deserializer;

    public static void main(String[] args) {


        SpringApplication.run(Lab2Application.class, args);
    }


    private void InitializeFromFiles() throws IOException {
        File[] files = File.listRoots();

        ArrayList<Line> lines = new ArrayList<>();

        for(File f: files) {
            Line line=deserializer.readLinesFromJsonFile(f.toPath());
            lines.add(line);
        }

        //Use lines to populate DataBase

    }

}
