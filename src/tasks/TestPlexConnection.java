package tasks;

import gui.AlertType;
import xyz.derkades.plex4j.Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static gui.App.alert;

public class
TestPlexConnection extends Thread {

    public TestPlexConnection() {

    }

    private void testPlex() throws IOException, URISyntaxException {
        Server server = new Server(new URI(Config.getPlexIp()).toURL(),Config.getPlexToken());
        if(server.testConnection() != null) {
            alert(AlertType.ERROR, "Cannot connect to plex server. All plex features will not be avaliable.\n" + server.testConnection());
        }
    }


    @Override
    public void run() {
        super.run();
        try {
            testPlex();
        } catch (IOException | URISyntaxException e) {
            alert(AlertType.ERROR, "Cannot connect to Plex server - the config value does not exist.\n" + e.getLocalizedMessage());
        }
    }
}
