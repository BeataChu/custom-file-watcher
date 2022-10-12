package com.internal_event_level;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchDirsProcessor {

    public static void processEvents(Set<WatchDir> watchDirs) {
        //multithreaded
        ExecutorService executor = Executors.newFixedThreadPool(watchDirs.size());
        for (WatchDir watchDir : watchDirs) {
            executor.execute(() ->
                    watchDir.processEvents());
        }

        //single-threaded
//        for (WatchDir watchDir : watchDirs) {
//            watchDir.processEvents();
//        }
    }
}
