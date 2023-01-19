package com.services;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

@Getter
public class DirMap {

    private Map<WatchKey, Path> watchKeys = new HashMap();

    private WatchService watcher = WatchServiceProvider.getNewWatcher();

    private static Logger LOG = LoggerFactory.getLogger(DirMap.class);

    /**
     * Register the given directory with the WatchService
     */
    public void registerDirectory(Path path) {
        try {
            if (!watchKeys.containsKey(path)) {
                WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                watchKeys.put(key, path);
                LOG.info("Register path: {}", path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
