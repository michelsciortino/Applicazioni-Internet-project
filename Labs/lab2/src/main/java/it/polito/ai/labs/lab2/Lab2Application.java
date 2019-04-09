package it.polito.ai.labs.lab2;

import it.polito.ai.labs.lab2.files.LinesDeserializer;
import it.polito.ai.labs.lab2.files.Utils;
import it.polito.ai.labs.lab2.models.json.Line;
import it.polito.ai.labs.lab2.models.json.PediStop;
import it.polito.ai.labs.lab2.repositories.LineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootApplication
public class Lab2Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Lab2Application.class);

    @Autowired
    LinesDeserializer deserializer;

    @Autowired
    private LineRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        InitializeFromFiles();
    }

    private void InitializeFromFiles() throws IOException {
        //File[] files = File.listRoots();
        ArrayList<File> files= Utils.readFile( new ClassPathResource("JsonFiles").getFile());
        System.out.println(files);

        ArrayList<Line> lines = new ArrayList<>();

        for(File f: files) {
            Line line=deserializer.readLinesFromJsonFile(f.toPath());
            lines.add(line);
            logger.info("Load Line"+ line.name+ " from json: "+f);
            InsertLines(line);
        }
        //Use lines to populate DataBase

    }

    public void TestInsert(){
        ArrayList<it.polito.ai.labs.lab2.models.mongo.PediStop> outboundStops= new ArrayList<>();
        outboundStops.add(new it.polito.ai.labs.lab2.models.mongo.PediStop(34,25,"prima"));

        ArrayList<it.polito.ai.labs.lab2.models.mongo.PediStop> returnStops= new ArrayList<>();
        returnStops.add(new it.polito.ai.labs.lab2.models.mongo.PediStop(34,25,"seconda"));

        it.polito.ai.labs.lab2.models.mongo.Line line=new it.polito.ai.labs.lab2.models.mongo.Line("prova",outboundStops,returnStops);
        repository.save(line);
    }

    public void InsertLines(Line line){
        ArrayList<it.polito.ai.labs.lab2.models.mongo.PediStop> outboundStops= new ArrayList<>();
        for (PediStop p:line.outboundStops)
            outboundStops.add(new it.polito.ai.labs.lab2.models.mongo.PediStop(p.longitude,p.latitude,p.name));

        ArrayList<it.polito.ai.labs.lab2.models.mongo.PediStop> returnStops= new ArrayList<>();
        for (PediStop p:line.returnStops)
            returnStops.add(new it.polito.ai.labs.lab2.models.mongo.PediStop(p.longitude,p.latitude,p.name));

        it.polito.ai.labs.lab2.models.mongo.Line lineMongo=new it.polito.ai.labs.lab2.models.mongo.Line(line.name,outboundStops,returnStops);
        repository.save(lineMongo);
    }

}
