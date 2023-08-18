
package tasks;

import org.json.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    JSONObject configFile = new JSONObject(Files.readString(Paths.get("config.json")));

    public String getQbittorrentIp() {
        return qbittorrentIp;
    }

    public String getPlexIp() {
        return plexIp;
    }

    public String getPlexToken() {
        return plexToken;
    }

    private final String qbittorrentIp;
    private final String plexIp;
    private final String plexToken;

    public Config() throws IOException {
        qbittorrentIp = configFile.getString("qbittorrentIP");
        plexIp = configFile.getString("plexServerIP");
        plexToken = configFile.getString("plexToken");
    }
}