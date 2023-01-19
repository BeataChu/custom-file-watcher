package com.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Initializer {

    Logger LOG = LoggerFactory.getLogger(Initializer.class);

   void initializeWatchDirs();

}
