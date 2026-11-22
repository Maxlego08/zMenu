package fr.maxlego08.menu.requirement.checker;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.checker.InventoryLoadRequirement;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.common.utils.yaml.YamlParser;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class InventoryRequirementChecker extends ConfigurationChecker {

    private static final Set<String> REQUIREMENT_LEVEL_KEYS = Set.of(
            "requirements", "requirement", "type", "clicks", "deny", "success",
            "permission", "placeholder", "minimum-requirement", "minimumrequirement"
    );

    public InventoryRequirementChecker(ZMenuPlugin plugin) {
        super(plugin);
    }

    /**
     * Checks if the given configuration can be loaded into an inventory.
     *
     * @param configuration the configuration to check
     * @param plugin        the plugin that owns the configuration
     * @param file          the file where the configuration is stored
     * @param classz        the class of the inventory to load
     * @return an empty optional if the configuration can be loaded, or an optional containing an inventory load requirement otherwise
     */
    public Optional<InventoryLoadRequirement> canLoadInventory(YamlDocument configuration, Plugin plugin, File file, Class<? extends Inventory> classz) {

        InventoryLoadRequirement inventoryLoadRequirement = new InventoryLoadRequirement(plugin, configuration, classz, file);

        this.checkPatterns(configuration, inventoryLoadRequirement);
        this.checkOpenRequirement(configuration, inventoryLoadRequirement);
        this.checkButtons(configuration, inventoryLoadRequirement);

        return inventoryLoadRequirement.canLoad() ? Optional.empty() : Optional.of(inventoryLoadRequirement);
    }

    /**
     * Checks for missing patterns in the given configuration.
     * <p>
     * This method iterates over the list of patterns provided in the
     * configuration. For each pattern name, it attempts to retrieve
     * the corresponding pattern from the pattern manager. If a pattern
     * is not found, it logs a message indicating the missing pattern
     * and adds a requirement to the inventory load requirement for the
     * missing pattern.
     *
     * @param configuration            the YamlDocument containing the list of patterns
     * @param inventoryLoadRequirement the object to store missing pattern requirements
     */
    private void checkPatterns(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement) {
        for (String patternName : YamlSections.getStringList(configuration, "patterns")) {
            Optional<Pattern> optional = this.patternManager.getPattern(patternName);
            if (optional.isEmpty()) {
                inventoryLoadRequirement.addRequirement(InventoryRequirementType.PATTERN, patternName);
            }
        }
    }

    /**
     * Checks and processes the open requirements for an inventory.
     * <p>
     * This method determines the appropriate key for open requirements
     * in the provided configuration. If a valid open requirement section is
     * found, it proceeds to check the associated requirements using the
     * `checkRequirement` method.
     *
     * @param configuration            the YamlDocument containing inventory details
     * @param inventoryLoadRequirement the object to store loaded requirements
     */
    private void checkOpenRequirement(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement) {
        String openRequirementKey = configuration.contains("open_requirement") ? "open_requirement" : configuration.contains("open-requirement") ? "open-requirement" : null;
        if (openRequirementKey != null) {
            if (configuration.contains(openRequirementKey) && configuration.isSection(openRequirementKey + ".")) {

                Section openReqSection = configuration.getSection(openRequirementKey + ".");
                if (openReqSection != null) {
                    Set<String> keys = openReqSection.getRoutesAsStrings(false);
                    boolean hasRequirementsList = configuration.isList(openRequirementKey + ".requirements")
                            || configuration.isList(openRequirementKey + ".requirement");
                    boolean hasDirectPermissibleKeys = keys.contains("type") || keys.contains("permission")
                            || keys.contains("placeholder");

                    if (!hasRequirementsList && hasDirectPermissibleKeys) {
                        Logger.info("Invalid open-requirement configuration in file " + inventoryLoadRequirement.getFile().getAbsolutePath(), Logger.LogType.WARNING);
                        Logger.info("open-requirement should contain 'requirements' as a list.", Logger.LogType.WARNING);
                        Logger.info("Example:", Logger.LogType.WARNING);
                        Logger.info("  open-requirement:", Logger.LogType.WARNING);
                        Logger.info("    requirements:", Logger.LogType.WARNING);
                        Logger.info("      - type: permission", Logger.LogType.WARNING);
                        Logger.info("        permission: 'example.permission'", Logger.LogType.WARNING);
                        Logger.info("Documentation: https://docs.groupez.dev/zmenu/configurations/inventories/inventory#open-requirement", Logger.LogType.WARNING);
                        return;
                    }
                }

                this.checkRequirement(configuration, openRequirementKey + ".", inventoryLoadRequirement);
            }
        }
    }

    /**
     * Iterates through the given configuration and checks for any buttons.
     * <p>
     * Checks each button configuration for the existence of a pattern,
     * and if found, it loads the pattern configuration and checks the
     * button recursively. Otherwise, it checks for a button loader and
     * verifies the associated actions and requirements.
     *
     * @param configuration            the YamlDocument containing button details
     * @param inventoryLoadRequirement the object to store loaded requirements
     */
    private void checkButtons(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement) {
        Section section = configuration.getSection("items.");
        if (section != null) {
            section.getRoutesAsStrings(false).forEach(buttonPath -> this.checkButton(configuration, inventoryLoadRequirement, "items." + buttonPath + "."));
        }
    }

    /**
     * Checks and processes the button configuration for a given path.
     * <p>
     * This method attempts to load button patterns and checks for any
     * requirements related to the button type, actions, click, and view
     * requirements. If a pattern is detected, it loads the pattern
     * configuration and checks the button recursively. Otherwise, it checks
     * for a button loader and verifies the associated actions and requirements.
     *
     * @param configuration            the YamlDocument containing button details
     * @param inventoryLoadRequirement the object to store loaded requirements
     * @param path                     the configuration path to the button
     */
    private void checkButton(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement, String path) {

        String buttonType = configuration.getString(path + "type", "NONE");
        Section patternSection = configuration.getSection(path + "pattern");

        // Check if there is a pattern
        // If there is a pattern, it will try to load it
        if (patternSection != null) {

            Map<String, Object> mapPlaceholders = new HashMap<>();
            patternSection.getRoutesAsStrings(false).forEach(key -> mapPlaceholders.put(key, patternSection.get(key)));

            String fileName = configuration.getString(path + "pattern.fileName", configuration.getString(path + "pattern.file-name"));
            String pluginName = configuration.getString(path + "pattern.pluginName", configuration.getString(path + "pattern.plugin-name", null));
            Plugin patternPlugin = pluginName != null ? Bukkit.getPluginManager().getPlugin(pluginName) : this.plugin;
            if (patternPlugin != null) {
                Path patternPath = patternPlugin.getDataFolder().toPath().resolve("patterns").resolve(fileName + ".yml");
                Optional<YamlDocument> yamlConfiguration = YamlFileCache.getYamlConfiguration(patternPath);
                if (yamlConfiguration.isPresent()) {

                    YamlDocument patternFile = yamlConfiguration.get();

                    mapPlaceholders.putAll(this.plugin.getGlobalPlaceholders());

                    this.loadLocalPlaceholders(patternFile, mapPlaceholders);

                    patternFile = YamlParser.parseConfiguration(patternFile, mapPlaceholders);
                    this.checkButton(patternFile, inventoryLoadRequirement, "button.");
                    return;
                }
            }
        }

        // Check button
        Optional<ButtonLoader> optional = this.buttonManager.getLoader(buttonType);
        if (optional.isEmpty()) {
            inventoryLoadRequirement.addRequirement(InventoryRequirementType.BUTTON, buttonType);
        }

        this.checkAction(configuration, path + "actions", inventoryLoadRequirement);
        this.checkClickRequirements(configuration, inventoryLoadRequirement, path);
        this.checkViewRequirement(configuration, inventoryLoadRequirement, path);

        if (configuration.contains(path + "else")) {
            this.checkButton(configuration, inventoryLoadRequirement, path + "else.");
        }
    }

    /**
     * Checks and verifies the click requirements for a specific configuration path.
     * <p>
     * Iterates through potential section strings to determine and load the
     * click requirements configuration. If found, each requirement within
     * the section is checked and processed.
     *
     * @param configuration            the YamlDocument containing the requirements
     * @param inventoryLoadRequirement the object to store loaded requirements
     * @param path                     the base path to search for click requirements
     */
    private void checkClickRequirements(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement, String path) {

        String[] sectionStrings = this.plugin.getClickRequirementKeys();
        Section section = null;
        String sectionString = "";
        for (String string : sectionStrings) {
            sectionString = string;
            section = configuration.getSection(path + sectionString);
            if (section != null) break;
        }
        if (section == null) return;

        boolean hasMalformedStructure = false;
        for (String key : section.getRoutesAsStrings(false)) {
            if (REQUIREMENT_LEVEL_KEYS.contains(key.toLowerCase(Locale.ROOT))) {
                hasMalformedStructure = true;
                break;
            }
        }

        if (hasMalformedStructure) {
            Logger.info("Invalid click-requirement configuration in file " + inventoryLoadRequirement.getFile().getAbsolutePath() + " at path '" + path + "'", Logger.LogType.WARNING);
            Logger.info("click-requirement expects named groups as children, each containing 'clicks', 'requirements', 'deny', and 'success'.", Logger.LogType.WARNING);
            Logger.info("Example:", Logger.LogType.WARNING);
            Logger.info("  click-requirement:", Logger.LogType.WARNING);
            Logger.info("    my_group:", Logger.LogType.WARNING);
            Logger.info("      clicks: [ALL]", Logger.LogType.WARNING);
            Logger.info("      requirements:", Logger.LogType.WARNING);
            Logger.info("        - type: permission", Logger.LogType.WARNING);
            Logger.info("          permission: 'example.permission'", Logger.LogType.WARNING);
            Logger.info("Documentation: https://docs.groupez.dev/zmenu/configurations/requirements#click-requirement-buttons", Logger.LogType.WARNING);
            return;
        }

        for (String key : section.getRoutesAsStrings(false)) {
            this.checkRequirement(configuration, path + sectionString + key + ".", inventoryLoadRequirement);
        }
    }

    /**
     * Allows checking the view requirement of a button
     *
     * @param configuration            the configuration of the button
     * @param inventoryLoadRequirement the inventory load requirement
     * @param path                     the path of the button
     */
    private void checkViewRequirement(YamlDocument configuration, InventoryLoadRequirement inventoryLoadRequirement, String path) {

        String requirementPath = configuration.isSection(path + "view_requirement.") ? "view_requirement." : configuration.isSection(path + "view-requirement.") ? "view-requirement." : null;
        if (requirementPath == null) return;

        Section viewReqSection = configuration.getSection(path + requirementPath);
        if (viewReqSection != null) {
            Set<String> keys = viewReqSection.getRoutesAsStrings(false);
            boolean hasRequirementsList = configuration.isList(path + requirementPath + "requirements")
                    || configuration.isList(path + requirementPath + "requirement");
            boolean hasDirectPermissibleKeys = keys.contains("type") || keys.contains("permission")
                    || keys.contains("placeholder");

            if (!hasRequirementsList && hasDirectPermissibleKeys) {
                Logger.info("Invalid view-requirement configuration at path '" + path + "' in file " + inventoryLoadRequirement.getFile().getAbsolutePath(), Logger.LogType.WARNING);
                Logger.info("view-requirement should contain 'requirements' as a list.", Logger.LogType.WARNING);
                Logger.info("Example:", Logger.LogType.WARNING);
                Logger.info("  view-requirement:", Logger.LogType.WARNING);
                Logger.info("    requirements:", Logger.LogType.WARNING);
                Logger.info("      - type: permission", Logger.LogType.WARNING);
                Logger.info("        permission: 'example.permission'", Logger.LogType.WARNING);
                Logger.info("Documentation: https://docs.groupez.dev/zmenu/configurations/requirements#view-requirement--open-requirement-inventories", Logger.LogType.WARNING);
                return;
            }
        }

        this.checkRequirement(configuration, path + requirementPath, inventoryLoadRequirement);
    }
}
