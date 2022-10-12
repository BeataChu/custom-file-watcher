package com;

import com.data_provider.MirrorPathData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;

import java.io.InputStream;
import java.nio.file.FileVisitor;

/**
 * Provides beans for Spring application:
 * path data obtained from resource json file,
 * map of keys - events in directories caught by a watcher,
 * watcher itself
 * and a list of paths excluded from watching
 */
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
}
