package fr.maxlego08.menu.loader.deluxemenu;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.requirement.ZRequirement;
import fr.maxlego08.menu.requirement.actions.*;
import fr.maxlego08.menu.requirement.permissible.*;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.traqueur.currencies.Currencies;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns DeluxeMenus click commands and requirements into zMenu actions and permissibles, following
 * the DeluxeMenus source: the {@code [tag]} must start the line, {@code <delay=>} and
 * {@code <chance=>} may appear anywhere after it, and requirement types match exactly, ignoring case.
 * <p>
 * What zMenu cannot reproduce fails closed: an unsupported action stops the actions after it and an
 * unsupported requirement denies. Every approximation or unsupported feature is recorded in
 * {@link #getWarnings()} and logged.
 */
public abstract class DeluxeMenuCommandUtils extends ZUtils {

    private static final Pattern TAG_PATTERN = Pattern.compile("^\\[([a-zA-Z_]+)]");
    private static final Pattern DELAY_PATTERN = Pattern.compile("<delay=([^<>]+)>");
    private static final Pattern CHANCE_PATTERN = Pattern.compile("<chance=([^<>]+)>");

    protected final List<String> warnings;

    protected DeluxeMenuCommandUtils(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getWarnings() {
        return this.warnings;
    }

    protected void warn(File file, String message) {
        String line = file.getName() + ": " + message;
        this.warnings.add(line);
        Logger.info("[DeluxeMenus] " + line, Logger.LogType.WARNING);
    }

    protected List<Action> loadActions(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, List<String> commands, File file) {
        List<Action> actions = new ArrayList<>();
        for (String command : commands) {
            Action action = this.loadAction(inventoryManager, commandManager, plugin, command, file);
            if (action != null) actions.add(action);
        }
        return actions;
    }

    private Action loadAction(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, String command, File file) {
        String line = command.trim();
        Matcher tagMatcher = TAG_PATTERN.matcher(line);
        if (!tagMatcher.find()) {
            this.warn(file, "the command \"" + command + "\" does not start with an action tag, DeluxeMenus ignores it");
            return null;
        }

        String tag = tagMatcher.group(1).toLowerCase(Locale.ROOT);
        String value = line.substring(tagMatcher.end());

        Integer delay = null;
        Matcher delayMatcher = DELAY_PATTERN.matcher(value);
        if (delayMatcher.find()) {
            delay = this.parseTicks(delayMatcher.group(1), command, file);
            value = delayMatcher.replaceFirst("");
        }
        Float chance = null;
        Matcher chanceMatcher = CHANCE_PATTERN.matcher(value);
        if (chanceMatcher.find()) {
            chance = this.parseChance(chanceMatcher.group(1), command, file);
            value = chanceMatcher.replaceFirst("");
        }
        value = value.trim();

        Action action = this.createAction(inventoryManager, commandManager, plugin, tag, value, command, file);
        if (delay != null && delay > 0) action.setDelay(delay);
        action.setChance(chance == null ? 100 : chance);
        return action;
    }

    private Action createAction(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, String tag, String value, String command, File file) {
        return switch (tag) {
            case "close" -> new CloseAction();
            case "console" -> new ConsoleCommandAction(List.of(value));
            case "player" -> new PlayerCommandAction(List.of(value), false);
            case "commandevent" -> new PlayerCommandAction(List.of(value), true);
            case "chat" -> new PlayerChatAction(List.of(value));
            case "message" -> new MessageAction(List.of(this.color(value)), false);
            case "minimessage" -> new MessageAction(List.of(value), true);
            case "broadcast" -> new BroadcastMessageAction(List.of(this.color(value)), false);
            case "minibroadcast" -> new BroadcastMessageAction(List.of(value), true);
            case "actionbar" -> new ActionBarAction(this.color(value), false);
            case "openguimenu", "openmenu" -> {
                String[] parts = value.split(" ");
                List<String> arguments = parts.length > 1 ? List.of(Arrays.copyOfRange(parts, 1, parts.length)) : new ArrayList<>();
                yield new InventoryAction(inventoryManager, commandManager, parts[0], "zMenu", "1", arguments);
            }
            case "connect" -> new ConnectAction(value, plugin);
            case "refresh" -> new RefreshInventoryAction();
            case "sound", "rawsound" -> new SoundAction(this.getSoundOption(value));
            case "broadcastsound", "broadcastrawsound" -> new BroadcastSoundAction(this.getSoundOption(value));
            case "broadcastsoundworld", "broadcastrawsoundworld" -> {
                this.warn(file, "\"" + command + "\" plays to the whole server in zMenu, not only to the player's world");
                yield new BroadcastSoundAction(this.getSoundOption(value));
            }
            case "takemoney" -> new CurrencyWithdrawAction(value, Currencies.VAULT, null, "no reason");
            case "givemoney" -> new CurrencyDepositAction(value, Currencies.VAULT, null, "no reason");
            case "giveexp", "takeexp" -> this.createExpAction(tag.equals("takeexp"), value, command, file);
            default -> {
                this.warn(file, "the action \"" + command + "\" is not supported, the actions after it will not run");
                yield new UnsupportedDeluxeMenusAction(command);
            }
        };
    }

    private Action createExpAction(boolean take, String value, String command, File file) {
        String amount = value.trim();
        boolean levels = amount.toUpperCase(Locale.ROOT).endsWith("L");
        if (levels) amount = amount.substring(0, amount.length() - 1);
        if (amount.isEmpty()) {
            this.warn(file, "the action \"" + command + "\" has no amount, the actions after it will not run");
            return new UnsupportedDeluxeMenusAction(command);
        }
        String signedAmount = take ? "-" + amount : amount;
        return new ConsoleCommandAction(List.of("xp add %player_name% " + signedAmount + (levels ? " levels" : " points")));
    }

    private Integer parseTicks(String value, String command, File file) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            this.warn(file, "the delay of \"" + command + "\" is not a number, zMenu runs the action without delay");
            return null;
        }
    }

    private Float parseChance(String value, String command, File file) {
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException exception) {
            this.warn(file, "the chance of \"" + command + "\" is not a number, zMenu always runs the action");
            return null;
        }
    }

    private SoundOption getSoundOption(String command) {
        String[] values = command.split(" ");
        String sound = values[0];
        float volume = values.length >= 2 ? this.parseFloat(values[1]) : 1f;
        float pitch = values.length >= 3 ? this.parseFloat(values[2]) : 1f;
        Optional<XSound> optionalXSound = sound.isEmpty() ? Optional.empty() : XSound.of(sound);
        return optionalXSound.map(xSound -> new ZSoundOption(xSound, XSound.Category.MASTER.name(), sound, pitch, volume, false)).orElseGet(() -> new ZSoundOption(null, XSound.Category.MASTER.name(), sound, pitch, volume, true));
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException exception) {
            return 1f;
        }
    }

    protected Requirement loadRequirement(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, List<Action> actions, ConfigurationSection section, List<ClickType> clickTypes, File file) {
        if (section == null) {
            return new ZRequirement(0, new ArrayList<>(), new ArrayList<>(), actions, clickTypes);
        }

        Set<Permissible> optional = Collections.newSetFromMap(new IdentityHashMap<>());
        List<Permissible> permissibles = new ArrayList<>();
        ConfigurationSection requirementsSection = section.getConfigurationSection("requirements");
        if (requirementsSection != null) {
            permissibles = this.loadPermissibles(inventoryManager, commandManager, plugin, requirementsSection, optional, file);
        }

        int minimum = permissibles.size();
        if (!optional.isEmpty()) {
            int minimumRequirements = section.getInt("minimum_requirements", 0);
            List<Permissible> required = permissibles.stream().filter(permissible -> !optional.contains(permissible)).toList();
            if (required.isEmpty()) {
                minimum = minimumRequirements;
            } else if (minimumRequirements <= required.size()) {
                this.warn(file, "the optional requirements of " + section.getCurrentPath() + " cannot change the outcome, their commands are not converted");
                permissibles = new ArrayList<>(required);
                minimum = permissibles.size();
            } else {
                this.warn(file, "the requirements of " + section.getCurrentPath() + " mix optional and required checks, zMenu requires all of them");
            }
        }
        if (section.getBoolean("stop_at_success", false)) {
            this.warn(file, "stop_at_success in " + section.getCurrentPath() + " is not supported, every requirement is checked");
        }

        List<Action> denyActions = this.loadActions(inventoryManager, commandManager, plugin, section.getStringList("deny_commands"), file);
        return new ZRequirement(minimum, permissibles, denyActions, actions, clickTypes);
    }


    protected List<Permissible> loadPermissibles(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, ConfigurationSection configurationSection, Set<Permissible> optional, File file) {
        List<Permissible> permissibles = new ArrayList<>();

        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection requirementSection = configurationSection.getConfigurationSection(key);
            if (requirementSection == null) continue;

            List<Permissible> loaded = this.loadPermissible(inventoryManager, commandManager, plugin, requirementSection, file);
            permissibles.addAll(loaded);
            if (requirementSection.getBoolean("optional", false)) optional.addAll(loaded);
        }

        return permissibles;
    }

    private List<Permissible> loadPermissible(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, ConfigurationSection section, File file) {
        String rawType = section.getString("type");
        List<Action> denyActions = this.loadActions(inventoryManager, commandManager, plugin, section.getStringList("deny_commands"), file);
        List<Action> successActions = this.loadActions(inventoryManager, commandManager, plugin, section.getStringList("success_commands"), file);

        if (rawType == null) {
            return List.of(this.unsupported(section, "without a type", denyActions, file));
        }

        String type = rawType.toLowerCase(Locale.ROOT).trim();
        boolean negated = type.startsWith("!");
        if (negated) type = type.substring(1).trim();

        String input = section.getString("input");
        String output = section.getString("output");

        switch (type) {
            case "has permission", "has perm", "haspermission", "hasperm", "perm" -> {
                String permission = section.getString("permission");
                return List.of(new ZPermissionPermissible(negated ? "!" + permission : permission, denyActions, successActions));
            }
            case "has permissions", "has perms", "haspermissions", "hasperms", "perms" -> {
                List<String> permissions = section.getStringList("permissions");
                int minimum = section.getInt("minimum", permissions.size());
                if (negated || minimum < permissions.size()) break;
                List<Permissible> permissibles = new ArrayList<>(permissions.size());
                for (int index = 0; index < permissions.size(); index++) {
                    permissibles.add(index == 0 ? new ZPermissionPermissible(permissions.get(index), denyActions, successActions) : new ZPermissionPermissible(permissions.get(index)));
                }
                return permissibles;
            }
            case "string equals", "stringequals", "equals" -> {
                return List.of(this.placeholder(negated ? PlaceholderAction.DIFFERENT_STRING : PlaceholderAction.EQUALS_STRING, input, output, denyActions, successActions));
            }
            case "string contains", "stringcontains", "contains" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.CONTAINS_STRING, input, output, denyActions, successActions));
                if (isLiteral(output)) return List.of(new ZRegexPermissible("(?s)^(?!.*" + Pattern.quote(output) + ")", input, denyActions, successActions));
            }
            case "string equals ignorecase", "stringequalsignorecase", "equalsignorecase" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.EQUALSIGNORECASE_STRING, input, output, denyActions, successActions));
                if (isLiteral(output)) return List.of(new ZRegexPermissible("(?i)^(?!" + Pattern.quote(output) + "$)", input, denyActions, successActions));
            }
            case "string contains ignorecase", "stringcontainsignorecase", "containsignorecase" -> {
                if (isLiteral(output)) {
                    String regex = negated ? "(?is)^(?!.*" + Pattern.quote(output) + ")" : "(?i)" + Pattern.quote(output);
                    return List.of(new ZRegexPermissible(regex, input, denyActions, successActions));
                }
            }
            case ">", "greater than", "greaterthan" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.SUPERIOR, input, output, denyActions, successActions));
            }
            case ">=", "greater than or equal to", "greaterthanorequalto" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.SUPERIOR_OR_EQUAL, input, output, denyActions, successActions));
            }
            case "==", "equal to", "equalto" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.EQUAL_TO, input, output, denyActions, successActions));
            }
            case "<=", "less than or equal to", "lessthanorequalto" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.LOWER_OR_EQUAL, input, output, denyActions, successActions));
            }
            case "<", "less than", "lessthan" -> {
                if (!negated) return List.of(this.placeholder(PlaceholderAction.LOWER, input, output, denyActions, successActions));
            }
            case "!=", "not equal to", "notequalto" -> {
                this.warn(file, "the requirement \"" + rawType + "\" compares the values as text in zMenu");
                return List.of(this.placeholder(PlaceholderAction.DIFFERENT_STRING, input, output, denyActions, successActions));
            }
            case "regex matches", "regex" -> {
                String regex = section.getString("regex");
                if (regex != null) {
                    return List.of(new ZRegexPermissible(negated ? "(?s)^(?!.*(?:" + regex + "))" : regex, input, denyActions, successActions));
                }
            }
            case "string length" -> {
                int min = section.getInt("min", 0);
                String max = section.contains("max") ? String.valueOf(section.getInt("max")) : "";
                if (!negated) return List.of(new ZRegexPermissible("(?s)^.{" + min + "," + max + "}$", input, denyActions, successActions));
            }
            case "has money", "hasmoney", "money" -> {
                String amount = section.getString("amount", section.getString("placeholder"));
                if (!negated && amount != null) return List.of(new ZCurrencyPermissible(denyActions, successActions, Currencies.VAULT, amount, null));
            }
            case "has exp", "hasexp", "exp" -> {
                if (!negated && section.getBoolean("level", false)) {
                    return List.of(this.placeholder(PlaceholderAction.SUPERIOR_OR_EQUAL, "%player_level%", section.getString("amount"), denyActions, successActions));
                }
            }
            case "has item", "hasitem", "item" -> {
                Permissible permissible = this.loadItemPermissible(inventoryManager, section, denyActions, successActions, negated, file);
                if (permissible != null) return List.of(permissible);
            }
            default -> {
            }
        }

        return List.of(this.unsupported(section, "\"" + rawType + "\"", denyActions, file));
    }


    private Permissible loadItemPermissible(InventoryManager inventoryManager, ConfigurationSection section, List<Action> denyActions, List<Action> successActions, boolean negated, File file) {
        String material = section.getString("material");
        boolean needsMore = section.getBoolean("strict", false) || section.contains("name") || section.contains("lore")
                || section.contains("data") || section.getBoolean("armor", false) || section.getBoolean("offhand", false)
                || section.contains("name_contains") || section.contains("lore_contains") || section.contains("model_data_component");
        if (negated || material == null || needsMore) return null;

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("material", material);
        String modelData = section.getString("model_data", section.getString("modeldata"));
        if (modelData != null) item.put("model-id", modelData);
        MenuItemStack menuItemStack = inventoryManager.loadItemStack(file, item);

        int amount = section.getInt("amount", 1);
        ItemVerification verification = modelData != null ? ItemVerification.MODELID : ItemVerification.MATERIAL;
        return new ZItemPermissible(menuItemStack, amount, denyActions, successActions, verification);
    }

    private Permissible unsupported(ConfigurationSection section, String description, List<Action> denyActions, File file) {
        this.warn(file, "the requirement " + section.getName() + " " + description + " is not supported, it always denies");
        return new UnsupportedDeluxeMenusPermissible(section.getName() + " " + description, denyActions);
    }

    private ZPlaceholderPermissible placeholder(PlaceholderAction action, String input, String output, List<Action> denyActions, List<Action> successActions) {
        return new ZPlaceholderPermissible(action, input, output, null, denyActions, successActions, false);
    }

    private static boolean isLiteral(String value) {
        return value != null && !value.contains("%");
    }
}
