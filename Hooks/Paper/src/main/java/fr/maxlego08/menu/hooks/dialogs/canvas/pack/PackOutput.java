package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Writes generated files so that the same content always gives the same bytes: files are sorted, and zip
 * entries carry a fixed date. A plugin caching zMenu's files, or a client caching the pack, then sees no
 * change when nothing changed.
 */
public final class PackOutput {

    private static final FileTime FIXED_TIME = FileTime.fromMillis(946684800000L);

    private PackOutput() {
    }

    /**
     * @param files The files, by path inside the pack.
     * @return The SHA-1 of every path and its bytes, in order.
     */
    public static @NotNull String contentHash(@NotNull SortedMap<String, byte[]> files) {
        MessageDigest digest = sha1();
        files.forEach((path, bytes) -> {
            digest.update(path.getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(bytes);
        });
        return HexFormat.of().formatHex(digest.digest());
    }

    /**
     * Makes a folder hold exactly these files: missing or different files are written, files that are no
     * longer generated are deleted, files that did not change are left untouched.
     *
     * @param folder The folder.
     * @param files  The files, by path relative to the folder.
     * @throws IOException If a file cannot be written or deleted.
     */
    public static void writeFolder(@NotNull Path folder, @NotNull SortedMap<String, byte[]> files) throws IOException {
        Files.createDirectories(folder);
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            Path target = folder.resolve(entry.getKey());
            if (Files.exists(target) && Arrays.equals(Files.readAllBytes(target), entry.getValue())) continue;
            Files.createDirectories(target.getParent());
            Files.write(target, entry.getValue());
        }

        List<Path> stale = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                String relative = folder.relativize(path).toString().replace('\\', '/');
                if (!files.containsKey(relative)) stale.add(path);
            });
        }
        for (Path path : stale) Files.deleteIfExists(path);
        deleteEmptyFolders(folder);
    }

    private static void deleteEmptyFolders(@NotNull Path folder) throws IOException {
        List<Path> folders;
        try (Stream<Path> stream = Files.walk(folder)) {
            folders = stream.filter(Files::isDirectory).filter(path -> !path.equals(folder)).sorted((a, b) -> b.getNameCount() - a.getNameCount()).toList();
        }
        for (Path path : folders) {
            try (Stream<Path> children = Files.list(path)) {
                if (children.findAny().isEmpty()) Files.delete(path);
            }
        }
    }

    /**
     * Writes a zip holding these files.
     *
     * @param zip   The zip file.
     * @param files The files, by path inside the zip.
     * @return The SHA-1 of the zip.
     * @throws IOException If the zip cannot be written.
     */
    public static @NotNull String writeZip(@NotNull Path zip, @NotNull SortedMap<String, byte[]> files) throws IOException {
        Files.createDirectories(zip.getParent());
        Path temporary = zip.resolveSibling(zip.getFileName() + ".tmp");
        try (OutputStream output = Files.newOutputStream(temporary); ZipOutputStream stream = new ZipOutputStream(output)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipEntry.setLastModifiedTime(FIXED_TIME);
                zipEntry.setCreationTime(FIXED_TIME);
                zipEntry.setLastAccessTime(FIXED_TIME);
                stream.putNextEntry(zipEntry);
                stream.write(entry.getValue());
                stream.closeEntry();
            }
        }
        byte[] bytes = Files.readAllBytes(temporary);
        Files.move(temporary, zip, StandardCopyOption.REPLACE_EXISTING);
        return HexFormat.of().formatHex(sha1().digest(bytes));
    }

    private static @NotNull MessageDigest sha1() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-1 is not available", exception);
        }
    }
}
