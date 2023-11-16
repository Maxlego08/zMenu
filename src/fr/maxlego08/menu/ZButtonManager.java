package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.exceptions.ButtonAlreadyRegisterException;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZButtonManager extends ZUtils implements ButtonManager {

    private final Map<String, List<ButtonLoader>> loaders = new HashMap<>();
    private final Map<String, Class<? extends Permissible>> permissibles = new HashMap<>();
    private final Map<String, ActionLoader> actionsLoader = new HashMap<>();

    @Override
    public void register(ButtonLoader button) {

        Optional<ButtonLoader> optional = this.getLoader(button.getButton());
        if (optional.isPresent()) {
            ButtonLoader loader = optional.get();
            if (loader.getName().equals(button.getName())) {
                throw new ButtonAlreadyRegisterException("Button " + button.getButton().getName()
                        + " was already register in the " + loader.getPlugin().getName());
            }
        }

        Plugin plugin = button.getPlugin();
        List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(plugin.getName(), new ArrayList<>());
        buttonLoaders.add(button);
        this.loaders.put(plugin.getName(), buttonLoaders);
    }

    @Override
    public void unregister(ButtonLoader button) {
        String pluginName = button.getPlugin().getName();
        List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(pluginName, new ArrayList<>());
        buttonLoaders.add(button);
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
    public Optional<ButtonLoader> getLoader(Class<? extends Button> classz) {
        return this.getLoaders().stream().filter(e -> e.getButton().isAssignableFrom(classz)).findFirst();
    }

    @Override
    public Optional<ButtonLoader> getLoader(String name) {
        return this.getLoaders().stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public void registerPermissible(String key, Class<? extends Permissible> pClass) {
        this.permissibles.put(key.toLowerCase(), pClass);
    }

    @Override
    public Map<String, Class<? extends Permissible>> getPermissibles() {
        return this.permissibles;
    }

    @Override
    public Optional<Class<? extends Permissible>> getPermission(String key) {
        return Optional.ofNullable(this.permissibles.getOrDefault(key.toLowerCase(), null));
    }

    @Override
    public void registerAction(ActionLoader actionLoader) {
        this.actionsLoader.put(actionLoader.getKey().toLowerCase(), actionLoader);
    }

    @Override
    public Optional<ActionLoader> getActionLoader(String key) {
        return Optional.ofNullable(this.actionsLoader.getOrDefault(key.toLowerCase(), null));
    }

    @Override
    public List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path) {
        return elements.stream().map(map -> {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) return null;
            Optional<Class<? extends Permissible>> optional = getPermission(type);
            if (optional.isPresent()) {
                Class<? extends Permissible> aClass = optional.get();
                try {
                    return aClass.getConstructor(Map.class).newInstance(map);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        }).filter(element -> {
            if (element != null && element.isValid()) return true;
            Logger.info("Error, an element is invalid in " + path +" for permissible");
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Action> loadActions(List<Map<String, Object>> elements, String path, File file) {
        return elements.stream().map(map -> {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) return null;
            Optional<ActionLoader> optional = getActionLoader(type);
            if (optional.isPresent()) {
                ActionLoader actionLoader = optional.get();
                return actionLoader.load(path, map, file);
            }
            return null;
        }).filter(element -> {
            if (element != null) return true;
            Logger.info("Error, an element is invalid in " + path +" for a success or deny");
            return false;
        }).collect(Collectors.toList());
    }
}
