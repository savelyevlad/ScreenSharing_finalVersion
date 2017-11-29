package com.savelyevlad.screensharing.settings;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Arrays;

public class PublicStaticObjects {

    private static InetAddress ip;

    private static int PORT;

    private PublicStaticObjects() { }

    private static String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ScreenSharing/";

    private static void init() {
        File file1 = new File(folderPath.substring(0, folderPath.length() - 1));
        if(!file1.exists()) {
            file1.mkdir();
        }
        File file = new File(folderPath + "Config.txt"); // IP and PORT
        if(!file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("192.168.43.1 50000".getBytes());
                fileOutputStream.close();
                ip = InetAddress.getByName("192.168.43.1");
                PORT = 50000;
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buf = new byte[fileInputStream.available()];
            fileInputStream.read(buf);
            String s = new String(buf);
            String[] kek = s.split(" ");
            ip = InetAddress.getByName(kek[0].substring(0, kek[0].length()));
            PORT = Integer.parseInt(kek[1]);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        init();
    }

    public static InetAddress getIp() {
        return ip;
    }

    public static void setIp(InetAddress ip) {
        PublicStaticObjects.ip = ip;
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setPORT(int PORT) {
        PublicStaticObjects.PORT = PORT;
    }
}
