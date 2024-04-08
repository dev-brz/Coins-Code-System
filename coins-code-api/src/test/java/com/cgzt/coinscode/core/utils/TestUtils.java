package com.cgzt.coinscode.core.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@UtilityClass
public class TestUtils {
    public void cleanDirectory(File directory) {
        FileSystemUtils.deleteRecursively(directory);
    }

    public void cleanDirectory(Path directory) throws IOException {
        FileSystemUtils.deleteRecursively(directory);
    }
}
