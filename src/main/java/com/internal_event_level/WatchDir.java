package com.internal_event_level;

import com.external_event_level.ModificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Creates entity that is going to listen to modifications and then deal with it depending on the event type
 */
public class WatchDir {

    private static Logger LOG = LoggerFactory.getLogger(WatchDir.class);

    private WatchService watcher;

    private SimpleFileVisitor<Path> fileVisitor;

    private Map<WatchKey, Path> keys;

    private List<Path> excludedPaths;

    private List<ModificationEvent> pendingEvents;

    public WatchService getWatcher() {
        return watcher;
    }

    public SimpleFileVisitor<Path> getFileVisitor() {
        return fileVisitor;
    }

    public Map<WatchKey, Path> getKeys() {
        return keys;
    }

    public List<Path> getExcludedPaths() {
        return excludedPaths;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir,
                    WatchService watcher,
                    SimpleFileVisitor<Path> fileVisitor,
                    Map<WatchKey, Path> keys,
                    List<Path> excludedPaths)  throws IOException {
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
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, fileVisitor);
    }


    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
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
                LOG.info("########## Watch key not recognized!!");
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

                if (excludedPaths.contains(child) || excludedPaths.stream()
                        .anyMatch(exclPath -> child.startsWith(exclPath))) {
                    LOG.info("Event is ignored for path " + child);
                    continue;
                }

                // print out event
                LOG.info("{}: {}", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readable
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
