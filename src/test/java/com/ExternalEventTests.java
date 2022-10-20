package com;

import com.models.ModificationEvent;
import com.services.WatchDir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExternalEventTests {

    @ParameterizedTest
    @MethodSource("fullRootAndRelativePaths")
    void nonEmptySourceShouldProvideCorrectRelativePath(String fullPathUri, String rootPathUri, String relativePathUri) {
        WatchDir watchDir = new WatchDir(Paths.get(rootPathUri));
        ModificationEvent modificationEvent = ModificationEvent.ofWatchEvent(ENTRY_CREATE, Paths.get(fullPathUri), watchDir);

        Assertions.assertEquals(relativePathUri, modificationEvent.getRelativePath().toString());
    }

    static Stream<Arguments> fullRootAndRelativePaths() {
        return Stream.of(
                arguments("D:/listened/1/df/myFile.txt", "D:/listened/1", "df\\myFile.txt"),
                arguments("D:/listened/1/df", "D:/listened/1", "df")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void emptySourceShouldResultInException() {

    }
}
