package Main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        QBitAPI.initializeConfigFile();
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            Gui.createAndShowGUI();
        });
    }
}
