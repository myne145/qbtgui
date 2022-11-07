package Main;

import java.io.*;
import java.util.Arrays;

public class TorrentListThread extends Thread {

    private static OutputStream refreshTorrentList() throws IOException {
        //stackoverflow code
        OutputStream output = new OutputStream() {
            private final StringBuilder string = new StringBuilder();
            @Override
            public void write(int b) {
                this.string.append((char) b );
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

    @Override
    public void run() {
        super.run();
        try {
            System.out.println(refreshTorrentList());
        } catch (IOException e) {
            Gui.alert(AlertType.ERROR, "Cannot load torrent list" + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }
}
