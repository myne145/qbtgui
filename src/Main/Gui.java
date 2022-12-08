package Main;


import com.formdev.flatlaf.ui.FlatFileChooserUI;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gui extends JPanel {

    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final DefaultListModel<Object> listModel = new DefaultListModel<>();
    private final JList<Object> list = new JList<>(listModel);
    private JCheckBoxMenuItem copyItem;
    private final FileDialog fileDialog = new FileDialog(new Frame(), "Select Torrent Files");
    private final static Dimension scrRes = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int scrX = scrRes.width;
    private final static int scrY = scrRes.height;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    public final JComboBox<String> selectUnit = new JComboBox<>();
    public final JLabel selectUnitText = new JLabel("Select displayed unit:");
    public static int unitIndex;
    private final JLabel refreshingPlsWait = new JLabel("Refreshing Torrents, Please Wait...");
    public DefaultTableModel torrentListModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
    };
    public JTable torrentListTable = new JTable(torrentListModel);
    //private JScrollPane jScrollPane = new JScrollPane(torrentListTable);



    private void startThread(JButton j) {
        TorrentListThread thread = new TorrentListThread(torrentListModel, unitIndex, j);


        if(torrentListTable.getRowCount() > 1) {
            torrentListModel.setRowCount(1);
        }
        thread.start();
    }
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }



    public Gui() {
        //super("TopLevelTransferHandlerDemo");

        //this.setLayout(new BorderLayout());
        this.setLayout(null);
        //this.setJMenuBar(createDummyMenuBar());
//        getContentPane().add(createDummyToolBar(), BorderLayout.NORTH);
//        setTitle("Qbittorrent WebUI Downloader");
        torrentListTable.setBounds(460, 40, 520,520);
        torrentListModel.addColumn("name");
        torrentListModel.addColumn("progress");
        torrentListModel.addColumn("size");
        torrentListModel.addRow(new String[]{"Filename","Progress","Size"});
        //torrentListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //torrentListTable.setAutoResizeMode(JTable.);
        torrentListTable.setBorder(new FlatRoundBorder());
        //jScrollPane.setVisible(true);
        //addToJTable();
        list.setBounds(0,40,450, 520);
        list.setDragEnabled(true);
        list.setFont(new Font("Segoe UI", Font.BOLD, 12));
        list.setBorder(new FlatRoundBorder());
        listModel.add(0, "Drop Files Here");
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //output.setBounds(480,40,300,520);
        tableModel.addColumn("Test");

//        refreshingPlsWait.setVisible(true);
//        refreshingPlsWait.setBounds(450, 570, 30, 90);
        refreshingPlsWait.setVisible(true);
        refreshingPlsWait.setBounds(750,570,230,20);

        //add(output);
        selectUnit.setEditable(false);
        selectUnit.setVisible(true);
        selectUnit.setBounds(130,570,60,20);
        selectUnit.addItem("KB");
        selectUnit.addItem("MB");
        selectUnit.addItem("GB");
        selectUnit.setSelectedIndex(1);
        unitIndex = 1;
        selectUnit.addItemListener(event -> {
            if(event.getStateChange() == ItemEvent.SELECTED) {
                unitIndex = selectUnit.getSelectedIndex();
                startThread(new JButton());
            }
        });
//        selectUnit.addItemListener(e -> {
//            System.out.println(e.getItem() + " " + e.getStateChange());
//        });
        selectUnitText.setVisible(true);
        selectUnitText.setBounds(10,570,130,20);
        add(selectUnitText);
        add(selectUnit);
        add(torrentListTable);
        //add(jScrollPane, BorderLayout.CENTER);
        add(list);
        add(refreshingPlsWait);
        add(createDummyToolBar(), BorderLayout.NORTH);
        add(createDummyMenuBar(), BorderLayout.NORTH);


        TransferHandler handler = new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }
                if (copyItem.isSelected()) {
                    boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
                    if (!copySupported) {
                        return false;
                    }
                    support.setDropAction(COPY);
                }
                return true;
            }

            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                Transferable t = support.getTransferable();
                ArrayList<File> fileList = new ArrayList<>();
                if(listModel.get(0).equals("Drop Files Here"))
                    listModel.remove(0);
                try {
                    List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : l) {
                        if(getFileExtension(f).equals(".torrent") || !fileList.contains(f)) {
                            listModel.add(listModel.size(), f.getName());
                            fileList.add(f);
                        } else {
                            listModel.add(listModel.size(), "File is not a torrent!");
                        }
                        //System.out.println(f);
                    }
                    QBitAPI.files.addAll(fileList);
                } catch (UnsupportedFlavorException | IOException e) {
                    return false;
                }

                return true;
            }
        };
        list.setTransferHandler(handler);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000,610);
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        System.out.println("test");
    }

    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            alert(AlertType.ERROR, "Cannot load windows decorations");
        }

        try {
            System.out.println(QBitAPI.initiateConnection());
        } catch (Exception e) {
            alert(AlertType.FATAL, "Cannot connect to server - IOException");
            System.exit(0);
        }
        Gui test = new Gui();
        //test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.setSize(1000, 650);
        test.setLocation(scrX / 2 - 232, scrY / 2 - 300);
        //test.setPreferredSize(new Dimension(1000,600));
        test.setVisible(true);
        test.list.requestFocus();
    }


    public void addTorrentData() {
        torrentListModel.addRow(new String[]{"Filename","Progress","Size"});
    }


    public static boolean isEmpty(JTable jTable) {
        if (jTable != null && jTable.getModel() != null) {
            return jTable.getModel().getRowCount()<=0?true:false;
        }
        return false;
    }

    //public void se
    public JToolBar createDummyToolBar() {
        JToolBar tb = new JToolBar();
        tb.setBounds(0,0,1000,40);
        JButton b;
        b = new JButton("Open Files");
        b.setRequestFocusEnabled(false);
        b.setPreferredSize(new Dimension(100,30));

        b.addActionListener(e -> {
            fileDialog.setMultipleMode(true);
            fileDialog.setVisible(true);
            File[] files = fileDialog.getFiles();
            ArrayList<File> arr = new ArrayList<>();
            for (File file : files) {
                if(getFileExtension(file).equals(".torrent")) {
                    listModel.add(listModel.size(), file);
                    arr.add(file);
                } else {
                    listModel.add(listModel.size(), "File is not a Torrent!");
                }
            }
            QBitAPI.files.addAll(arr);
        });
        tb.add(b);
        b = new JButton("Clear All Files");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e -> {
            QBitAPI.files.clear();
            listModel.removeAllElements();
            listModel.add(0, "Drop Files Here");
        });
        tb.add(b);


        tb.add(b);
        b = new JButton("Add Magnet Link");
        b.setRequestFocusEnabled(false);
        JButton finalB = b;
        b.addActionListener(e-> {
                JDialog jDialog = new JDialog(new JFrame());
                finalB.setEnabled(false);
                JLabel label = new JLabel("Enter magnet link below:");
                JTextArea textArea = new JTextArea();
                JButton ok = new JButton("Ok");
                jDialog.setPreferredSize(new Dimension(400, 500));
                jDialog.setLocation(scrX / 2 - 200, scrY / 2 - 250);
                jDialog.pack();
                jDialog.setLayout(null);
                jDialog.setTitle("Enter Magnet Link (ONLY 1 MAGNET LINK FOR NOW)");
                jDialog.setResizable(false);
                label.setBounds(0, 10, 200, 30);
                textArea.setBounds(0, 50, jDialog.getWidth(), 300);
                textArea.setLineWrap(true);
                textArea.setBorder(new FlatRoundBorder());
                ok.setBounds(300, 400, 70, 30);
                ok.addActionListener(ee -> {
                    if (!textArea.getText().equals("")) {
                        String text = textArea.getText();
                        String[] magnetSplitted = text.split("magnet:");
                        QBitAPI.magnetLinks.addAll(Arrays.asList(magnetSplitted));
                        for (int i = 0; i < QBitAPI.magnetLinks.size(); i++) {
                            QBitAPI.magnetLinks.set(i, QBitAPI.magnetLinks.get(i).replace("\n", ""));
                        }
                        finalB.setEnabled(true);
                        //QBitAPI.magnetLinks.add(textArea.getText()); //TODO: add more than 1 link
                        jDialog.setVisible(false);
                        if (listModel.get(0).equals("Drop Files Here"))
                            listModel.remove(0);
                        listModel.add(listModel.size(), "Magnet Link");
                    } else {
                        finalB.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "Magnet link specified is invalid", "Invalid magnet link",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
                jDialog.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                       finalB.setEnabled(true);
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {

                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });

                jDialog.add(ok);
                jDialog.add(textArea);
                jDialog.add(label);
                jDialog.setVisible(true);
        });
        tb.add(b);
        b = new JButton("Debug");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e-> {

        });
        tb.add(b);

        b = new JButton("Start the download on server");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e-> {
            StartTheDownloadThread s = new StartTheDownloadThread(listModel);
            s.start();
//            if(QBitAPI.files.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "There are no files to add!", "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            } else {
//                int errorCode = QBitAPI.addTorrent();
//                if(errorCode == 0)
//                    alert(AlertType.INFO, "Succesfully added " + QBitAPI.files.size() + " torrents and " +
//                            QBitAPI.magnetLinks.size() + " magnet links.");
//                if(errorCode == -1)
//                    JOptionPane.showMessageDialog(null, "Torrent list is empty (somehow), torrents were not added",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                if(errorCode == 1)
//                    JOptionPane.showMessageDialog(null, "Critical Error",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                QBitAPI.files.clear();
//                QBitAPI.magnetLinks.clear();
//                listModel.clear();
//                listModel.add(listModel.size(), "Drop Files Here");
//            }
        });
        tb.add(b);
        b = new JButton("Show Currently Downloading Torrents");
        b.setRequestFocusEnabled(false); //TODO: cooldown crashing th app and server
        JButton finalB1 = b;
        b.addActionListener(e-> {
            finalB1.setEnabled(false);
            selectUnit.setEnabled(false);
            selectUnitText.setEnabled(false);
            startThread(finalB1);

            });
        tb.add(b);
        tb.setFloatable(false);
        return tb;
    }

    public JMenuBar createDummyMenuBar() {
        JMenuBar mb = new JMenuBar();
        copyItem = new JCheckBoxMenuItem("Use COPY Action");
        copyItem.setMnemonic(KeyEvent.VK_C);

        return mb;
    }
}
