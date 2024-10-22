package it.polito.ai.labs.lab3.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.labs.lab3.files.json.Line;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class LinesDeserializer {
    public Line readLinesFromJsonFile(Path path) throws IOException {
        String json = null;

        try {
            byte[] jsonBytes = Files.readAllBytes(path);
            json = new String(jsonBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return new ObjectMapper().readValue(json, Line.class);
    }


}
