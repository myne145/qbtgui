package Tasks;

import Gui.AlertType;
import xyz.derkades.plex4j.Server;

import java.io.IOException;
import java.net.URL;

import static Gui.App.alert;

public class TestQbittorrentConnection extends Thread{

    public TestQbittorrentConnection() {

    }

    private void testQbittorrent() throws IOException {
        Server server = new Server(new URL(new ConfigFileManager().getQbittorrentIp()),new ConfigFileManager().getPlexToken()); //haha lazy code go brrrrrrrrrrrr
        if(server.testConnection() != null) {
            alert(AlertType.FATAL, "Cannot connect to Qbittorrent WebUI server. The program will now exit.\n" + server.testConnection());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            testQbittorrent();
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot connect to Qbittorrent WebUI server - the config value does not exist.\nThe program will now exit\n" + e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
