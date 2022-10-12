package com.internal_event_level;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchServiceProvider {

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
