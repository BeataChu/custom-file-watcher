package com;

import com.data_provider.*;
import com.data_provider.dao.MirrorPathData;
import com.data_provider.dao.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        PathDataProvider pathDataProvider = new PathDataProviderImpl();
        MirrorPathData mirrorPathData = pathDataProvider.getPathDataForProject();
        List<String> excludePatterns = mirrorPathData.getExclude();
        FilteringFileVisitor fileVisitor = new FilteringFileVisitor(excludePatterns);

        //get required number of listeners
        List<WatchDir> watchDirs = new ArrayList<>();
        for (Source source : mirrorPathData.getSources()) {
            try {
                WatchDir watchDir = new WatchDir(source.resolvePath(), fileVisitor);
                watchDirs.add(watchDir);
                //todo: improve logging
                System.out.println("Registering new WatchDir object for path " + source.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (WatchDir watchDir : watchDirs) {
            watchDir.processEvents();
        }

    }

}
