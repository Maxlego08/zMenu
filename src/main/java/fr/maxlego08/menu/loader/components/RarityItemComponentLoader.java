package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.RarityComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyRarityComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class RarityItemComponentLoader extends ItemComponentLoader {

    public RarityItemComponentLoader(){
        super("rarity");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        ResolvableEnum<ItemRarity> rarityResolvable = ResolvableEnum.auto(ItemRarity.class, value);
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new RarityComponent(rarityResolvable)
                : new LegacyRarityComponent(rarityResolvable);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemRarity rarity = itemStack.getData(DataComponentTypes.RARITY);
        return rarity == null ? null : new RarityComponent(ResolvableEnum.of(ItemRarity.class, rarity));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasRarity()) return null;
        return new LegacyRarityComponent(ResolvableEnum.of(ItemRarity.class, itemMeta.getRarity()));
    }
}
