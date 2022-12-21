package Main;

import java.io.*;
import java.util.*;


public class QBitAPI {
    public static ArrayList<File> files = new ArrayList<>();
    public static ArrayList<String> magnetLinks = new ArrayList<>();
    public static String execAndOutput(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", command);

        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }


    private static OutputStream getCmdData(String cmd) throws IOException {
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
        Process p = Runtime.getRuntime().exec(cmd);
        p.getInputStream().transferTo(output);
        p.getErrorStream().transferTo(output);
        return output;
    }

    public static String initiateConnection() throws IOException {
        String outUsername = getCmdData(".\\qbt\\qbt.exe settings set username admin").toString();
        String outUrl = getCmdData(".\\qbt\\qbt.exe settings set url http://192.168.2.99:8080").toString();
        if(outUsername.equals("") && outUrl.equals("")) {
            return "Connection Initialized successfully.";
        } else {
            return "Message Log from setting username: " + outUsername + "\n" + "Message Log from Setting URL: " + outUrl;
        }
    }

    public static int addTorrent() {
        if(!magnetLinks.isEmpty()) {
            StringBuilder magnetList = new StringBuilder();
            for (String magnetLink : magnetLinks) {
                magnetList.append(magnetLink).append(" ");
            }
        }
        StringBuilder torrentList = new StringBuilder();
        for (File file : files) {
            torrentList.append(file).append(" ");
        }
        String outputMagnet = (execAndOutput(".\\qbt\\qbt.exe torrent add url " + magnetLinks));
        String output = (execAndOutput(".\\qbt\\qbt.exe torrent add file " + torrentList));

        if(!torrentList.isEmpty() || output.isEmpty()) {
            torrentList.delete(0, torrentList.length());
            return 0;
        }
        if(torrentList.isEmpty())
            return -1;
        return 1;
    }
}
