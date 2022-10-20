package com.services;

import com.interfaces.FolderStructureProcessor;

import java.nio.file.Path;

public class FolderStructureProcessorImpl implements FolderStructureProcessor {
    @Override
    public void copyDirectory(Path sourcePath, Path destinationPath) {
//incl copy metadata
    }

    @Override
    public void deleteDirectory(Path destinationPath) {

    }
}
