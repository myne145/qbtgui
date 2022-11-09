package Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;

public class TorrentList {
    private static Unit selectedUnit;

    String data = """
              {
                "hash": "ff3f0ea6f906b4b17b273b3228a53e7e441ec6e7",
                "name": "debian-10.0.0-amd64-xfce-CD-1.iso",
                "magnet_uri": "magnet:?xt=urn:btih:ff3f0ea6f906b4b17b273b3228a53e7e441ec6e7&dn=debian-10.0.0-amd64-xfce-CD-1.iso&tr=http%3a%2f%2fbttracker.debian.org%3a6969%2fannounce",
                "size": 672137216,
                "progress": 1.0,
                "dlspeed": 0,
                "upspeed": 0,
                "priority": 0,
                "num_seeds": 0,
                "num_complete": 152,
                "num_leechs": 1,
                "num_incomplete": 191,
                "ratio": 0.26183559930694206,
                "eta": "100.00:00:00",
                "state": "stalledUP",
                "seq_dl": false,
                "f_l_piece_prio": false,
                "category": "",
                "tags": "",
                "super_seeding": false,
                "force_start": false,
                "save_path": "D:\\\\Downloads\\\\",
                "added_on": 1564406858,
                "completion_on": 1564406972,
                "tracker": "http://bttracker.debian.org:6969/announce",
                "dl_limit": null,
                "up_limit": null,
                "downloaded": 679284022,
                "uploaded": 177860739,
                "downloaded_session": 0,
                "uploaded_session": 180224,
                "amount_left": 0,
                "completed": 672137216,
                "ratio_limit": -2.0,
                "seen_complete": 1566310784,
                "last_activity": 1566310804,
                "time_active": "8.19:07:29",
                "auto_tmm": false,
                "total_size": 672137216,
                "max_ratio": -1,
                "max_seeding_time": -1,
                "seeding_time_limit": -2
              },
              {
                "hash": "3ab528e8324ba52a44c7b1e39010340b3f04c696",
                "name": "manjaro-xfce-17.1.10-stable-x86_64.iso",
                "magnet_uri": "magnet:?xt=urn:btih:3ab528e8324ba52a44c7b1e39010340b3f04c696&dn=manjaro-xfce-17.1.10-stable-x86_64.iso&tr=udp%3a%2f%2fmirror.strits.dk%3a6969&ws=http%3a%2f%2fonet.dl.osdn.jp%2fstorage%2fg%2fm%2fma%2fmanjaro%2fxfce%2f17.1.10%2fmanjaro-xfce-17.1.10-stable-x86_64.iso",
                "size": 1832333312,
                "progress": 1.0,
                "dlspeed": 0,
                "upspeed": 0,
                "priority": 0,
                "num_seeds": 0,
                "num_complete": 268,
                "num_leechs": 1,
                "num_incomplete": 18,
                "ratio": 1.2664005520872796,
                "eta": "100.00:00:00",
                "state": "stalledUP",
                "seq_dl": false,
                "f_l_piece_prio": false,
                "category": "Application",
                "tags": "",
                "super_seeding": false,
                "force_start": false,
                "save_path": "D:\\\\Downloads\\\\",
                "added_on": 1527803689,
                "completion_on": 1527804002,
                "tracker": "",
                "dl_limit": null,
                "up_limit": null,
                "downloaded": 1847868693,
                "uploaded": 2340141933,
                "downloaded_session": 0,
                "uploaded_session": 0,
                "amount_left": 0,
                "completed": 1832333312,
                "ratio_limit": -2.0,
                "seen_complete": 1529004204,
                "last_activity": 1566311221,
                "time_active": "13.22:07:20",
                "auto_tmm": false,
                "total_size": 1832333312,
                "max_ratio": -1,
                "max_seeding_time": -1,
                "seeding_time_limit": -2
              }""";

    public TorrentList() {

    }

