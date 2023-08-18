package tasks;

import gui.AlertType;
import xyz.derkades.plex4j.Server;

import java.io.IOException;
import java.net.URL;

import static gui.App.alert;

public class TestPlexConnection extends Thread {

    public TestPlexConnection() {

    }

    private void testPlex() throws IOException {
        Server server = new Server(new URL(new Config().getPlexIp()),new Config().getPlexToken()); //haha lazy code go brrrrrrrrrrrr
        if(server.testConnection() != null) {
            alert(AlertType.ERROR, "Cannot connect to plex server. All plex features will not be avaliable.\n" + server.testConnection());
        }
    }


    @Override
    public void run() {
        super.run();
        try {
            testPlex();
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot connect to Plex server - the config value does not exist.\n" + e.getLocalizedMessage());
        }
    }
}
