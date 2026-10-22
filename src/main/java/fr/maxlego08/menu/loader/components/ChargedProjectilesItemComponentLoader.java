package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ChargedProjectilesComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyChargedProjectilesComponent;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class ChargedProjectilesItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public ChargedProjectilesItemComponentLoader(MenuPlugin plugin){
        super("charged-projectiles", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<MenuItemStack> projectiles = this.loadItemStackList(mapList, file);
        if (projectiles.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new ChargedProjectilesComponent(projectiles)
                : new LegacyChargedProjectilesComponent(projectiles);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ChargedProjectiles chargedProjectiles = itemStack.getData(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectiles == null || chargedProjectiles.projectiles().isEmpty()) return null;

        List<MenuItemStack> projectiles = this.convertItemStackList(chargedProjectiles.projectiles());
        return projectiles == null ? null : new ChargedProjectilesComponent(projectiles);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof CrossbowMeta crossbowMeta) || !crossbowMeta.hasChargedProjectiles()) return null;

        List<MenuItemStack> projectiles = this.convertItemStackListFromItemMeta(crossbowMeta.getChargedProjectiles());
        return projectiles == null ? null : new LegacyChargedProjectilesComponent(projectiles);
    }
}