    public void setData(String data) {
        this.data = data;
    }
    private ArrayList<String> processJsonData(TorrentInfo torrentInfo) {
        Scanner sc = new Scanner(data);
        ArrayList<String> arr = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        //add new line to array
        while(sc.hasNextLine()) {
            arr.add(sc.nextLine());
        }
        //remove spaces from array elements
        arr.replaceAll(s -> s.replaceAll("\\s", ""));

        //check for occurences and add them to result array
        for(int i = 0; i < arr.size(); i++) {
            //System.out.println(arr.get(i));
            if(arr.get(i).split(":")[0].equals("Name") && torrentInfo == TorrentInfo.NAME) {
                result.add(arr.get(i).split(":")[1]);
            }
            if(arr.get(i).split(":")[0].equals("Size") && torrentInfo == TorrentInfo.SIZE) {
                String temp = arr.get(i).split(":")[1];
                result.add(temp.split("bytes")[0]); //ALWAYS returns bytes
                //result.add(arr.get(i).split(":")[1]);
            }
            if(arr.get(i).split(":")[0].equals("Hash") && torrentInfo == TorrentInfo.HASH) {
                result.add(arr.get(i).split(":")[1]);
            }
            if(arr.get(i).split(":")[0].equals("Progress") && torrentInfo == TorrentInfo.PROGRESS) {
                result.add(arr.get(i).split(":")[1]);
            }
        }


        return result;
    }

    public ArrayList<String> processJsonData(TorrentInfo torrentInfo) {
        String[] dataSplit = data.split("},");
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < dataSplit.length; i++) {
            if(i != dataSplit.length - 1)
                dataSplit[i] = dataSplit[i] + "}";
            System.out.println(dataSplit[i]);
        }
        for(int i = 0; i < dataSplit.length; i++) {
            JSONObject jsonObject = new JSONObject(dataSplit[i]);
            System.out.println(jsonObject.getString("name"));
            System.out.println(jsonObject.getInt("size"));
            if(torrentInfo == TorrentInfo.NAME) {
                result.add(jsonObject.getString("name"));
            }
            if(torrentInfo == TorrentInfo.SIZE) {
                result.add(String.valueOf(jsonObject.getLong("size"))); //ALWAYS returns bytes
            }
            if(torrentInfo == TorrentInfo.HASH) {
                result.add(jsonObject.getString("hash"));
            }
            if(torrentInfo == TorrentInfo.PROGRESS) {
                result.add(jsonObject.getString("progress"));
            }
        }
        return result;

    }

    private ArrayList<String> reloadUnits(Unit convertTo) {
        selectedUnit = convertTo;
        ArrayList<String> sizes = processJsonData(TorrentInfo.SIZE);
        for(int i = 0; i < sizes.size(); i++) {
            sizes.set(i, String.valueOf(convertUnits(convertTo, Double.parseDouble(sizes.get(i)))));
        }
        return sizes;
    }
    private static double convertUnits(Unit convertTo, double value) {
        //TODO: convertFrom certain unit to make it possible for user to change preffered unit (too fucking lazy to implement that now)
        double result;
        switch(convertTo) {
            //case BYTE -> result = value;
            case KILOBYTE -> result = value / 1024D;
            case MEGABYTE -> result = value / 1048576D;
            case GIGABYTE -> result = value / 1073741824D;
            //case TERABYTE -> result = value / 1099511627776D;
            default -> result = -1D;
        }
        return result;
    }
    public ArrayList<String> getInfoToAdd(TorrentInfo torrentInfo, Unit sizeUnit) {
        //jframe table here
        ArrayList<String> names = processJsonData(TorrentInfo.NAME);
        ArrayList<String> progress = processJsonData(TorrentInfo.PROGRESS);
        ArrayList<String> sizes = processJsonData(TorrentInfo.SIZE);
        ArrayList<String> hashes = processJsonData(TorrentInfo.HASH);
        StringBuilder line = new StringBuilder();
        String unitName = "ERROR";
        for(int i = 0; i < sizes.size(); i++) {
            sizes.set(i, String.valueOf(convertUnits(sizeUnit, Double.parseDouble(sizes.get(i)))));
        }
        switch(sizeUnit) {
            case BYTE -> unitName = "b";
            case KILOBYTE -> unitName = "kb";
            case MEGABYTE -> unitName = "mb";
            case GIGABYTE -> unitName = "gb";
        }
        ArrayList<String> realSizeList = new ArrayList<>();
        for(int i = 0; i < names.size(); i++) { //very unsafe may throw an outofbounds exception //TODO: fix this
            String[] s = sizes.get(i).split("\\.");
            if(s[1].charAt(0) == '0')
                realSizeList.add(s[0] + unitName);
            else
                realSizeList.add(s[0] + "," + s[1].charAt(0) + unitName);
            }
        if(torrentInfo == TorrentInfo.NAME)
            return names;
        if(torrentInfo == TorrentInfo.SIZE)
            return realSizeList;
        if(torrentInfo == TorrentInfo.HASH)
            return hashes;
        if(torrentInfo == TorrentInfo.PROGRESS)
            return progress;

        return new ArrayList<>();
    }
}