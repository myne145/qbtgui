package com.myne145.torrentutils;

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
import java.util.prefs.Preferences;

public class Window extends JFrame{

    private static void loginToQbittorrent() throws URISyntaxException, IOException, ProtocolException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/auth/login");
        ArrayList<NameValuePair> values = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", "admin"),
                new BasicNameValuePair("password", "adminadmin")));
        httpPost.setEntity(new UrlEncodedFormEntity(values));
        CloseableHttpResponse httpresponse = client.execute(httpPost);

        Config.cookie = httpresponse.getHeader("set-cookie").toString().split("SID=")[1].split("; ")[0];
        System.out.println(Config.cookie);
    }

    private static void logoutOfQbittorrent() throws IOException, URISyntaxException {
        URL url = new URI(Config.getQbittorrentIp() + "/api/v2/auth/logout").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        System.out.println(connection.getResponseCode());
    }

    private static void addTorrentsToQbittorrent(File[] torrents) throws IOException {
        String cookie = "SID=your_sid";

        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create the multipart entity
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("torrents", new File("INSERT_TORRENT_HERE"), ContentType.APPLICATION_OCTET_STREAM, "8f18036b7a205c9347cb84a253975e12f7adddf2.torrent")
//                .addBinaryBody("torrents", new File("UFS.torrent"), ContentType.APPLICATION_OCTET_STREAM, "UFS.torrent")
                .setBoundary("-------------------------acebdf13572468")
                .build();

        // Create the POST request
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/torrents/add");
        httpPost.setHeader("User-Agent", "Fiddler");
        httpPost.setHeader("Cookie", "SID=" + Config.cookie);
        httpPost.setEntity(entity);

        // Execute the request and get the response
        CloseableHttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getCode();
        System.out.println("Response Code: " + statusCode);

        // Cleanup
        response.close();
        httpClient.close();
        
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
        loginToQbittorrent();



        if(!Arrays.asList(args).contains("-nogui")) {
            FlatDarkLaf.setup();
            new Window();
        }
        if(Arrays.asList(args).contains("-refresh_plex")) {
            new RefreshPlexLibrary().start();
        }
    }
}
