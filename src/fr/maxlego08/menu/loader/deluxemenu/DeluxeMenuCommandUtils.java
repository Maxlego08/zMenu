package fr.maxlego08.menu.loader.deluxemenu;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.requirement.actions.BroadcastAction;
import fr.maxlego08.menu.requirement.actions.BroadcastSoundAction;
import fr.maxlego08.menu.requirement.actions.CloseAction;
import fr.maxlego08.menu.requirement.actions.ConnectAction;
import fr.maxlego08.menu.requirement.actions.ConsoleCommandAction;
import fr.maxlego08.menu.requirement.actions.CurrencyDepositAction;
import fr.maxlego08.menu.requirement.actions.CurrencyWithdrawAction;
import fr.maxlego08.menu.requirement.actions.InventoryAction;
import fr.maxlego08.menu.requirement.actions.MessageAction;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAction;
import fr.maxlego08.menu.requirement.actions.RefreshAction;
import fr.maxlego08.menu.requirement.actions.SoundAction;
import fr.maxlego08.menu.requirement.permissible.ZCurrencyPermissible;
import fr.maxlego08.menu.requirement.permissible.ZItemPermissible;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;
import fr.maxlego08.menu.requirement.permissible.ZRegexPermissible;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.traqueur.currencies.Currencies;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DeluxeMenuCommandUtils extends ZUtils {

    protected List<Action> loadActions(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, List<String> commands) {
        List<Action> actions = new ArrayList<>();

        Map<String, Function<String, Action>> actionMap = new HashMap<>();

        actionMap.put("[close]", cmd -> new CloseAction());
        actionMap.put("[console]", cmd -> new ConsoleCommandAction(Collections.singletonList(removePrefix(cmd, "[console]"))));
        actionMap.put("[player]", cmd -> new PlayerCommandAction(Collections.singletonList(removePrefix(cmd, "[player]")), false));
        actionMap.put("[commandevent]", cmd -> new PlayerCommandAction(Collections.singletonList(removePrefix(cmd, "[commandevent]")), true));
        actionMap.put("[minimessage]", cmd -> new MessageAction(Collections.singletonList(removePrefix(cmd, "[minimessage]")), true));
        actionMap.put("[minibroadcast]", cmd -> new BroadcastAction(Collections.singletonList(removePrefix(cmd, "[minibroadcast]")), true));
        actionMap.put("[message]", cmd -> new MessageAction(Collections.singletonList(color(removePrefix(cmd, "[message]"))), false));
        actionMap.put("[broadcast]", cmd -> new BroadcastAction(Collections.singletonList(color(removePrefix(cmd, "[message]"))), false));
        actionMap.put("[openguimenu]", cmd -> new InventoryAction(inventoryManager, commandManager, removePrefix(cmd, "[openguimenu]"), "zMenu", "1", new ArrayList<>()));
        actionMap.put("[openmenu]", cmd -> new InventoryAction(inventoryManager, commandManager, removePrefix(cmd, "[openmenu]"), "zMenu", "1", new ArrayList<>()));
        actionMap.put("[connect]", cmd -> new ConnectAction(removePrefix(cmd, "[connect]"), plugin));
        actionMap.put("[refresh]", cmd -> new RefreshAction());
        actionMap.put("[broadcastsound]", cmd -> new BroadcastSoundAction(getSoundOption(removePrefix(cmd, "[broadcastsound]"))));
        actionMap.put("[broadcastsoundworld]", cmd -> new BroadcastSoundAction(getSoundOption(removePrefix(cmd, "[broadcastsoundworld]"))));
        actionMap.put("[sound]", cmd -> new SoundAction(getSoundOption(removePrefix(cmd, "[sound]"))));
        actionMap.put("[takemoney]", cmd -> new CurrencyWithdrawAction(removePrefix(cmd, "[takemoney]"), Currencies.VAULT, null));
        actionMap.put("[givemoney]", cmd -> new CurrencyDepositAction(removePrefix(cmd, "[givemoney]"), Currencies.VAULT, null));

        for (String command : commands) {
            CommandDelayResult result = extractAndRemoveDelay(command);
            String cleanedCommand = result.getCommand();
            int delay = result.getDelay();

            for (Map.Entry<String, Function<String, Action>> entry : actionMap.entrySet()) {
                if (cleanedCommand.startsWith(entry.getKey())) {
                    String finalCommand = removePrefix(cleanedCommand, entry.getKey());
                    Action action = entry.getValue().apply(finalCommand);

                    if (delay > 0) {
                        action.setDelay(delay);
                    }

                    actions.add(action);
                    break;
                }
            }
        }

        return actions;
    }

    private CommandDelayResult extractAndRemoveDelay(String command) {
        Pattern delayPattern = Pattern.compile("<delay=(\\d+)>");
        Matcher matcher = delayPattern.matcher(command);
        int delay = 0; // Valeur par défaut du délai

        if (matcher.find()) {
            delay = Integer.parseInt(matcher.group(1));
            command = matcher.replaceFirst("").trim();
        }

        return new CommandDelayResult(command, delay);
    }

    private String removePrefix(String command, String prefix) {
        return command.replace(prefix + " ", "").trim();
    }

    private SoundOption getSoundOption(String command) {
        String[] values = command.split(" ");
        if (values.length == 0) return null;
        String sound = values[0];
        float volume = values.length >= 2 ? Float.parseFloat(values[1]) : 1f;
        float pitch = values.length >= 3 ? Float.parseFloat(values[2]) : 1f;
        Optional<XSound> optionalXSound = sound == null || sound.isEmpty() ? Optional.empty() : XSound.of(sound);
        return optionalXSound.map(xSound -> new ZSoundOption(xSound, XSound.Category.MASTER, null, volume, pitch, false)).orElseGet(() -> new ZSoundOption(null, XSound.Category.MASTER, sound, volume, pitch, true));
    }

    protected List<Permissible> loadPermissibles(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, ConfigurationSection configurationSection) {
        List<Permissible> permissibles = new ArrayList<>();

        for (String key : configurationSection.getKeys(false)) {

            ConfigurationSection requirementSection = configurationSection.getConfigurationSection(key);
            if (requirementSection == null) continue;

            Permissible permissible = loadPermissible(inventoryManager, commandManager, plugin, requirementSection);
            if (permissible != null) {
                permissibles.add(permissible);
            }
        }

        return permissibles;
    }

    protected Permissible loadPermissible(InventoryManager inventoryManager, CommandManager commandManager, Plugin plugin, ConfigurationSection section) {
        String type = section.getString("type");
        if (type == null) return null;

        List<Action> denyActions = loadActions(inventoryManager, commandManager, plugin, section.getStringList("deny_commands"));

        String permission = section.getString("permission");
        switch (type.toLowerCase()) {
            case "has permission":
            case "has perm":
            case "haspermission":
            case "hasperm":
            case "perm":
                return new ZPermissionPermissible(permission, denyActions, new ArrayList<>());

            case "!has permission":
            case "!has perm":
            case "!haspermission":
            case "!hasperm":
            case "!perm":
                return new ZPermissionPermissible("!" + permission, denyActions, new ArrayList<>());

            case "string contains":
            case "stringcontains":
            case "contains":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.CONTAINS_STRING);

            case "string equals":
            case "stringequals":
            case "equals":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.EQUALS_STRING);

            case "stringequalsignorecase":
            case "string equals ignorecase":
            case "equalsignorecase":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.EQUALSIGNORECASE_STRING);

            case ">":
            case "greater than":
            case "greaterthan":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.SUPERIOR);

            case ">=":
            case "greater than or equal to":
            case "greaterthanorequalto":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.SUPERIOR_OR_EQUAL);

            case "==":
            case "equal to":
            case "equalto":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.EQUAL_TO);

            case "<=":
            case "less than or equal to":
            case "lessthanorequalto":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.LOWER_OR_EQUAL);

            case "<":
            case "less than":
            case "lessthan":
                return createPlaceholderPermissible(section, denyActions, PlaceholderAction.LOWER);

            case "regex matches":
            case "regex":
                String input = section.getString("input");
                String regex = section.getString("regex");
                return new ZRegexPermissible(regex, input, denyActions, new ArrayList<>());

            case "has money":
            case "hasmoney":
            case "money": {
                String amount = section.getString("amount");
                return new ZCurrencyPermissible(denyActions, new ArrayList<>(), Currencies.VAULT, amount, null);
            }

            case "has item":
            case "item":
            case "hasitem": {
                MenuItemStack menuItemStack = new MenuItemStack(inventoryManager, "", "");
                int amount = section.getInt("amount");
                menuItemStack.setMaterial(section.getString("material"));
                return new ZItemPermissible(menuItemStack, amount, denyActions, new ArrayList<>(), ItemVerification.SIMILAR);
            }

            default:
                return null;
        }
    }

    private ZPlaceholderPermissible createPlaceholderPermissible(ConfigurationSection section, List<Action> denyActions, PlaceholderAction action) {
        String input = section.getString("input");
        String output = section.getString("output");
        return new ZPlaceholderPermissible(action, input, output, null, denyActions, new ArrayList<>());
    }

    private static class CommandDelayResult {
        private final String command;
        private final int delay;

        public CommandDelayResult(String command, int delay) {
            this.command = command;
            this.delay = delay;
        }

        public String getCommand() {
            return command;
        }

        public int getDelay() {
            return delay;
        }
    }


}
