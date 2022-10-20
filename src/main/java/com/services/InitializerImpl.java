package com.services;

import com.interfaces.Initializer;
import com.models.MirrorPathDTO;
import com.models.SourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InitializerImpl implements Initializer {

    @Autowired
    private MirrorPathDTO pathDataFromJson;

    @Autowired
    private FileSystemEventProcessorImpl externalEventProcessor;

    public Set<WatchDir> initializeWatchDirs() {
        Set<WatchDir> watchDirs = externalEventProcessor.getWatchDirs();

        for (SourceDTO source : pathDataFromJson.getSources()) {

            FilteringFileVisitor fileVisitor = new FilteringFileVisitor();
            WatchDir watchDir = new WatchDir(source.resolvePath(), fileVisitor);
            watchDirs.add(watchDir);

            LOG.info("Registering new WatchDir object for path " + source.getPath());
        }

        LOG.info("{} of {} directory listeners initialized.", externalEventProcessor.getWatchDirs().size(), pathDataFromJson.getSources().size());

        return watchDirs;
    }
}