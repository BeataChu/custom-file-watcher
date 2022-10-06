package com;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatchDir {
    private final WatchService watcher;

    private SimpleFileVisitor<Path> fileVisitor;

    private Map<WatchKey, Path> keys;

    private List<Path> excludedPaths;

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir, WatchService watcher, SimpleFileVisitor<Path> fileVisitor, Map<WatchKey, Path> keys, List<Path> excludedPaths)  throws IOException {
        this.watcher = watcher;
        this.fileVisitor = fileVisitor;
        this.keys = keys;
        this.excludedPaths = excludedPaths;
        System.out.format("Scanning %s ...\n", dir);
        registerAll(dir);
        System.out.println("Done.");
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
//    public WatchDir(Path dir, SimpleFileVisitor fileVisitor, WatchService watcher) throws IOException {
//        this.watcher = watcher;
//        this.fileVisitor = fileVisitor;
//
//        System.out.format("Scanning %s ...\n", dir);
//        registerAll(dir);
//        System.out.println("Done.");
//
//        // enable trace after initial registration
////        this.trace = true;
//    }

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
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                if (excludedPaths.contains(child) || excludedPaths.stream()
                        .anyMatch(exclPath -> child.startsWith(exclPath))) {
                    System.out.println("****Path should be excluded " + child);
                    continue;
                }

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
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
