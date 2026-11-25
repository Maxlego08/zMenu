package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DamageResistantComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageTypeTag;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class DamageResistantItemComponentLoader extends ItemComponentLoader {

    public DamageResistantItemComponentLoader(){
        super("damage-resistant");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        String damageTypeString = componentSection.getString("types");
        ResolvableDamageTypeTag resolvable = ResolvableDamageTypeTag.autoOrNull(damageTypeString);
        return resolvable != null ? new DamageResistantComponent(resolvable) : null;
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
        DamageResistant damageResistant = itemStack.getData(DataComponentTypes.DAMAGE_RESISTANT);
        if (damageResistant == null) return null;
        Object types;
        try {
            types = DamageResistant.class.getMethod("types").invoke(damageResistant);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
        Key tagKey;
        if (types instanceof TagKey<?> key) {
            tagKey = key.key();
        } else if (types instanceof Tag<?> tag) {
            tagKey = tag.tagKey().key();
        } else {
            return null;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(tagKey.asString());
        if (namespacedKey == null) return null;
        org.bukkit.Tag<DamageType> damageTypeTag = Bukkit.getTag("damage-types", namespacedKey, DamageType.class);
        return damageTypeTag == null ? null : new DamageResistantComponent(ResolvableDamageTypeTag.of(damageTypeTag));
    }
}
