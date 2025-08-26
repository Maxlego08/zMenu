package fr.maxlego08.menu.config;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.configuration.ConfigDialog;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.configuration.ConfigOption;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.enums.DialogType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.AbstractDialogManager;
import fr.maxlego08.menu.hooks.dialogs.ZDialogInventoryDeveloper;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.*;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigManager extends AbstractDialogManager implements ConfigManagerInt {
    private final MenuPlugin menuPlugin;
    private final Map<String, ZDialogInventoryDeveloper> zDialogInventoryDev = new HashMap<>();
    private final ComponentMeta paperComponent;

    public ConfigManager(MenuPlugin menuPlugin) {
        super(menuPlugin);
        this.menuPlugin = menuPlugin;
        this.paperComponent = (ComponentMeta) menuPlugin.getMetaUpdater();
    }
    @Override
    public <T> void registerConfig(Class<T> configClass, Plugin plugin) {
        ConfigDialog configDialog = configClass.getAnnotation(ConfigDialog.class);
        if (configDialog == null) {
            throw new IllegalArgumentException("La classe " + configClass.getSimpleName() + " n'a pas d'annotation @ConfigDialog");
        }
        String configName = plugin.getName()+":"+configClass.getSimpleName();
        ZDialogInventoryDeveloper dialogInventory = new ZDialogInventoryDeveloper(this.menuPlugin, configDialog.name(), configName, configDialog.externalTitle());
        dialogInventory.setDialogType(DialogType.CONFIRMATION);
        dialogInventory.setYesText(configDialog.yesText());
        dialogInventory.setNoText(configDialog.noText());
        dialogInventory.setYesWidth(configDialog.yesWidth());
        dialogInventory.setNoWidth(configDialog.noWidth());
        dialogInventory.setPause(true);
        dialogInventory.setCanCloseWithEscape(false);
        List<InputButton> inputButtons = new ArrayList<>();
        Map<String, Consumer<Boolean>> consumerMap = new HashMap<>();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigOption.class)){
                ConfigOption configOption = field.getAnnotation(ConfigOption.class);
                field.setAccessible(true);
                switch (configOption.type()) {
                    case BOOLEAN -> {
                        InputButton inputButton = new InputButton();
                        inputButton.setInputType(DialogInputType.BOOLEAN);
                        inputButton.setLabel(configOption.label());
                        String key = configOption.key();
                        inputButton.setKey(key);
                        inputButton.setInitialValueSupplier(()-> {
                            try {
                                return field.getBoolean(null);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        inputButton.setTextTrue(configOption.trueText());
                        inputButton.setTextFalse(configOption.falseText());
                        consumerMap.put(key,(value -> {
                            try {
                                field.setBoolean(null, value);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }));
                        inputButtons.add(inputButton);
                    }
                }
            }
        }
        dialogInventory.setConsumerMap(consumerMap);
        dialogInventory.setInputButtons(inputButtons);
        zDialogInventoryDev.put(plugin.getName(), dialogInventory);

    }
    @Override
    public void openConfig( Plugin plugin,Player player) {
        openConfig(plugin.getName(), player);
    }
    public void openConfig(String pluginName, Player player){
        try{
            ZDialogInventoryDeveloper zDialog = zDialogInventoryDev.get(pluginName);
            if (zDialog == null) {
                if (Config.enableDebug) {
                    Logger.info("No dialog found for plugin: " + pluginName);
                }
                return;
            }
            ZDialogInventoryBuild dialogBuild = zDialog.getBuild(player);
            List<DialogInput> inputs = getDialogInputs(player, zDialog.getDialogInputs(player));
            DialogBase.Builder dialogBase = createDialogBase(dialogBuild.name(), dialogBuild.externalTitle(), dialogBuild.canCloseWithEscape(), zDialog.isPause(), zDialog.getAfterAction());
            Dialog dialog = Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(ActionButton.create(paperComponent.getComponent(zDialog.getYesText(player)),paperComponent.getComponent(zDialog.getYesTooltip(player)),zDialog.getYesWidth(), createAction(inputs,zDialog.getConsumerMap())), ActionButton.create(paperComponent.getComponent(zDialog.getNoText(player)),paperComponent.getComponent(zDialog.getNoTooltip(player)),zDialog.getNoWidth(), createAction(inputs,new HashMap<>())))).base(dialogBase.inputs(inputs).build())
            );
            player.showDialog(dialog);
        } catch (Exception e) {
            if (Config.enableDebug){
                Logger.info("Failed to open configuration dialog for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
            }
        }
    }
    private DialogAction createAction(List<DialogInput> inputs, Map<String, Consumer<Boolean>> consumerMap) {
        return DialogAction.customClick((view,audience)-> {
            Placeholders placeholders = new Placeholders();
            for (DialogInput input : inputs) {
                String key = input.key();
                String value = null;

                Object rawValue;

                switch (input) {
                    case NumberRangeDialogInput numberRangeDialogInput -> {
                        rawValue = view.getFloat(key);
                        value = String.valueOf(rawValue);
                    }
                    case TextDialogInput textDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                    }
                    case BooleanDialogInput booleanDialogInput -> {
                        rawValue = view.getBoolean(key);
                        value = String.valueOf(rawValue);
                        if (consumerMap.containsKey(key)) {
                            consumerMap.get(key).accept((Boolean) rawValue);
                            Config.updated = true;
                        }
                    }
                    case SingleOptionDialogInput singleOptionDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                    }
                    default -> {
                    }
                }
                if (value == null) {
                    continue;
                }
                placeholders.register(key, value);
            }

        }, ClickCallback.Options.builder().uses(-1).build());
    }
    @Override
    public List<String> getRegisteredConfigs() {
        return new ArrayList<>(zDialogInventoryDev.keySet());
    }

}