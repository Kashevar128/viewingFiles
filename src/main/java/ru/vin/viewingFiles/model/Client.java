package ru.vin.viewingFiles.model;

import lombok.Builder;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Builder
public class Client {

    private final String server;
    private final int port;
    private final String user;
    private final String password;
    private FTPClient ftp;

    private final List<String> collectorFiles = new ArrayList<>();

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
        String startPath = "/";
        FTPFileFilter fileFilter = (file) -> {
            if (file.getName().startsWith(".") && file.isDirectory()) return false;
            if (!file.getName().startsWith("GRP327_") && file.isFile()) return false;
            return true;
        };
        filesWalk(startPath, fileFilter);
        return collectorFiles;
    }

    private void filesWalk(String path, FTPFileFilter fileFilter) {
        try {
            FTPFile[] ftpFiles = ftp.listFiles(path, fileFilter);
            for (FTPFile file : ftpFiles) {
                StringBuilder entryPath = new StringBuilder(path);
                if (!entryPath.toString().endsWith("/")) entryPath.append("/");
                if (file.isDirectory()) {
                    entryPath.append(file.getName());
                    filesWalk(entryPath.toString(), fileFilter);
                } else {
                    if (path.endsWith("/фотографии")) {
                        entryPath.append(file.getName());
                        collectorFiles.add(entryPath.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
