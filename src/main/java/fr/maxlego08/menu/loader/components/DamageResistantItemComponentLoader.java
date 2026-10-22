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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class DamageResistantItemComponentLoader extends ItemComponentLoader {

    public DamageResistantItemComponentLoader(){
        super("damage-resistant");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String damageTypeString = componentSection.getString("types");
        ResolvableDamageTypeTag resolvable = ResolvableDamageTypeTag.autoOrNull(damageTypeString);
        return resolvable != null ? new DamageResistantComponent(resolvable) : null;
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
