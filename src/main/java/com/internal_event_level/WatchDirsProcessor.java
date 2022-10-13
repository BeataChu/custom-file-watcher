package com.internal_event_level;

import com.external_event_level.ExternalEventProcessor;

public class WatchDirsProcessor {

    public static void processEvents(ExternalEventProcessor externalEventProcessor) {
        //multithreaded
//        ExecutorService executor = Executors.newFixedThreadPool(watchDirs.size());
//        for (WatchDir watchDir : watchDirs) {
//            executor.execute(() ->
//                    watchDir.processEvents());
//        }

//        single-threaded
        for (WatchDir watchDir : externalEventProcessor.getWatchDirs()) {
            watchDir.processEvents(externalEventProcessor);
        }
    }
}
