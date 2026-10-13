package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class DeluxeMenusConverter {

    private final ZMenuPlugin plugin;

    public DeluxeMenusConverter(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the folder DeluxeMenus keeps its menus in
     */
    public File getSourceFolder() {
        return new File(this.plugin.getDataFolder().getParentFile(), "DeluxeMenus/gui_menus");
    }

    public File getInventoryFolder() {
        return new File(this.plugin.getDataFolder(), "inventories/converted/deluxemenu");
    }

    public File getCommandFolder() {
        return new File(this.plugin.getDataFolder(), "commands/converted/deluxemenu");
    }

    /**
     * @return every menu file of the DeluxeMenus folder, sorted by path
     */
    public List<File> findMenus() throws IOException {
        Path folder = this.getSourceFolder().toPath();
        if (!Files.isDirectory(folder)) return List.of();
        try (Stream<Path> files = Files.walk(folder)) {
            return files.filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".yml")).sorted().map(Path::toFile).toList();
        }
    }

    /**
     * Finds a menu of the DeluxeMenus folder by file name, with or without its extension.
     */
    public @Nullable File findMenu(@NotNull String name) throws IOException {
        String fileName = name.toLowerCase(Locale.ROOT).endsWith(".yml") ? name : name + ".yml";
        return this.findMenus().stream().filter(file -> file.getName().equalsIgnoreCase(fileName)).findFirst().orElse(null);
    }

    /**
     * Converts one DeluxeMenus menu.
     *
     * @param source The DeluxeMenus menu file.
     * @return What was written, or why the menu was not converted.
     */
    public @NotNull Result convert(@NotNull File source) {
        String name = source.getName().replaceFirst("(?i)\\.yml$", "");
        File inventoryFile = new File(this.getInventoryFolder(), name + ".yml");
        if (inventoryFile.exists()) {
            return Result.failure(name, List.of(), "already converted, delete " + this.relative(inventoryFile) + " to convert it again");
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(source);
        if (!configuration.contains("menu_title") && !configuration.contains("items")) {
            return Result.failure(name, List.of(), "this is not a DeluxeMenus menu");
        }

        InventoryDeluxeMenuLoader loader = new InventoryDeluxeMenuLoader(this.plugin);
        YamlConfiguration converted = new YamlConfiguration();
        try {
            Inventory inventory = loader.load(configuration, "", source, ZInventory.class, this.plugin);
            inventory.serialize(converted);
        } catch (InventoryException | UnsupportedOperationException exception) {
            return Result.failure(name, loader.getWarnings(), exception.getMessage());
        }

        List<String> header = new ArrayList<>();
        header.add("Converted from the DeluxeMenus menu " + source.getName() + " by zMenu.");
        header.add("Review it before use: https://docs.groupez.dev/zmenu/getting-started");
        if (!loader.getWarnings().isEmpty()) {
            header.add("");
            header.add("Not converted exactly:");
            loader.getWarnings().forEach(warning -> header.add("- " + warning));
        }
        converted.options().setHeader(header);

        try {
            this.getInventoryFolder().mkdirs();
            converted.save(inventoryFile);
            File commandFile = this.convertOpenCommand(configuration, name);
            return new Result(name, inventoryFile, commandFile, loader.getWarnings(), null);
        } catch (IOException exception) {
            return Result.failure(name, loader.getWarnings(), "cannot write the converted menu: " + exception.getMessage());
        }
    }

    /**
     * DeluxeMenus opens a menu with {@code open_command}, a command or a list of them; the first
     * becomes the zMenu command and the others its aliases.
     *
     * @return The command file written, or null when the menu has no command or the file exists.
     */
    private @Nullable File convertOpenCommand(YamlConfiguration configuration, String name) throws IOException {
        List<String> openCommands = configuration.isList("open_command") ? configuration.getStringList("open_command") : new ArrayList<>();
        if (openCommands.isEmpty() && configuration.getString("open_command") != null) {
            openCommands.add(configuration.getString("open_command"));
        }
        openCommands = openCommands.stream().map(command -> command.toLowerCase(Locale.ROOT).trim()).filter(command -> !command.isEmpty()).toList();
        if (openCommands.isEmpty()) return null;

        File commandFile = new File(this.getCommandFolder(), name + ".yml");
        if (commandFile.exists()) return null;

        YamlConfiguration commands = new YamlConfiguration();
        ConfigurationSection command = commands.createSection("commands." + name);
        command.set("command", openCommands.getFirst());
        if (openCommands.size() > 1) command.set("aliases", openCommands.subList(1, openCommands.size()));
        command.set("inventory", name);
        commands.options().setHeader(List.of("Converted from the open_command of the DeluxeMenus menu " + name + " by zMenu."));

        this.getCommandFolder().mkdirs();
        commands.save(commandFile);
        return commandFile;
    }

    public String relative(File file) {
        return this.plugin.getDataFolder().toPath().relativize(file.toPath()).toString().replace('\\', '/');
    }

    /**
     * The outcome of converting one menu.
     *
     * @param inventoryFile The converted menu, null when it was not converted.
     * @param commandFile   The command converted from {@code open_command}, if any.
     * @param warnings      What was not converted exactly.
     * @param error         Why the menu was not converted, null on success.
     */
    public record Result(String name, @Nullable File inventoryFile, @Nullable File commandFile, List<String> warnings, @Nullable String error) {

        static Result failure(String name, List<String> warnings, String error) {
            return new Result(name, null, null, warnings, error);
        }

        public boolean isSuccess() {
            return this.error == null;
        }
    }
}
