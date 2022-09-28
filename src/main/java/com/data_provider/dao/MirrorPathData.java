package com.data_provider.dao;

import java.util.List;

public class MirrorPathData {
    private String name;
    private List<String> exclude;
    private List<Source> sources;

    public String getName() {
        return name;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public List<Source> getSources() {
        return sources;
    }

}
