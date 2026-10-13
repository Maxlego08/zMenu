package fr.maxlego08.menu.inventory;

import com.sun.nio.file.ExtendedWatchEventModifier;
import fr.maxlego08.menu.ZInventoryManager;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Watches the {@code inventories} folder and reloads an inventory as soon as its file is modified,
 * added or deleted. Disabled by default, see {@code hot-reload} in config.yml.
 * <p>
 * The file system is watched on a dedicated daemon thread. The folder is also scanned regularly, because
 * file system events are not delivered on some file systems (Docker or WSL mounts, network shares, SFTP...).
 * Events are debounced (editors often write a file in several steps) and the actual reload always runs on
 * the main thread.
 */
public class InventoryHotReload {

    private static final String PREFIX = "[HotReload] ";
    private static final long POLL_MILLISECONDS = 100L;
    private static final long SCAN_INTERVAL_MILLISECONDS = 2000L;
    private static final WatchEvent.Kind<?>[] KINDS = {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY};

    private final ZMenuPlugin plugin;
    private final Map<WatchKey, Path> watchedDirectories = new ConcurrentHashMap<>();
    private final Map<Path, Long> pendingPaths = new ConcurrentHashMap<>();
    private final Map<Path, FileVersion> handledVersions = new ConcurrentHashMap<>();
    private Map<Path, FileState> knownFiles = new HashMap<>();

    private volatile WatchService watchService;
    private volatile WatchKey rootKey;
    private volatile Path rootFolder;
    private volatile boolean fileTree;
    private volatile Object rootFileKey;
    private long lastRootCheck;
    private long lastScan;
    private Thread thread;
    private boolean startFailed;

    public InventoryHotReload(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    private static void debug(String message) {
        if (Configuration.enableDebug) Logger.info(PREFIX + message, Logger.LogType.INFO);
    }

    private static boolean isYamlFile(Path path) {
        return path.getFileName() != null && path.getFileName().toString().endsWith(".yml") && !Files.isDirectory(path);
    }

    private static Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private static String hash(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException exception) {
            // SHA-256 is always available, fall back on a weaker hash anyway
            return content.length + ":" + java.util.Arrays.hashCode(content);
        }
    }

    /**
     * Start or stop the watcher according to the current configuration. Called regularly, so a change
     * made with the configuration dialog is applied without any reload.
     */
    public synchronized void update() {
        if (Configuration.enableHotReload) {
            if (!this.isRunning()) {
                if (this.thread != null) {
                    plugin.getLogger().warning(PREFIX + "The watcher thread stopped unexpectedly, restarting it.");
                    this.stop();
                }
                this.start();
            }
        } else this.stop();
    }

    public synchronized boolean isRunning() {
        return this.thread != null && this.thread.isAlive();
    }

    public synchronized void start() {
        if (this.isRunning()) return;

        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            this.rootFolder = normalize(folder.toPath());
            this.watchService = FileSystems.getDefault().newWatchService();
            this.registerRoot();
        } catch (IOException | UncheckedIOException exception) {
            // update() retries regularly, only log the first failure
            if (!this.startFailed) {
                Logger.info(PREFIX + "Unable to start the inventory hot reload: " + exception.getMessage(), Logger.LogType.ERROR);
                if (Configuration.enableDebug) Logger.error(exception);
            }
            this.startFailed = true;
            this.closeWatchService();
            this.watchedDirectories.clear();
            return;
        }

        // The inventories loaded at startup or by /zm reload are already up to date
        this.handledVersions.clear();
        for (Inventory inventory : this.plugin.getInventoryManager().getInventories(this.plugin)) {
            File file = inventory.getFile();
            if (file == null) continue;
            Path path = normalize(file.toPath());
            if (!path.startsWith(this.rootFolder)) continue;
            try {
                this.handledVersions.put(path, new FileVersion(file.lastModified(), hash(Files.readAllBytes(path))));
            } catch (IOException ignored) {
            }
        }

