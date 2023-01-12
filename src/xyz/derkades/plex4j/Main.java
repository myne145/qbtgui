package xyz.derkades.plex4j;

import org.xml.sax.SAXException;
import xyz.derkades.plex4j.library.Library;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {
        Server server = new Server(new URL("http://192.168.2.19:8080"), "tEdit_GycX2Y58K7WNmR");
        //server.refreshLibrary(7);
//        if(server.testConnection() != null) {
//            System.out.println("Cannot connect to plex server: \n" + server.testConnection());
//        }

        System.out.println(server.testConnection());
    }
}
