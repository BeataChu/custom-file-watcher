package com;

import com.data_provider.*;
import com.data_provider.dao.MirrorPathData;
import com.data_provider.dao.Source;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        PathDataProvider pathDataProvider = new PathDataProviderImpl();
        MirrorPathData mirrorPathData = pathDataProvider.getPathDataForProject();
        List<String> excludePatterns = mirrorPathData.getExclude();
        WatchService watcher =  FileSystems.getDefault().newWatchService();
        Map<WatchKey, Path> keys = new HashMap<>();
        List<Path> excludedPaths = new ArrayList<>();
        FilteringFileVisitor fileVisitor = new FilteringFileVisitor(excludePatterns, watcher, keys, excludedPaths);

        //get required number of listeners
        List<WatchDir> watchDirs = new ArrayList<>();
        for (Source source : mirrorPathData.getSources()) {
            try {
                WatchDir watchDir = new WatchDir(source.resolvePath(), fileVisitor, watcher, keys, excludedPaths);
                watchDirs.add(watchDir);
                //todo: improve logging
                System.out.println("Registering new WatchDir object for path " + source.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(String.format("%s of %s directory listeners initiated.", watchDirs.size(), mirrorPathData.getSources().size()));

        for (WatchDir watchDir : watchDirs) {
            watchDir.processEvents();
        }

    }

}
