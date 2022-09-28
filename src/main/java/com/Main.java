package com;

import com.data_provider.*;
import com.data_provider.dao.MirrorPathData;
import com.data_provider.dao.Source;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        PathDataProvider pathDataProvider = new PathDataProviderImpl();
        MirrorPathData mirrorPathData = pathDataProvider.getPathDataForProject();
        List<String> excludePatterns = mirrorPathData.getExclude();
        Map<WatchKey, Path> keys = new HashMap<>();
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        FilteringFileVisitor fileVisitor = new FilteringFileVisitor(excludePatterns, keys, watcher);

        //get required number of listeners
        List<WatchDir> watchDirs = new ArrayList<>();
        for (Source source : mirrorPathData.getSources()) {
            WatchDir watchDir = new WatchDir(source.resolvePath(), fileVisitor, watcher, keys);
            watchDirs.add(watchDir);
            //todo: improve logging
            System.out.println("Registering new WatchDir object for path " + source.getPath());


        }

        for (WatchDir watchDir : watchDirs) {
            watchDir.processEvents();
        }

    }

}
