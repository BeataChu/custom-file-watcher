package com.data_provider;

import java.nio.file.*;

public class Source {

    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

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
