package xyz.rrtt217.util;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import xyz.rrtt217.HDRMod;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class LibraryExtractor {
    public static Path extractLibraries(HashMap<String,String> platformLibNameMap, String targetDir) throws IOException, FileNotFoundException {
        Path gamedir = Platform.getGameFolder();
        Path libExtractDir = gamedir.resolve(targetDir);
        Files.createDirectories(libExtractDir);
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        String fullLibName = "";
        String subDirectory;
        HDRMod.LOGGER.info("Extracting libraries...");
        HDRMod.LOGGER.info("OS: {}", osName);
        HDRMod.LOGGER.info("OS arch: {}", osArch);
        if (osName.contains("win")) {
            fullLibName = platformLibNameMap.get("win") + ".dll";
            subDirectory = "windows/";
            if (osArch.contains("amd64") || osArch.contains("x86_64") || osArch.contains("x64")) {
                subDirectory += "x64";
            } else if (osArch.contains("i386") || osArch.contains("i686")) {
                subDirectory += "i386";
            } else if (osArch.contains("arm")) {
                subDirectory += "arm";
            } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                subDirectory += "arm64";
            } else {
                subDirectory += osArch;
            }
        } else if (osName.contains("mac")) {
            fullLibName = platformLibNameMap.get("mac") + ".dylib";
            subDirectory = "mac/";
            if (osArch.contains("amd64") || osArch.contains("x86_64") || osArch.contains("x64")) {
                subDirectory += "x64";
            } else if (osArch.contains("i386") || osArch.contains("i686")) {
                subDirectory += "i386";
            } else if (osArch.contains("arm")) {
                subDirectory += "arm";
            } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                subDirectory += "arm64";
            } else {
                subDirectory += osArch;
            }
        } else if (osName.contains("linux")) {
            fullLibName = platformLibNameMap.get("linux") + ".so";
            subDirectory = "linux/";
            if (osArch.contains("amd64") || osArch.contains("x86_64") || osArch.contains("x64")) {
                subDirectory += "x64";
            } else if (osArch.contains("i386") || osArch.contains("i686")) {
                subDirectory += "i386";
            } else if (osArch.contains("arm")) {
                subDirectory += "arm";
            } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                subDirectory += "arm64";
            } else {
                subDirectory += osArch;
            }
        } else {
            if(platformLibNameMap.get(osName) != null) fullLibName = platformLibNameMap.get(osName) + ".so";
            else fullLibName = platformLibNameMap.get("linux") + ".so";
            subDirectory = osName+"/";
            if (osArch.contains("amd64") || osArch.contains("x86_64") || osArch.contains("x64")) {
                subDirectory += "x64";
            } else if (osArch.contains("i386") || osArch.contains("i686")) {
                subDirectory += "i386";
            } else if (osArch.contains("arm")) {
                subDirectory += "arm";
            } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                subDirectory += "arm64";
            } else {
                subDirectory += osArch;
            }
        }
        ClassLoader loader = LibraryExtractor.class.getClassLoader();
        HDRMod.LOGGER.info("Finding libraries in path {}", "libraries/" + subDirectory + "/" + fullLibName);
        Path outputLibPath = libExtractDir.resolve(fullLibName);
        if(!Files.exists(outputLibPath)) {
            try (InputStream is = loader.getResourceAsStream("libraries/" + subDirectory + "/" + fullLibName)) {
                if (is == null) {
                    throw new FileNotFoundException("Could not find library file: " + fullLibName);
                } else {
                    Files.copy(is, outputLibPath);
                }
            }
        }
        return libExtractDir.resolve(fullLibName);
    }
}
