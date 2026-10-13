package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MobVisibilityComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKeySet;
import fr.maxlego08.menu.api.utils.resolvable.paper.TagKeySetResolvable;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MobVisibility;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("26.3")
public final class MobVisibilityItemComponentLoader extends ItemComponentLoader {

    public MobVisibilityItemComponentLoader() {
        super("mob-visibility");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        Object rawEntityTypes = componentSection.get("targeting-entity-types");
        Resolvable<RegistryKeySet<EntityType>> entityTypes;
        if (rawEntityTypes instanceof String entityType && entityType.startsWith("#")) {
            entityTypes = TagKeySetResolvable.of(RegistryKey.ENTITY_TYPE, entityType);
        } else {
            entityTypes = ResolvableRegistryKeySet.typedKeySetOrNull(RegistryKey.ENTITY_TYPE, rawEntityTypes);
        }

        ResolvableFloat visibility = this.asResolvableFloat(componentSection, "visibility");
        if (entityTypes == null || visibility == null) {
            Logger.info("The mob-visibility component at " + path + " in " + file.getName() + " needs targeting-entity-types and visibility, it is ignored.", Logger.LogType.WARNING);
            return null;
        }

        Float fixedVisibility = visibility.getResolvedValue();
        if (fixedVisibility != null && (fixedVisibility < 0 || fixedVisibility > 10)) {
            Logger.info("visibility of the mob-visibility component at " + path + " in " + file.getName() + " must be between 0 and 10.", Logger.LogType.WARNING);
            visibility = ResolvableFloat.of(Math.clamp(fixedVisibility, 0f, 10f));
        }

        return new MobVisibilityComponent(entityTypes, visibility);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        MobVisibility mobVisibility = itemStack.getData(DataComponentTypes.MOB_VISIBILITY);
        if (mobVisibility == null) return null;

        float visibility = mobVisibility.visibility();
        if (!(visibility >= 0 && visibility <= 10)) return null;

        RegistryKeySet<EntityType> targetingEntityTypes = mobVisibility.targetingEntityTypes();
        Resolvable<RegistryKeySet<EntityType>> entityTypes;
        if (targetingEntityTypes instanceof Tag<EntityType> tag) {
            entityTypes = TagKeySetResolvable.of(RegistryKey.ENTITY_TYPE, tag.tagKey().key().asString());
        } else {
            if (targetingEntityTypes.values().isEmpty()) return null;
            List<String> keys = new ArrayList<>(targetingEntityTypes.values().size());
            for (TypedKey<EntityType> key : targetingEntityTypes.values()) {
                keys.add(key.key().asString());
            }
            entityTypes = ResolvableRegistryKeySet.typedKeySet(RegistryKey.ENTITY_TYPE, keys);
        }

        return new MobVisibilityComponent(entityTypes, ResolvableFloat.of(visibility));
    }
}
