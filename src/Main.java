import gui.App;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import tasks.RefreshPlexLibrary;
import com.formdev.flatlaf.FlatDarkLaf;

import tasks.Config;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class Main {

    private static void loginToQbittorrent() throws URISyntaxException, IOException, ProtocolException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Config.getQbittorrentIp() + "/api/v2/auth/login");
        ArrayList<NameValuePair> values = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", "admin"),
                new BasicNameValuePair("password", "adminadmin")));
        httpPost.setEntity(new UrlEncodedFormEntity(values));
        CloseableHttpResponse httpresponse = client.execute(httpPost);

        String cookie = httpresponse.getHeader("set-cookie").toString().split("SID=")[1].split("; ")[0];
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ProtocolException {
        //possible args: -refresh_plex, -nogui
        //load gui only if there's no nogui argument
        Config.initialize();
        loginToQbittorrent();


        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        if(!Arrays.asList(args).contains("-nogui")) {
            ImageIcon iconImg = new ImageIcon(".\\icon_temp.png");
            FlatDarkLaf.setup();
            JFrame window = new JFrame();
            window.add(new App());
            window.getRootPane().setDefaultButton(App.startTheDownload);
            window.setVisible(true);
            window.setLocation(preferences.getInt("WINDOW_LOCATION_X", 0), preferences.getInt("WINDOW_LOCATION_Y", 0));
            window.pack();
            window.setTitle("Qbittorrent WebUI Downloader");
            window.setPreferredSize(new Dimension(1000, 600));
            window.setIconImage(iconImg.getImage());

            window.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {
                    preferences.putInt("WINDOW_LOCATION_X", window.getX());
                    preferences.putInt("WINDOW_LOCATION_Y", window.getY());
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
        if(Arrays.asList(args).contains("-refresh_plex")) {
            new RefreshPlexLibrary().start();
        }
    }
}
