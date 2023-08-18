package tasks;

import gui.AlertType;
import xyz.derkades.plex4j.Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static gui.App.alert;

public class TestQbittorrentConnection extends Thread{

    public TestQbittorrentConnection() {

    }

    private void testQbittorrent() throws IOException, URISyntaxException {
        Server server = new Server(new URI(Config.getQbittorrentIp()).toURL(),Config.getPlexToken());
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
        } catch (IOException | URISyntaxException e) {
            alert(AlertType.ERROR, "Cannot connect to Qbittorrent WebUI server - the config value does not exist.\nThe program will now exit\n" + e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
