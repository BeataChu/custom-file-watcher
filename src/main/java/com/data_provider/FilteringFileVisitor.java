package com.data_provider;

import com.data_provider.dao.MirrorPathData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardWatchEventKinds.*;

@Component
public class FilteringFileVisitor extends SimpleFileVisitor<Path> {

    private List<PathMatcher> matchers;
    @Autowired
    private WatchService watcher;

    private Map<WatchKey, Path> keys;
    @Autowired
    private List<Path> excludedPaths;
    @Autowired
    private MirrorPathData pathDataFromJson;

    public void run() {
        System.out.println(pathDataFromJson);

        FileSystem fileSystem = FileSystems.getDefault();
        matchers = new ArrayList<>(pathDataFromJson
                .getExclude()
                .size());
        for (String patternStr : pathDataFromJson.getExclude()) {
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
