package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.itemstack.ItemStackService;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.button.buttons.ZItemDragButton;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

@AutoButtonLoader
public class ItemDragLoader extends ButtonLoader {
    private final MenuPlugin plugin;

    private final ItemStackService itemStackService;

    public ItemDragLoader(MenuPlugin plugin) {
        super(plugin, "item_drag");
        this.plugin = plugin;
        this.itemStackService = plugin.getItemStackService();
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        ZItemDragButton button = new ZItemDragButton(this.plugin);
        String check_element = configuration.contains(path + "check_item") ? "check_item" : configuration.contains(path + "check-item") ? "check-item" : null;

        if (check_element != null) {
            MenuItemStack menuItemStack = this.itemStackService.load(configuration, path + check_element + ".item.", defaultButtonValue.getFile());
            String type = configuration.getString(path + check_element + ".type", "full");
            ItemStackSimilar itemStackSimilar = this.itemStackService.getVerification(type).orElseGet(() -> {
                Logger.info("Unknown check-item type '" + type + "' for the item_drag button " + path + " in " + defaultButtonValue.getFile().getName() + ", using 'full'.", Logger.LogType.WARNING);
                return new FullSimilar();
            });
            button.setCheckItem(menuItemStack, itemStackSimilar);
        }

        String error_element = configuration.contains(path + "error_item") ? "error_item" : configuration.contains(path + "error-item") ? "error-item" : null;
        if (error_element != null) {
            int ticks = configuration.getInt(path + error_element + ".duration", 20);
            boolean useCache = configuration.getBoolean(path + error_element + ".use-cache", configuration.getBoolean(path + error_element + ".use_cache", true));
            MenuItemStack menuItemStack = this.itemStackService.load(configuration, path + error_element + ".item.", defaultButtonValue.getFile());
            button.setErrorItem(menuItemStack, ticks, useCache);
        }
        ConfigurationSection ruleSection = configuration.getConfigurationSection(path + "rule");
        if (ruleSection != null) {
            Rule rule = ZRuleLoaderRegistry.loadRule(ruleSection.getValues(true));
            if (rule == null || !rule.isValid()) {
                Logger.info("The rule of the item_drag button " + path + " in " + defaultButtonValue.getFile().getName() + " is invalid, the button will refuse every item.", Logger.LogType.WARNING);
                rule = context -> false;
            }
            button.setRule(rule);
        }

        return button;
    }
}