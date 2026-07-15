package fr.maxlego08.menu.config;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.ConfigFieldProcessor;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.configuration.annotation.ConfigUpdate;
import fr.maxlego08.menu.api.configuration.dialog.ConfigDialogBuilder;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.ZDialogInventoryDeveloper;
import fr.maxlego08.menu.hooks.dialogs.action.ZConfigManagerCustomDialogAction;
import fr.maxlego08.menu.hooks.dialogs.action.ZCustomClickDialogAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigManager implements ConfigManagerInt {

    private final MenuPlugin menuPlugin;
    private final Map<String, ZDialogInventoryDeveloper> zDialogInventoryDev = new HashMap<>();
    private final ComponentMeta paperComponent;
    private final List<ConfigFieldProcessor> processors = new ArrayList<>();

    public ConfigManager(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.paperComponent = (ComponentMeta) menuPlugin.getMetaUpdater();
        this.processors.add(new DefaultBooleanProcessor());
        this.processors.add(new DefaultNumberProcessor());
        this.processors.add(new DefaultEnumProcessor());
        this.processors.add(new DefaultListProcessor());
        this.processors.add(new DefaultTextProcessor());
    }

    @Override
    public <T> void registerConfig(@NotNull ConfigDialogBuilder configDialogBuilder, @NotNull Class<T> configClass, @NotNull Plugin plugin) {
        ConfigFieldContext context = this.processConfigFields(configClass);
        String configName = plugin.getName() + ":" + configClass.getSimpleName();

        Map<String, ZConfigManagerCustomDialogAction.FieldEntry> fieldEntries = new HashMap<>();
        for (Map.Entry<String, ConfigFieldContext.ConfigFieldEntry> entry : context.getEntries().entrySet()) {
            String key = entry.getKey();
            Consumer<Object> consumer = context.getConsumer(key);
            if (consumer != null) {
                fieldEntries.put(key, new ZConfigManagerCustomDialogAction.FieldEntry(entry.getValue().resolvedType(), consumer));
            }
        }

        ActionButtonRecord yesActionRecord = new ActionButtonRecord(
                configDialogBuilder.getYesText(),
                configDialogBuilder.getYesTooltip(),
                configDialogBuilder.getYesWidth(),
                new ZConfigManagerCustomDialogAction(
                        fieldEntries,
                        context.getUpdateConsumer(),
                        this.paperComponent,
                        configDialogBuilder.getBooleanConfirmText(),
                        configDialogBuilder.getNumberRangeConfirmText(),
                        configDialogBuilder.getTextConfirmText(),
                        configDialogBuilder.getYesUsageLimit(),
                        configDialogBuilder.getYesCooldown()
                )
        );

        ActionButtonRecord noActionRecord = new ActionButtonRecord(
                configDialogBuilder.getNoText(),
                configDialogBuilder.getNoTooltip(),
                configDialogBuilder.getNoWidth(),
                new ZCustomClickDialogAction(List.of(), configDialogBuilder.getNoUsageLimit(), configDialogBuilder.getNoCooldown(), Map.of())
        );

        ZDialogInventoryDeveloper dialogInventory = new ZDialogInventoryDeveloper(
                this.menuPlugin,
                configDialogBuilder.getName(),
                configName,
                configDialogBuilder.getExternalTitle(),
                context,
                yesActionRecord,
                noActionRecord
        );
        dialogInventory.setPause(configDialogBuilder.isPauseOnOpen());
        dialogInventory.setCanCloseWithEscape(configDialogBuilder.canCloseWithEscape());

        this.zDialogInventoryDev.put(plugin.getName(), dialogInventory);
    }

    private <T> ConfigFieldContext processConfigFields(Class<T> configClass) {
        ConfigFieldContext context = new ConfigFieldContext();
        for (Field field : configClass.getDeclaredFields()) {
            ConfigOption configOption = field.getAnnotation(ConfigOption.class);
            if (configOption != null) {
                this.processField(field, configOption, context);
            } else if (field.isAnnotationPresent(ConfigUpdate.class)) {
                field.setAccessible(true);
                context.setUpdateConsumer(value -> {
                    try {
                        field.setBoolean(null, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to update @ConfigUpdate field: " + field.getName(), e);
                    }
                });
            }
        }
        if (context.getUpdateConsumer() == null) {
            Logger.info("No @ConfigUpdate field found in " + configClass.getSimpleName() + ", changes won't trigger save.", Logger.LogType.WARNING);
            context.setUpdateConsumer(value -> {
            });
        }
        return context;
    }

    private void processField(Field field, ConfigOption configOption, ConfigFieldContext context) {
        field.setAccessible(true);
        DialogInputType resolvedType = this.resolveInputType(field, configOption);
        String key = configOption.key();
        if (key == null || key.isEmpty()) {
            key = field.getName();
        }

        ConfigFieldProcessor matchedProcessor = null;
        for (ConfigFieldProcessor processor : this.processors) {
            if (processor.canProcess(field, configOption)) {
                matchedProcessor = processor;
                break;
            }
        }

        context.register(key, field, configOption, resolvedType, matchedProcessor);

        final ConfigFieldProcessor finalProcessor = matchedProcessor;
        Consumer<Object> consumer = value -> {
            try {
                if (finalProcessor != null) {
                    finalProcessor.setFieldValue(field, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field: " + field.getName(), e);
            }
        };

        context.registerConsumer(key, consumer);
    }

    private DialogInputType resolveInputType(Field field, ConfigOption configOption) {
        for (ConfigFieldProcessor processor : this.processors) {
            if (processor.canProcess(field, configOption)) {
                return processor.resolveInputType(field, configOption);
            }
        }
        return DialogInputType.TEXT;
    }

    @Override
    public void openConfig(@NonNull Plugin plugin, @NonNull Player player) {
        this.openConfig(plugin.getName(), player);
    }

    @Override
    public void openConfig(@NonNull String pluginName, @NonNull Player player) {
        try {
            ZDialogInventoryDeveloper zDialog = this.zDialogInventoryDev.get(pluginName);
            if (zDialog == null) {
                if (Configuration.enableDebug) {
                    Logger.info("No dialog inventory found for plugin: " + pluginName);
                }
                return;
            }

            ConfigFieldContext context = zDialog.getContext();
            if (context == null || context.isEmpty()) {
                if (Configuration.enableDebug) {
                    Logger.info("No config fields registered for plugin: " + pluginName);
                }
                return;
            }

            var dialog = zDialog.buildDialog(
                    player,
                    this.paperComponent,
                    this.menuPlugin.getInventoryManager().getFakeInventory(),
                    new fr.maxlego08.menu.api.utils.Placeholders()
            );

            if (dialog != null) {
                player.showDialog(dialog);
            }
        } catch (Exception e) {
            if (Configuration.enableDebug) {
                Logger.info("Failed to open configuration dialog for player: " + player.getName() + " error: " + e.getMessage(), Logger.LogType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @Override
    public @NonNull List<String> getRegisteredConfigs() {
        return new ArrayList<>(this.zDialogInventoryDev.keySet());
    }

    @Override
    public void registerProcessor(@NotNull ConfigFieldProcessor processor) {
        Preconditions.checkNotNull(processor, "Processor cannot be null");
        this.processors.add(processor);
    }

    private static class DefaultBooleanProcessor implements ConfigFieldProcessor {
        @Override
        public boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption) {
            DialogInputType declared = configOption.type();
            if (declared == DialogInputType.BOOLEAN) {
                return true;
            }
            if (declared == DialogInputType.TEXT) {
                Class<?> type = field.getType();
                return type == boolean.class || type == Boolean.class;
            }
            return false;
        }

        @Override
        public @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption) {
            return DialogInputType.BOOLEAN;
        }

        @Override
        public void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException {
            if (field.getType() == boolean.class) {
                field.setBoolean(null, (Boolean) value);
            } else {
                field.set(null, value);
            }
        }
    }

    private static class DefaultNumberProcessor implements ConfigFieldProcessor {
        @Override
        public boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption) {
            DialogInputType declared = configOption.type();
            if (declared == DialogInputType.NUMBER_RANGE) {
                return true;
            }
            if (declared == DialogInputType.TEXT) {
                Class<?> type = field.getType();
                return type == int.class || type == Integer.class || type == long.class || type == Long.class || type == float.class || type == Float.class || type == double.class || type == Double.class;
            }
            return false;
        }

        @Override
        public @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption) {
            return DialogInputType.NUMBER_RANGE;
        }

        @Override
        public void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException {
            Number number = (Number) value;
            Class<?> type = field.getType();
            if (type == int.class) {
                field.setInt(null, number.intValue());
            } else if (type == Integer.class) {
                field.set(null, number.intValue());
            } else if (type == long.class) {
                field.setLong(null, number.longValue());
            } else if (type == Long.class) {
                field.set(null, number.longValue());
            } else if (type == float.class) {
                field.setFloat(null, number.floatValue());
            } else if (type == Float.class) {
                field.set(null, number.floatValue());
            } else if (type == double.class) {
                field.setDouble(null, number.doubleValue());
            } else if (type == Double.class) {
                field.set(null, number.doubleValue());
            }
        }
    }

    private static class DefaultEnumProcessor implements ConfigFieldProcessor {
        @Override
        public boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption) {
            DialogInputType declared = configOption.type();
            if (declared == DialogInputType.SINGLE_OPTION) {
                return true;
            }
            if (declared == DialogInputType.TEXT) {
                return field.getType().isEnum();
            }
            return false;
        }

        @Override
        public @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption) {
            return DialogInputType.SINGLE_OPTION;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException {
            Class<?> type = field.getType();
            if (type.isEnum()) {
                String strValue = (String) value;
                Object[] constants = type.getEnumConstants();
                if (constants != null) {
                    for (Object constant : constants) {
                        if (((Enum) constant).name().equalsIgnoreCase(strValue)) {
                            field.set(null, constant);
                            return;
                        }
                    }
                }
                Logger.info("No enum constant found for value '" + strValue + "' in " + type.getSimpleName(), Logger.LogType.WARNING);
            }
        }
    }

    private static class DefaultListProcessor implements ConfigFieldProcessor {
        @Override
        public boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption) {
            return field.getType() == List.class;
        }

        @Override
        public @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption) {
            return DialogInputType.TEXT;
        }

        @Override
        public Object getDisplayValue(@NotNull Field field) throws IllegalAccessException {
            List<?> list = (List<?>) field.get(null);
            if (list == null || list.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                Object item = list.get(i);
                if (item instanceof Enum<?> e) {
                    sb.append(e.name());
                } else if (item != null) {
                    sb.append(item);
                }
            }
            return sb.toString();
        }

        @Override
        public void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException {
            if (!(value instanceof String str)) {
                return;
            }
            Class<?> genericType = String.class; // default
            if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType pt) {
                java.lang.reflect.Type[] actualTypeArguments = pt.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?> c) {
                    genericType = c;
                }
            }

            String[] split = str.split(",");
            List<Object> parsedList = new ArrayList<>();
            for (String part : split) {
                part = part.trim();
                if (part.isEmpty()) {
                    continue;
                }
                try {
                    if (genericType == String.class) {
                        parsedList.add(part);
                    } else if (genericType == Integer.class || genericType == int.class) {
                        parsedList.add(Integer.parseInt(part));
                    } else if (genericType == Long.class || genericType == long.class) {
                        parsedList.add(Long.parseLong(part));
                    } else if (genericType == Double.class || genericType == double.class) {
                        parsedList.add(Double.parseDouble(part));
                    } else if (genericType == Float.class || genericType == float.class) {
                        parsedList.add(Float.parseFloat(part));
                    } else if (genericType == Boolean.class || genericType == boolean.class) {
                        parsedList.add(Boolean.parseBoolean(part));
                    } else if (genericType.isEnum()) {
                        Object matchedConstant = null;
                        for (Object constant : genericType.getEnumConstants()) {
                            if (((Enum<?>) constant).name().equalsIgnoreCase(part)) {
                                matchedConstant = constant;
                                break;
                            }
                        }
                        if (matchedConstant != null) {
                            parsedList.add(matchedConstant);
                        } else {
                            Logger.info("No enum constant found for value '" + part + "' in " + genericType.getSimpleName(), Logger.LogType.WARNING);
                        }
                    } else {
                        parsedList.add(part);
                    }
                } catch (Exception e) {
                    Logger.info("Failed to parse list element '" + part + "' as " + genericType.getSimpleName(), Logger.LogType.WARNING);
                }
            }
            field.set(null, parsedList);
        }
    }

    private static class DefaultTextProcessor implements ConfigFieldProcessor {
        @Override
        public boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption) {
            return true;
        }

        @Override
        public @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption) {
            Class<?> type = field.getType();
            if (type != String.class && configOption.type() == DialogInputType.TEXT) {
                Logger.info("Unrecognized field type " + type.getSimpleName() + " for field " + field.getName() + ", falling back to TEXT.", Logger.LogType.WARNING);
            }
            return DialogInputType.TEXT;
        }

        @Override
        public void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException {
            field.set(null, value);
        }
    }
}
