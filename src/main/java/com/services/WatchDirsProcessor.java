package com.services;

public class WatchDirsProcessor {

    public static void processEvents(FileSystemEventProcessorImpl externalEventProcessor) {
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
