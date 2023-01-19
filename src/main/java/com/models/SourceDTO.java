package com.models;

import lombok.Getter;

import java.nio.file.*;

@Getter
public class SourceDTO {

    private String name;
    private String path;

    public Path resolvePath() {
        Path filePath = null;

        try {
            filePath = Paths.get(path);
        } catch (InvalidPathException e) {
            //TODO: implement logging
            //todo: improve exception handling
            System.out.println("Illegal path in json: " + path);
            e.printStackTrace();
        }

        return filePath;
    }
}
