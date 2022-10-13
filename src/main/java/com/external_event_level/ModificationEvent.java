package com.external_event_level;

import com.internal_event_level.WatchDir;
import lombok.*;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Data
@AllArgsConstructor
public class ModificationEvent {

    private WatchEvent.Kind kind;
    private Path rootPath;
    private Path relativePath;

    public static ModificationEvent ofWatchEvent(WatchEvent.Kind kind, Path fullPath, WatchDir watchDir) {
        ModificationEvent newModEvent = new ModificationEvent(kind, fullPath, watchDir.getRootPath().relativize(fullPath));
        System.out.println(newModEvent);

        return newModEvent;
    }

}
