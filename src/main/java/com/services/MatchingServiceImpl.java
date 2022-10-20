package com.services;

import com.interfaces.MatchingService;
import com.models.MirrorPathDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MatchingServiceImpl implements MatchingService {

    @Autowired
    private MirrorPathDTO pathDataFromJson;

    private List<PathMatcher> matchers;

    public MatchingServiceImpl() {
        setFileSystemMatchers(pathDataFromJson.getExclude());
    }

    private void setFileSystemMatchers(List<String> exclude) {
        FileSystem fileSystem = FileSystems.getDefault();
        List<PathMatcher> pathMatchers = new ArrayList<>(exclude.size());
        for (String patternStr : exclude) {
            pathMatchers.add(fileSystem.getPathMatcher("glob:" + patternStr));
        }
    }

    public boolean shouldBeExcluded(Path path) {
        Path name = path.getFileName();
        if (name != null && (path.endsWith("bin") || path.endsWith("obj") || path.endsWith("appsettings.json") || path.endsWith(".lic"))) {
            return true;
        }
        return false;
    }

    // TODO: Compares the glob patterns against the file or directory name.
//    private boolean shouldBeExcluded(Path file) {
//        Path name = file.getFileName();
//        if (name != null && matchers.stream().anyMatch(matcher -> matcher.matches(name))) {
//            return false;
//        }
//        return true;
//    }
}
