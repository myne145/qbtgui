package Main;

import java.io.*;
import java.util.*;


public class QBitAPI {
    public static ArrayList<File> files = new ArrayList<>();
    public static ArrayList<String> magnetLinks = new ArrayList<>();
    private static String username;
    private static String password;
    private static String serverIp;
    private static String qbtPath;

    public static void initializeConfigFile() {
        ConfigFile configFile = new ConfigFile();
        username = configFile.getLoginValue(Authentication.USERNAME);
        password = configFile.getLoginValue(Authentication.PASSWORD);
        serverIp = configFile.getLoginValue(Authentication.SERVERIP);
        qbtPath = configFile.getLoginValue(Authentication.QBTPATH);
    }

    //Execute files and get output (not working for qbt!!!)
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

            int exitVal = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static <T> List getDuplicate(Collection<T> list) {

        final List<T> duplicatedObjects = new ArrayList<>();
        Set<T> set = new HashSet<>() {
            @Override
            public boolean add(T e) {
                if (contains(e)) {
                    duplicatedObjects.add(e);
                }
                return super.add(e);
            }
        };
        set.addAll(list);
        return duplicatedObjects;
    }


    public static <T> boolean hasDuplicate(Collection<T> list) {
        return !getDuplicate(list).isEmpty();
    }

    public void deDuplicateFiles(ArrayList<File> arr) {
        System.out.println( "\n" + "Has Duplicates:");
        for(int i = 0; i < arr.size(); i++) {
            System.out.println(arr.get(i));
        }
        System.out.println("\n" + "Only Duplicates");
        List<File> deduplicated = (List<File>) getDuplicate(arr);
        for(int i = 0; i < deduplicated.size(); i++) {
            System.out.println(deduplicated.get(i));
        }
    }
    //test method that works with qbt
    private void test() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"cmd.exe", "/c cd qbt && qbt settings"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

// Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

// Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    public static String initiateConnection() {
        String outUsername = execAndOutput(qbtPath + "settings set username " + username);
        String outUrl = execAndOutput(qbtPath + "settings set url " + serverIp);
        if(outUsername.equals("") && outUrl.equals("")) {
            return "Connection Initialized successfully.";
        } else {
            return "Message Log from setting username: " + outUsername + "\n" + "Message Log from Setting URL: " + outUrl;
        }
    }

    public static int addTorrent() {
        StringBuilder torrentList = new StringBuilder();
        for(int i = 0; i < files.size(); i++) {
            torrentList.append(files.get(i)).append(" ");
        }
        String output = (execAndOutput(".\\qbt\\qbt.exe torrent add file " + torrentList));
        if(!torrentList.isEmpty() || output.isEmpty()) {
            torrentList.delete(0, torrentList.length());
            return 0;
        }
        if(torrentList.isEmpty())
            return -1;
        return 1;
        //execAndOutput()
    }
}
