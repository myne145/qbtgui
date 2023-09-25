package com.myne145.torrentutils.gui;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.myne145.torrentutils.tasks.StartDownloading;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.myne145.torrentutils.gui.App.alert;

public class AddMagnetDialog extends JDialog {
    public AddMagnetDialog(App mainPanel, ActionEvent e) {
        this.setTitle("Enter Magnet Link");
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(400, 500));
        this.pack();
        this.setIconImage(new ImageIcon(".\\icon_temp.png").getImage());
        this.setVisible(true);

        JButton magnetButton = (JButton) e.getSource();
        magnetButton.setEnabled(false);

        JLabel titleLabel = new JLabel("Enter magnet link below (max one link!)");
//        titleLabel.putClientProperty("FlatLaf.styleClass", "h3");
        JTextArea textArea = new JTextArea();
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

//        this.setLocation(App.SCREEN_SIZE.width / 2 - 200, App.SCREEN_SIZE.height / 2 - 250); //center

        textArea.setLineWrap(true);
        textArea.setBorder(new FlatLineBorder(new Insets(10,10,10,10), new Color(44, 44, 44), 1, 32));
        textArea.setBackground(new Color(60, 63, 65));
//        textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textArea, BorderLayout.CENTER);
//        inputPanel.add(separator, BorderLayout.PAGE_END);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(okButton);


        add(titlePanel, BorderLayout.PAGE_START);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
//        add(separator);
//        add(textArea);


        cancelButton.addActionListener(eee -> {
            this.setVisible(false);
            magnetButton.setEnabled(true);
        });
        okButton.addActionListener(ee -> {
            String text = textArea.getText();
            if (verifyMagnetLink(text)) {
                StartDownloading.getMagnetQueueList().add(text);
                StartDownloading.getMagnetQueueList().replaceAll(s -> s.replace("\n", ""));
                magnetButton.setEnabled(true);
                this.setVisible(false);
                if (mainPanel.getListModel().get(0).equals(mainPanel.getDefaultDropMessage()))
                    mainPanel.getListModel().remove(0);
                mainPanel.getListModel().add(mainPanel.getListModel().size(), "Magnet Link");
            } else {
                alert(AlertType.ERROR, "Magnet link is invalid");
            }
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                magnetButton.setEnabled(true);
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

    private boolean verifyMagnetLink(String magnetLink) {
        Pattern pattern = Pattern.compile("magnet:\\?xt=urn:[a-z\\d]+:[a-zA-Z\\d]{32}");
        Matcher matcher = pattern.matcher(magnetLink);
        return matcher.find();
    }
}
