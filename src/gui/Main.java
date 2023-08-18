package gui;

import tasks.RefreshPlexLibrary;
import com.formdev.flatlaf.FlatDarkLaf;

import tasks.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.prefs.Preferences;


public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //possible args: -refresh_plex, -nogui
        //load gui only if there's no nogui argument
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        if(!Arrays.asList(args).contains("-nogui")) {
//            Config cfg = new Config();
            Config.initialize();

            ImageIcon iconImg = new ImageIcon(".\\qbtapiicon.png");
            FlatDarkLaf.setup();
            JFrame window = new JFrame();
            window.add(new App());
            window.getRootPane().setDefaultButton(App.startTheDownload);
            window.setVisible(true);
            window.setLocation(preferences.getInt("WINDOW_LOCATION_X", 0), preferences.getInt("WINDOW_LOCATION_Y", 0));
            window.pack();
            window.setTitle("Qbittorrent WebUI Downloader");
            window.setPreferredSize(new Dimension(1000, 600));
            window.setIconImage(iconImg.getImage());

            window.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {
                    preferences.putInt("WINDOW_LOCATION_X", window.getX());
                    preferences.putInt("WINDOW_LOCATION_Y", window.getY());
                    System.exit(1);
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
        if(Arrays.asList(args).contains("-refresh_plex")) {
            new RefreshPlexLibrary().start();
        }
    }
}
