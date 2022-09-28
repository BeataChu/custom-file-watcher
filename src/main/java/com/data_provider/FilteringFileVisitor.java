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
    private final WatchService watcher;
    private Map<WatchKey, Path> keys;
    private List<Path> excludedPaths;

    public FilteringFileVisitor(List<String> excludePatterns, WatchService watcher, Map<WatchKey, Path> keys, List<Path> excludedPaths) {
        this.watcher = watcher;
        this.keys = keys;
        this.excludedPaths = excludedPaths;
        FileSystem fileSystem = FileSystems.getDefault();
        matchers = new ArrayList<>(excludePatterns.size());
        for (String patternStr : excludePatterns) {
            matchers.add(fileSystem.getPathMatcher("glob:" + patternStr));
        }
    }

    // TODO: Compares the glob patterns against the file or directory name.
//    private boolean shouldBeExcluded(Path file) {
//        Path name = file.getFileName();
//        if (name != null && matchers.stream().anyMatch(matcher -> matcher.matches(name))) {
//            return false;
//        }
//        return true;
//    }

    private boolean shouldBeExcluded(Path file) {
        Path name = file.getFileName();
        if (name != null && (file.endsWith("bin") || file.endsWith("obj") || file.endsWith("appsettings.json") || file.endsWith(".lic"))) {
            excludedPaths.add(file);
            System.out.println("******Exclude path " + file);
            return true;
        }
        return false;
    }

    // Invoke the pattern matching method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {
        return CONTINUE;
    }

    // Invoke the pattern matching method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {
        if (shouldBeExcluded(dir)) {
            excludedPaths.add(dir);
            return SKIP_SUBTREE;
        }
        register(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        //TODO: add logger
        return CONTINUE;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) {
        try {
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            Path prev = keys.get(key);
            if (prev == null) {
                if (!excludedPaths.contains(prev)) {
                    System.out.format("register: %s\n", dir);
                }
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
            keys.put(key, dir);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
