package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.button.buttons.ZItemDragButton;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

public class ItemDragLoader extends ButtonLoader {
    private final MenuPlugin plugin;

    private final InventoryManager inventoryManager;

    public ItemDragLoader(MenuPlugin plugin) {
        super(plugin, "item_drag");
        this.plugin = plugin;
        this.inventoryManager = plugin.getInventoryManager();
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        ZItemDragButton button = new ZItemDragButton(this.plugin);
        String check_element = configuration.contains(path + "check_item") ? "check_item" : configuration.contains(path + "check-item") ? "check-item" : null;

        if (check_element != null) {
            MenuItemStack menuItemStack = this.inventoryManager.loadItemStack(configuration, path + check_element + ".item.", defaultButtonValue.getFile());
            String type = configuration.getString(path + check_element + ".type", "full");
            ItemStackSimilar itemStackSimilar = this.inventoryManager.getItemStackVerification(type).orElseGet(FullSimilar::new);
            button.setCheckItem(menuItemStack, itemStackSimilar);
        }

        String error_element = configuration.contains(path + "error_item") ? "error_item" : configuration.contains(path + "error-item") ? "error-item" : null;
        if (error_element != null) {
            int ticks = configuration.getInt(path + error_element + ".duration", 20);
            boolean useCache = configuration.getBoolean(path + error_element + ".use_cache", true);
            MenuItemStack menuItemStack = this.inventoryManager.loadItemStack(configuration, path + error_element + ".item.", defaultButtonValue.getFile());
            button.setErrorItem(menuItemStack, ticks, useCache);
        }
        ConfigurationSection ruleSection = configuration.getConfigurationSection(path + "rule");
        if (ruleSection != null) {
            Rule rule = ZRuleLoaderRegistry.getInstance().loadRule(ruleSection.getValues(true));
            button.setRule(rule);
        }

        return button;
    }
}