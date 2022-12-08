package Main;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        Gui gui = new Gui();
        JFrame window = new JFrame();
        window.add(new Gui());
        window.setVisible(true);
        window.pack();
        //window.setJMenuBar(gui.createDummyMenuBar());
        window.setTitle("Qbittorrent WebUI Downloader");
        window.setPreferredSize(new Dimension(1000, 600));
        QBitAPI.initializeConfigFile();
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            Gui.createAndShowGUI();
        });
    }
}
