package com.models;

import lombok.Getter;

import java.util.List;

@Getter
public class MirrorPathDTO {
    private String name;
    private List<String> exclude;
    private List<SourceDTO> sources;
}
