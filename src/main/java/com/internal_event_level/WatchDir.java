package com.internal_event_level;

import com.external_event_level.ExternalEventProcessor;
import com.external_event_level.ModificationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Creates entity that is going to listen to modifications and then deal with it depending on the event type
 */
@Getter
@RequiredArgsConstructor
public class WatchDir {

    private static Logger LOG = LoggerFactory.getLogger(WatchDir.class);

    private final Path rootPath;

    private WatchService watcher;

    private SimpleFileVisitor<Path> fileVisitor;

    private Map<WatchKey, Path> keys;

    private List<Path> excludedPaths;

    private List<ModificationEvent> pendingEvents = new ArrayList<>();

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir,
                    WatchService watcher,
                    SimpleFileVisitor<Path> fileVisitor,
                    Map<WatchKey, Path> keys,
                    List<Path> excludedPaths) {

        this.rootPath = dir;
        this.watcher = watcher;
        this.fileVisitor = fileVisitor;
        this.keys = keys;
        this.excludedPaths = excludedPaths;
        LOG.info("Scanning %s ...\n", dir);
        registerAll(dir);
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
    private void registerAll(final Path start) {
        try {
            Files.walkFileTree(start, fileVisitor);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents(ExternalEventProcessor externalEventProcessor) {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                x.printStackTrace();
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                LOG.error("Watch key not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    LOG.info("Watch key overflow");
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (excludedPaths.stream().anyMatch(exclPath -> child.startsWith(exclPath))) {
                    LOG.info("Event is ignored for path " + child);
                    continue;
                }

                // print out event
                LOG.info("{}: {}", event.kind().name(), child);

                externalEventProcessor.pushEvent(event.kind(), child, this);
                // if directory is created and is being watched recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}
