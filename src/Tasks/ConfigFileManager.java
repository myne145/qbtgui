
package Tasks;

import org.json.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigFileManager {
    JSONObject j = new JSONObject(Files.readString(Paths.get("config.json")));
    JSONArray networkVariables = j.getJSONArray("networkVariables");
//    JSONArray appVariables = j.getJSONArray("appVariables");

    public int getLaunchPosX() {
        return launchPosX;
    }

    public int getLaunchPosY() {
        return launchPosY;
    }

    public String getQbittorrentIp() {
        return qbittorrentIp;
    }

    public String getPlexIp() {
        return plexIp;
    }

    public String getPlexToken() {
        return plexToken;
    }

    private int launchPosX;
    private int launchPosY;
    private String qbittorrentIp;
    private String plexIp;
    private String plexToken;

    private Object getKey(JSONArray array, String key) {
        Object value = null;
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            if (item.keySet().contains(key)) {
                value = item.get(key);
                break;
            }
        }

        return value;
    }

    public ConfigFileManager() throws IOException {
        qbittorrentIp = (String) getKey(networkVariables, "qbittorrentIP");
        plexIp = (String) getKey(networkVariables, "plexServerIP");
        plexToken = (String) getKey(networkVariables, "plexToken");

        launchPosX = j.getInt("windowPosX");
        launchPosY = j.getInt("windowPosY");
    }

    public void updateCfg(int x, int y) throws IOException {
        j.put("windowPosX", x);
        j.put("windowPosY", y);

        String s = j.toString(1);
        FileWriter fileWriter = new FileWriter("config.json");
        fileWriter.write(s);
        fileWriter.close();
    }
}