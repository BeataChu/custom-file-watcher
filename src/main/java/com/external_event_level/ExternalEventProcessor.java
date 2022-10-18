package com.external_event_level;

import com.internal_event_level.WatchDir;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.file.StandardWatchEventKinds.*;

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

    public void run() throws IOException {
        while (true) {
            ModificationEvent modificationEvent = queue.poll();
            FolderStructureFitter structureFitter = new FolderStructureFitterImpl();
            for (WatchDir watchDir : watchDirs) {
                if (!watchDir.getRootPath().equals(modificationEvent.getRootPath())) {
                    if (ENTRY_CREATE.equals(modificationEvent.getKind())
                            || (ENTRY_MODIFY.equals(modificationEvent.getKind()))) {
                        structureFitter.copyDirectory(modificationEvent.getRootPath().resolve(modificationEvent.getRelativePath()), watchDir.getRootPath().resolve(modificationEvent.getRelativePath()));
                    }

                    if (ENTRY_DELETE.equals(modificationEvent.getKind())) {
                        structureFitter.deleteDirectory(watchDir.getRootPath().resolve(modificationEvent.getRelativePath()));
                    }
                    //TODO: handle other types of events, like OVERFLOW, and exceptions)
                    throw new IOException("Unexpected type of modification event");

                }
            }
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
