package Main;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class TorrentListThread extends Thread {
    public ArrayList<String> names = new ArrayList<>();
    private final ArrayList<BigDecimal> progresses = new ArrayList<>();
    private final ArrayList<Double> sizes = new ArrayList<>();
    private final ArrayList<String> hashes = new ArrayList<>();
    private final ArrayList<Double> speeds = new ArrayList<>();
    private String unitSymbol;
    private final JButton guiButton;
    private int unitIndex;
    private DefaultTableModel model;
    private JComboBox<String> spinner;

    private ArrayList<String> speedUnitSymbol = new ArrayList<>();
    public TorrentListThread(DefaultTableModel model, int unitIndex, JButton j, JComboBox<String> jsp) {
        this.unitIndex = unitIndex;
        this.model = model;
        this.guiButton = j;
        this.spinner = jsp;
    }
    private OutputStream refreshTorrentList() throws IOException {
        OutputStream output = new OutputStream() {
            final StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) {
                this.string.append((char) b);
            }

            public String toString() {
                return this.string.toString();
            }
        };
        Process p = Runtime.getRuntime().exec(".\\qbt\\qbt torrent list --format json");
        p.getInputStream().transferTo(output);
        p.getErrorStream().transferTo(output);
        return output;
    }

    private ArrayList<String> splitJsons() throws IOException {
        String[] split = refreshTorrentList().toString().split("},");
        for(int i = 0; i < split.length; i++) {
            if(i != split.length - 1)
                split[i] = split[i] + "\t" + "}";
            split[i] = split[i].replaceAll("\\[", "");
            split[i] = split[i].replaceAll("]", "");

        }
        return new ArrayList<>(Arrays.asList(split));
    }
    private void processTorrentData() throws IOException {
        ArrayList<String> splitData = splitJsons();
        for(int i = 0; i < splitData.size(); i++) {
            JSONObject data = new JSONObject();
            try {
                data = new JSONObject(splitData.get(i));
            } catch(Exception e) {
                Gui.alert(AlertType.ERROR, e.getLocalizedMessage() + "\n whatever that means");
            }
            this.names.add(data.getString("name"));
            this.sizes.add((double) data.getLong("size")); //ALWAYS returns bytes
            this.hashes.add(data.getString("hash"));
            this.progresses.add(data.getBigDecimal("progress"));
            this.speeds.add(data.getDouble("dlspeed"));
        }
    }
    //SPEED IS IN BYTES!!!
    private void unitConversion() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        switch(unitIndex) {
            case 0 -> {
                sizes.replaceAll(aLong -> Double.valueOf(decimalFormat.format(aLong / 1024)));
                unitSymbol = "kb";
            }
            case 1 -> {
                sizes.replaceAll(aLong -> Double.valueOf(decimalFormat.format(aLong / 1048576)));
                unitSymbol = "mb";
            }
            case 2 -> {
                sizes.replaceAll(aLong -> Double.valueOf(decimalFormat.format(aLong / 1073741824)));
                unitSymbol = "gb";
            }
        }
    }

    private ArrayList<String> progressConverter() {
        ArrayList<String> arr = new ArrayList<>();
        for(int i = 0; i < progresses.size(); i++) {
            int progressPerrcentage = 100;
            if(progresses.get(i).toString().length() >= 4)
                progressPerrcentage = Integer.parseInt(progresses.get(i).toString().substring(2,4));

            if(progresses.get(i).toString().charAt(0) == '1')
                progressPerrcentage = 100;

            arr.add(progressPerrcentage + "%");

        }
        return arr;
    }

    private void convertSpeedvalue() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for(int i = 0; i < speeds.size(); i++) {
            if(speeds.get(i) >= 1048576) {
                speedUnitSymbol.add("mb/s");
                speeds.set(i, Double.valueOf(decimalFormat.format(speeds.get(i) / Math.pow(1024, 2))));
            } else if(speeds.get(i) < 1048576 && speeds.get(i) > 0) {
                speedUnitSymbol.add("kb/s");
                speeds.set(i, Double.valueOf(decimalFormat.format(speeds.get(i) / 1024)));
            }
            else {
                speedUnitSymbol.add("kb/s");
            }
        }
    }


    @Override
    public void run() {
        super.run();
        try {
            Gui g = new Gui();
            processTorrentData();
            unitConversion();
            convertSpeedvalue();
            for(int i = 0; i < names.size(); i++) {
                if(!names.get(i).isEmpty())
                    model.addRow(new String[]{names.get(i),progressConverter().get(i), speeds.get(i) + speedUnitSymbol.get(i), sizes.get(i) + unitSymbol});
            }
            g.isThreadRunning = false;
            g.b.setEnabled(true);
            guiButton.setEnabled(true);
            spinner.setEnabled(true);
            progressConverter();
        } catch (Exception e) {
            Gui.alert(AlertType.ERROR, e.getLocalizedMessage());
        }

    }
}