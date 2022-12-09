package Main;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config cfg = new Config();
        FlatDarkLaf.setup();
        Gui gui = new Gui();
        JFrame window = new JFrame();
        window.add(new Gui());
        window.setVisible(true);
        window.setLocation(cfg.launchPosX, cfg.launchPosY);
        window.pack();
        //window.setJMenuBar(gui.createDummyMenuBar());
        window.setTitle("Qbittorrent WebUI Downloader");
        window.setPreferredSize(new Dimension(1000, 600));
        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    cfg.saveValues(window.getX(), window.getY(), new Dimension(window.getWidth(), window.getHeight()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
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
        QBitAPI.initializeConfigFile();
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            Gui.createAndShowGUI();
        });
    }
}
