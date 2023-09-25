package com.myne145.torrentutils.tasks;

import com.myne145.torrentutils.gui.AlertType;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;

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


    public static int addTorrent() {
        StringBuilder magnetList = new StringBuilder();
        for (String magnetLink : mangnetLinksQueue) {
            magnetList.append(magnetLink).append(" ");
        }

        System.out.println(magnetList);
        StringBuilder torrentList = new StringBuilder();
        for (File file : torrentFilesQueue) {
            torrentList.append(file).append(" ");
        }
        String outputMagnet = (execAndOutput(".\\qbt_api\\qbt.exe torrent add url " + magnetList));
        String output = (execAndOutput(".\\qbt_api\\qbt.exe torrent add file " + torrentList + " --category Movies"));

        if(!magnetList.isEmpty() || !torrentList.isEmpty() || output.isEmpty()) {
            torrentList.delete(0, torrentList.length());
            magnetList.delete(0, magnetList.length());
            return 0;
        }
        if(torrentList.isEmpty() && magnetList.isEmpty())
            return -1;
        return 1;
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
            int errorCode = addTorrent();
            if(errorCode == 0) {
                alert(AlertType.INFO, "Succesfully added " + torrentFilesQueue.size() + " torrents and " +
                        mangnetLinksQueue.size() + " magnet links.");
                isWaiting = false;
            }
            if(errorCode == -1)
                JOptionPane.showMessageDialog(null, "Torrent list is empty (somehow), torrents were not added",
                        "Error", JOptionPane.ERROR_MESSAGE);
            if(errorCode == 1)
                JOptionPane.showMessageDialog(null, "Critical Error",
                        "Error", JOptionPane.ERROR_MESSAGE);
            torrentFilesQueue.clear();
            mangnetLinksQueue.clear();
            listModel.clear();
            listModel.add(listModel.size(), "Drop Files Here");

            if(errorCode == 0) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static ArrayList<File> getTorrentQueueList() {
        return torrentFilesQueue;
    }

    public static ArrayList<String> getMagnetQueueList() {
        return mangnetLinksQueue;
    }
}
