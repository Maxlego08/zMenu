package fr.maxlego08.menu.hooks.bedrock.loader;

import fr.maxlego08.menu.api.InventoryOption;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.hooks.bedrock.AbstractBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.BedrockInventoryTypeRegistry;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class BedrockLoader implements Loader<AbstractBedrockInventory<?,?,?>> {
    private final MenuPlugin menuPlugin;
    private final ZBedrockManager manager;

    public BedrockLoader(MenuPlugin menuPlugin, ZBedrockManager manager) {
        this.menuPlugin = menuPlugin;
        this.manager = manager;
    }

    @Override
    public AbstractBedrockInventory<?,?,?> load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];

        String title = configuration.getString("name", "");

        String typeString = configuration.getString("type", "SIMPLE");
        BedrockType bedrockType;
        try {
            bedrockType = BedrockType.valueOf(typeString.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InventoryException("Invalid dialog type: " + typeString);
        }

        Optional<BedrockInventoryTypeLoader<?>> bedrockInventoryTypeLoader = BedrockInventoryTypeRegistry.getInstance().get(bedrockType);
        if (bedrockInventoryTypeLoader.isEmpty()) {
            throw new InventoryException("No loader found for bedrock type: " + bedrockType);
        }
        AbstractBedrockInventory<?,?,?> bedrockInventory = bedrockInventoryTypeLoader.get().load(this.menuPlugin, file, configuration, title);

        if (bedrockInventory == null) {
            throw new InventoryException("Failed to load bedrock inventory of type: " + bedrockType);
        }

        if (configuration.isConfigurationSection("open-requirement")){
            try {
                Requirement openRequirement = this.loadRequirement(configuration, "open-requirement.", file);
                bedrockInventory.setOpenRequirement(openRequirement);
            } catch (InventoryException e) {
                Logger.info("Failed to load open requirement: " + e.getMessage(), Logger.LogType.WARNING);
            }
        }

        bedrockInventory.setOpenActions(this.menuPlugin.getButtonManager().loadActions(configuration, "open-actions", file));
        bedrockInventory.setCloseActions(this.menuPlugin.getButtonManager().loadActions(configuration, "close-actions", file));

        bedrockInventory.setFile(file);
        bedrockInventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));

        List<InventoryOption> inventoryOptions = this.menuPlugin.getInventoryManager().getInventoryOptions().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(inventoryOption -> this.createInstance(entry.getKey(), inventoryOption))).filter(Objects::nonNull).toList();
        for (InventoryOption inventoryOption : inventoryOptions) {
            inventoryOption.loadInventory(bedrockInventory, file, configuration, this.menuPlugin.getInventoryManager(), this.menuPlugin.getButtonManager());
        }
        return bedrockInventory;
    }

    /**
     * Loads InventoryOption
     */
    private InventoryOption createInstance(Plugin plugin, Class<? extends InventoryOption> aClass) {
        try {
            Constructor<? extends InventoryOption> constructor = aClass.getConstructor(Plugin.class);
            return constructor.newInstance(plugin);
        } catch (NoSuchMethodException ignored) {
            try {
                return aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void save(AbstractBedrockInventory<?,?,?> object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: Implement save logic if needed
    }

    protected Requirement loadRequirement(YamlConfiguration configuration, String path, File file) throws InventoryException {
        return this.menuPlugin.getButtonManager().loadRequirement(configuration, path, file);
    }

}