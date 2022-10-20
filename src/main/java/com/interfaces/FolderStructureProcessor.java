package com.interfaces;

import java.nio.file.Path;

public interface FolderStructureProcessor {

    void copyDirectory(Path sourcePath, Path destinationPath);

    void deleteDirectory(Path destinationPath);

}
