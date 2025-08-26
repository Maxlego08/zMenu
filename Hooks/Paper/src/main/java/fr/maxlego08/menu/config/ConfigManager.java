package fr.maxlego08.menu.config;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.configuration.ConfigDialog;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.configuration.ConfigOption;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.enums.DialogType;
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
import java.util.*;
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
        dialogInventory.setBooleanConfirmText(configDialog.booleanConfirmText());
        dialogInventory.setYesText(configDialog.yesText());
        dialogInventory.setNoText(configDialog.noText());
        dialogInventory.setYesWidth(configDialog.yesWidth());
        dialogInventory.setNoWidth(configDialog.noWidth());
        dialogInventory.setPause(true);
        dialogInventory.setCanCloseWithEscape(false);
        List<InputButton> inputButtons = new ArrayList<>();
        Map<String, Consumer<Boolean>> consumerMap = new HashMap<>();
        Map<String, Consumer<Float>> consumerMapFloat = new HashMap<>();
        Map<String, Consumer<Integer>> consumerMapInt = new HashMap<>();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigOption.class)){
                ConfigOption configOption = field.getAnnotation(ConfigOption.class);
                field.setAccessible(true);
                switch (Objects.requireNonNull(configOption).type()) {
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
                    case NUMBER_RANGE -> {
                        InputButton inputButton = new InputButton();
                        inputButton.setInputType(DialogInputType.NUMBER_RANGE);
                        inputButton.setLabel(configOption.label());
                        String key = configOption.key();
                        inputButton.setKey(key);
                        inputButton.setStart(configOption.startRange());
                        inputButton.setEnd(configOption.endRange());
                        inputButton.setStep(configOption.stepRange());
                        if (field.getType() == int.class) {
                            inputButton.setInitialValueRangeSupplier(() -> {
                                try {
                                    return (float) field.getInt(null);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            consumerMapInt.put(key, value -> {
                                try {
                                    field.setInt(null, value);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            inputButton.setInitialValueRangeSupplier(() -> {
                                try {
                                    return field.getFloat(null);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            consumerMapFloat.put(key, value -> {
                                try {
                                    field.setFloat(null, value);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                        inputButtons.add(inputButton);
                    }
                }
            }
        }
        dialogInventory.setConsumerMap(consumerMap);
        dialogInventory.setFloatConsumerMap(consumerMapFloat);
        dialogInventory.setIntegerConsumerMap(consumerMapInt);
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
            Dialog dialog = Dialog.create(builder -> builder.empty().type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(ActionButton.create(paperComponent.getComponent(zDialog.getYesText(player)),paperComponent.getComponent(zDialog.getYesTooltip(player)),zDialog.getYesWidth(), createAction(inputs,zDialog.getConsumerMap(), zDialog.getBooleanConfirmText(),zDialog.getFloatConsumerMap(),zDialog.getIntegerConsumerMap())), ActionButton.create(paperComponent.getComponent(zDialog.getNoText(player)),paperComponent.getComponent(zDialog.getNoTooltip(player)),zDialog.getNoWidth(), createAction(new ArrayList<>(),new HashMap<>(),"",new HashMap<>(),new HashMap<>())))).base(dialogBase.inputs(inputs).build())
            );
            player.showDialog(dialog);
        } catch (Exception e) {
            if (Config.enableDebug){
                Logger.info("Failed to open configuration dialog for player: " + player.getName()+" error :"+ e.getMessage(), Logger.LogType.ERROR);
            }
        }
    }
    private DialogAction createAction(List<DialogInput> inputs, Map<String, Consumer<Boolean>> consumerMap, String booleanText, Map<String, Consumer<Float>> floatMap, Map<String, Consumer<Integer>> consumerMapInt) {
        return DialogAction.customClick((view,audience)-> {
            if (inputs.isEmpty()) return;
            StringBuilder sb = new StringBuilder("Config Input Results:\n");
            for (DialogInput input : inputs) {
                String key = input.key();

                Object rawValue;

                switch (input) {
                    case NumberRangeDialogInput numberRangeDialogInput -> {
                        if (consumerMapInt.containsKey(key)) {
                            int intValue = view.getFloat(key) == null ? 0 : (int) view.getFloat(key).floatValue();
                            consumerMapInt.get(key).accept(intValue);
                            Config.updated = true;
                        } else if (floatMap.containsKey(key)) {
                            float floatValue = view.getFloat(key);
                            floatMap.get(key).accept(floatValue);
                            Config.updated = true;
                        }
                    }
                    case TextDialogInput textDialogInput -> {
                        rawValue = view.getText(key);
                    }
                    case BooleanDialogInput booleanDialogInput -> {
                        rawValue = view.getBoolean(key);
                        boolean booleanValue = (Boolean) rawValue;
                        if (consumerMap.containsKey(key)) {
                            consumerMap.get(key).accept(booleanValue);
                            Config.updated = true;
                        }
                        String valueIcon = booleanValue
                                ? "<green>✔<gray> |<red> ❌"
                                : "<red>✔<gray> |<green> ❌";
                        String text = booleanValue ? booleanDialogInput.onTrue() : booleanDialogInput.onFalse();
                        String line = booleanText
                                .replace("%key%", key)
                                .replace("%text%", text)
                                .replace("%value%", valueIcon);
                        sb.append(line).append("\n");
                    }
                    case SingleOptionDialogInput singleOptionDialogInput -> {
                        rawValue = view.getText(key);
                    }
                    default -> {
                    }
                }
            }
            getPaperComponent().sendMessage((Player) audience, sb.toString());
        }, ClickCallback.Options.builder().uses(-1).build());
    }
    @Override
    public List<String> getRegisteredConfigs() {
        return new ArrayList<>(zDialogInventoryDev.keySet());
    }

}