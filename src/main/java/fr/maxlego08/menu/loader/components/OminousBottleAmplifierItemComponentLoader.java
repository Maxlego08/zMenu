package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.OminousBottleAmplifierComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyOminousBottleAmplifierComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class OminousBottleAmplifierItemComponentLoader extends ItemComponentLoader {

    public OminousBottleAmplifierItemComponentLoader(){
        super("ominous-bottle-amplifier");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        ResolvableInt amplifier = this.asResolvableInt(configuration, path);
        if (amplifier == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new OminousBottleAmplifierComponent(amplifier)
                : new LegacyOminousBottleAmplifierComponent(amplifier);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        OminousBottleAmplifier amplifier = itemStack.getData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
        return amplifier == null ? null : new OminousBottleAmplifierComponent(ResolvableInt.of(amplifier.amplifier()));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof OminousBottleMeta ominousBottleMeta) || !ominousBottleMeta.hasAmplifier()) return null;
        return new LegacyOminousBottleAmplifierComponent(ResolvableInt.of(ominousBottleMeta.getAmplifier()));
    }
}
