package com.interfaces;

import com.services.WatchDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public interface Initializer {

    Logger LOG = LoggerFactory.getLogger(Initializer.class);

//    Set<WatchDir> createWatchDirs();
    Set<WatchDir> initializeWatchDirs();

}
