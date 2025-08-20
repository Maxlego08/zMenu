package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogType;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.DialogFileNotFound;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.body.ItemBodyLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.body.PlainMessageBodyLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderClass;
import fr.maxlego08.menu.hooks.dialogs.loader.input.BooleanInputLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.input.NumberRangeInputLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.input.SingleOptionInputLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.input.TextInputLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.BodyLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogActionIntLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.utils.record.ZDialogInventoryBuild;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ZDialogManager implements DialogManager {
    private final MenuPlugin menuPlugin;
    private static InventoryManager inventoryManager;
    private final Map<String, List<ZDialogs>> dialogs = new HashMap<>();
    private final Map<UUID, ZDialogs> activeDialogs = new HashMap<>();
    private final Map<String, BodyLoader> bodyLoader = new HashMap<>();
    private final Map<String, InputLoader> inputLoader = new HashMap<>();
    private final DialogBuilderClass dialogBuilders;

    private final PaperComponent paperComponent;

    public ZDialogManager(final MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.paperComponent = PaperComponent.getInstance();
        this.dialogBuilders = new DialogBuilderClass();
        inventoryManager = menuPlugin.getInventoryManager();

    }

    @Override
    public void load() {
        this.registerBodyLoader(new ItemBodyLoader());
        this.registerBodyLoader(new PlainMessageBodyLoader());

        this.registerInputLoader(new TextInputLoader());
        this.registerInputLoader(new BooleanInputLoader());
        this.registerInputLoader(new NumberRangeInputLoader());
        this.registerInputLoader(new SingleOptionInputLoader());
    }

    @Override
    public ZDialogs loadDialog(Plugin plugin, String fileName) throws DialogException {
        try {
            return loadInventory(plugin, fileName);
        } catch (InventoryException e) {
            throw new DialogException("Failed to load dialog: " + e.getMessage());
        }
    }

    @Override
    public Optional<ZDialogs> getDialog(String dialogName) {
        Optional<ZDialogs> dialogs;
        if (dialogName.contains(":")){
            String[] values = dialogName.split(":",2);
            dialogs = getDialog(values[0], values[1]);
        } else {
            dialogs = getDialogOptional(dialogName);
        }
        return dialogs;
    }
    public Optional<ZDialogs> getDialogOptional(String name) {
        for (List<ZDialogs> dialogList : dialogs.values()) {
            for (ZDialogs dialog : dialogList) {
                if (dialog.getFileName().equals(name) || dialog.getName(null).equals(name)) {
                    return Optional.of(dialog);
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public Optional<ZDialogs> getDialog(String pluginName, String fileName) {
        List<ZDialogs> pluginDialogs = dialogs.get(pluginName);
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName) || dialog.getName(null).equals(fileName))
                .findFirst();
    }

    @Override
    public Optional<ZDialogs> getDialog(Plugin plugin, String fileName) {
        List<ZDialogs> pluginDialogs = dialogs.get(plugin.getName());
        if (pluginDialogs == null) return Optional.empty();

        return pluginDialogs.stream()
                .filter(dialog -> dialog.getFileName().equals(fileName))
                .findFirst();
    }

    @Override
    public void deleteDialog(String name) {
        for (List<ZDialogs> dialogList : dialogs.values()) {
            dialogList.removeIf(dialog ->
                    dialog.getFileName().equals(name) || dialog.getName(null).equals(name)
            );
        }
    }

    @Override
    public void deleteDialog(Plugin plugin) {
        dialogs.remove(plugin.getName());
    }

    @Override
    public void loadDialogs() {
        File folder = new File(menuPlugin.getDataFolder(), "dialogs");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".yml"))
                    .forEach(file -> {
                        try {
                            this.loadInventory(this.menuPlugin, file);
                        } catch (DialogException | InventoryException exception) {
                            Logger.info("Failed to load dialog from file: " + file.getName(), Logger.LogType.WARNING);

                        }
                    });
        } catch (IOException exception) {
            Logger.info("Failed to load dialogs", Logger.LogType.WARNING);
        }
    }

    @Override
    public ZDialogs loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException {
        return this.loadInventory(plugin, fileName, ZDialogInventory.class);
    }

    @Override
    public ZDialogs loadInventory(Plugin plugin, File file) throws DialogException, InventoryException {
        return this.loadInventory(plugin, file, ZDialogInventory.class);
    }

    @Override
    public ZDialogs loadInventory(Plugin plugin, String fileName, Class<? extends ZDialogs> dialogClass)
            throws DialogException, InventoryException {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            throw new DialogFileNotFound("Dialog file not found: " + fileName + " in " +
                    plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        }
        return this.loadInventory(plugin, file, dialogClass);
    }

    @Override
    public ZDialogs loadInventory(Plugin plugin, File file, Class<? extends ZDialogs> dialogClass)
            throws DialogException, InventoryException {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<ZDialogs> loader = new DialogLoader(this.menuPlugin, this);
        ZDialogs dialog = loader.load(configuration, "", file, dialogClass, plugin);

        List<ZDialogs> dialogsList = this.dialogs.computeIfAbsent(plugin.getName(), k -> new ArrayList<>());
        dialogsList.add(dialog);

        if (Config.enableInformationMessage) {
            Logger.info(file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void registerBodyLoader(BodyLoader loader) {
        if (this.bodyLoader.containsKey(loader.getKey())) {
            Logger.info("BodyLoader " + loader.getKey() + " is already registered!", Logger.LogType.WARNING);
        } else {
            this.bodyLoader.put(loader.getKey(), loader);
        }
    }

    @Override
    public void registerInputLoader(InputLoader inputLoader) {
        if (this.inputLoader.containsKey(inputLoader.getKey())) {
            Logger.info("InputLoader " + inputLoader.getKey() + " is already registered!", Logger.LogType.WARNING);
        } else {
            this.inputLoader.put(inputLoader.getKey(), inputLoader);
        }
    }

    @Override
    public void registerBuilder(DialogBuilder builder) {
        this.dialogBuilders.registerBuilder(builder);
    }

    @Override
    public void reloadDialogs() {
        dialogs.clear();
        activeDialogs.clear();

        loadDialogs();

        Logger.info("Dialogs reloaded successfully!");
    }

    /**
     * Opens a specific dialog for the specified player
     */
    @Override
    public void openDialog(Player player, ZDialogs zDialog) {
        try {
            ZDialogInventoryBuild dialogBuild = zDialog.getBuild(player);
            List<DialogBody> bodies = zDialog.getDialogBodies(player);
            List<DialogInput> inputs = zDialog.getDialogInputs(player);

            DialogBase.Builder dialogBase = DialogBase.builder(paperComponent.getComponent(dialogBuild.name()))
                    .externalTitle(paperComponent.getComponent(dialogBuild.externalTitle()))
                    .canCloseWithEscape(dialogBuild.canCloseWithEscape())
                    .pause(zDialog.isPause())
                    .afterAction(zDialog.getAfterAction());

            Dialog dialog = createDialogByType(zDialog.getDialogType(), dialogBase, bodies, inputs, zDialog, player);

            activeDialogs.put(player.getUniqueId(), zDialog);

            player.showDialog(dialog);

        } catch (Exception e) {
            if (Config.enableDebug){
                Logger.info("Failed to open dialog for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
            }
        }
    }

    /**
     * Creates a dialog based on the dialog type
     */
    private Dialog createDialogByType(DialogType dialogType, DialogBase.Builder dialogBase, List<DialogBody> bodies, List<DialogInput> inputs, ZDialogs zDialog, Player player) {
        return switch (dialogType) {
            case NOTICE ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.notice(ActionButton.create(paperComponent.getComponent(zDialog.getLabel(player)),paperComponent.getComponent(zDialog.getLabelTooltip(player)), zDialog.getLabelWidth(), createAction(inputs,zDialog.getActions())))).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case CONFIRMATION ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(ActionButton.create(paperComponent.getComponent(zDialog.getYesText(player)),paperComponent.getComponent(zDialog.getYesTooltip(player)),zDialog.getYesWidth(), createAction(inputs, zDialog.getYesActions())), ActionButton.create(paperComponent.getComponent(zDialog.getNoText(player)),paperComponent.getComponent(zDialog.getNoTooltip(player)),zDialog.getNoWidth(), createAction(inputs,zDialog.getNoActions())))).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case MULTI_ACTION ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.multiAction(createActionButtons(zDialog,inputs,zDialog.getActionButtons(player))).build()).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case SERVER_LINKS ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.serverLinks(createActionButton(zDialog.getActionButtonServerLink(player),inputs), zDialog.getNumberOfColumns(), 100)).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

//             case DIALOG_LIST ->
//                     Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.dialogList(createDialogList(zDialog.getDialogList(),player)).build()).base(dialogBase.body(bodies).inputs(inputs).build())
//                     );
            // Impossible beacause we need the dialogs to register on the server and this required the plugin to be a PaperPlugin
        };
    }
    private List<ActionButton> createActionButtons(ZDialogs zDialogs, List<DialogInput> inputs, List<ActionButtonRecord> actionButtonRecords) {
        if (zDialogs == null) {
            return Collections.emptyList();
        }
        List<ActionButton> actionButtons = new ArrayList<>();
        for (ActionButtonRecord actionButtonRecord : actionButtonRecords) {
            actionButtons.add(createActionButton(actionButtonRecord, inputs));
        }
        return actionButtons;
    }
    private ActionButton createActionButton(ActionButtonRecord actionButtonRecord, List<DialogInput> inputs) {
        return ActionButton.create(paperComponent.getComponent(actionButtonRecord.label()), paperComponent.getComponent(actionButtonRecord.tooltip()), actionButtonRecord.width(), createAction(inputs,actionButtonRecord.actions()));
    }

    public DialogAction createAction(List<DialogInput> inputs, List<Action> actions) {
        return DialogAction.customClick((view,audience)-> {
            Placeholders placeholders = new Placeholders();
            for (DialogInput input : inputs) {
                String key = input.key();
                String value = null;

                Object rawValue;

                if (view.getText(key) != null) {
                    rawValue = view.getText(key);
                    value = (String) rawValue;
                }
                else if (view.getFloat(key) != null) {
                    rawValue = view.getFloat(key);
                    value = String.valueOf(rawValue);
                }
                else if (view.getBoolean(key) != null) {
                    rawValue = view.getBoolean(key);
                    value = String.valueOf(rawValue);
                }
                if (value == null) {
                    continue;
                }

                placeholders.register(key, value);
            }

            for (Action action : actions) {
                action.preExecute((Player) audience, null, inventoryManager.getFakeInventory(), placeholders);
            }

        }, ClickCallback.Options.builder().uses(-1).build());
    }
    /**
     * Gets the active dialog for a player
     */
    public Optional<ZDialogs> getActiveDialog(Player player) {
        return Optional.ofNullable(activeDialogs.get(player.getUniqueId()));
    }

    /**
     * Removes the active dialog for a player
     */
    public void removeActiveDialog(Player player) {
        activeDialogs.remove(player.getUniqueId());
    }

    public boolean openDialogByName(Player player, String dialogName) {
        Optional<ZDialogs> dialog = getDialog(dialogName);
        if (dialog.isPresent()) {
            openDialog(player, dialog.get());
            return true;
        }
        return false;
    }
    @Override
    public Collection<ZDialogs> getDialogs() {
        List<ZDialogs> allDialogs = new ArrayList<>();
        for (List<ZDialogs> dialogList : dialogs.values()) {
            allDialogs.addAll(dialogList);
        }
        return Collections.unmodifiableCollection(allDialogs);
    }
    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    @Override
    public Optional<BodyLoader> getBodyLoader(String name) {
        return Optional.ofNullable(this.bodyLoader.get(name));
    }

    @Override
    public Optional<InputLoader> getInputLoader(String name) {
        return Optional.ofNullable(this.inputLoader.get(name));
    }

    @Override
    public Optional<DialogBuilder> getDialogBuilder(DialogBodyType type) {
        return DialogBuilderClass.getDialogBuilder(type);
    }

    public static MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        if (inventoryManager == null){
            Logger.info("InventoryManager is not initialized. Please ensure it is set before calling loadItemStack.", Logger.LogType.WARNING);
            return null;
        } else {
            return inventoryManager.loadItemStack(configuration, path, file);
        }
    }

}