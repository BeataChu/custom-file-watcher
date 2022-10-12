package com.external_event_level;

import com.internal_event_level.WatchDir;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ExternalEventProcessor {

    private ConcurrentLinkedQueue<ModificationEvent> queue = new ConcurrentLinkedQueue<>();

    private Set<WatchDir> watchDirs = new HashSet<>();

    void pushEvent(ModificationEvent event) {
        queue.add(event);
        //iterate watchdirs, if event.rootPath != watchdir.rootPath - add to watchdir.pendingevents
    }

    void run() {
        while (true) {
            ModificationEvent event = queue.poll();
            // IO operations
        }
    }

    public boolean addWatchDir(WatchDir watchDir) {
        return watchDirs.add(watchDir);
    }

    public Set<WatchDir> getWatchDirs() {
        return watchDirs;
    }
}
