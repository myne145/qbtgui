package gui;

import tasks.*;
import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends JPanel {
    private final DefaultListModel<Object> listModel = new DefaultListModel<>();
    private final JList<Object> addedFilesList = new JList<>(listModel);
    private final FileDialog fileDialog = new FileDialog((Dialog) null, "Select Torrent Files");
    private final static Dimension scrRes = Toolkit.getDefaultToolkit().getScreenSize();
    private final static int scrX = scrRes.width;
    private final static int scrY = scrRes.height;
    private final DefaultTableModel tableModel = new DefaultTableModel();
    public final JComboBox<String> selectUnit = new JComboBox<>();
    public final JLabel selectUnitText = new JLabel("Select display unit:");
    public static int unitIndex;
    public static JButton b;
    public DefaultTableModel torrentListModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

    };

    public JTable torrentListTable = new JTable(torrentListModel);
    private final JToolBar tb = new JToolBar();

    JButton finalB;
    public static JButton startTheDownload = new JButton("Start Downloading");

    public static boolean isThreadRunning = false;
    private final String DROP_FILES_TEXT = "Drop Files Here                                                       ";

    public void startThread(JButton j, JComboBox<String> jsp) {
        ShowDownloadingTorrents thread = new ShowDownloadingTorrents(torrentListModel, unitIndex, j, jsp);


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

    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public App() {
        new TestQbittorrentConnection().start();
        new TestPlexConnection().start();
        this.setLayout(new BorderLayout());
        listModel.add(0, "Drop Files Here                                                       ");
        selectUnit.setEditable(false);
        torrentListModel.addColumn("name");
        torrentListModel.addColumn("status");
        torrentListModel.addColumn("progress");
        torrentListModel.addColumn("speed");
        torrentListModel.addColumn("size");


        torrentListTable.getTableHeader().setResizingAllowed(false);
        torrentListTable.setColumnSelectionAllowed(true);
        torrentListTable.setRowSelectionAllowed(true);

        torrentListModel.addRow(new String[]{"Filename","Status","Progress","Speed","Size"});
        torrentListTable.setBorder(new FlatRoundBorder());
        addedFilesList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.addColumn("Test");
        addedFilesList.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addedFilesList.setBorder(new FlatRoundBorder());
        addedFilesList.setDragEnabled(true);
        selectUnit.addItem("KB");
        selectUnit.addItem("MB");
        selectUnit.addItem("GB");
        selectUnit.setSelectedIndex(1);
        unitIndex = 1;
        selectUnit.addItemListener(event -> {
            if(event.getStateChange() == ItemEvent.SELECTED) {
                unitIndex = selectUnit.getSelectedIndex();
                selectUnit.setEnabled(false);
                b.setEnabled(false);
                isThreadRunning = true;
                startThread(b, selectUnit);
            }
        });
        addedFilesList.setLayoutOrientation(JList.VERTICAL);

//        addedFilesList.setPreferredSize(new Dimension(300, 100));

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(selectUnitText, BorderLayout.LINE_START);
        bottomPanel.add(selectUnit, BorderLayout.CENTER);
        bottomPanel.add(startTheDownload, BorderLayout.LINE_END);

        add(createDummyToolBar(), BorderLayout.PAGE_START);
        add(addedFilesList, BorderLayout.LINE_START);
        add(torrentListTable, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.PAGE_END);


        startTheDownload.addActionListener(e -> new StartDownloading(listModel).start());




        TransferHandler handler = new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            public boolean importData(TransferSupport support) {
                if (!StartDownloading.isWaiting) {
                    if (!canImport(support)) {
                        return false;
                    }
                    Transferable t = support.getTransferable();
                    ArrayList<File> fileList = new ArrayList<>();
                    if (listModel.get(0).equals(DROP_FILES_TEXT))
                        listModel.remove(0);
                    try {
                        List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        for (File f : l) {
                            if (getFileExtension(f).equals(".torrent")) {
                                fileList.add(f);
                                listModel.add(listModel.size(), f.getName());
                            } else {
                                listModel.add(listModel.size(), "File is not a torrent!");
                            }
                        }
                        StartDownloading.files.addAll(fileList);
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    }
                }
                return true;
            }
        };
        addedFilesList.setTransferHandler(handler);



    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000,610);
    }


    private boolean verifyMagnetLink(String magnetLink) {
        Pattern pattern = Pattern.compile("magnet:\\?xt=urn:[a-z\\d]+:[a-zA-Z\\d]{32}");
        Matcher matcher = pattern.matcher(magnetLink);
        return matcher.find();
    }

    public JToolBar createDummyToolBar() {
        b = new JButton("Open Files");
        b.setRequestFocusEnabled(false);
        b.setPreferredSize(new Dimension(100,30));

        b.addActionListener(e -> {
            if(!StartDownloading.isWaiting) {
                fileDialog.setMultipleMode(true);
                fileDialog.setFile("*.torrent");
                fileDialog.setVisible(true);
                File[] files = fileDialog.getFiles();
                ArrayList<File> arr = new ArrayList<>();
                if (files.length > 0) {
                    for (File file : files) {
                        if (getFileExtension(file).equals(".torrent")) {
                            listModel.add(listModel.size(), file.getName());
                            arr.add(file);
                        } else {
                            listModel.add(listModel.size(), "File is not a Torrent!");
                        }
                    }
                    if (listModel.get(0).equals(DROP_FILES_TEXT))
                        listModel.remove(0);
                    StartDownloading.files.addAll(arr);
                }
            }
        });
        tb.add(b);
        b = new JButton("Clear All Files");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e -> {
            StartDownloading.files.clear();
            StartDownloading.magnetLinks.clear();
            listModel.removeAllElements();
            listModel.add(0, DROP_FILES_TEXT);
        });
        tb.add(b);


        tb.add(b);
        b = new JButton("Add Magnet Link");
        b.setRequestFocusEnabled(false);
