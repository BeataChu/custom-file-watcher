package com.interfaces;

import com.models.ModificationEvent;
import com.services.WatchDir;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface FileSystemEventProcessor {

    BlockingQueue<ModificationEvent> queue = new LinkedBlockingQueue<>();

    Set<WatchDir> watchDirs = new HashSet<>();

    void pushEvent(WatchEvent.Kind kind, Path fullPath, WatchDir watchDir);

    void processEvents();

    boolean addWatchDir(WatchDir watchDir);
}
