package com.external_event_level;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class ModificationEvent {

    WatchEvent.Kind kind;
    Path path;
    //or String path;

    String relativePath;

}
