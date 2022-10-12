package com;

import com.external_event_level.ExternalEventProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
@SpringJUnitConfig
@ContextConfiguration(classes = PathDataConfig.class)
public class MainTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ExternalEventProcessor externalEventProcessor;

    @Test
    void checkBeans() {
        assertTrue(applicationContext.containsBean("pathDataFromJson"));
        assertTrue(applicationContext.containsBean("keys"));
        assertTrue(applicationContext.containsBean("excludedPaths"));
        assertTrue(applicationContext.containsBean("watcher"));
    }

    @Test
    void checkExternalWatcher() {
        assertTrue(3 == externalEventProcessor.getWatchDirs().size());
    }
}
