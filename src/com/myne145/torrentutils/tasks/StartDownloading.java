package com.myne145.torrentutils.tasks;

import com.myne145.torrentutils.gui.AlertType;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.myne145.torrentutils.gui.App.alert;
import static com.myne145.torrentutils.tasks.Console.execAndOutput;

public class StartDownloading extends Thread {
    private static ArrayList<File> torrentFilesQueue = new ArrayList<>();
    private static ArrayList<String> mangnetLinksQueue = new ArrayList<>();
    private final DefaultListModel<Object> listModel;
    public static boolean isWaiting = false;
    public StartDownloading(DefaultListModel<Object> l) {
        this.listModel = l;
    }


//    public static int addTorrent() {
//        StringBuilder magnetList = new StringBuilder();
//        for (String magnetLink : mangnetLinksQueue) {
//            magnetList.append(magnetLink).append(" ");
//        }
//
//        System.out.println(magnetList);
//        StringBuilder torrentList = new StringBuilder();
//        for (File file : torrentFilesQueue) {
//            torrentList.append(file).append(" ");
//        }
//        String outputMagnet = (execAndOutput(".\\qbt_api\\qbt.exe torrent add url " + magnetList));
//        String output = (execAndOutput(".\\qbt_api\\qbt.exe torrent add file " + torrentList + " --category Movies"));
//
//        if(!magnetList.isEmpty() || !torrentList.isEmpty() || output.isEmpty()) {
//            torrentList.delete(0, torrentList.length());
//            magnetList.delete(0, magnetList.length());
//            return 0;
//        }
//        if(torrentList.isEmpty() && magnetList.isEmpty())
//            return -1;
//        return 1;
//    }

    private static boolean addTorrentsToQbittorrent(List<File> torrents, List<String> magnetLinks) throws IOException {
        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create the multipart entity
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setBoundary("-------------------------acebdf13572468");

        //Magnet links
        StringBuilder magnets = new StringBuilder();
        if(magnetLinks != null) {
            for (String s : magnetLinks)
                magnets.append(s).append("\n");
            entityBuilder.addTextBody("urls", magnets.toString());
        }
        System.out.println(magnets);


        // Add torrent files
        if (torrents != null) {
            for (File torrent : torrents) {
                entityBuilder.addBinaryBody("torrents", torrent, ContentType.APPLICATION_OCTET_STREAM, torrent.getName());
            }
        }


        HttpEntity entity = entityBuilder.build();

        // Create the POST request
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/torrents/add");
        httpPost.setHeader("User-Agent", "Fiddler");
        httpPost.setHeader("Cookie", "SID=" + Config.cookie);
        httpPost.setEntity(entity);

        // Execute the request and get the response
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // Cleanup
        response.close();
        httpClient.close();

        // Check the response code
        return response.getCode() == 200;
    }

    @Override
    public void run() {
        super.run();
        if(torrentFilesQueue.isEmpty() && mangnetLinksQueue.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no files to add!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            isWaiting = true;
            listModel.clear();
            listModel.add(0, "Adding torrents, please wait...");
            boolean wereTorrentsAdded = false;
            try {
                wereTorrentsAdded = addTorrentsToQbittorrent(torrentFilesQueue, mangnetLinksQueue);
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot close the HTTP connection.");
            }
            if(wereTorrentsAdded) {
                alert(AlertType.INFO, "Succesfully added " + torrentFilesQueue.size() + " torrents and " +
                        mangnetLinksQueue.size() + " magnet links.");
                isWaiting = false;
            } else {
                alert(AlertType.ERROR, "415 - Torrent file is not valid.");
            }


            torrentFilesQueue.clear();
            mangnetLinksQueue.clear();
            listModel.clear();
            listModel.add(listModel.size(), "Drop Files Here");

//            if(wereTorrentsAdded == 0) {
//                try {
//                    sleep(2000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
    }

    public static ArrayList<File> getTorrentQueueList() {
        return torrentFilesQueue;
    }

    public static ArrayList<String> getMagnetQueueList() {
        return mangnetLinksQueue;
    }
}
