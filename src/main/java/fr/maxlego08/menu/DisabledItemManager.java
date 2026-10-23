package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.configuration.Feature;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * The {@link ItemManager} used when {@link Feature#CUSTOM_ITEMS} is disabled: it knows no item, loads
 * no file and ignores every registration, so addons keep working without registering anything.
 */
public class DisabledItemManager implements ItemManager {

    @Override
    public void loadAll() {
    }

    @Override
    public void loadCustomItems() {
    }

    @Override
    public void loadCustomItem(File file) {
    }

    @Override
    public void reloadCustomItems() {
    }

    @Override
    public boolean isCustomItem(String itemId) {
        return false;
    }

    @Override
    public boolean isCustomItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public Optional<String> getItemId(ItemStack itemStack) {
        return Optional.empty();
    }

    @Override
    public Set<String> getItemIds() {
        return Set.of();
    }

    @Override
    public void registerListeners(Plugin plugin, String mechanicId, MechanicListener listener) {
        this.ignored(plugin.getName() + " registered the listener of mechanic " + mechanicId);
    }

    @Override
    public void unloadListeners() {
    }

    @Override
    @SuppressWarnings("removal")
    public void registerMechanicFactory(MechanicFactory<?> factory) {
        this.ignored("Mechanic " + factory.getMechanicId() + " was registered");
    }

    @Override
    public Optional<MechanicFactory<?>> getMechanicFactory(String mechanicId) {
        return Optional.empty();
    }

    @Override
    public void giveItem(Player player, String itemId) {
    }

    @Override
    public void executeCheckInventoryItems(Player player) {
    }

    private void ignored(String registration) {
        if (Configuration.enableDebug) {
            Logger.info(registration + ", ignored because features." + Feature.CUSTOM_ITEMS.getPath() + " is disabled.");
        }
    }
}
