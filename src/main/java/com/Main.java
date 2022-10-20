package com;

import com.interfaces.Initializer;
import com.models.MirrorPathDTO;
import com.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

import java.io.IOException;
import java.util.EventListener;
import java.util.Set;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class Main implements CommandLineRunner {

    @Autowired
    private MirrorPathDTO pathDataFromJson;

    @Autowired
    FileSystemEventProcessorImpl externalEventProcessor;

    @Autowired
    Initializer initializer;

    @Autowired
    EventListener listener;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //initialize watch dirs
        Set<WatchDir> watchdirs = initializer.initializeWatchDirs();
        //       WatchDirsProcessor.processEvents(externalEventProcessor);
//        ProcessClass.process(watchdirs);

        //process watch dirs
    }

}


