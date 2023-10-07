package com.myne145.torrentutils.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.myne145.torrentutils.tasks.RefreshPlexLibrary;
import com.myne145.torrentutils.tasks.ShowDownloadingTorrents;
import com.myne145.torrentutils.tasks.StartDownloading;
import com.sun.javafx.application.PlatformImpl;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.util.List;

public class App extends JPanel {
    private final DefaultListModel<Object> listModel = new DefaultListModel<>();
    private final JList<Object> addedFilesList = new JList<>(listModel);
    public final static Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    public final JComboBox<String> selectUnit = new JComboBox<>();
    public final JLabel selectUnitText = new JLabel("Select display unit:");
    public static int unitIndex;
    public static JButton toolbarButton;
    private final FileChooser fileDialog = new FileChooser();
    public DefaultTableModel torrentListModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

    };

    public JTable torrentListTable = new JTable(torrentListModel);
    private final JToolBar toolbar = new JToolBar();
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
    private static boolean isTorrent(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false; // empty extension
        }
        return name.substring(lastIndexOf).equals(".torrent");
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
        torrentListTable.setFocusable(false);
//        torrentListTable.setBackground(new Color(60, 63, 65));

        torrentListModel.addRow(new String[]{"Filename","Status","Progress","Speed","Size"});
        torrentListTable.setBorder(new FlatRoundBorder());
//        torrentListTable.setBorder(new FlatLineBorder(new Insets(10,10,10,10),Color.BLACK));
        addedFilesList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addedFilesList.setFocusable(false);


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
                toolbarButton.setEnabled(false);
                isThreadRunning = true;
                startThread(toolbarButton, selectUnit);
            }
        });
        addedFilesList.setLayoutOrientation(JList.VERTICAL);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(51, 51, 52));
        bottomPanel.add(selectUnitText, BorderLayout.LINE_START);
        bottomPanel.add(selectUnit, BorderLayout.CENTER);
        bottomPanel.add(startTheDownload, BorderLayout.LINE_END);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.add(addedFilesList);
        splitPane.add(torrentListTable);
        add(getToolbar(), BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.PAGE_END);


        startTheDownload.addActionListener(e -> new StartDownloading(listModel).start());




        TransferHandler handler = new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return true;
            }

            public boolean importData(TransferSupport support) {
                if (!StartDownloading.isWaiting) {
                    Transferable t = support.getTransferable();
                    ArrayList<File> fileList = new ArrayList<>();
                    if (listModel.get(0).equals(DROP_FILES_TEXT))
                        listModel.remove(0);
                    try {
                        List<File> addedFiles = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        for (File addedFile : addedFiles) {
                            if (isTorrent(addedFile)) {
                                fileList.add(addedFile);
                                listModel.add(listModel.size(), addedFile.getName());
                            } else {
                                listModel.add(listModel.size(), "File is not a torrent!");
                            }
                        }
                        StartDownloading.getTorrentQueueList().addAll(fileList);
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

    public JToolBar getToolbar() {
        toolbarButton = new JButton("Open Files");
        toolbarButton.setIcon(new FlatSVGIcon(new File("src/com/myne145/torrentutils/resources/addFile.svg")));
        toolbarButton.setRequestFocusEnabled(false);
        toolbarButton.setPreferredSize(new Dimension(100,30));

        toolbarButton.addActionListener(e -> {
            if(StartDownloading.isWaiting) {
                return;
            }
            PlatformImpl.startup(() -> {
                fileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Torrent Files", "*.torrent"));
                List<File> files = fileDialog.showOpenMultipleDialog(null);
                ArrayList<File> arr = new ArrayList<>();
                if (files != null && !files.isEmpty()) {
                    for (File file : files) {
                        if (isTorrent(file)) {
                            listModel.add(listModel.size(), file.getName());
                            arr.add(file);
                        } else {
                            listModel.add(listModel.size(), "File is not a Torrent!");
                        }
                    }
                    if (listModel.get(0).equals(DROP_FILES_TEXT))
                        listModel.remove(0);
                    StartDownloading.getTorrentQueueList().addAll(arr);


                }
            });
        });
        toolbar.add(toolbarButton);
        toolbarButton = new JButton("Clear All Files");
        toolbarButton.setIcon(new FlatSVGIcon(new File("src/com/myne145/torrentutils/resources/delete.svg")));
        toolbarButton.setRequestFocusEnabled(false);
        toolbarButton.addActionListener(e -> {
            StartDownloading.getTorrentQueueList().clear();
            StartDownloading.getMagnetQueueList().clear();
            listModel.removeAllElements();
            listModel.add(0, DROP_FILES_TEXT);
        });
        toolbar.add(toolbarButton);


        toolbar.add(toolbarButton);
        toolbarButton = new JButton("Add Magnet Link");
        toolbarButton.setIcon(new FlatSVGIcon(new File("src/com/myne145/torrentutils/resources/addLink.svg")));
        toolbarButton.setRequestFocusEnabled(false);
        toolbarButton.addActionListener(e-> {
            JButton magnetButton = (JButton) e.getSource();
            magnetButton.setEnabled(false);
            new AddMagnetDialog(this, e);
            magnetButton.setEnabled(true);
        });
        toolbar.add(toolbarButton);
        toolbarButton = new JButton("Refresh Plex Media Library");
        toolbarButton.setRequestFocusEnabled(false);
        toolbarButton.addActionListener(e-> {
            new RefreshPlexLibrary().start();
        });
        toolbar.add(toolbarButton);
        toolbarButton = new JButton("Show Currently Downloading Torrents");
        toolbarButton.setRequestFocusEnabled(false);
        JButton finalB1 = toolbarButton;
        toolbarButton.addActionListener(e-> {
            finalB1.setEnabled(false);
            selectUnit.setEnabled(false);
            //selectUnitText.setEnabled(false);
            startThread(finalB1, selectUnit);
            });
        toolbar.add(toolbarButton);
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(51, 51, 52));
        return toolbar;
    }

    public DefaultListModel<Object> getListModel() {
        return listModel;
    }

    public String getDefaultDropMessage() {
        return DROP_FILES_TEXT;
    }
}
