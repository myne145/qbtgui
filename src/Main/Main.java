package Main;

import com.formdev.flatlaf.FlatDarkLaf;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import static Main.Gui.alert;
import static Main.Gui.startTheDownload;

public class Main {
    public static void main(String[] args) throws IOException {
        Config cfg = new Config();
        ImageIcon iconImg = new ImageIcon(".\\qbtapiicon.png");
        FlatDarkLaf.setup();
        JFrame window = new JFrame();
        window.getRootPane().setDefaultButton(startTheDownload);
        window.add(new Gui(window));
        window.setVisible(true);
        window.setLocation(cfg.launchPosX, cfg.launchPosY);
        window.pack();
        window.setTitle("Qbittorrent WebUI Downloader");
        window.setPreferredSize(new Dimension(1000, 600));
        window.setIconImage(iconImg.getImage());

        QBitAPI.initiateConnection();

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
