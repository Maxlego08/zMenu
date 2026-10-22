package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.loader.RequirementLoader;
import fr.maxlego08.menu.loader.ZButtonLoader;
import fr.maxlego08.menu.registry.RegistryOwners;
import fr.maxlego08.menu.registry.ZRegistryManager;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import net.kyori.adventure.key.Key;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.*;

public class ZButtonManager extends ZUtils implements ButtonManager {

    private final ZMenuPlugin plugin;

    public ZButtonManager(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(@NonNull ButtonLoader button) {
        RegistryOwners.registerLegacy(this.registries().writable(Registries.BUTTON), button.getPlugin().getName(), button, button.aliases());
    }

    @Override
    public void unregister(@NotNull ButtonLoader button) {
        if (this.registries().registry(Registries.BUTTON).containsValue(button)) {
            this.registries().writable(Registries.BUTTON).unregister(button.key());
        }
    }

    @Override
    public void unregisters(@NotNull Plugin plugin) {
        this.registries().writable(Registries.BUTTON).unregisterAll(plugin);
    }

    @Override
    public @NonNull Collection<ButtonLoader> getLoaders() {
        return this.registries().registry(Registries.BUTTON).values();
    }

    @Override
    public @NotNull Collection<ButtonLoader> getLoaders(@NotNull Plugin plugin) {
        KeyedRegistry<ButtonLoader> registry = this.registries().registry(Registries.BUTTON);
        return registry.stream().filter(loader -> registry.owner(loader.key()).filter(plugin.getName()::equals).isPresent()).toList();
    }

    @Override
    public @NotNull Optional<ButtonLoader> getLoader(String name) {
        Key id = name == null ? null : ZMenuKeys.tryParse(name);
        KeyedRegistry<ButtonLoader> registry = this.registries().registry(Registries.BUTTON);
        Optional<ButtonLoader> loader = id == null ? Optional.empty() : registry.get(id);
        if (loader.isEmpty()) {
            Set<String> names = new TreeSet<>();
            registry.keys().forEach(key -> names.add(key.value()));
            registry.aliases().keySet().forEach(key -> names.add(key.value()));
            Logger.info("Button " + name + " not found. Available buttons: " + String.join(", ", names), Logger.LogType.ERROR);
        }
        return loader;
    }

    @Override
    public void registerPermissible(@NotNull PermissibleLoader permissibleLoader) {
        RegistryOwners.registerLegacy(this.registries().writable(Registries.PERMISSIBLE), permissibleLoader, List.of());
    }

    @Override
    public @NotNull Map<String, PermissibleLoader> getPermissibles() {
        Map<String, PermissibleLoader> permissibles = new LinkedHashMap<>();
        for (PermissibleLoader loader : this.registries().registry(Registries.PERMISSIBLE)) {
            permissibles.put(loader.getKey().toLowerCase(Locale.ROOT), loader);
        }
        return permissibles;
    }

    @Override
    public @NotNull Optional<PermissibleLoader> getPermission(@NotNull String key) {
        Key id = ZMenuKeys.tryParse(key);
        return id == null ? Optional.empty() : this.registries().registry(Registries.PERMISSIBLE).get(id);
    }

    private ZRegistryManager registries() {
        return this.plugin.getInventoryManager().getRegistries();
    }

    @Override
    public void registerAction(@NotNull ActionLoader actionLoader) {
        RegistryOwners.registerLegacy(this.registries().writable(Registries.ACTION), actionLoader, actionLoader.aliases());
    }

    @Override
    public void registerRuleLoader(@NotNull RuleLoader ruleLoader) {
        ZRuleLoaderRegistry.getInstance().register(ruleLoader);
    }

    @Override
    public @NotNull Optional<ActionLoader> getActionLoader(@NotNull String key) {
        Key id = ZMenuKeys.tryParse(key);
        return id == null ? Optional.empty() : this.registries().registry(Registries.ACTION).get(id);
    }

    @Override
    public @NotNull List<Permissible> loadPermissible(@NotNull List<@NotNull Map<String, Object>> elements, @NotNull String path, @NotNull File file) {
        List<Permissible> permissibles = new ArrayList<>();
        for (Map<String, Object> map : elements) {
            Permissible permissible = null;
            String type = (String) map.getOrDefault("type", null);
            if (type != null) {
                Optional<PermissibleLoader> optional = this.getPermission(type);
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

    @Override @SuppressWarnings("unchecked")
    public @NotNull List<Permissible> loadPermissible(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return this.loadPermissible(elements, path, file);
    }

    @Override
    public @NotNull List<@NotNull Action> loadActions(@NotNull List<@NotNull Map<String, Object>> elements, @NotNull String path, @NotNull File file) {
        return this.loadActions(elements, path, file, new ArrayList<>(), true, true);
    }

    @Override
    public @NotNull List<@NotNull Action> loadActions(@NotNull List<Map<String, Object>> elements, @NotNull String path, @NotNull File file, @NotNull List<ActionPattern> defaultActions, boolean useSuccess, boolean stopOnEmpty) {
        List<Action> actions = new ArrayList<>();
        Set<String> seenTypes = new HashSet<>();
        for (Map<String, Object> map : elements) {
            String type = (String) map.getOrDefault("type", null);
            if (type == null) {
                Logger.info("Error, an element is invalid in " + path + ", type is invalid", Logger.LogType.ERROR);
                continue;
            }
            Optional<ActionLoader> optional = this.getActionLoader(type);
            if (optional.isPresent()) {
                ActionLoader actionLoader = optional.get();
                TypedMapAccessor accessor = new TypedMapAccessor(map);
                Action action = actionLoader.load(path, accessor, file);
                if (action != null) {
                    action.setDelay(accessor.getInt("delay", 0));
                    action.setChance(accessor.getFloat("chance", 100));
                    action.setDebug(accessor.getBoolean("debug", false));
                    action.setType(type);
                    List<Map<String, Object>> denyChanceAction = accessor.getMapList("deny-chance-actions");
                    if (!denyChanceAction.isEmpty()) {
                        List<Action> denyActions = this.loadActions(denyChanceAction, path + ".deny-chance-actions", file, defaultActions, useSuccess, stopOnEmpty);
                        if (!denyActions.isEmpty()) {
                            action.setDenyChanceActions(denyActions);
                        }
                    }
                    actions.add(action);
                    seenTypes.add(type);
                }
            } else {
                Logger.info("Error, an element is invalid in " + path + " with type " + type + ", he doesn't exist!", Logger.LogType.ERROR);
            }
        }
        if (stopOnEmpty && actions.isEmpty()) {
            return actions;
        }
        for (ActionPattern actionPattern : defaultActions) {
            for (Action action : useSuccess ? actionPattern.actions() : actionPattern.denyActions()){
                if (!seenTypes.contains(action.getType())){
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override @SuppressWarnings("unchecked")
    public @NonNull List<Action> loadActions(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull File file) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return this.loadActions(elements, path, file);
    }

    @Override @SuppressWarnings("unchecked")
    public @NonNull List<Action> loadActions(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull File file, @NotNull List<ActionPattern> defaultActions, boolean useSuccess, boolean stopOnEmpty) {
        List<Map<String, Object>> elements = (List<Map<String, Object>>) configuration.getList(path, new ArrayList<>());
        return this.loadActions(elements, path, file, defaultActions, useSuccess, stopOnEmpty);
    }

    @Override
    public @NotNull List<Requirement> loadRequirements(@Nullable YamlConfiguration configuration, @NotNull String path, @NotNull File file) throws InventoryException {
        if (configuration == null) return List.of();

        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section == null) return List.of();

        List<Requirement> requirements = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            requirements.add(this.loadRequirement(configuration, path + "." + key + ".", file));
        }
        return requirements;
    }

    @Override
    public @NotNull Requirement loadRequirement(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) throws InventoryException {
        Loader<Requirement> requirementLoader = new RequirementLoader(this.plugin);
        return requirementLoader.load(configuration, path, file);
    }

    @Override
    public @NotNull List<String> getEmptyActions(@NonNull List<Map<String, Object>> elements) {
        List<String> invalid = new ArrayList<>();
        for (Map<String, Object> element : elements) {
            Object typeObj = element.get("type");
            if (typeObj instanceof String type && this.getActionLoader(type).isEmpty()) {
                invalid.add(type);
            }
        }
        return invalid;
    }

    @Override
    public @NotNull List<String> getEmptyPermissible(@NotNull List<Map<String, Object>> elements) {
        List<String> invalid = new ArrayList<>();
        for (Map<String, Object> element : elements) {
            Object typeObj = element.get("type");
            if (typeObj instanceof String type && this.getPermission(type).isEmpty()) {
                invalid.add(type);
            }
        }
        return invalid;
    }

    @Override
    public @NotNull Loader<Button> getLoaderButton(@NotNull MenuPlugin menuPlugin, @NotNull File file, int size, @NotNull Map<Character, @NotNull List<Integer>> matrix) {
        return new ZButtonLoader(menuPlugin, file, size, matrix);
    }
}
