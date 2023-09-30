package com.myne145.torrentutils;

import com.formdev.flatlaf.IntelliJTheme;
import com.myne145.torrentutils.gui.App;
import com.myne145.torrentutils.tasks.RefreshPlexLibrary;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import com.formdev.flatlaf.FlatDarkLaf;

import com.myne145.torrentutils.tasks.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class Window extends JFrame{

    private static boolean loginToQbittorrent() throws URISyntaxException, IOException, ProtocolException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/auth/login");
        ArrayList<NameValuePair> values = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", "admin"),
                new BasicNameValuePair("password", "adminadmin")));
        httpPost.setEntity(new UrlEncodedFormEntity(values));
        CloseableHttpResponse httpresponse = client.execute(httpPost);

        Config.cookie = httpresponse.getHeader("set-cookie").toString().split("SID=")[1].split("; ")[0];
        System.out.println(Config.cookie);
        return httpresponse.getCode() == 200;
    }

    private static void logoutOfQbittorrent() throws IOException, URISyntaxException {
        URL url = new URI(Config.getQbittorrentIp() + "/api/v2/auth/logout").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
    }


    private static boolean addTorrentsToQbittorrent(List<File> torrents, List<String> magnetLinks) throws IOException {
        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create the multipart entity
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setBoundary("-------------------------acebdf13572468");

        //Magnet links
        StringBuilder magnets = new StringBuilder();
        if(magnetLinks != null) {
            for (String s : magnetLinks)
                magnets.append(s).append("\n");
            entityBuilder.addTextBody("urls", magnets.toString());
        }
        System.out.println(magnets);


        // Add torrent files
        if (torrents != null) {
            for (File torrent : torrents) {
                entityBuilder.addBinaryBody("torrents", torrent, ContentType.APPLICATION_OCTET_STREAM, torrent.getName());
            }
        }


        HttpEntity entity = entityBuilder.build();

        // Create the POST request
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/torrents/add");
        httpPost.setHeader("User-Agent", "Fiddler");
        httpPost.setHeader("Cookie", "SID=" + Config.cookie);
        httpPost.setEntity(entity);

        // Execute the request and get the response
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // Cleanup
        response.close();
        httpClient.close();

        // Check the response code
        return response.getCode() == 200;
    }


    private Window() {
        Preferences preferences = Preferences.userNodeForPackage(Window.class);
        ImageIcon iconImg = new ImageIcon(".\\icon_temp.png");
        this.add(new App());
        this.getRootPane().setDefaultButton(App.startTheDownload);
        this.setVisible(true);
        this.setLocation(preferences.getInt("WINDOW_LOCATION_X", 0), preferences.getInt("WINDOW_LOCATION_Y", 0));
        this.pack();
        this.setTitle("Qbittorrent WebUI Downloader");
        this.setPreferredSize(new Dimension(1000, 600));
        this.setIconImage(iconImg.getImage());

        JFrame.setDefaultLookAndFeelDecorated(true);
        this.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(51, 51, 52));
        this.getRootPane().putClientProperty("JRootPane.titleBarForeground", new Color(204, 204, 204));
//        this.getRootPane().putClientProperty("MenuItem.background", new Color(253, 0, 0));
//        this.getRootPane().putClientProperty("MenuItem.opaque", true);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                preferences.putInt("WINDOW_LOCATION_X", getX());
                preferences.putInt("WINDOW_LOCATION_Y", getY());
                try {
                    logoutOfQbittorrent();
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(1);
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ProtocolException {
        //possible args: -refresh_plex, -nogui
        //load com.myne145.gui only if there's no nogui argument
        Config.initialize();
        System.out.println(loginToQbittorrent());


        if(!Arrays.asList(args).contains("-nogui")) {
            InputStream inputStream = new FileInputStream("src/com/myne145/torrentutils/resources/DarkFlatTheme/DarkFlatTheme.json");
            IntelliJTheme.setup(inputStream);
            new Window();
        }
        if(Arrays.asList(args).contains("-refresh_plex")) {
            new RefreshPlexLibrary().start();
        }
    }
}
