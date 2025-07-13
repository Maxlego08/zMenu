package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.exceptions.ButtonAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

        var optional = loaders.entrySet().stream().flatMap(e -> e.getValue().stream()).filter(e -> e.getName().equalsIgnoreCase(button.getName())).findFirst();
        if (optional.isPresent()) {
            var loader = optional.get();
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
        return this.loaders.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public Collection<ButtonLoader> getLoaders(Plugin plugin) {
        return this.loaders.getOrDefault(plugin.getName(), new ArrayList<>());
    }

    @Override
    public Optional<ButtonLoader> getLoader(String name) {
        return this.getLoaders().stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
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
        return elements.stream().map(map -> {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) return null;
            Optional<PermissibleLoader> optional = getPermission(type);
            if (optional.isPresent()) {
                PermissibleLoader permissibleLoader = optional.get();
                return permissibleLoader.load(path, new TypedMapAccessor(map), file);
            }
            return null;
        }).filter(element -> {
            if (element != null && element.isValid()) return true;
            Logger.info("Error, an element is invalid in " + path + " for permissible", Logger.LogType.ERROR);
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Permissible> loadPermissible(YamlConfiguration configuration, String path, File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return loadPermissible(elements, path, file);
    }

    @Override
    public List<Action> loadActions(List<Map<String, Object>> elements, String path, File file) {
        return elements.stream().map(map -> {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) {
                Logger.info("Error, an element is invalid in " + path + ", type is invalid", Logger.LogType.ERROR);
                return null;
            }
            Optional<ActionLoader> optional = getActionLoader(type);
            if (optional.isPresent()) {
                ActionLoader actionLoader = optional.get();
                TypedMapAccessor accessor = new TypedMapAccessor(map);
                Action action = actionLoader.load(path, accessor, file);
                if (action != null) {
                    action.setDelay(accessor.getInt("delay", 0));
                }
                return action;
            }
            Logger.info("Error, an element is invalid in " + path + " with type " + type + ", he doesn't exist!", Logger.LogType.ERROR);
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Action> loadActions(YamlConfiguration configuration, String path, File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return loadActions(elements, path, file);
    }

    @Override
    public List<String> getEmptyActions(List<Map<String, Object>> elements) {
        return elements.stream().map(element -> (String) element.get("type")).filter(Objects::nonNull).filter(type -> getActionLoader(type).isEmpty()).collect(Collectors.toList());
    }

    @Override
    public List<String> getEmptyPermissible(List<Map<String, Object>> elements) {
        return elements.stream().map(element -> (String) element.get("type")).filter(Objects::nonNull).filter(type -> getPermission(type).isEmpty()).collect(Collectors.toList());
    }
}
