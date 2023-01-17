package Gui;

import Tasks.*;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import xyz.derkades.plex4j.Server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends JPanel {



    private final DefaultListModel<Object> listModel = new DefaultListModel<>();
    private final JList<Object> list = new JList<>(listModel);
    private JCheckBoxMenuItem copyItem;
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
            //all cells false
            return false;
        }
    };
    public JTable torrentListTable = new JTable(torrentListModel);
    private final JToolBar tb = new JToolBar();
    private final JScrollPane scrollTest = new JScrollPane(torrentListTable);

    JButton finalB;
    private final JScrollPane listScrollPane = new JScrollPane();
    public static JButton startTheDownload = new JButton("Start Downloading");

    public static boolean isThreadRunning = false;
    private final JLabel processingPlsWait = new JLabel("");
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
        this.setLayout(null);
        scrollTest.setEnabled(true);
        scrollTest.setVisible(true);
        listModel.add(0, "Drop Files Here");
        selectUnit.setEditable(false);
        torrentListModel.addColumn("name");
        torrentListModel.addColumn("status");
        torrentListModel.addColumn("progress");
        torrentListModel.addColumn("speed");
        torrentListModel.addColumn("size");

//        torrentListTable.getColumn("progress").setPreferredWidth(30);
//        torrentListTable.getColumn("name").setPreferredWidth(250);
//        torrentListTable.getColumn("size").setPreferredWidth(75);
//        torrentListTable.getColumn("speed").setPreferredWidth(75);

        torrentListModel.addRow(new String[]{"Filename","Status","Progress","Speed","Size"});
        torrentListTable.setBorder(new FlatRoundBorder());
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.addColumn("Test");
        list.setFont(new Font("Segoe UI", Font.BOLD, 12));
        list.setBorder(new FlatRoundBorder());
        list.setDragEnabled(true);
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
                isThreadRunning = true; //the worst solution ever, if I add even one more jbutton to the toolbar, it's gonna break af
                startThread(b, selectUnit);
            }
        });
        listScrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        add(listScrollPane);
        add(selectUnitText);
        add(selectUnit);
        add(torrentListTable);
        add(list);
        add(scrollTest, BorderLayout.CENTER);
        add(createDummyToolBar(), BorderLayout.NORTH);
        add(createDummyMenuBar(), BorderLayout.NORTH);
        add(startTheDownload);


        //torrentListTable.getTableHeader().setResizingAllowed(true);
        startTheDownload.addActionListener(e -> {
            StartDownloading s = new StartDownloading(listModel);
            s.start();
            listModel.clear();
            listModel.add(0, "Adding torrents, please wait...");

        });




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
                        if(getFileExtension(f).equals(".torrent")) {
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        torrentListTable.setBounds(getWidth() / 2 - 40, 40, getWidth() / 2 + 35,getHeight() - 90);
        list.setBounds(5,40,getWidth() / 2 - 55, getHeight() - 90);
        selectUnit.setBounds(120,getHeight() - 40,60,20);
        selectUnitText.setBounds(10,getHeight() - 40,130,20);
        tb.setBounds(0,0,getWidth(),40);
        startTheDownload.setBounds(getWidth() - 150, getHeight() - 40, 140, 30);
        processingPlsWait.setBounds(30, 100, 125, 25);
    }

    private boolean verifyMagnetLink(String magnetLink) {
        Pattern pattern = Pattern.compile("magnet:\\?xt=urn:[a-z0-9]+:[a-zA-Z0-9]{32}");
        Matcher matcher = pattern.matcher(magnetLink);
        return matcher.find();
    }

    public JToolBar createDummyToolBar() {
        b = new JButton("Open Files");
        b.setRequestFocusEnabled(false);
        b.setPreferredSize(new Dimension(100,30));

        b.addActionListener(e -> {
            fileDialog.setMultipleMode(true);
            fileDialog.setFile("*.torrent");
            fileDialog.setVisible(true);
            File[] files = fileDialog.getFiles();
            ArrayList<File> arr = new ArrayList<>();
            if(files.length > 0) {
                for (File file : files) {
                    if (getFileExtension(file).equals(".torrent")) {
                        listModel.add(listModel.size(), file.getName());
                        arr.add(file);
                    } else {
                        listModel.add(listModel.size(), "File is not a Torrent!");
                    }
                }
                if (listModel.get(0).equals("Drop Files Here"))
                    listModel.remove(0);
                StartDownloading.files.addAll(arr);
            }
        });
        tb.add(b);
        b = new JButton("Clear All Files");
        b.setRequestFocusEnabled(false);
        b.addActionListener(e -> {
            StartDownloading.files.clear();
            StartDownloading.magnetLinks.clear();
            listModel.removeAllElements();
            listModel.add(0, "Drop Files Here");
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
                jDialog.setIconImage(new ImageIcon(".\\qbtapiicon.png").getImage());
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
                        if (listModel.get(0).equals("Drop Files Here"))
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
            } catch (IOException ex) {
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

    public JMenuBar createDummyMenuBar() {
        JMenuBar mb = new JMenuBar();
        copyItem = new JCheckBoxMenuItem("Use COPY Action");
        copyItem.setMnemonic(KeyEvent.VK_C);
        return mb;
    }
}
