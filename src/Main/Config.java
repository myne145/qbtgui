package Main;

import org.json.*;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    public int lastSelectedSpamMode;
    public int lastSelectedEmoteSlot;
    public int launchPosX;
    public int launchPosY;
    public int emotePages;
    public int resX;
    public int resY;
    public String displayScale;
    public int windowSizeX;
    public int windowSizeY;

    String content = Files.readString(Paths.get("config.json"));;
    JSONObject j = new JSONObject(content);

    public Config() throws IOException {

        this.lastSelectedSpamMode = j.getInt("lastSelectedSpamMode");
        this.lastSelectedEmoteSlot = j.getInt("lastSelectedEmoteSlot");
        this.launchPosX = j.getInt("lastLaunchedPosX");
        this.launchPosY = j.getInt("lastLaunchedPosY");
        this.emotePages = j.getInt("emotePages");
        this.resX = j.getInt("resolutionX");
        this.resY = j.getInt("resolutionY");
        this.displayScale = j.getString("displayScale");
        this.windowSizeX = j.getInt("windowSizeX");
        this.windowSizeY = j.getInt("windowSizeY");
    }

    public void saveValues(int x, int y, Dimension windowSize) throws IOException {
        j.put("lastLaunchedPosX", x);
        j.put("lastLaunchedPosY", y);
        j.put("windowSizeX", windowSize.width);
        j.put("windowSizeY", windowSize.height);
        //fuck me
        PrintWriter writer = new PrintWriter("config.json", "UTF-8");
        writer.println(j);
        writer.close();
        //fileWriter.write(j.toString());
    }

    public void saveValues() {
        this.launchPosX = j.getInt("lastLaunchedPosX");
        this.launchPosY = j.getInt("lastLaunchedPosY");
    }

}