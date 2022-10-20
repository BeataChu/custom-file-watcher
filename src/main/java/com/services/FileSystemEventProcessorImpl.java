package com.services;

import com.interfaces.FileSystemEventProcessor;
import com.interfaces.FolderStructureProcessor;
import com.models.ModificationEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.*;

@Component
public class FileSystemEventProcessorImpl implements FileSystemEventProcessor {

    public void pushEvent(WatchEvent.Kind kind, Path fullPath, WatchDir watchDir) {
        ModificationEvent modificationEvent = ModificationEvent.ofWatchEvent(kind, fullPath, watchDir);
        queue.offer(modificationEvent);
        for (WatchDir localWatchDir : watchDirs) {
            if (!modificationEvent.getRootPath().equals(localWatchDir.getRootPath())) {
                localWatchDir.getPendingEvents().add(modificationEvent);
            }
        }
    }

    public void processEvents() {
        while (true) {
            ModificationEvent modificationEvent = queue.poll();
            FolderStructureProcessor structureProcessor = new FolderStructureProcessorImpl();
            for (WatchDir watchDir : watchDirs) {
                if (!watchDir.getRootPath().equals(modificationEvent.getRootPath())) {
                    if (ENTRY_CREATE.equals(modificationEvent.getKind())
                            || (ENTRY_MODIFY.equals(modificationEvent.getKind()))) {
                        structureProcessor.copyDirectory(modificationEvent.getRootPath().resolve(modificationEvent.getRelativePath()), watchDir.getRootPath().resolve(modificationEvent.getRelativePath()));
                    }

                    if (ENTRY_DELETE.equals(modificationEvent.getKind())) {
                        structureProcessor.deleteDirectory(watchDir.getRootPath().resolve(modificationEvent.getRelativePath()));
                    }
                    //TODO: handle other types of events, like OVERFLOW, and exceptions)
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
