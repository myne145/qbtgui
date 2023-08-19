package tasks;

import gui.AlertType;
import org.xml.sax.SAXException;
import xyz.derkades.plex4j.Server;
import xyz.derkades.plex4j.library.Library;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class RefreshPlexLibrary extends Thread{
    private boolean doAllLibrariesNeedToBeRefreshed = false;
    private final String serverIP = Config.getPlexIp();
    private final String serverToken = Config.getPlexToken();
    private final Server server = new Server(new URI(serverIP).toURL(), serverToken);

    public RefreshPlexLibrary() throws MalformedURLException, URISyntaxException {
    }


    private ArrayList<Library> getWhichLibrariesToRefresh() throws IOException, SAXException {
        final ArrayList<Library> libraries = (ArrayList<Library>) server.getLibraries();
        ArrayList<Library> librariesToRefresh = new ArrayList<>();
        for(Library lib : libraries) {
            if(lib.getLibraryType().equals("movie") || lib.getLibraryType().equals("show")) {
                librariesToRefresh.add(lib);
            }
        }
        doAllLibrariesNeedToBeRefreshed = libraries.equals(librariesToRefresh);
        return librariesToRefresh;
    }

    private void refreshPlex() throws IOException, SAXException {
        ArrayList<Library> libsToRefresh = getWhichLibrariesToRefresh();
        int passedChecks = 0;
        int failedChecks = 0;
        if(!doAllLibrariesNeedToBeRefreshed) {
            if (libsToRefresh.size() == 1) {
                if(server.refreshLibrary(libsToRefresh.get(0).getId()))
                    gui.App.alert(AlertType.INFO, "Succesfully refreshed " + passedChecks +
                            " libraries, failed to refresh " + failedChecks + " libraries.");
            } else {
                for (Library lib : libsToRefresh) {
                    if(server.refreshLibrary(lib.getId())) {
                        passedChecks++;
                    } else {
                        failedChecks++;
                    }
                }
                gui.App.alert(AlertType.INFO, "Succesfully refreshed " + passedChecks +
                        " libraries, failed to refresh " + failedChecks + " libraries.");
            }
        } else {
            if(server.refreshAllLibraries())
                gui.App.alert(AlertType.INFO, "Succesfuly refreshed " + libsToRefresh.size() + " plex libraries.");
        }
    }

    private void refreshAllPlexLibraries() throws URISyntaxException, IOException {
        URL url = new URI(Config.getPlexIp() + "/library/sections/all/refresh?X-Plex-Token=" + Config.getPlexToken()).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int code = connection.getResponseCode();
        if(code == 200) {
            gui.App.alert(AlertType.INFO, "Succesfuly refreshed all Plex libraries.");
        } else if(code == 401) {
            gui.App.alert(AlertType.ERROR, "Provided Plex API token is invalid.");
        }

    }
    @Override
    public void run() {



//        try {
//            refreshPlex();
//        } catch (IOException | SAXException e) {
//            gui.App.alert(AlertType.ERROR, "Plex server is unavaliable.");
//        }
    }
}
