package xyz.rrtt217.HDRMod.util;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.sun.jna.Platform.ARCH;

public class LibraryExtractor {
    public static final Logger LOGGER = LoggerFactory.getLogger("hdr_mod_library_extractor");
    public static Path extractLibraries(Map<String, String> platformLibNameMap, String targetDir) throws IOException{
        return extractLibraries(platformLibNameMap, targetDir, "");
    }
    public static Path extractLibraries(Map<String, String> platformLibNameMap, String targetDir, String version) throws IOException {
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));
        Path libExtractDir = tempDir.resolve(targetDir);
        Files.createDirectories(libExtractDir);

        String osName = System.getProperty("os.name").toLowerCase();

        LOGGER.info("Extracting libraries...");
        LOGGER.info("OS: {}", osName);
        LOGGER.info("OS arch: {}", ARCH);

        String platformKey;
        String libExtension;
        String subDirBase;

        if (Platform.isWindows()) {
            platformKey = "windows";
            libExtension = ".dll";
            subDirBase = "windows";
        } else if (Platform.isMac()) {
            platformKey = "mac";
            libExtension = ".dylib";
            subDirBase = "mac";
        } else if (Platform.isLinux()) {
            platformKey = "linux";
            libExtension = ".so";
            subDirBase = "linux";
        } else {
            platformKey = osName;
            libExtension = ".so";
            subDirBase = osName;
        }

        // Get base name of the lib.
        String libBaseName = platformLibNameMap.get(platformKey);
        if (libBaseName == null) {
            // fallback to linux mapping on unknown system. The linux mapping is usually common *nix mapping.
            if (!platformKey.equals("linux") && !platformKey.equals("win") && !platformKey.equals("mac")) {
                libBaseName = platformLibNameMap.get("linux");
                if (libBaseName == null) {
                    throw new FileNotFoundException("No library name mapping found for OS: " + osName +
                            " (key: " + platformKey + ") and no fallback 'linux' mapping.");
                }
                LOGGER.warn("No mapping for OS '{}', using fallback 'linux' mapping.", osName);
            } else {
                throw new FileNotFoundException("Missing library name mapping for key: " + platformKey);
            }
        }

        String fullLibName = libBaseName + libExtension;
        String resourcePath = "libraries/" + subDirBase + "/" + ARCH + "/" + fullLibName;

        LOGGER.info("Looking for library in classpath: {}", resourcePath);

        Path outputLibPath = libExtractDir.resolve(libBaseName + "." + version + libExtension);

        if (Files.exists(outputLibPath)) {
            LOGGER.info("Library already exists at {}, skipping extraction.", outputLibPath);
        } else {
            try (InputStream is = LibraryExtractor.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new FileNotFoundException("Could not find library resource: " + resourcePath);
                }
                Files.copy(is, outputLibPath);
                LOGGER.info("Extracted library to {}", outputLibPath);
            }
        }
        return outputLibPath;
    }
}