package com.models;

import java.util.List;


public class MirrorPathDTO {
    private String name;
    private List<String> exclude;
    private List<SourceDTO> sources;

    public String getName() {
        return name;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public List<SourceDTO> getSources() {
        return sources;
    }
}
