package Main;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TorrentListThread extends Thread {
    public ArrayList<String> names = new ArrayList<>();
    private ArrayList<BigDecimal> progresses = new ArrayList<>();
    private ArrayList<Double> sizes = new ArrayList<>();
    private ArrayList<String> hashes = new ArrayList<>();
    private String unitSymbol;
    public static boolean isReady = false;

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
            JSONObject data = new JSONObject(splitData.get(i));
            this.names.add(data.getString("name"));
            this.sizes.add((double) data.getLong("size")); //ALWAYS returns bytes
            this.hashes.add(data.getString("hash"));
            this.progresses.add(data.getBigDecimal("progress"));
        }
    }

    private void unitConversion() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        switch(Gui.unitIndex) {
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
                unitSymbol = "gb;";
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
            isReady = true;
            for(int i = 0; i < names.size(); i++) {
                System.out.println(names.get(i) + "\t" + sizes.get(i) + unitSymbol + "\t" + progresses.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
