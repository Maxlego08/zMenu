package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.itemstack.ItemStackService;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.itemstack.MenuItemStackConversion;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.PlayerUtil;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFromItemStack;
import fr.maxlego08.menu.common.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class ZItemStackService extends ZUtils implements ItemStackService {

    private final MenuPlugin plugin;

    public ZItemStackService(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public MenuItemStack load(YamlConfiguration configuration, String path, File file) {
        return new MenuItemStackLoader(this.plugin.getInventoryManager()).load(configuration, path, file);
    }

    @Override
    public MenuItemStack load(File file, Map<String, Object> map) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.createSection("item", map);
        return this.load(configuration, "item.", file);
    }

    @Override
    public @NotNull MenuItemStackConversion fromItemStack(@NotNull ItemStack itemStack) {
        return MenuItemStackFromItemStack.convert(this.plugin.getInventoryManager(), itemStack);
    }

    @Override
    public String serialize(ItemStack itemStack) {
        return ItemStackUtils.serializeItemStack(itemStack);
    }

    @Override
    public ItemStack deserialize(String serializedItemStack) {
        return ItemStackUtils.deserializeItemStack(serializedItemStack);
    }

    @Override
    public ItemStack applyPlayerHead(ItemStack itemStack, Button button, Player player, Placeholders placeholders) {

        PerformanceDebug performanceDebug = PerformanceDebug.create("Skull item stack processing");

        performanceDebug.start("papi");
        String name = this.papi(this.plugin.parse(player, placeholders.parse(button.getPlayerHead().replace("%player%", player.getName()))), player, true);
        performanceDebug.end();

        if (!PlayerUtil.isValidMinecraftUsername(name)) {
            performanceDebug.printSummary();
            return itemStack;
        }

        performanceDebug.start("findOfflinePlayer");
        OfflinePlayer offlinePlayer = OfflinePlayerCache.get(name);
        performanceDebug.end();

        if (itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
            performanceDebug.start("applyOwnerProfile");
            skullMeta.setOwnerProfile(offlinePlayer.getPlayerProfile().getTextures().isEmpty() ? offlinePlayer.getPlayerProfile().update().join() : offlinePlayer.getPlayerProfile());
            itemStack.setItemMeta(skullMeta);
            performanceDebug.end();
        }

        performanceDebug.printSummary();

        return itemStack;
    }

    @Override
    public Optional<ItemStackSimilar> getVerification(String name) {
        Key key = ZMenuKeys.tryParse(name);
        return key == null ? Optional.empty() : this.plugin.getInventoryManager().getRegistries().registry(Registries.ITEM_VERIFICATION).get(key);
    }
}
