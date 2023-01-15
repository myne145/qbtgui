package Tasks;

import Gui.AlertType;
import org.xml.sax.SAXException;
import xyz.derkades.plex4j.Server;
import xyz.derkades.plex4j.library.Library;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RefreshPlexLibrary extends Thread{
    private boolean doAllLibrariesNeedToBeRefreshed = false;
    private final ConfigFileManager config = new ConfigFileManager();
    private final String serverIP = config.getPlexIp();
    private final String serverToken = config.getPlexToken();
    private final Server server = new Server(new URL(serverIP), serverToken);
    public RefreshPlexLibrary() throws IOException {

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
                    Gui.App.alert(AlertType.INFO, "Succesfully refreshed " + passedChecks +
                            " libraries, failed to refresh " + failedChecks + " libraries.");
            } else {
                for (Library lib : libsToRefresh) {
                    if(server.refreshLibrary(lib.getId())) {
                        passedChecks++;
                    } else {
                        failedChecks++;
                    }
                }
                Gui.App.alert(AlertType.INFO, "Succesfully refreshed " + passedChecks +
                        " libraries, failed to refresh " + failedChecks + " libraries.");
            }
        } else {
            if(server.refreshAllLibraries())
                Gui.App.alert(AlertType.INFO, "Succesfuly refreshed " + libsToRefresh.size() + " plex libraries.");
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            refreshPlex();
        } catch (IOException | SAXException e) {
            Gui.App.alert(AlertType.ERROR, "Plex server is unavaliable.");
        }
    }
}
