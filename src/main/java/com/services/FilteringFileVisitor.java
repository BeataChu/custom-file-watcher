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

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

/**
 * Defines rules of directories tree traversal
 */
@Getter
@Component
public class FilteringFileVisitor extends SimpleFileVisitor<Path> {

    private static Logger LOG = LoggerFactory.getLogger(FilteringFileVisitor.class);

    private DirMap dirMap;

    @Autowired
    private MatchingService matchingService;

    public FilteringFileVisitor(DirMap dirMap) {
        this.dirMap = dirMap;
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
            LOG.info("Exclude path " + dir);
            return SKIP_SUBTREE;
        }
        dirMap.registerDirectory(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        LOG.error(exc.getMessage());
        return CONTINUE;
    }

}
