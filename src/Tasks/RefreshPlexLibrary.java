package Tasks;

import Gui.AlertType;
import org.xml.sax.SAXException;
import xyz.derkades.plex4j.Server;
import xyz.derkades.plex4j.library.Library;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RefreshPlexLibrary extends Thread{
    private boolean doAllLibrariesNeedToBeRefreshed = false;
    private String serverIP = "http://192.168.2.200:32400";
    private String serverToken = "tEdit_GycX2Y58K7WNmR";
    private int libraryTorefresh;
    private final Server server = new Server(new URL(serverIP), serverToken);
    public RefreshPlexLibrary() throws MalformedURLException {
        //config
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
        for(Library lib : libsToRefresh)
            System.out.println(lib.getTitle());
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
            throw new RuntimeException(e);
        }
    }
}
