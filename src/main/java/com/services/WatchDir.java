package com.services;

import com.interfaces.FileSystemEventProcessor;
import com.interfaces.MatchingService;
import com.models.ModificationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Creates entity that is going to listen to modifications and then deal with it depending on the event type
 */
@Getter
@RequiredArgsConstructor
public class WatchDir {

    @Autowired
    MatchingService matcher;

    private static Logger LOG = LoggerFactory.getLogger(WatchDir.class);

    private final Path rootPath;

    private FilteringFileVisitor fileVisitor;

    private List<ModificationEvent> pendingEvents = new ArrayList<>();

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir, FilteringFileVisitor fileVisitor) {

        this.rootPath = dir;
        this.fileVisitor = fileVisitor;
    }


    public void scanDirs(Path startDir) {

        LOG.info("Scanning %s ...\n", startDir);

        registerDirectories(startDir);
        LOG.info("Done.");
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerDirectories(final Path start) {
        try {
            Files.walkFileTree(start, fileVisitor);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents(FileSystemEventProcessorImpl eventProcessor) {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            DirMap dirMap = fileVisitor.getDirMap();
            try {

                key = dirMap.getWatcher().take();

            } catch (InterruptedException x) {
                x.printStackTrace();
                return;
            }

            if (!dirMap.getWatchKeys().containsKey(key)) {
                LOG.error("Watch key not recognized!!");
                continue;
            }

            processEventsForTheKey(dirMap, key, eventProcessor);

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                dirMap.getWatchKeys().remove(key);

                // all directories are inaccessible
                if (dirMap.getWatchKeys().isEmpty()) {
                    break;
                }
            }
        }
    }


    private void processEventsForTheKey(DirMap dirMap, WatchKey key, FileSystemEventProcessor eventProcessor) {
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind kind = event.kind();

            // TBD - provide example of how OVERFLOW event is handled
            if (kind == OVERFLOW) {
                LOG.info("Watch key overflow");
                continue;
            }

            // Context for directory entry event is the file name of entry
            Path dir = dirMap.getWatchKeys().get(key);
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);


            if (matcher.shouldBeExcluded(child)) {
                LOG.info("Event is ignored for path " + child);
                continue;
            }

            // print out event
            LOG.info("{}: {}", event.kind().name(), child);

            eventProcessor.pushEvent(event.kind(), child, this);
            // if directory is created and is being watched recursively, then
            // register it and its sub-directories
            if (kind == ENTRY_CREATE) {
                if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                    registerDirectories(child);
                }
            }
        }
    }


}
