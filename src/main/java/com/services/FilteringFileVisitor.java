package com.services;

import com.interfaces.MatchingService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Defines rules of directories tree traversal
 */
@Getter
@Component
public class FilteringFileVisitor extends SimpleFileVisitor<Path> {

    private static Logger LOG = LoggerFactory.getLogger(FilteringFileVisitor.class);

    //todo: remove
//    private List<Path> excludedPaths = new ArrayList<>();
    //todo: to hanger
    private Map<WatchKey, Path> watchKeys = new HashMap();
    //todo: to hanger
    @Autowired
    private MatchingService matchingService;
    //todo: to hanger
    private WatchService watcher;
    //todo: to hanger
    public FilteringFileVisitor() {
        watcher = WatchServiceProvider.getNewWatcher();
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

        if (matchingService.shouldBeExcluded(dir)) {
            //todo: to hanger
//            excludedPaths.add(dir);
            System.out.println("Exclude path " + dir);
            return SKIP_SUBTREE;
        }
        //todo: to hanger
        addPathToKeys(dir);
        return CONTINUE;
    }

    //todo: to hanger
    /**
     * Register the given directory with the WatchService
     */
    private void addPathToKeys(Path path) {
        try {
            if (!watchKeys.containsKey(path)) {
                WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                watchKeys.put(key, path);
                LOG.info("register: {}", path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        LOG.error(exc.getMessage());
        return CONTINUE;
    }

}
