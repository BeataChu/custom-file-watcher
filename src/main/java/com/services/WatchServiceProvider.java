package com.services;

import com.models.MirrorPathDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchServiceProvider {

    @Autowired
    MirrorPathDTO pathDataFromJson;

    public static WatchService getNewWatcher() {
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
         return watcher;
    }

}
