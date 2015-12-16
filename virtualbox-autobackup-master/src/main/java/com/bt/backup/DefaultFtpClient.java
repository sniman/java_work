package com.bt.backup;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by 608761587 on 11/11/2015.
 */
public class DefaultFtpClient {
    private String url;
    private String username;
    private String password;
    private FTPClient ftpClient;

    public DefaultFtpClient(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    public void init() {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(url);

            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                throw new Exception("Unable to connect.");
            }

            if (!ftpClient.login(username, password)) {
                ftpClient.disconnect();
                throw new Exception("Failed to login.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String file, String targetPath, CopyStreamAdapter streamListener) {
        try {
            FileInputStream inputFile = new FileInputStream(file);
            ftpClient.setCopyStreamListener(streamListener);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.storeFile(targetPath, inputFile);
            inputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO send file here
    }
    
    public void sendFileX(String file,String targetPath){
           try {
            FileInputStream inputFile = new FileInputStream(file);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.storeFile(targetPath, inputFile);
            inputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
