package fr.maxlego08.menu.requirement.checker;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.checker.InventoryLoadRequirement;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ConfigurationChecker extends ZUtils {

    protected final ZMenuPlugin plugin;
    protected final InventoryManager inventoryManager;
    protected final ButtonManager buttonManager;
    protected final PatternManager patternManager;

    public ConfigurationChecker(ZMenuPlugin plugin) {
        this.plugin = plugin;
        this.buttonManager = plugin.getButtonManager();
        this.inventoryManager = plugin.getInventoryManager();
        this.patternManager = plugin.getPatternManager();
    }

    /**
     * Checks all requirements in the given configuration path.
     *
     * @param configuration            The configuration to check
     * @param path                     The path to check
     * @param inventoryLoadRequirement The inventory load requirement to add missing requirements to
     */
    protected void checkRequirement(YamlConfiguration configuration, String path, InventoryLoadRequirement inventoryLoadRequirement) {

        List<Map<String, Object>> requirements = (List<Map<String, Object>>) configuration.getList(path + "requirements", configuration.getList(path + "requirement", new ArrayList<>()));

        this.buttonManager.getEmptyPermissible(requirements).forEach(name -> inventoryLoadRequirement.addRequirement(InventoryRequirementType.PERMISSIBLE, name));
        checkAction(configuration, path + "success", inventoryLoadRequirement);
        checkAction(configuration, path + "deny", inventoryLoadRequirement);
    }

    /**
     * Checks for any missing actions in the given configuration path.
     *
     * @param configuration            The configuration to check within
     * @param path                     The path to the actions within the configuration
     * @param inventoryLoadRequirement The inventory load requirement to add missing actions to
     */
    protected void checkAction(YamlConfiguration configuration, String path, InventoryLoadRequirement inventoryLoadRequirement) {
        List<Map<String, Object>> actions = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        this.buttonManager.getEmptyActions(actions).forEach(name -> inventoryLoadRequirement.addRequirement(InventoryRequirementType.ACTION, name));
    }
}
