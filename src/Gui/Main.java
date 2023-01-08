package Gui;

import com.formdev.flatlaf.FlatDarkLaf;

import Tasks.ConfigFileManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import static Gui.App.alert;

public class Main {
    public static void main(String[] args) throws IOException {
        ConfigFileManager cfg = new ConfigFileManager();
        ImageIcon iconImg = new ImageIcon(".\\qbtapiicon.png");
        FlatDarkLaf.setup();
        JFrame window = new JFrame();
        window.add(new App());
        window.getRootPane().setDefaultButton(App.startTheDownload);
        window.setVisible(true);
        window.setLocation(cfg.launchPosX, cfg.launchPosY);
        window.pack();
        window.setTitle("Qbittorrent WebUI Downloader");
        window.setPreferredSize(new Dimension(1000, 600));
        window.setIconImage(iconImg.getImage());

//        try {
//            //QBitAPI.initiateConnection();
//        } catch (Exception e) {
//            alert(AlertType.FATAL, "Cannot connect to server \n" + e.getLocalizedMessage());
//            System.exit(0);
//        }



        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    cfg.saveValues(window.getX(), window.getY());
                } catch (IOException ex) {
                    alert(AlertType.ERROR, "Cannot save values to config \n" + ex.getLocalizedMessage());
                }
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }
}
