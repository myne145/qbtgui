package Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;

public class TorrentList {
    private static Unit selectedUnit;

//    String data = """
//              {
//                "hash": "ff3f0ea6f906b4b17b273b3228a53e7e441ec6e7",
//                "name": "debian-10.0.0-amd64-xfce-CD-1.iso",
//                "magnet_uri": "magnet:?xt=urn:btih:ff3f0ea6f906b4b17b273b3228a53e7e441ec6e7&dn=debian-10.0.0-amd64-xfce-CD-1.iso&tr=http%3a%2f%2fbttracker.debian.org%3a6969%2fannounce",
//                "size": 672137216,
//                "progress": 1.0,
//                "dlspeed": 0,
//                "upspeed": 0,
//                "priority": 0,
//                "num_seeds": 0,
//                "num_complete": 152,
//                "num_leechs": 1,
//                "num_incomplete": 191,
//                "ratio": 0.26183559930694206,
//                "eta": "100.00:00:00",
//                "state": "stalledUP",
//                "seq_dl": false,
//                "f_l_piece_prio": false,
//                "category": "",
//                "tags": "",
//                "super_seeding": false,
//                "force_start": false,
//                "save_path": "D:\\\\Downloads\\\\",
//                "added_on": 1564406858,
//                "completion_on": 1564406972,
//                "tracker": "http://bttracker.debian.org:6969/announce",
//                "dl_limit": null,
//                "up_limit": null,
//                "downloaded": 679284022,
//                "uploaded": 177860739,
//                "downloaded_session": 0,
//                "uploaded_session": 180224,
//                "amount_left": 0,
//                "completed": 672137216,
//                "ratio_limit": -2.0,
//                "seen_complete": 1566310784,
//                "last_activity": 1566310804,
//                "time_active": "8.19:07:29",
//                "auto_tmm": false,
//                "total_size": 672137216,
//                "max_ratio": -1,
//                "max_seeding_time": -1,
//                "seeding_time_limit": -2
//              },
//              {
//                "hash": "3ab528e8324ba52a44c7b1e39010340b3f04c696",
//                "name": "manjaro-xfce-17.1.10-stable-x86_64.iso",
//                "magnet_uri": "magnet:?xt=urn:btih:3ab528e8324ba52a44c7b1e39010340b3f04c696&dn=manjaro-xfce-17.1.10-stable-x86_64.iso&tr=udp%3a%2f%2fmirror.strits.dk%3a6969&ws=http%3a%2f%2fonet.dl.osdn.jp%2fstorage%2fg%2fm%2fma%2fmanjaro%2fxfce%2f17.1.10%2fmanjaro-xfce-17.1.10-stable-x86_64.iso",
//                "size": 1832333312,
//                "progress": 1.0,
//                "dlspeed": 0,
//                "upspeed": 0,
//                "priority": 0,
//                "num_seeds": 0,
//                "num_complete": 268,
//                "num_leechs": 1,
//                "num_incomplete": 18,
//                "ratio": 1.2664005520872796,
//                "eta": "100.00:00:00",
//                "state": "stalledUP",
//                "seq_dl": false,
//                "f_l_piece_prio": false,
//                "category": "Application",
//                "tags": "",
//                "super_seeding": false,
//                "force_start": false,
//                "save_path": "D:\\\\Downloads\\\\",
//                "added_on": 1527803689,
//                "completion_on": 1527804002,
//                "tracker": "",
//                "dl_limit": null,
//                "up_limit": null,
//                "downloaded": 1847868693,
//                "uploaded": 2340141933,
//                "downloaded_session": 0,
//                "uploaded_session": 0,
//                "amount_left": 0,
//                "completed": 1832333312,
//                "ratio_limit": -2.0,
//                "seen_complete": 1529004204,
//                "last_activity": 1566311221,
//                "time_active": "13.22:07:20",
//                "auto_tmm": false,
//                "total_size": 1832333312,
//                "max_ratio": -1,
//                "max_seeding_time": -1,
//                "seeding_time_limit": -2
//              }""";
    String data;

    public TorrentList() {

    }

    public void setData(String data) {
        this.data = data;
    }


}