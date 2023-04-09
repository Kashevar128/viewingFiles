package ru.vin.viewingFiles.model;

import lombok.Builder;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Builder
public class Client {

    private final String server;
    private final int port;
    private final String user;
    private final String password;
    private FTPClient ftp;


    private final String startPath = "/";
    private final FTPFileFilter fileFilter = (file) -> {
        if (file.getName().startsWith(".") && file.isDirectory()) return false;
        if (!file.getName().startsWith("GRP327_") && file.isFile()) return false;
        return true;
    };

    public void open() throws IOException {
        ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
    }

    public void close() throws IOException {
        if (ftp != null) ftp.disconnect();
    }

    public List<String> getListFiles() throws IOException {
        List<String> collectorFiles = new ArrayList<>();
        BiConsumer<StringBuilder, FTPFile> biConsumerReturnNamesFiles = (entryPath, file) -> {
            if (entryPath.toString().endsWith("фотографии/")) {
                entryPath.append(file.getName());
                collectorFiles.add(entryPath.toString());
            }
        };
        filesWalk(startPath, fileFilter, biConsumerReturnNamesFiles);
        return collectorFiles;
    }

    public String getDataFile(String nameFile) throws IOException {
        StringBuilder dataFile = new StringBuilder();
        BiConsumer<StringBuilder, FTPFile> biConsumerReturnDataFile = (entryPath, file) -> {
            if (file.getName().equals(nameFile)) {
                entryPath.append(file.getName());
                try {
                    dataFile.append(ftp.mlistFile(entryPath.toString()).toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        filesWalk(startPath, fileFilter, biConsumerReturnDataFile);
        return dataFile.toString();
    }

    private void filesWalk(String path, FTPFileFilter fileFilter, BiConsumer<StringBuilder, FTPFile> biConsumer) {
        try {
            FTPFile[] ftpFiles = ftp.listFiles(path, fileFilter);
            for (FTPFile file : ftpFiles) {
                StringBuilder entryPath = new StringBuilder(path);
                if (!entryPath.toString().endsWith("/")) entryPath.append("/");
                if (file.isDirectory()) {
                    entryPath.append(file.getName());
                    filesWalk(entryPath.toString(), fileFilter, biConsumer);
                } else {
                    biConsumer.accept(entryPath, file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
