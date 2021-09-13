package net.minestom.script.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String readFile(@NotNull Path path) {
        String fileString = null;
        try {
            fileString = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileString;
    }
}
