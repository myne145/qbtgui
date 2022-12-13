package Main;

import javax.swing.*;

import static Main.Gui.alert;

public class StartTheDownloadThread extends Thread {
    private final DefaultListModel<Object> listModel;
    public StartTheDownloadThread(DefaultListModel<Object> l) {
        this.listModel = l;
    }

    @Override
    public void run() {
        super.run();
        if(QBitAPI.files.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no files to add!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            int errorCode = QBitAPI.addTorrent();
            if(errorCode == 0)
                alert(AlertType.INFO, "Succesfully added " + QBitAPI.files.size() + " torrents and " +
                        QBitAPI.magnetLinks.size() + " magnet links.");
            if(errorCode == -1)
                JOptionPane.showMessageDialog(null, "Torrent list is empty (somehow), torrents were not added",
                        "Error", JOptionPane.ERROR_MESSAGE);
            if(errorCode == 1)
                JOptionPane.showMessageDialog(null, "Critical Error",
                        "Error", JOptionPane.ERROR_MESSAGE);
            QBitAPI.files.clear();
            QBitAPI.magnetLinks.clear();
            listModel.clear();
            listModel.add(listModel.size(), "Drop Files Here");
        }
    }
}
