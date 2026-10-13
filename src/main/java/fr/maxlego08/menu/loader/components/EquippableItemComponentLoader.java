package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EquippableComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyEquippableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.*;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.21.2")
public class EquippableItemComponentLoader extends ItemComponentLoader {

    public EquippableItemComponentLoader() {
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

        return MinecraftVersion.isServerAtLeast("1.21.6")
                ? new EquippableComponent(
                        slot, equipSound, assetId,
                        dispensable, swappable, damageOnHurt, equipOnInteract,
                        cameraOverlay, canBeSheared, shearingSound,
                        allowedEntities, allowedEntityTags
                )
                : new LegacyEquippableComponent(
                        slot, equipSound, assetId,
                        dispensable, swappable, damageOnHurt, equipOnInteract,
                        cameraOverlay, canBeSheared, shearingSound,
                        allowedEntities, allowedEntityTags
                );
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.6")) return null;
        Equippable equippable = itemStack.getData(DataComponentTypes.EQUIPPABLE);
        if (equippable == null || equippable.allowedEntities() != null) return null;
        return new EquippableComponent(
                ResolvableEquipmentSlot.of(equippable.slot()),
                this.toResolvableKey(equippable.equipSound()),
                this.toResolvableKey(equippable.assetId()),
                ResolvableBoolean.of(equippable.dispensable()),
                ResolvableBoolean.of(equippable.swappable()),
                ResolvableBoolean.of(equippable.damageOnHurt()),
                ResolvableBoolean.of(equippable.equipOnInteract()),
                this.toResolvableKey(equippable.cameraOverlay()),
                ResolvableBoolean.of(equippable.canBeSheared()),
                this.toResolvableKey(equippable.shearSound()),
                null,
                null
        );
    }

    private @Nullable ResolvableNamespacedKey toResolvableKey(@Nullable Key key) {
        if (key == null) return null;
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
        return namespacedKey == null ? null : ResolvableNamespacedKey.of(namespacedKey);
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