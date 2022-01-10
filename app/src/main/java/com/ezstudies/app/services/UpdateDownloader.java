package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Download update from GitHub
 */
public class UpdateDownloader extends Service implements Runnable{
    /**
     * Download url
     */
    private String url;
    /**
     * Download path
     */
    private String path;
    /**
     * Broadcast target
     */
    private String target;

    /**
     * On bind
     * @param intent Intent
     * @return IBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On start command
     * @param intent Intent
     * @param flags Flags
     * @param startId ID
     * @return Success
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        path = intent.getStringExtra("path");
        target = intent.getStringExtra("target");
        Thread thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start
     */
    @Override
    public void run() {
        try {
            File file = new File(path);
            if(file.exists()){
                file.delete();
            }
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(path, false);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            Intent intent1 = new Intent(target);
            intent1.putExtra("path", path);
            sendBroadcast(intent1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
