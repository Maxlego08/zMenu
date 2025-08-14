package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogType;
import fr.maxlego08.menu.hooks.dialogs.exception.DialogException;
import fr.maxlego08.menu.hooks.dialogs.exception.DialogFileNotFound;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.actions.MessageLoader;
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
import fr.maxlego08.menu.hooks.dialogs.utils.record.ZDialogInventoryBuild;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ZDialogManager implements DialogManager {
    private final MenuPlugin plugin;
    private static InventoryManager inventoryManager;
    private final Map<String, List<ZDialogs>> dialogs = new HashMap<>();
    private final Map<UUID, ZDialogs> activeDialogs = new HashMap<>();
    private final Map<String, BodyLoader> bodyLoader = new HashMap<>();
    private final Map<String, InputLoader> inputLoader = new HashMap<>();
    private final Map<String, DialogActionIntLoader> dialogActionLoaders = new HashMap<>();
    private final DialogBuilderClass dialogBuilders;

    public ZDialogManager(final Plugin plugin) {
        this.plugin = (MenuPlugin) plugin;
        this.dialogBuilders = new DialogBuilderClass(this.plugin);
        inventoryManager = ((MenuPlugin) plugin).getInventoryManager();

    }

    @Override
    public void load() {
        this.registerBodyLoader(new ItemBodyLoader());
        this.registerBodyLoader(new PlainMessageBodyLoader());

        this.registerInputLoader(new TextInputLoader());
        this.registerInputLoader(new BooleanInputLoader());
        this.registerInputLoader(new NumberRangeInputLoader());
        this.registerInputLoader(new SingleOptionInputLoader());

        this.registerActions(new MessageLoader());
        loadDialogs();
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
        File folder = new File(plugin.getDataFolder(), "dialogs");
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
                            this.loadInventory(this.plugin, file);
                        } catch (DialogException | InventoryException exception) {
                            plugin.getLogger().log(Level.SEVERE,
                                    "Failed to load dialog from file: " + file.getName(), exception);
                        }
                    });
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load dialogs", exception);
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

        Loader<ZDialogs> loader = new DialogLoader(this.plugin, this);
        ZDialogs dialog = loader.load(configuration, "", file, dialogClass, plugin);

        List<ZDialogs> dialogsList = this.dialogs.computeIfAbsent(plugin.getName(), k -> new ArrayList<>());
        dialogsList.add(dialog);

        if (Config.enableInformationMessage) {
            plugin.getLogger().log(Level.INFO, file.getPath() + " loaded successfully!");
        }

        return dialog;
    }

    @Override
    public void registerBodyLoader(BodyLoader loader) {
        if (this.bodyLoader.containsKey(loader.getKey())) {
            plugin.getLogger().log(Level.WARNING, "BodyLoader " + loader.getKey() + " is already registered!");
        } else {
            this.bodyLoader.put(loader.getKey(), loader);
            plugin.getLogger().log(Level.INFO, "BodyLoader " + loader.getKey() + " registered successfully!");
        }
    }

    @Override
    public void registerInputLoader(InputLoader inputLoader) {
        if (this.inputLoader.containsKey(inputLoader.getKey())) {
            plugin.getLogger().log(Level.WARNING, "InputLoader " + inputLoader.getKey() + " is already registered!");
        } else {
            this.inputLoader.put(inputLoader.getKey(), inputLoader);
            plugin.getLogger().log(Level.INFO, "InputLoader " + inputLoader.getKey() + " registered successfully!");
        }
    }

    @Override
    public void registerBuilder(DialogBuilder builder) {
        this.dialogBuilders.registerBuilder(builder);
    }

    @Override
    public void registerActions(DialogActionIntLoader action) {
        if (action != null ){
            for (String name : action.getActionNames()){
                if (dialogActionLoaders.containsKey(name)) {
                    plugin.getLogger().log(Level.WARNING, "DialogActionIntLoader " + name + " is already registered!");
                } else {
                    dialogActionLoaders.put(name, action);
                    plugin.getLogger().log(Level.INFO, "DialogActionIntLoader " + name + " registered successfully!");
                }
            }
        }
    }

    @Override
    public void reloadDialogs() {
        dialogs.clear();
        activeDialogs.clear();

        loadDialogs();

        plugin.getLogger().log(Level.INFO, "Dialogs reloaded successfully!");
    }

    /**
     * Opens a dialog for the specified player
     */
    @Override
    public void openDialog(Player player, String dialogName) {
        Optional<ZDialogs> optionalDialog = this.getDialog(dialogName);
        if (optionalDialog.isEmpty()) {
            plugin.getLogger().log(Level.WARNING,
                    "Attempted to open non-existent dialog: " + dialogName + " for player: " + player.getName());
            return;
        }

        ZDialogs dialog = optionalDialog.get();
        openDialog(player, dialog);
    }

    /**
     * Opens a specific dialog for the specified player
     */
    public void openDialog(Player player, ZDialogs zDialog) {
        PaperComponent paperComponent = PaperComponent.getInstance();
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
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to open dialog for player: " + player.getName(), e);
        }
    }

    /**
     * Creates a dialog based on the dialog type
     */
    private Dialog createDialogByType(DialogType dialogType, DialogBase.Builder dialogBase, List<DialogBody> bodies, List<DialogInput> inputs, ZDialogs zDialog, Player player) {
        PaperComponent paperComponent = PaperComponent.getInstance();
        return switch (dialogType) {
            case NOTICE ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.notice(ActionButton.create(paperComponent.getComponent(zDialog.getLabel(player)),paperComponent.getComponent(zDialog.getLabelTooltip(player)), zDialog.getLabelWidth(), createAction(inputs,zDialog.getActions())))).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case CONFIRMATION ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(ActionButton.create(paperComponent.getComponent(zDialog.getYesText(player)),paperComponent.getComponent(zDialog.getYesTooltip(player)),zDialog.getYesWidth(), createAction(inputs, zDialog.getYesActions())), ActionButton.create(paperComponent.getComponent(zDialog.getNoText(player)),paperComponent.getComponent(zDialog.getNoTooltip(player)),zDialog.getNoWidth(), createAction(inputs,zDialog.getNoActions())))).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case MULTI_ACTION ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.multiAction(List.of(ActionButton.create(paperComponent.getComponent("Test"), paperComponent.getComponent("Tooltype"), 100, null))).build()).base(dialogBase.body(bodies).inputs(inputs).build())
                    );

            case SERVER_LINKS ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.serverLinks(null, 100, 100)).base(dialogBase.body(bodies).build())
                    );

            case DIALOG_LIST ->
                    Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.dialogList(null).build()).base(dialogBase.body(bodies).build())
                    );
        };
    }
    public DialogAction createAction(List<DialogInput> inputs, List<fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction> actions) {
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

            for (fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction action : actions) {
                action.execute(view,audience, placeholders, (Player) audience);
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

    @Override
    public Optional<DialogActionIntLoader> getDialogAction(String name) {
        return Optional.ofNullable(this.dialogActionLoaders.get(name));
    }


    private <T> T getProvider(Class<T> classProvider) {
        RegisteredServiceProvider<T> provider = Bukkit.getServer().getServicesManager().getRegistration(classProvider);
        return provider == null ? null : provider.getProvider();
    }

    public static MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        if (inventoryManager == null){
            Bukkit.getLogger().log(Level.WARNING, "InventoryManager is not initialized. Please ensure it is set before calling loadItemStack.");
            return null;
        } else {
            return inventoryManager.loadItemStack(configuration, path, file);
        }
    }

    public static void log(String message){
        Bukkit.getLogger().log(Level.INFO, message);
    }


}