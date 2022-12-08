package Main;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class TorrentListThread extends Thread {
    public ArrayList<String> names = new ArrayList<>();
    private ArrayList<BigDecimal> progresses = new ArrayList<>();
    private ArrayList<Double> sizes = new ArrayList<>();
    private ArrayList<String> hashes = new ArrayList<>();
    private String unitSymbol;
    public static boolean isReady = false;
    private JButton guiButton;
    private int unitIndex;
    private DefaultTableModel model;
    public TorrentListThread(DefaultTableModel model, int unitIndex, JButton j) {
        this.unitIndex = unitIndex;
        this.model = model;
        this.guiButton = j;
    }
    private OutputStream refreshTorrentList() throws IOException {
        //stackoverflow code
        OutputStream output = new OutputStream() {
            StringBuilder string = new StringBuilder();

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
        //System.out.println(output);
        return output;
    }

    private ArrayList<String> splitJsons() throws IOException {
        String[] split = refreshTorrentList().toString().split("},");
        for(int i = 0; i < split.length; i++) {
            if(i != split.length - 1)
                split[i] = split[i] + "\t" + "}";
            split[i] = split[i].replaceAll("\\[", "");
            split[i] = split[i].replaceAll("\\]", "");

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
                Gui.alert(AlertType.ERROR, e.getMessage() + "\n whatever that means");
            }
            this.names.add(data.getString("name"));
            this.sizes.add((double) data.getLong("size")); //ALWAYS returns bytes
            this.hashes.add(data.getString("hash"));
            this.progresses.add(data.getBigDecimal("progress"));
        }
    }

    private void unitConversion() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        System.out.println("Index :" + unitIndex);
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



    @Override
    public void run() {
        super.run();
        try {
            Gui g = new Gui();
            processTorrentData();
            unitConversion();
            DefaultTableModel d = new DefaultTableModel();
            //g.torrentListTable.setModel(d);

//            g.torrentListModel.addRow(new String[]{"test","test1","test2"});
//            g.torrentListModel.fireTableDataChanged();
            //isReady = true;
//            if(!names.isEmpty() || !progressConverter().isEmpty() || !sizes.isEmpty()) {
//                names.clear();
//                progressConverter().clear();
//                sizes.clear();
//                processTorrentData();
//             }
            for(int i = 0; i < names.size(); i++) {
                if(!names.get(i).isEmpty())
                    model.addRow(new String[]{names.get(i),progressConverter().get(i), sizes.get(i) + unitSymbol});
                //System.out.println(names.get(i) + "\t" + sizes.get(i) + unitSymbol + "\t" + progresses.get(i));
            }
            guiButton.setEnabled(true);
            g.selectUnit.setEnabled(true);
            g.selectUnitText.setEnabled(true);
            progressConverter();
        } catch (Exception e) {
            Gui.alert(AlertType.ERROR, e.getLocalizedMessage());
        }

    }
}
