package it.polito.ai.lab5;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties
public class ApplicationConfig {
    public List<String> filenames;
    public String foldername;

    public String getFolderName() {
        return foldername;
    }

    public void setFolderName(String arg) {
        String[] splitted = arg.split("=");
        if (splitted.length < 2)
            return;
        foldername = splitted[1];
        if (!foldername.endsWith("/"))
            foldername += "/";
        if (!foldername.startsWith("/"))
            foldername = "/" + foldername;
    }

    public List<String> getFileNames() {
        return filenames;
    }

    public void setFileNames(String arg) {
        filenames = new ArrayList<>();
        String[] splitted = arg.split("=");
        if (splitted.length < 2)
            return;
        String[] fileList = splitted[1].split(",");
        for (int i = 0, size = fileList.length; i < size; i++) {
            filenames.add(fileList[i]);
        }
    }
}