package Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class TorrentListThread extends Thread {

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

    @Override
    public void run() {
        super.run();
        System.out.println(execCmd("cmd /c .\\qbt\\qbt.exe"));
    }
}