        Map<Path, FileState> files = this.scanFiles();
        this.knownFiles = files == null ? new HashMap<>() : files;
        this.lastScan = System.currentTimeMillis();
        this.pendingPaths.clear();

        this.startFailed = false;
        this.thread = new Thread(this::run, "zMenu-HotReload");
        this.thread.setDaemon(true);
        this.thread.start();

        String mode = this.fileTree ? "native recursive watch" : "watch of " + this.watchedDirectories.size() + " folder(s)";
        Logger.info(PREFIX + "Inventory hot reload enabled, watching " + this.rootFolder + " (" + mode + ", " + this.knownFiles.size() + " file(s), " + this.handledVersions.size() + " loaded inventory(ies), scan every " + SCAN_INTERVAL_MILLISECONDS + "ms, delay " + Configuration.hotReloadDelayMilliseconds + "ms).", Logger.LogType.SUCCESS);
    }

    public synchronized void stop() {
        if (this.thread == null) return;

        this.thread.interrupt();
        this.thread = null;
        this.closeWatchService();
        this.watchedDirectories.clear();
        this.pendingPaths.clear();
        this.handledVersions.clear();
        this.rootKey = null;

        Logger.info(PREFIX + "Inventory hot reload disabled.", Logger.LogType.INFO);
    }

    private void closeWatchService() {
        if (this.watchService == null) return;
        try {
            this.watchService.close();
        } catch (IOException ignored) {
        }
        this.watchService = null;
    }

    /**
     * Register the root folder. On Windows the whole tree is watched with a single handle: watching each
     * subfolder separately keeps a handle open on it, which prevents renaming or deleting its parent.
     */
    private void registerRoot() throws IOException {
        this.rootFileKey = Files.readAttributes(this.rootFolder, BasicFileAttributes.class).fileKey();
        try {
            this.rootKey = this.rootFolder.register(this.watchService, KINDS, ExtendedWatchEventModifier.FILE_TREE);
            this.watchedDirectories.put(this.rootKey, this.rootFolder);
            this.fileTree = true;
        } catch (UnsupportedOperationException exception) {
            this.fileTree = false;
            this.registerAll(this.rootFolder);
        }
    }

    private void registerAll(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            Iterator<Path> iterator = stream.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    WatchKey key = path.register(this.watchService, KINDS);
                    this.watchedDirectories.put(key, path);
                    if (path.equals(this.rootFolder)) this.rootKey = key;
                    debug("Watching the folder " + this.relativeName(path));
                }
            }
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    private void run() {
        WatchService service = this.watchService;
        long lastError = 0;
        while (!Thread.currentThread().isInterrupted() && service == this.watchService) {
            try {
                WatchKey key = service.poll(POLL_MILLISECONDS, TimeUnit.MILLISECONDS);
                while (key != null) {
                    this.handleKey(key);
                    key = service.poll();
                }
                this.checkRootFolder();
                this.scanForChanges();
                this.flushPendingPaths();
            } catch (InterruptedException | ClosedWatchServiceException exception) {
                return;
            } catch (Exception exception) {
                // Do not flood the console if the error repeats on each loop
                long now = System.currentTimeMillis();
                if (now - lastError > 5000L) {
                    lastError = now;
                    Logger.info(PREFIX + "Error in the inventory hot reload: " + exception.getMessage(), Logger.LogType.ERROR);
                    if (Configuration.enableDebug) Logger.error(exception);
                }
            }
        }
    }

    /**
     * The inventories folder may be deleted then recreated (backup restore, git checkout...). Its watch
     * key is then invalid, so the folder is registered again as soon as it exists.
     */
    private void checkRootFolder() throws IOException {
        long now = System.currentTimeMillis();
        if (now - this.lastRootCheck < 1000L) return;
        this.lastRootCheck = now;

        if (!Files.isDirectory(this.rootFolder)) return;
        WatchKey key = this.rootKey;
        // A folder moved away keeps a valid key on Linux, so the folder at this path is compared with the watched one
        Object fileKey = Files.readAttributes(this.rootFolder, BasicFileAttributes.class).fileKey();
        boolean replaced = fileKey != null && !fileKey.equals(this.rootFileKey);
        if (key != null && key.isValid() && !replaced) return;

        this.watchedDirectories.keySet().forEach(WatchKey::cancel);
        this.watchedDirectories.clear();
        this.registerRoot();
        this.pendingPaths.put(this.rootFolder, System.currentTimeMillis());
        Logger.info(PREFIX + "The inventories folder was recreated, hot reload is watching it again.", Logger.LogType.INFO);
    }

    private void handleKey(WatchKey key) {
        try {
            Path directory = this.watchedDirectories.get(key);
            long now = System.currentTimeMillis();

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    // Some events were lost, check the whole folder again
                    Logger.info(PREFIX + "Too many file events at once, the whole inventories folder will be checked.", Logger.LogType.WARNING);
                    this.pendingPaths.put(this.rootFolder, now);
                    continue;
                }
                if (directory == null || !(event.context() instanceof Path name)) continue;

                Path path = directory.resolve(name);
                debug("Event " + event.kind().name() + " on " + this.relativeName(path));

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    if (!this.fileTree) {
                        try {
                            this.registerAll(path);
                        } catch (IOException exception) {
                            Logger.info(PREFIX + "Unable to watch the folder " + path + ": " + exception.getMessage(), Logger.LogType.ERROR);
                        }
                    }
                    // Files may have been copied in the folder before it was registered
                    this.pendingPaths.put(path, now);
                    continue;
                }

                // A deleted path may be a folder, it is not possible to know it anymore
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE || isYamlFile(path)) {
                    this.pendingPaths.put(path, now);
                }
            }
        } finally {
            if (!key.reset()) {
                this.watchedDirectories.remove(key);
            }
        }
    }

    /**
     * Compare the files of the folder with the previous scan. This catches the changes whose event was
     * never delivered by the file system.
     */
    private void scanForChanges() {
        long now = System.currentTimeMillis();
        if (now - this.lastScan < SCAN_INTERVAL_MILLISECONDS) return;
        this.lastScan = now;

        Map<Path, FileState> current = this.scanFiles();
        if (current == null) return;

        for (Map.Entry<Path, FileState> entry : current.entrySet()) {
            FileState previous = this.knownFiles.get(entry.getKey());
            if (!entry.getValue().equals(previous)) {
                debug("Scan: " + (previous == null ? "new" : "modified") + " file " + this.relativeName(entry.getKey()));
                this.pendingPaths.putIfAbsent(entry.getKey(), now);
            }
        }
        for (Path path : this.knownFiles.keySet()) {
            if (!current.containsKey(path)) {
                debug("Scan: deleted file " + this.relativeName(path));
                this.pendingPaths.putIfAbsent(path, now);
            }
        }
        this.knownFiles = current;
    }

    /**
     * @return the state of every yml file of the inventories folder, or null if the folder cannot be read
     */
    private Map<Path, FileState> scanFiles() {
        Path root = this.rootFolder;
        Map<Path, FileState> files = new HashMap<>();
        if (root == null || !Files.isDirectory(root)) return files;

        try (Stream<Path> stream = Files.walk(root)) {
            Iterator<Path> iterator = stream.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (!isYamlFile(path)) continue;
                try {
                    BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                    files.put(normalize(path), new FileState(attributes.lastModifiedTime().toMillis(), attributes.size()));
                } catch (IOException ignored) {
                    // Deleted during the scan
                }
            }
        } catch (IOException | UncheckedIOException exception) {
            // A file or folder was deleted during the scan, try again on the next one
            debug("Scan of the inventories folder failed: " + exception.getMessage());
            return null;
        }
        return files;
    }

    private void flushPendingPaths() {
        if (this.pendingPaths.isEmpty()) return;

        long limit = System.currentTimeMillis() - Math.max(0, Configuration.hotReloadDelayMilliseconds);
        List<Path> paths = new ArrayList<>();
        for (Map.Entry<Path, Long> entry : this.pendingPaths.entrySet()) {
            if (entry.getValue() <= limit && this.pendingPaths.remove(entry.getKey(), entry.getValue())) {
                paths.add(entry.getKey());
            }
        }

        if (!paths.isEmpty()) {
            debug("Processing " + paths.size() + " change(s): " + paths.stream().map(this::relativeName).collect(Collectors.joining(", ")));
            this.plugin.getScheduler().runNextTick(w -> this.processPaths(paths));
        }
    }

    /**
     * Apply the changes, must be called on the main thread. Deletions are applied first, so a file moved
     * to another folder is unloaded before being loaded again.
     */
    private void processPaths(List<Path> paths) {
        if (!this.isRunning()) {
            debug("Hot reload stopped, " + paths.size() + " change(s) ignored.");
            return;
        }

        List<Path> existingPaths = new ArrayList<>();
        for (Path path : paths) {
            if (Files.exists(path)) {
                existingPaths.add(path);
            } else {
                this.safeProcess(path);
            }
        }
        existingPaths.forEach(this::safeProcess);
    }

    private void safeProcess(Path path) {
        try {
            this.processPath(path);
        } catch (Exception exception) {
            Logger.info(PREFIX + "Error while processing " + this.relativeName(path) + ": " + exception.getMessage(), Logger.LogType.ERROR);
            Logger.error(exception);
        }
    }

    private void processPath(Path path) {
        if (Files.isDirectory(path)) {
            // New folder, moved folder or lost events: remove the inventories whose file is gone, then
            // load every new or modified file
            debug("Checking the folder " + this.relativeName(path));
            this.removeDeletedInventories(path);
            List<Path> files;
            try (Stream<Path> stream = Files.walk(path)) {
                files = stream.filter(InventoryHotReload::isYamlFile).toList();
            } catch (IOException | UncheckedIOException exception) {
                Logger.info(PREFIX + "Unable to read the folder " + path + ": " + exception.getMessage(), Logger.LogType.ERROR);
                return;
            }
            files.forEach(this::safeProcessFile);
        } else if (Files.exists(path)) {
            if (isYamlFile(path)) this.processFile(path);
        } else {
            this.removeDeletedInventories(path);
        }
    }

    private void safeProcessFile(Path path) {
        try {
            this.processFile(path);
        } catch (Exception exception) {
            Logger.info(PREFIX + "Error while processing " + this.relativeName(path) + ": " + exception.getMessage(), Logger.LogType.ERROR);
            Logger.error(exception);
        }
    }

    private void processFile(Path path) {
        Path normalized = normalize(path);
        String fileName = this.relativeName(path);

        long lastModified = path.toFile().lastModified();
        byte[] content;
        try {
            content = Files.readAllBytes(path);
        } catch (NoSuchFileException exception) {
            this.removeDeletedInventories(path);
            return;
        } catch (IOException exception) {
            // The file may still be locked by the editor, try again later
            Logger.info(PREFIX + "Unable to read " + fileName + ", it will be checked again: " + exception.getMessage(), Logger.LogType.WARNING);
            this.pendingPaths.put(path, System.currentTimeMillis());
            return;
        }

        // This version of the file was already handled (the watcher and the scan both detected it). The
        // content is compared too, the modification date alone is not precise enough on every file system
        FileVersion version = new FileVersion(lastModified, hash(content));
        if (version.equals(this.handledVersions.get(normalized))) {
            debug(fileName + " is unchanged, ignored.");
            return;
        }
        this.handledVersions.put(normalized, version);

        Optional<Inventory> optional = this.findInventoryByFile(path);
        Logger.info(PREFIX + "Change detected on " + fileName + ", " + (optional.isPresent() ? "reloading" : "loading") + " the inventory...", Logger.LogType.INFO);

        // Do not unload a working inventory because of a YAML error, the file is probably being edited
        try {
            new YamlConfiguration().loadFromString(new String(content, StandardCharsets.UTF_8));
        } catch (InvalidConfigurationException exception) {
            Logger.info(PREFIX + fileName + " contains a YAML error and was not " + (optional.isPresent() ? "reloaded, the previous version stays loaded" : "loaded") + ": " + exception.getMessage(), Logger.LogType.ERROR);
            return;
        }

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        YamlFileCache.invalidateCache(path);
        optional.ifPresent(inventory -> YamlFileCache.invalidateCache(inventory.getFile().toPath()));
        // A previous version of the file may be waiting for a load requirement, it must not be loaded later
        if (inventoryManager instanceof ZInventoryManager zInventoryManager) {
            zInventoryManager.removeInventoryLoadRequirement(path.toFile());
        }

        long start = System.currentTimeMillis();
        Inventory newInventory;
        try {
            // The new version is loaded before removing the old one, so the old one stays loaded if it fails
            newInventory = inventoryManager.loadInventory(this.plugin, path.toFile());
        } catch (Exception exception) {
            Logger.info(PREFIX + "Unable to load " + fileName + (optional.isPresent() ? ", the previous version stays loaded" : "") + ": " + exception.getMessage(), Logger.LogType.ERROR);
            if (Configuration.enableDebug || !(exception instanceof InventoryException)) Logger.error(exception);
            return;
        }
        long duration = System.currentTimeMillis() - start;

        // The file was written again while being read, the loaded version may be incomplete: check it again
        if (path.toFile().lastModified() != lastModified) {
            debug(fileName + " was modified while being loaded, it will be checked again.");
            YamlFileCache.invalidateCache(path);
            this.handledVersions.remove(normalized);
            this.pendingPaths.put(path, System.currentTimeMillis());
        }

        if (optional.isPresent()) {
            Inventory oldInventory = optional.get();
            inventoryManager.deleteInventory(oldInventory);
            if (newInventory != null) {
                int viewers = this.reopenInventory(oldInventory, newInventory);
                Logger.info(PREFIX + "Inventory " + fileName + " reloaded in " + duration + "ms" + (viewers > 0 ? ", reopened for " + viewers + " player(s)." : "."), Logger.LogType.SUCCESS);
            } else {
                this.closeInventory(oldInventory);
                Logger.info(PREFIX + "Inventory " + fileName + " unloaded, it is disabled (enable: false) or waiting for a load requirement.", Logger.LogType.WARNING);
            }
        } else if (newInventory != null) {
            Logger.info(PREFIX + "Inventory " + fileName + " loaded in " + duration + "ms.", Logger.LogType.SUCCESS);
        } else {
            Logger.info(PREFIX + "Inventory " + fileName + " was not loaded, it is disabled (enable: false) or waiting for a load requirement.", Logger.LogType.WARNING);
        }
    }

    /**
     * Remove every inventory whose file is {@code path} or inside {@code path} and no longer exists.
     */
    private void removeDeletedInventories(Path path) {
        Path target = normalize(path);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        this.handledVersions.keySet().removeIf(handledPath -> handledPath.startsWith(target) && !Files.exists(handledPath));
        if (inventoryManager instanceof ZInventoryManager zInventoryManager) {
            zInventoryManager.removeInventoryLoadRequirements(file -> !file.exists() && normalize(file.toPath()).startsWith(target));
        }

        int removed = 0;
        List<Inventory> inventories = new ArrayList<>(inventoryManager.getInventories(this.plugin));
        for (Inventory inventory : inventories) {
            File file = inventory.getFile();
            if (file == null || file.exists()) continue;

            Path inventoryPath = normalize(file.toPath());
            if (!inventoryPath.startsWith(target)) continue;

            YamlFileCache.invalidateCache(file.toPath());
            this.closeInventory(inventory);
            inventoryManager.deleteInventory(inventory);
            removed++;
            Logger.info(PREFIX + "Inventory " + this.relativeName(inventoryPath) + " unloaded, its file was deleted.", Logger.LogType.WARNING);
        }

        if (removed == 0) debug("No loaded inventory to remove for " + this.relativeName(target));
    }

    /**
     * Open the new version of the inventory over the old one for the players viewing it, on the same page
     * and with the same previous inventories. Closing it then opening it again does not work, the client
     * needs a few ticks between both. The old version is also replaced in the previous inventories of the
     * players, so a back button opens the new version.
     *
     * @return the number of players viewing the old inventory
     */
    private int reopenInventory(Inventory oldInventory, Inventory newInventory) {
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        int viewers = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // On Folia the open inventory of a player can only be read from its own thread, the count is only for the log
            if (this.getViewedInventory(player) instanceof InventoryDefault inventoryDefault && oldInventory.equals(inventoryDefault.getMenuInventory())) {
                viewers++;
            }
            this.plugin.getScheduler().runAtEntity(player, task -> {
                if (!player.isOnline() || !(this.getViewedInventory(player) instanceof InventoryDefault inventoryDefault) || inventoryDefault.isClose()) {
                    return;
                }

                List<Inventory> oldInventories = inventoryDefault.getOldInventories();
                if (oldInventories.contains(oldInventory)) {
                    try {
                        oldInventories.replaceAll(inventory -> inventory.equals(oldInventory) ? newInventory : inventory);
                    } catch (UnsupportedOperationException ignored) {
                    }
                }

                if (!oldInventory.equals(inventoryDefault.getMenuInventory())) return;

                List<Inventory> previousInventories = new ArrayList<>(oldInventories);
                previousInventories.replaceAll(inventory -> inventory.equals(oldInventory) ? newInventory : inventory);
                debug("Reopening " + newInventory.getFileName() + " for " + player.getName() + " on page " + inventoryDefault.getPage());
                inventoryManager.openInventory(player, newInventory, Math.max(1, inventoryDefault.getPage()), previousInventories);
            });
        }
        return viewers;
    }

    private Object getViewedInventory(Player player) {
        try {
            org.bukkit.inventory.Inventory topInventory = CompatibilityUtil.getTopInventory(player);
            return topInventory == null ? null : topInventory.getHolder();
        } catch (Exception exception) {
            // Not readable from this thread (Folia)
            return null;
        }
    }

    private void closeInventory(Inventory inventory) {
        this.plugin.getVInventoryManager().close(v -> v instanceof InventoryDefault inventoryDefault && !inventoryDefault.isClose() && inventory.equals(inventoryDefault.getMenuInventory()));
    }

    /**
     * Find the zMenu inventory loaded from this file. Inventories are indexed by file name, which is
     * ambiguous across subfolders, so the comparison is done on the path.
     */
    private Optional<Inventory> findInventoryByFile(Path path) {
        Path target = normalize(path);
        for (Inventory inventory : this.plugin.getInventoryManager().getInventories(this.plugin)) {
            File file = inventory.getFile();
            if (file != null && normalize(file.toPath()).equals(target)) {
                return Optional.of(inventory);
            }
        }
        return Optional.empty();
    }

    private String relativeName(Path path) {
        Path normalized = normalize(path);
        Path root = this.rootFolder;
        return root != null && normalized.startsWith(root) ? root.relativize(normalized).toString() : normalized.toString();
    }

    private record FileVersion(long lastModified, String hash) {
    }

    private record FileState(long lastModified, long size) {
    }
}
