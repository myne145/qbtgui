package Tasks;

import Gui.AlertType;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;

import static Gui.App.alert;
import static Tasks.Console.execAndOutput;

public class StartDownloading extends Thread {
    public static ArrayList<File> files = new ArrayList<>();
    public static ArrayList<String> magnetLinks = new ArrayList<>();
    private final DefaultListModel<Object> listModel;
    public static boolean isWaiting = false;
    public StartDownloading(DefaultListModel<Object> l) {
        this.listModel = l;
    }


    public static int addTorrent() {
        StringBuilder magnetList = new StringBuilder();
        for (String magnetLink : magnetLinks) {
            magnetList.append(magnetLink).append(" ");
        }

        System.out.println(magnetList);
        StringBuilder torrentList = new StringBuilder();
        for (File file : files) {
            torrentList.append(file).append(" ");
        }
        String outputMagnet = (execAndOutput(".\\qbt\\qbt.exe torrent add url " + magnetList));
        String output = (execAndOutput(".\\qbt\\qbt.exe torrent add file " + torrentList + " --category Movies"));

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
        if(files.isEmpty() && magnetLinks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no files to add!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            isWaiting = true;
            listModel.clear();
            listModel.add(0, "Adding torrents, please wait...");
            int errorCode = addTorrent();
            if(errorCode == 0) {
                alert(AlertType.INFO, "Succesfully added " + files.size() + " torrents and " +
                        magnetLinks.size() + " magnet links.");
                isWaiting = false;
            }
            if(errorCode == -1)
                JOptionPane.showMessageDialog(null, "Torrent list is empty (somehow), torrents were not added",
                        "Error", JOptionPane.ERROR_MESSAGE);
            if(errorCode == 1)
                JOptionPane.showMessageDialog(null, "Critical Error",
                        "Error", JOptionPane.ERROR_MESSAGE);
            files.clear();
            magnetLinks.clear();
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
}
