package Tasks;

import Gui.AlertType;
import Gui.App;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigFileManager {
    public int launchPosX;
    public int launchPosY;
    public int unitIndex;

    String content;
    JSONObject j;

    public ConfigFileManager() throws IOException {
        try {
            content = Files.readString(Paths.get("config.json"));
             j = new JSONObject(content);
        } catch (Exception e) {
            App.alert(AlertType.ERROR, "Config File not found " + e.getLocalizedMessage());
        }
        this.launchPosX = j.getInt("lastLaunchedPosX");
        this.launchPosY = j.getInt("lastLaunchedPosY");
        this.unitIndex = j.getInt("lastSelectedUnit");
    }

    public void saveValues(int x, int y) throws IOException {
        j.put("lastSelectedUnit", App.unitIndex);
        j.put("lastLaunchedPosX", x);
        j.put("lastLaunchedPosY", y);
        PrintWriter writer = new PrintWriter("config.json", StandardCharsets.UTF_8);
        writer.println(j);
        writer.close();
    }

    public void saveValues() throws IOException {
        j.put("lastSelectedUnit", App.unitIndex);
        j.put("lastLaunchedPosX", 200);
        j.put("lastLaunchedPosY", 200);
        PrintWriter writer = new PrintWriter("config.json", StandardCharsets.UTF_8);
        writer.println(j);
        writer.close();
    }
}