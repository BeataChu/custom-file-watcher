package com;

import com.data_provider.MirrorPathDTO;
import com.data_provider.SourceDTO;
import com.external_event_level.ExternalEventProcessor;
import com.internal_event_level.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class Main implements CommandLineRunner {

    @Autowired
    private MirrorPathDTO pathDataFromJson;

    @Autowired
    ExternalEventProcessor externalEventProcessor;

    private static Logger LOG = LoggerFactory
            .getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOG.info("START TRACKING FOLDERS");
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //get required number of listeners
        WatchService watcher = WatchServiceProvider.getNewWatcher();
        HashMap<WatchKey, Path> keys = new HashMap<>();
        List<Path> excludedPaths = new ArrayList<>();
        Set<WatchDir> watchDirs = externalEventProcessor.getWatchDirs();
        for (SourceDTO source : pathDataFromJson.getSources()) {
            FilteringFileVisitor fileVisitor = new FilteringFileVisitor(watcher, keys, excludedPaths, pathDataFromJson);
            fileVisitor.run();
            WatchDir watchDir = new WatchDir(source.resolvePath(),
                    watcher,
                    fileVisitor, keys, excludedPaths);
            watchDirs.add(watchDir);

            LOG.info("Registering new WatchDir object for path " + source.getPath());
        }

        LOG.info("{} of {} directory listeners initiated.", externalEventProcessor.getWatchDirs().size(), pathDataFromJson.getSources().size());

       WatchDirsProcessor.processEvents(externalEventProcessor);
    }
}


