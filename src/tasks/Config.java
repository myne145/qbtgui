
package tasks;

import org.json.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    public static String getQbittorrentIp() {
        return qbittorrentIp;
    }

    public static String getPlexIp() {
        return plexIp;
    }

    public static String getPlexToken() {
        return plexToken;
    }

    private static String qbittorrentIp;
    private static String plexIp;
    private static String plexToken;

    public static void initialize() throws IOException {
        JSONObject configFile = new JSONObject(Files.readString(Paths.get("config.json")));
        qbittorrentIp = configFile.getString("qbittorrentIP");
        plexIp = configFile.getString("plexServerIP");
        plexToken = configFile.getString("plexToken");
    }
}