package com.data_provider;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardWatchEventKinds.*;

public class FilteringFileVisitor extends SimpleFileVisitor<Path> {

    private final List<PathMatcher> matchers;
    private Map<WatchKey, Path> keys;
    private List<Path> excludedPaths = new ArrayList<>();
    private WatchService watcher;

    public FilteringFileVisitor(List<String> excludePatterns, Map<WatchKey, Path> keys, WatchService watcher) {
        FileSystem fileSystem = FileSystems.getDefault();
        this.keys = keys;
        this.watcher = watcher;
        matchers = new ArrayList<>(excludePatterns.size());
        for (String patternStr : excludePatterns) {
            matchers.add(fileSystem.getPathMatcher("glob:" + patternStr));
        }
    }

    // Invoke the pattern matching method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {
        isRegisteredFile(file);
        return CONTINUE;
    }

    private boolean isRegisteredFile(Path file) {
        //todo: resolve this method
        return true;
    }

    // Invoke the pattern matching method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {
        if (!isRegistered(dir)) {
            return SKIP_SUBTREE;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        //TODO: add logger
        return CONTINUE;
    }


    // Compares the glob patterns against the file or directory name.
    boolean isRegistered(Path file) {
        Path name = file.getFileName();

        if (name != null &&
                matchers.stream()
                        .anyMatch(matcher -> matcher.matches(file.toAbsolutePath()))) {
            excludedPaths.add(file);
            System.out.println("Skip path: " + name);
            return false;
        }

        register(file, watcher, keys);
        return true;
    }

    private void register(Path dir, WatchService watcher, Map<WatchKey, Path> keys) {
        WatchKey key = null;
        try {
            key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Path prev = keys.get(key);
        if (prev == null) {
            System.out.format("register: %s\n", dir);
        } else {
            if (!dir.equals(prev)) {
                System.out.format("update: %s -> %s\n", prev, dir);
            }
        }
        keys.put(key, dir);
    }
}
