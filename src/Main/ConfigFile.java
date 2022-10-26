package Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigFile {
    private Scanner input;
    private final File configFile = new File(".\\config.txt");
    private final ArrayList<String> cfg = new ArrayList<>();
    public ConfigFile() {
        try {
            input = new Scanner(configFile);
        } catch(Exception e) {
            Gui.alert(AlertType.ERROR, "Config file not found");
        }
        //Reading config file and adding lines to arraylist
        while(input.hasNextLine()) {
            cfg.add(input.nextLine());
        }

    }
    public String getLoginValue(Authentication authentication) {
        String[] splitLine;
        for(int i = 0; i < cfg.size(); i++) {
             splitLine = cfg.get(i).split("=");
//             if(splitLine.length > 2) {
//                 Gui.alert(AlertType.ERROR, "Values cannot contain \"=\". The program will now exit.");
//                 System.exit(0);
//             }
            if(authentication == Authentication.USERNAME && splitLine[0].equals("username")) {
                return splitLine[1];
            }
            if(authentication == Authentication.PASSWORD && splitLine[0].equals("password")) {
                return splitLine[1];
            }
            if(authentication == Authentication.SERVERIP && splitLine[0].equals("serverIp")) {
                return splitLine[1];
            }
            if(authentication == Authentication.QBTPATH && splitLine[0].equals("qbtPath")) {
                return splitLine[1];
            }
        }
        Gui.alert(AlertType.ERROR, "Invalid value in config file");
        return "invalid_value";
    }
}
