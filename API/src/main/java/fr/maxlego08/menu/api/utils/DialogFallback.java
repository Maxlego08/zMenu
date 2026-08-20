package fr.maxlego08.menu.api.utils;

/**
 * The inventory opened in place of a dialog when the player's client is too old to
 * render dialogs (below Minecraft 1.21.6).
 *
 * @param inventoryName the name of the inventory file, without its extension
 * @param plugin        the plugin owning the inventory, defaults to {@code zMenu}
 * @param page          the page to open, defaults to {@code 1}
 */
public record DialogFallback(String inventoryName, String plugin, int page) {

    public DialogFallback(String inventoryName, String plugin, int page) {
        this.inventoryName = inventoryName;
        this.plugin = plugin == null || plugin.isEmpty() ? "zMenu" : plugin;
        this.page = Math.max(1, page);
    }

    /**
     * @return true if an inventory name was configured.
     */
    public boolean isValid() {
        return this.inventoryName != null && !this.inventoryName.isEmpty();
    }
}
