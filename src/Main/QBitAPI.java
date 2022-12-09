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
//        Config configFile = new Config();
//        username = configFile.getLoginValue(Authentication.USERNAME);
//        password = configFile.getLoginValue(Authentication.PASSWORD);
//        serverIp = configFile.getLoginValue(Authentication.SERVERIP);
//        qbtPath = configFile.getLoginValue(Authentication.QBTPATH);
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


    public static String execCmd(String cmd) {
        String result = null;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
             Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
            result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static void test() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"cmd", " /c .\\qbt\\qbt torrent list --format list"};
        Process proc = rt.exec(commands);
        for(int i = 0; i < commands.length; i++)
            System.out.println(commands[i]);
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

    private static OutputStream getCmdData(String cmd) throws IOException {
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
        Process p = Runtime.getRuntime().exec(cmd);
        p.getInputStream().transferTo(output);
        p.getErrorStream().transferTo(output);
        //System.out.println(output);
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
            for(int i = 0; i < magnetLinks.size(); i++) {
                magnetList.append(magnetLinks.get(i)).append(" ");
            }
        }
        StringBuilder torrentList = new StringBuilder();
        for(int i = 0; i < files.size(); i++) {
            torrentList.append(files.get(i)).append(" ");
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
        //execAndOutput()
    }

    public static void getTorrentInfo() throws IOException {
        //test();
//        TorrentListThread torrentListThread = new TorrentListThread();
//        torrentListThread.start();
        //System.out.println(execAndOutput(".\\qbt\\qbt.exe torrent list"));
    }
}
