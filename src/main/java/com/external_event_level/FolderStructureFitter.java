package com.external_event_level;

import java.nio.file.Path;

public interface FolderStructureFitter {

    void copyDirectory(Path sourcePath, Path destinationPath);

    void deleteDirectory(Path destinationPath);

}
