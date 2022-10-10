package com;

import com.data_provider.dao.MirrorPathData;
import com.data_provider.dao.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@SpringBootApplication(exclude = { WebMvcAutoConfiguration.class})
public class Main implements CommandLineRunner {

    @Autowired
    private MirrorPathData pathDataFromJson;

    @Autowired
    private FilteringFileVisitor fileVisitor;

    @Autowired
    private Map<WatchKey, Path> keys;

    @Autowired
    private List<Path> excludedPaths;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    WatchService watcher;

    private static Logger LOG = LoggerFactory
            .getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOG.info("START TRACKING FOLDERS");
        SpringApplication.run(Main.class, args);
        LOG.info("STOP TRACKING FOLDERS");
    }

    @Override
    public void run(String... args) throws Exception {

        this.fileVisitor.run();

        //get required number of listeners
        List<WatchDir> watchDirs = new ArrayList<>();
        for (Source source : pathDataFromJson.getSources()) {
            try {
                WatchDir watchDir = new WatchDir(source.resolvePath(), watcher, this.fileVisitor, keys, excludedPaths);
                watchDirs.add(watchDir);
                LOG.info("Registering new WatchDir object for path " + source.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOG.info("{} of {} directory listeners initiated.", watchDirs.size(), pathDataFromJson.getSources().size());

        //implement multithreading
        for (WatchDir watchDir : watchDirs) {
            watchDir.processEvents();
        }
    }
}

