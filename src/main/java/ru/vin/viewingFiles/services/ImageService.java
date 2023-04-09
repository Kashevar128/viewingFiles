package ru.vin.viewingFiles.services;

import org.springframework.stereotype.Service;
import ru.vin.viewingFiles.model.Client;

import java.io.IOException;
import java.util.List;

@Service
public class ImageService {

    Client client;

    public ImageService createClientConnect() {
        client = Client.builder()
                .server("185.27.134.11")
                .port(21)
                .user("epiz_33891104")
                .password("CLc195rPV8h3cv")
                .build();

        try {
            client.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public List<String> getListFilesClient() {
        try {
            List<String> listFiles = client.getListFiles();
            client.close();
            return listFiles;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getDataPhotoClient(String nameFile) {
        try {
            String dataFile = client.getDataFile(nameFile);
            client.close();
            return dataFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}