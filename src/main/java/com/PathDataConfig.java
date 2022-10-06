package com;

import com.data_provider.dao.MirrorPathData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@Configuration
@ComponentScan(basePackageClasses = FileVisitor.class)
public class PathDataConfig {

    @Bean
    public MirrorPathData pathDataFromJson() {
        MirrorPathData mirrorPathData = null;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mirror_folders_config.json")) {
            ObjectMapper mapper = new ObjectMapper();
            mirrorPathData = mapper.readValue(in, MirrorPathData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mirrorPathData;
    }

    @Bean
    public Map<WatchKey, Path> keys() {
        return new HashMap<>();
    }

    @Bean
    public List<Path> excludedPaths() {
        return new ArrayList<>();
    }

}
