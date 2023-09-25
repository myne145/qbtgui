package com.myne145.torrentutils.tasks;

import com.myne145.torrentutils.gui.AlertType;
import com.myne145.torrentutils.gui.App;

import java.io.IOException;
import java.net.*;

public class RefreshPlexLibrary extends Thread{
    public RefreshPlexLibrary() throws MalformedURLException, URISyntaxException {

    }


    private void refreshAllPlexLibraries() throws URISyntaxException, IOException {
        URL url = new URI(Config.getPlexIp() + "/library/sections/all/refresh?X-Plex-Token=" + Config.getPlexToken()).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000); //6 seconds

        int code = connection.getResponseCode();
        if(code == 200) {
            App.alert(AlertType.INFO, "Succesfuly refreshed all Plex libraries.");
        } else {
            App.alert(AlertType.ERROR, "Provided Plex API token is invalid.");
        }

    }
    @Override
    public void run() {
        try {
            refreshAllPlexLibraries();
        } catch (URISyntaxException | IOException e) {
            App.alert(AlertType.ERROR, "Invalid Plex server IP or token.");
        }
    }
}
