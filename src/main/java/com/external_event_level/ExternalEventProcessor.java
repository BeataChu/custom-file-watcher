package com.external_event_level;

import com.internal_event_level.WatchDir;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ExternalEventProcessor {

    private ConcurrentLinkedQueue<ModificationEvent> queue = new ConcurrentLinkedQueue<>();

    private Set<WatchDir> watchDirs = new HashSet<>();

    public void pushEvent(WatchEvent.Kind kind, Path fullPath, WatchDir watchDir) {
        ModificationEvent modificationEvent = ModificationEvent.ofWatchEvent(kind, fullPath, watchDir);
        queue.offer(modificationEvent);
        for (WatchDir localWatchDir : watchDirs) {
            if (!modificationEvent.getRootPath().equals(localWatchDir.getRootPath())) {
                localWatchDir.getPendingEvents().add(modificationEvent);
            }
        }
    }

    public void run() {
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
