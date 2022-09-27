package com.data_provider;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class FilteringFileVisitor extends SimpleFileVisitor<Path> {

    private final List<PathMatcher> matchers;

    public FilteringFileVisitor(List<String> excludePatterns) {
        FileSystem fileSystem = FileSystems.getDefault();
        matchers = new ArrayList<>(excludePatterns.size());
        for (String patternStr : excludePatterns) {
            matchers.add(fileSystem.getPathMatcher("glob:" + patternStr));
        }
    }

    // Compares the glob patterns against the file or directory name.
    boolean shouldBeExcluded(Path file) {
        Path name = file.getFileName();
        if (name != null && matchers.stream().anyMatch(matcher -> matcher.matches(name))) {
            return false;
        }
        return true;
    }

    // Invoke the pattern matching method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {
        shouldBeExcluded(file);
        return CONTINUE;
    }

    // Invoke the pattern matching method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {
        if (shouldBeExcluded(dir)) {
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
}
