package fr.maxlego08.menu.loader;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.action.ActionLoader;
import fr.maxlego08.menu.action.loader.ActionPlayerDataLoader;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.button.ZPermissibleButton;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZButtonLoader implements Loader<Button> {

    private final MenuPlugin plugin;
    private final File file;
    private final int inventorySize;

    /**
     * @param plugin
     * @param file
     * @param inventorySize
     */
    public ZButtonLoader(MenuPlugin plugin, File file, int inventorySize) {
        super();
        this.plugin = plugin;
        this.file = file;
        this.inventorySize = inventorySize;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        String buttonType = configuration.getString(path + "type", "NONE");
        String buttonName = (String) objects[0];

        ButtonManager buttonManager = this.plugin.getButtonManager();
        Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

        if (!optional.isPresent()) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path
                    + " in inventory " + this.file.getAbsolutePath());
        }

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ButtonLoader loader = optional.get();
        ZButton button = (ZButton) loader.load(configuration, path);
        button.setPlugin(this.plugin);

        int slot = 0;
        int page;

        try {

            String slotString = configuration.getString(path + "slot", "0");
            if (slotString != null && slotString.contains("-")) {

                String[] strings = slotString.split("-");
                page = Integer.parseInt(strings[0]);
                slot = Integer.parseInt(strings[1]);

            } else {
                slot = configuration.getInt(path + "slot", 0);
                page = configuration.getInt(path + "page", 1);
            }

        } catch (Exception e) {
            slot = configuration.getInt(path + "slot", 0);
            page = configuration.getInt(path + "page", 1);
        }

        page = Math.max(page, 1);
        slot = slot + ((page - 1) * this.inventorySize);

        List<String> slotsAsString = configuration.getStringList(path + "slots");
        List<Integer> slots = ButtonLoader.loadSlot(slotsAsString);

        button.setSlots(slots);
        button.setSlot(slot);
        button.setPermanent(configuration.getBoolean(path + "isPermanent", false));
        button.setCloseInventory(configuration.getBoolean(path + "closeInventory", false));
        button.setItemStack(itemStackLoader.load(configuration, path + "item."));
        button.setButtonName(buttonName);
        button.setMessages(configuration.getStringList(path + "messages"));

        String playerHead = configuration.getString(path + "playerHead",
                configuration.getString(path + "item.playerHead", null));
        button.setPlayerHead(playerHead);

        button.setUpdated(configuration.getBoolean(path + "update", false));
        button.setRefreshOnClick(configuration.getBoolean(path + "refreshOnClick", false));

        if (configuration.contains(path + "openLink")) {

            Loader<OpenLink> loaderLink = new OpenLinkLoader();
            button.setOpenLink(loaderLink.load(configuration, path + "openLink."));

        }

        Optional<XSound> optionalXSound = XSound.matchXSound(configuration.getString(path + "sound", null));

        if (optionalXSound.isPresent()) {
            XSound xSound = optionalXSound.get();
            float pitch = Float.parseFloat(configuration.getString(path + "pitch", "1.0f"));
            float volume = Float.parseFloat(configuration.getString(path + "volume", "1.0f"));
            button.setSoundOption(new ZSoundOption(xSound, pitch, volume));
        }

        Loader<ActionPlayerData> loaderActions = new ActionPlayerDataLoader();

        List<ActionPlayerData> actionPlayerDatas = new ArrayList<ActionPlayerData>();
        if (configuration.isConfigurationSection(path + "datas")) {
            for (String key : configuration.getConfigurationSection(path + "datas.").getKeys(false)) {

                ActionPlayerData actionPlayerData = loaderActions.load(configuration, path + "datas." + key + ".");
                actionPlayerDatas.add(actionPlayerData);

            }
        }

        button.setDatas(actionPlayerDatas);
        button.setPermission(configuration.getString(path + "permission", null));

        if (configuration.contains(path + "else")) {

            Button elseButton = this.load(configuration, path + "else.", buttonName + ".else");
            button.setElseButton(elseButton);

            if (elseButton instanceof PermissibleButton) {
                ZPermissibleButton elsePermissibleButton = (ZPermissibleButton) elseButton;
                elsePermissibleButton.setParentButton(button);
            }

        }

        // Placeholder
        button.setPlaceholder(configuration.getString(path + "placeHolder", null));
        button.setAction(PlaceholderAction.from(configuration.getString(path + "action", null)));
        button.setValue(configuration.getString(path + "value", null));

        // Perform
        List<String> commands = configuration.getStringList(path + "commands");
        List<String> consoleCommands = configuration.getStringList(path + "consoleCommands");
        List<String> consoleRightCommands = configuration.getStringList(path + "consoleRightCommands");
        List<String> consoleLeftCommands = configuration.getStringList(path + "consoleLeftCommands");
        List<String> consolePermissionCommands = configuration.getStringList(path + "consolePermissionCommands");
        String consolePermission = configuration.getString(path + "consolePermission");

        List<Action> actions = new ArrayList<Action>();
        Loader<Action> actiuonLoader = new ActionLoader(this.plugin);

        if (configuration.isConfigurationSection(path + "actions.")) {
            ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "actions.");
            for (String key : configurationSection.getKeys(false)) {

                try {
                    actions.add(actiuonLoader.load(configuration, path + "actions." + key + "."));
                } catch (InventoryException e) {
                    e.printStackTrace();
                }
            }
        }

        button.setCommands(commands);
        button.setConsoleCommands(consoleCommands);
        button.setConsoleRightCommands(consoleRightCommands);
        button.setConsoleLeftCommands(consoleLeftCommands);
        button.setConsolePermissionCommands(consolePermissionCommands);
        button.setConsolePermission(consolePermission);
        button.setActions(actions);

        return button;
    }

    @Override
    public void save(Button object, YamlConfiguration configuration, String path, Object... objects) {

    }

}
