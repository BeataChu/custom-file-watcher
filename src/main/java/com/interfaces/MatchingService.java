package com.interfaces;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public interface MatchingService {

    boolean shouldBeExcluded(Path path);
}