//        JButton finalB = b;
        finalB = b;
        b.addActionListener(e-> {
                JDialog jDialog = new JDialog((Dialog) null);
                finalB.setEnabled(false);
                jDialog.setIconImage(new ImageIcon(".\\icon_temp.png").getImage());
                JLabel label = new JLabel("Enter magnet link below (max one link!)");
                label.putClientProperty("FlatLaf.styleClass", "h3");
                JTextArea textArea = new JTextArea();
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                JButton ok = new JButton("Ok");
                JButton cancel = new JButton("Cancel");
                jDialog.setPreferredSize(new Dimension(400, 500));
                jDialog.setLocation(scrX / 2 - 200, scrY / 2 - 250);
                jDialog.pack();
                jDialog.setLayout(null);
                jDialog.setTitle("Enter Magnet Link");
                jDialog.setResizable(false);
                label.setBounds(25, 10, 280, 30);
                textArea.setBounds(15, 50, 355, 330);
                separator.setBounds(0, 400, 400, 10);
                textArea.setLineWrap(true);
                textArea.setBorder(new FlatRoundBorder());
                ok.setBounds(215, 425, 80, 24);
                cancel.setBounds(300, 425, 80, 24);
                cancel.addActionListener(eee -> {
                    jDialog.setVisible(false);
                    finalB.setEnabled(true);
                });
                ok.addActionListener(ee -> {
                    String text = textArea.getText();
                    System.out.println(text);
                    if (verifyMagnetLink(text)) { //TODO: think of a good condition
                        StartDownloading.magnetLinks.add(text);
                        StartDownloading.magnetLinks.replaceAll(s -> s.replace("\n", ""));
                        finalB.setEnabled(true);
                        jDialog.setVisible(false);
                        if (listModel.get(0).equals(DROP_FILES_TEXT))
                            listModel.remove(0);
                        listModel.add(listModel.size(), "Magnet Link");
                    } else {
                        alert(AlertType.ERROR, "Magnet link is invalid");
                    }
                });
                jDialog.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}
                    @Override
                    public void windowClosing(WindowEvent e) {
                       finalB.setEnabled(true);
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
                jDialog.add(cancel);
                jDialog.add(separator);
                jDialog.add(ok);
                jDialog.add(textArea);
                jDialog.add(label);
                jDialog.setVisible(true);
        });
        tb.add(b);
        b = new JButton("Refresh Plex Media Library");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e-> {
            RefreshPlexLibrary refreshPlexLibrary = null;
            try {
                refreshPlexLibrary = new RefreshPlexLibrary();
            } catch (IOException | URISyntaxException ex) {
                alert(AlertType.ERROR, "Failed to connect to plex server:\n" + ex.getLocalizedMessage());
            }
            assert refreshPlexLibrary != null;
            refreshPlexLibrary.start();
        });
        tb.add(b);
        b = new JButton("Show Currently Downloading Torrents");
        b.setRequestFocusEnabled(false);
        JButton finalB1 = b;
        b.addActionListener(e-> {
            finalB1.setEnabled(false);
            selectUnit.setEnabled(false);
            //selectUnitText.setEnabled(false);
            startThread(finalB1, selectUnit);
            });
        tb.add(b);
        tb.setFloatable(false);
        return tb;
    }
}
