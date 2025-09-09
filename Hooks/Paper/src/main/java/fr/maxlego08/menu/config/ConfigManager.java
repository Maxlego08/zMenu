package fr.maxlego08.menu.config;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.configuration.annotation.ConfigDialog;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.configuration.annotation.ConfigUpdate;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import fr.maxlego08.menu.config.processors.ConfigFieldProcessor;
import fr.maxlego08.menu.config.processors.ConfigFieldProcessorFactory;
import fr.maxlego08.menu.config.processors.ConfigFieldProcessorRegistry;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.AbstractDialogManager;
import fr.maxlego08.menu.hooks.dialogs.ZDialogInventoryDeveloper;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.BooleanDialogInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
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
    private final ConfigFieldProcessorRegistry processorRegistry;

    public ConfigManager(MenuPlugin menuPlugin) {
        super(menuPlugin);
        this.menuPlugin = menuPlugin;
        this.paperComponent = (ComponentMeta) menuPlugin.getMetaUpdater();
        this.processorRegistry = new ConfigFieldProcessorRegistry();
        initializeDefaultProcessors();
    }

    private void initializeDefaultProcessors() {
        ConfigFieldProcessorFactory factory = new ConfigFieldProcessorFactory();
        processorRegistry.registerProcessor(DialogInputType.BOOLEAN, factory.createBooleanProcessor());
        processorRegistry.registerProcessor(DialogInputType.NUMBER_RANGE, factory.createNumberRangeProcessor());
        processorRegistry.registerProcessor(DialogInputType.TEXT, factory.createTextProcessor());
    }

    @Override
    public <T> void registerConfig(Class<T> configClass, Plugin plugin) {
        ConfigDialog configDialog = validateConfigClass(configClass);

        String configName = plugin.getName() + ":" + configClass.getSimpleName();
        ConfigFieldContext context = processConfigFields(configClass);

        ZDialogInventoryDeveloper dialogInventory = createDialogInventory(configDialog, configName, context.getUpdateConsumer());

        applyContextToDialog(dialogInventory, context);
        zDialogInventoryDev.put(plugin.getName(), dialogInventory);
    }

    private <T> ConfigDialog validateConfigClass(Class<T> configClass) {
        ConfigDialog configDialog = configClass.getAnnotation(ConfigDialog.class);
        if (configDialog == null) {
            throw new IllegalArgumentException("The class " + configClass.getName() + " must be annotated with @ConfigDialog");
        }
        return configDialog;
    }

    private ZDialogInventoryDeveloper createDialogInventory(ConfigDialog configDialog, String configName, Consumer<Boolean> updateConsumer) {
        ZDialogInventoryDeveloper dialogInventory = new ZDialogInventoryDeveloper(
                this.menuPlugin,
                configDialog.name(),
                configName,
                configDialog.externalTitle(),
                updateConsumer
        );

        dialogInventory.setDialogType(DialogType.CONFIRMATION);
        dialogInventory.setBooleanConfirmText(configDialog.booleanConfirmText());
        dialogInventory.setNumberRangeConfirmText(configDialog.numberRangeConfirmText());
        dialogInventory.setStringConfirmText(configDialog.textConfirmText());
        dialogInventory.setYesText(configDialog.yesText());
        dialogInventory.setNoText(configDialog.noText());
        dialogInventory.setYesWidth(configDialog.yesWidth());
        dialogInventory.setNoWidth(configDialog.noWidth());
        dialogInventory.setPause(true);
        dialogInventory.setCanCloseWithEscape(false);

        return dialogInventory;
    }

    private <T> ConfigFieldContext processConfigFields(Class<T> configClass) {
        ConfigFieldContext context = new ConfigFieldContext();

        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigOption.class)) {
                processField(field, context);
            } else if (field.isAnnotationPresent(ConfigUpdate.class)){
                field.setAccessible(true);
                context.setUpdateConsumer(value-> {
                    try {
                        field.setBoolean(null, value );
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        if (context.getUpdateConsumer() == null) {
            Logger.info("No update consumer found for field " + configClass.getSimpleName()+", this may be safe if you don't need to handle updates.", Logger.LogType.WARNING);
            context.setUpdateConsumer(value -> {});
        }

        return context;
    }

    private void processField(Field field, ConfigFieldContext context) {
        ConfigOption configOption = field.getAnnotation(ConfigOption.class);
        field.setAccessible(true);

        DialogInputType inputType = configOption.type();
        ConfigFieldProcessor processor = processorRegistry.getProcessor(inputType);

        if (processor != null) {
            processor.processField(field, configOption, context);
        } else {
            Logger.info("No processor found for input type: " + inputType, Logger.LogType.WARNING);
        }
    }

    private void applyContextToDialog(ZDialogInventoryDeveloper dialogInventory, ConfigFieldContext context) {
        dialogInventory.setConsumerMap(context.getBooleanConsumers());
        dialogInventory.setFloatConsumerMap(context.getFloatConsumers());
        dialogInventory.setIntegerConsumerMap(context.getIntegerConsumers());
        dialogInventory.setStringConsumerMap(context.getStringConsumers());
        dialogInventory.setInputButtons(context.getInputButtons());
    }

    @Override
    public void openConfig(Plugin plugin, Player player) {
        openConfig(plugin.getName(), player);
    }

    public void openConfig(String pluginName, Player player) {
        try {
            ZDialogInventoryDeveloper zDialog = zDialogInventoryDev.get(pluginName);
            if (zDialog == null) {
                if (Config.enableDebug) {
                    Logger.info("No dialog found for plugin: " + pluginName);
                }
                return;
            }

            ZDialogInventoryBuild dialogBuild = zDialog.getBuild(player);
            List<DialogInput> inputs = getDialogInputs(player, zDialog.getDialogInputs(player));
            DialogBase.Builder dialogBase = createDialogBase(
                    dialogBuild.name(),
                    dialogBuild.externalTitle(),
                    dialogBuild.canCloseWithEscape(),
                    zDialog.isPause(),
                    zDialog.getAfterAction()
            );

            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(
                            ActionButton.create(
                                    paperComponent.getComponent(zDialog.getYesText(player)),
                                    paperComponent.getComponent(zDialog.getYesTooltip(player)),
                                    zDialog.getYesWidth(),
                                    createAction(inputs, zDialog.getConsumerMap(), zDialog.getBooleanConfirmText(),
                                            zDialog.getFloatConsumerMap(), zDialog.getIntegerConsumerMap(),
                                            zDialog.getNumberRangeConfirmText(), zDialog.getStringConsumerMap(),
                                            zDialog.getStringConfirmText(), zDialog.getUpdateConsumer())
                            ),
                            ActionButton.create(
                                    paperComponent.getComponent(zDialog.getNoText(player)),
                                    paperComponent.getComponent(zDialog.getNoTooltip(player)),
                                    zDialog.getNoWidth(),
                                    createAction(new ArrayList<>(), new HashMap<>(), "", new HashMap<>(),
                                            new HashMap<>(), "", new HashMap<>(), "", null)
                            )
                    ))
                    .base(dialogBase.inputs(inputs).build())
            );

            player.showDialog(dialog);
        } catch (Exception e) {
            if (Config.enableDebug) {
                Logger.info("Failed to open configuration dialog for player: " + player.getName() + " error :" + e.getMessage(), Logger.LogType.ERROR);
            }
        }
    }

    private DialogAction createAction(List<DialogInput> inputs, Map<String, Consumer<Boolean>> consumerMap,
                                      String booleanText, Map<String, Consumer<Float>> floatMap,
                                      Map<String, Consumer<Integer>> consumerMapInt, String numberRangeText,
                                      Map<String, Consumer<String>> stringMap, String stringText, Consumer<Boolean> updateConsumer) {
        return DialogAction.customClick((view, audience) -> {
            if (inputs.isEmpty()) return;

            StringBuilder sb = new StringBuilder("Config Input Results:\n");

            for (DialogInput input : inputs) {
                String key = input.key();
                processInputResult(input, key, view, sb, consumerMap, booleanText, floatMap,
                        consumerMapInt, numberRangeText, stringMap, stringText, updateConsumer );
            }

            getPaperComponent().sendMessage((Player) audience, sb.toString());
        }, ClickCallback.Options.builder().uses(-1).build());
    }

    private void processInputResult(DialogInput input, String key,
                                    io.papermc.paper.dialog.DialogResponseView view, StringBuilder sb,
                                    Map<String, Consumer<Boolean>> consumerMap, String booleanText,
                                    Map<String, Consumer<Float>> floatMap, Map<String, Consumer<Integer>> consumerMapInt,
                                    String numberRangeText, Map<String, Consumer<String>> stringMap, String stringText, Consumer<Boolean> updateConsumer) {
        switch (input) {
            case NumberRangeDialogInput numberRangeDialogInput ->
                    processNumberRangeInput(key, view, sb, floatMap, consumerMapInt, numberRangeText, updateConsumer);
            case TextDialogInput textDialogInput ->
                    processTextInput(key, view, sb, stringMap, stringText, updateConsumer);
            case BooleanDialogInput booleanDialogInput ->
                    processBooleanInput(key, view, sb, consumerMap, booleanText, booleanDialogInput, updateConsumer);
            default -> {
            }
        }
    }

    private void processNumberRangeInput(String key, io.papermc.paper.dialog.DialogResponseView view, StringBuilder sb,
                                         Map<String, Consumer<Float>> floatMap, Map<String, Consumer<Integer>> consumerMapInt,
                                         String numberRangeText, Consumer<Boolean> updateConsumer) {
        if (consumerMapInt.containsKey(key)) {
            int intValue = view.getFloat(key) == null ? 0 : (int) view.getFloat(key).floatValue();
            consumerMapInt.get(key).accept(intValue);
            executeUpdateConsumer(updateConsumer);
        } else if (floatMap.containsKey(key)) {
            float floatValue = view.getFloat(key);
            floatMap.get(key).accept(floatValue);
            executeUpdateConsumer(updateConsumer);
        }
        String line = numberRangeText.replace("%key%", key)
                .replace("%value%", String.valueOf(view.getFloat(key)));
        sb.append(line).append("\n");
    }

    private void processTextInput(String key, io.papermc.paper.dialog.DialogResponseView view, StringBuilder sb,
                                  Map<String, Consumer<String>> stringMap, String stringText, Consumer<Boolean> updateConsumer) {
        String stringValue = view.getText(key);
        if (stringMap.containsKey(key)) {
            stringMap.get(key).accept(stringValue);
            executeUpdateConsumer(updateConsumer);
        }
        if (stringValue != null) {
            String line = stringText.replace("%key%", key).replace("%text%", stringValue);
            sb.append(line).append("\n");
        }
    }

    private void processBooleanInput(String key, io.papermc.paper.dialog.DialogResponseView view, StringBuilder sb,
                                     Map<String, Consumer<Boolean>> consumerMap, String booleanText,
                                     BooleanDialogInput booleanDialogInput, Consumer<Boolean> updateConsumer) {
        Boolean booleanValue = view.getBoolean(key);
        if (booleanValue != null && consumerMap.containsKey(key)) {
            consumerMap.get(key).accept(booleanValue);
            executeUpdateConsumer(updateConsumer);
        }

        if (booleanValue != null) {
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
    }
    private void executeUpdateConsumer(Consumer<Boolean> updateConsumer) {
        if (updateConsumer != null) {
            updateConsumer.accept(true);
        }
    }

    @Override
    public List<String> getRegisteredConfigs() {
        return new ArrayList<>(zDialogInventoryDev.keySet());
    }

    public ConfigFieldProcessorRegistry getProcessorRegistry() {
        return processorRegistry;
    }
}