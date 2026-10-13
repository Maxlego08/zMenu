package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EnchantmentGlintOverrideComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyEnchantmentGlintOverrideComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class EnchantmentGlintOverrideItemComponentLoader extends ItemComponentLoader {

    public EnchantmentGlintOverrideItemComponentLoader(){
        super("enchantment-glint-override");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableBoolean hasGlint = this.asResolvableBoolean(configuration, path);
        if (hasGlint != null) {
            return MinecraftVersion.isServerAtLeast("1.21.3")
                    ? new EnchantmentGlintOverrideComponent(hasGlint)
                    : new LegacyEnchantmentGlintOverrideComponent(hasGlint);
        }
        return null;
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Boolean hasGlint = itemStack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        return hasGlint == null ? null : new EnchantmentGlintOverrideComponent(ResolvableBoolean.of(hasGlint));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasEnchantmentGlintOverride()) return null;
        return new LegacyEnchantmentGlintOverrideComponent(ResolvableBoolean.of(itemMeta.getEnchantmentGlintOverride()));
    }
}
