package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EquippableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.*;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.21.2")
public class SpigotEquippableItemComponentLoader extends ItemComponentLoader {

    public SpigotEquippableItemComponentLoader() {
        super("equippable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableEquipmentSlot slot = this.loadEquipmentSlot(componentSection.getString("slot"));
        fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey equipSound = this.loadNamespacedKey(componentSection.getString("equip-sound"));
        ResolvableNamespacedKey assetId = this.loadNamespacedKey(componentSection.getString("asset-id"));

        ResolvableBoolean dispensable = this.asResolvableBoolean(componentSection, "dispensable", true);
        ResolvableBoolean swappable = this.asResolvableBoolean(componentSection, "swappable", true);
        ResolvableBoolean damageOnHurt = this.asResolvableBoolean(componentSection, "damage-on-hurt", true);
        ResolvableBoolean equipOnInteract = this.asResolvableBoolean(componentSection, "equip-on-interact", false);

        ResolvableNamespacedKey cameraOverlay = this.loadNamespacedKey(componentSection.getString("camera-overlay"));
        ResolvableBoolean canBeSheared = this.asResolvableBoolean(componentSection, "can-be-sheared", false);
        ResolvableNamespacedKey shearingSound = this.loadNamespacedKey(componentSection.getString("shearing-sound"));

        List<String> entityStrings = new ArrayList<>();
        Object allowedEntitiesObj = componentSection.get("allowed-entities");
        if (allowedEntitiesObj instanceof List<?> list) {
            for (Object obj : list) {
                if (obj instanceof String s) entityStrings.add(s);
            }
        } else if (allowedEntitiesObj instanceof String s) {
            entityStrings.add(s);
        }

        List<ResolvableEntityType> allowedEntities = null;
        ResolvableEntityTypeTag allowedEntityTags = null;

        for (String es : entityStrings) {
            if (es.startsWith("#")) {
                allowedEntityTags = ResolvableEntityTypeTag.autoOrNull(es.substring(1));
            } else {
                if (allowedEntities == null) allowedEntities = new ArrayList<>();
                ResolvableEntityType et = ResolvableEntityType.autoOrNull(es);
                allowedEntities.add(et);
            }
        }

        return new EquippableComponent(
                slot, equipSound, assetId,
                dispensable, swappable, damageOnHurt, equipOnInteract,
                cameraOverlay, canBeSheared, shearingSound,
                allowedEntities, allowedEntityTags
        );
    }

    private @Nullable ResolvableEquipmentSlot loadEquipmentSlot(@Nullable String value) {
        return ResolvableEquipmentSlot.autoOrNull(value);
    }

    private @Nullable ResolvableSound loadSound(@Nullable String value) {
        return ResolvableSound.autoOrNull(value);
    }

    private @Nullable ResolvableNamespacedKey loadNamespacedKey(@Nullable String value) {
        return ResolvableNamespacedKey.autoOrNull(value);
    }
}