package com.dermai.gradle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;
import java.util.stream.Stream;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class GenerateBrandAssetsTask extends DefaultTask {
    private static final byte[] PNG_SIGNATURE = new byte[] {
        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    @InputDirectory
    public abstract DirectoryProperty getEncodedAssets();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        Path sourceRoot = getEncodedAssets().get().getAsFile().toPath();
        Path outputRoot = getOutputDirectory().get().getAsFile().toPath();
        clearOutput(outputRoot);

        try (Stream<Path> files = Files.walk(sourceRoot)) {
            files.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".base64"))
                .forEach(path -> decodeAsset(sourceRoot, outputRoot, path));
        }
    }

    private static void decodeAsset(Path sourceRoot, Path outputRoot, Path encodedPath) {
        try {
            String relative = sourceRoot.relativize(encodedPath).toString();
            relative = relative.substring(0, relative.length() - ".base64".length());
            Path outputPath = outputRoot.resolve(relative);
            String encoded = Files.readString(encodedPath, StandardCharsets.US_ASCII);
            byte[] decoded = Base64.getMimeDecoder().decode(encoded);
            verifyPng(decoded, encodedPath);
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, decoded);
        } catch (IOException exception) {
            throw new IllegalStateException("Marka gorseli uretilemedi: " + encodedPath, exception);
        }
    }

    private static void verifyPng(byte[] bytes, Path source) {
        if (bytes.length < PNG_SIGNATURE.length) {
            throw new IllegalStateException("Gecersiz marka gorseli: " + source);
        }
        for (int index = 0; index < PNG_SIGNATURE.length; index++) {
            if (bytes[index] != PNG_SIGNATURE[index]) {
                throw new IllegalStateException("Gecersiz marka gorseli: " + source);
            }
        }
    }

    private static void clearOutput(Path outputRoot) throws IOException {
        if (!Files.exists(outputRoot)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(outputRoot)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException exception) {
                    throw new IllegalStateException("Eski marka ciktisi silinemedi: " + path, exception);
                }
            });
        }
    }
}
