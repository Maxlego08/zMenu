package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.exceptions.ButtonAlreadyRegisterException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.RequirementLoader;
import fr.maxlego08.menu.loader.ZButtonLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class ZButtonManager extends ZUtils implements ButtonManager {

    private final ZMenuPlugin plugin;
    private final Map<String, List<ButtonLoader>> loaders = new HashMap<>();
    private final Map<String, PermissibleLoader> permissibles = new HashMap<>();
    private final Map<String, ActionLoader> actionsLoader = new HashMap<>();

    public ZButtonManager(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(ButtonLoader button) {

        ButtonLoader existingLoader = null;
        for (List<ButtonLoader> buttonLoaders : loaders.values()) {
            for (ButtonLoader loader : buttonLoaders) {
                if (loader.getName().equalsIgnoreCase(button.getName())) {
                    existingLoader = loader;
                    break;
                }
            }
            if (existingLoader != null) {
                break;
            }
        }
        if (existingLoader != null) {
            var loader = existingLoader;
            throw new ButtonAlreadyRegisterException("Button " + button.getName() + " was already register in the " + loader.getPlugin().getName());
        }

        Plugin plugin = button.getPlugin();
        List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(plugin.getName(), new ArrayList<>());
        buttonLoaders.add(button);
        this.loaders.put(plugin.getName(), buttonLoaders);

        this.plugin.getInventoryManager().loadElement(InventoryRequirementType.BUTTON, button.getName());
    }

    @Override
    public void unregister(ButtonLoader button) {
        String pluginName = button.getPlugin().getName();
        List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(pluginName, new ArrayList<>());
        buttonLoaders.remove(button);
        this.loaders.put(pluginName, buttonLoaders);
    }

    @Override
    public void unregisters(Plugin plugin) {
        this.loaders.remove(plugin.getName());
    }

    @Override
    public Collection<ButtonLoader> getLoaders() {
        List<ButtonLoader> flattened = new ArrayList<>();
        for (List<ButtonLoader> buttonLoaders : this.loaders.values()) {
            flattened.addAll(buttonLoaders);
        }
        return flattened;
    }

    @Override
    public Collection<ButtonLoader> getLoaders(Plugin plugin) {
        return this.loaders.getOrDefault(plugin.getName(), new ArrayList<>());
    }

    @Override
    public Optional<ButtonLoader> getLoader(String name) {
        for (ButtonLoader loader : this.getLoaders()) {
            if (loader.getName().equalsIgnoreCase(name)) {
                return Optional.of(loader);
            }
        }
        return Optional.empty();
    }

    @Override
    public void registerPermissible(PermissibleLoader permissibleLoader) {
        this.permissibles.put(permissibleLoader.getKey().toLowerCase(), permissibleLoader);
        this.plugin.getInventoryManager().loadElement(InventoryRequirementType.PERMISSIBLE, permissibleLoader.getKey());
    }

    @Override
    public Map<String, PermissibleLoader> getPermissibles() {
        return this.permissibles;
    }

    @Override
    public Optional<PermissibleLoader> getPermission(String key) {
        return Optional.ofNullable(this.permissibles.getOrDefault(key.toLowerCase(), null));
    }

    @Override
    public void registerAction(ActionLoader actionLoader) {
        for (String value : actionLoader.getKeys()) {
            this.actionsLoader.put(value, actionLoader);
            this.plugin.getInventoryManager().loadElement(InventoryRequirementType.ACTION, value);
        }
    }

    @Override
    public Optional<ActionLoader> getActionLoader(String key) {
        return Optional.ofNullable(this.actionsLoader.getOrDefault(key.toLowerCase(), null));
    }

    @Override
    public List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path, File file) {
        List<Permissible> permissibles = new ArrayList<>();
        for (Map<String, Object> map : elements) {
            Permissible permissible = null;
            String type = (String) map.getOrDefault("type", null);
            if (type != null) {
                Optional<PermissibleLoader> optional = getPermission(type);
                if (optional.isPresent()) {
                    PermissibleLoader permissibleLoader = optional.get();
                    permissible = permissibleLoader.load(path, new TypedMapAccessor(map), file);
                }
            }
            if (permissible != null && permissible.isValid()) {
                permissibles.add(permissible);
            } else {
                Logger.info("Error, an element is invalid in " + path + " for permissible", Logger.LogType.ERROR);
            }
        }
        return permissibles;
    }

    @Override
    public List<Permissible> loadPermissible(YamlConfiguration configuration, String path, File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return loadPermissible(elements, path, file);
    }

    @Override
    public List<Action> loadActions(List<Map<String, Object>> elements, String path, File file) {
        List<Action> actions = new ArrayList<>();
        for (Map<String, Object> map : elements) {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) {
                Logger.info("Error, an element is invalid in " + path + ", type is invalid", Logger.LogType.ERROR);
                continue;
            }
            Optional<ActionLoader> optional = getActionLoader(type);
            if (optional.isPresent()) {
                ActionLoader actionLoader = optional.get();
                TypedMapAccessor accessor = new TypedMapAccessor(map);
                Action action = actionLoader.load(path, accessor, file);
                if (action != null) {
                    action.setDelay(accessor.getInt("delay", 0));
                    action.setChance(accessor.getFloat("chance", 100));
                    List<Map<String, Object>> denyChanceAction = accessor.getMapList("deny-chance-actions");
                    if (!denyChanceAction.isEmpty()) {
                        List<Action> denyActions = loadActions(denyChanceAction, path + ".deny-chance-actions", file);
                        if (!denyActions.isEmpty()) {
                            action.setDenyChanceActions(denyActions);
                        }
                    }
                    actions.add(action);
                }
            } else {
                Logger.info("Error, an element is invalid in " + path + " with type " + type + ", he doesn't exist!", Logger.LogType.ERROR);
            }
        }
        return actions;
    }

    @Override
    public List<Action> loadActions(YamlConfiguration configuration, String path, File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return loadActions(elements, path, file);
    }

    @Override
    public List<Requirement> loadRequirements(YamlConfiguration configuration, String path, File file) throws InventoryException {
        if (configuration == null) return List.of();

        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section == null) return List.of();

        List<Requirement> requirements = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            requirements.add(loadRequirement(configuration, path + "." + key + ".", file));
        }
        return requirements;
    }

    @Override
    public Requirement loadRequirement(YamlConfiguration configuration, String path, File file) throws InventoryException {
        Loader<Requirement> requirementLoader = new RequirementLoader(this.plugin);
        return requirementLoader.load(configuration, path, file);
    }

    @Override
    public List<String> getEmptyActions(List<Map<String, Object>> elements) {
        List<String> invalid = new ArrayList<>();
        for (Map<String, Object> element : elements) {
            Object typeObj = element.get("type");
            if (typeObj instanceof String type && getActionLoader(type).isEmpty()) {
                invalid.add(type);
            }
        }
        return invalid;
    }

    @Override
    public List<String> getEmptyPermissible(List<Map<String, Object>> elements) {
        List<String> invalid = new ArrayList<>();
        for (Map<String, Object> element : elements) {
            Object typeObj = element.get("type");
            if (typeObj instanceof String type && getPermission(type).isEmpty()) {
                invalid.add(type);
            }
        }
        return invalid;
    }

    @Override
    public Loader<Button> getLoaderButton(MenuPlugin menuPlugin, File file, int size, Map<Character, List<Integer>> matrix) {
        return new ZButtonLoader(menuPlugin, file, size, matrix);
    }
}
