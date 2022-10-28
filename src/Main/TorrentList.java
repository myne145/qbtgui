package Main;

import java.util.ArrayList;
import java.util.Scanner;

public class TorrentList {
    private static Unit selectedUnit;
        private String data = """
//                Name:       debian-10.0.0-amd64-xfce-CD-1.iso
//                State:      StalledUpload
//                Hash:       ff3f0ea6f906b4b17b273b3228a53e7e441ec6e7
//                Size:       672 137 216 bytes
//                Progress:   100%
//                DL Speed:   0  B/s
//                UP Speed:   0  B/s
//                Priority:   0
//                Seeds:      0 of 120
//                Leechers:   0 of 184
//                Ratio:      0,26
//                ETA:
//                Category:
//                Tags:
//                Save path:  D:\\Downloads\\
//                Added:      29.07.2019 16:27:38
//                Completion: 29.07.2019 16:29:32
//                Options:
//
//                Name:       manjaro-xfce-17.1.10-stable-x86_64.iso
//                State:      StalledUpload
//                Hash:       3ab528e8324ba52a44c7b1e39010340b3f04c696
//                Size:       1 832 333 312 bytes
//                Progress:   100%
//                DL Speed:   0  B/s
//                UP Speed:   0  B/s
//                Priority:   0
//                Seeds:      0 of 268
//                Leechers:   0 of 18
//                Ratio:      1,27
//                ETA:
//                Category:   Application
//                Tags:
//                Save path:  D:\\Downloads\\
//                Added:      01.06.2018 0:54:49
//                Completion: 01.06.2018 1:00:02
//                Options:
//                
//                Name:       testfile.exe
//                State:      StalledUpload
//                Hash:       3ab528e8324ba52a44c7b1e39010340b3f04c696
//                Size:       1 832 333 312 bytes
//                Progress:   100%
//                DL Speed:   0  B/s
//                UP Speed:   0  B/s
//                Priority:   0
//                Seeds:      0 of 268
//                Leechers:   0 of 18
//                Ratio:      1,27
//                ETA:
//                Category:   Application
//                Tags:
//                Save path:  D:\\Downloads\\
//                Added:      01.06.2018 0:54:49
//                Completion: 01.06.2018 1:00:02
//                Options:
//                
//                Name:       crackedgame.zip
//                State:      StalledUpload
//                Hash:       3ab528e8324ba52a44c7b1e39010340b3f04c696
//                Size:       1 832 333 312 bytes
//                Progress:   100%
//                DL Speed:   0  B/s
//                UP Speed:   0  B/s
//                Priority:   0
//                Seeds:      0 of 268
//                Leechers:   0 of 18
//                Ratio:      1,27
//                ETA:
//                Category:   Application
//                Tags:
//                Save path:  D:\\Downloads\\
//                Added:      01.06.2018 0:54:49
//                Completion: 01.06.2018 1:00:02
//                Options:Name:       manjaro-xfce-17.1.10-stable-x86_64.iso
//                State:      StalledUpload
//                Hash:       3ab528e8324ba52a44c7b1e39010340b3f04c696
//                Size:       1 832 333 312 bytes
//                Progress:   100%
//                DL Speed:   0  B/s
//                UP Speed:   0  B/s
//                Priority:   0
//                Seeds:      0 of 268
//                Leechers:   0 of 18
//                Ratio:      1,27
//                ETA:
//                Category:   Application
//                Tags:
//                Save path:  D:\\Downloads\\
//                Added:      01.06.2018 0:54:49
//                Completion: 01.06.2018 1:00:02
//                Options:
//                """;
    public TorrentList() {

    }

    public void setData(String data) {
        this.data = data;
    }
    private ArrayList<String> getTorrentInfo(TorrentInfo torrentInfo) {
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

    private ArrayList<String> reloadUnits(Unit convertTo) {
        selectedUnit = convertTo;
        ArrayList<String> sizes = getTorrentInfo(TorrentInfo.SIZE);
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
        ArrayList<String> names = getTorrentInfo(TorrentInfo.NAME);
        ArrayList<String> progress = getTorrentInfo(TorrentInfo.PROGRESS);
        ArrayList<String> sizes = getTorrentInfo(TorrentInfo.SIZE);
        ArrayList<String> hashes = getTorrentInfo(TorrentInfo.HASH);
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