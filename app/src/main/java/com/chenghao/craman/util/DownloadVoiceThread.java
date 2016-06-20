package com.chenghao.craman.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Hao on 16/6/21.
 */
public class DownloadVoiceThread extends Thread {
    private Context context;
    private String urlStr;             //下载链接
    private String filePath;           //文件全路径，包含文件名以及各式
    private String fileName;           //文件名
    private File f_voice;
    private MediaPlayer player;

    public DownloadVoiceThread(Context context, String urlStr, MediaPlayer player) {
        this.context = context;
        this.urlStr = urlStr;
        this.fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        Log.i("File Name", fileName);
        filePath = "data/data/com.chenghao.craman/voices/" + fileName;
        f_voice = new File(filePath);
        this.player = player;
    }

    public void run() {
        OutputStream output = null;
        if (!f_voice.exists()) {
            try {
                File f_voice = new File(filePath);
                Log.i("文件地址", filePath);
                URLConnection conn = new URL(urlStr).openConnection();
                InputStream input = conn.getInputStream();
                int len = conn.getContentLength();
                if (f_voice.exists()) {
                    System.out.println("exits");
                } else {
                    // String dir = SDCard + "/" + path;
//                    File dir = new File("data/data/com.chenghao.craman/voices/");
//                    if (!dir.exists()) dir.mkdir();
//                    new File("data/data/com.chenghao.craman/voices/").mkdir();// 新建文件夹
                    f_voice.createNewFile();// 新建文件
                    output = new FileOutputStream(f_voice);
                    // 读取大文件

                    byte[] voice_bytes = new byte[1024];
                    int len1 = -1;
                    while ((len1 = input.read(voice_bytes)) != -1) {
                        output.write(voice_bytes, 0, len1);
                        output.flush();
                    }
                    System.out.println("success");
                    output.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                //return;
            }
        }

        try {
            player.reset();
            player.setDataSource(f_voice.getAbsolutePath());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
