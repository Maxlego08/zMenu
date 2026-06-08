package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EquippableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

@AutoComponentLoader
@SinceVersion("1.21.2")
public class SpigotEquippableItemComponentLoader extends ItemComponentLoader {

    public SpigotEquippableItemComponentLoader() {
        super("equippable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        Optional<EquipmentSlot> slot = this.loadEquipmentSlot(componentSection.getString("slot", ""));
        Optional<Sound> equipSound = this.loadSound(componentSection.getString("equip-sound", ""));
        Optional<NamespacedKey> assetId = this.loadNamespacedKey(componentSection.getString("asset-id", ""));

        boolean dispensable = componentSection.getBoolean("dispensable", true);
        boolean swappable = componentSection.getBoolean("swappable", true);
        boolean damageOnHurt = componentSection.getBoolean("damage-on-hurt", true);
        boolean equipOnInteract = componentSection.getBoolean("equip-on-interact", false);

        Optional<NamespacedKey> cameraOverlay = this.loadNamespacedKey(componentSection.getString("camera-overlay", ""));
        boolean canBeSheared = componentSection.getBoolean("can-be-sheared", false);
        Optional<Sound> shearingSound = this.loadSound(componentSection.getString("shearing-sound", ""));

        Optional<Collection<EntityType>> allowedEntities = this.loadAllowedEntities(componentSection.get("allowed-entities"));
        Optional<Tag<EntityType>> allowedEntityTags = this.loadAllowedEntityTags(componentSection.get("allowed-entities"));

        return new EquippableComponent(
                slot,
                equipSound,
                assetId,
                dispensable,
                swappable,
                damageOnHurt,
                equipOnInteract,
                cameraOverlay,
                canBeSheared,
                shearingSound,
                allowedEntities,
                allowedEntityTags
        );
    }

    private Optional<EquipmentSlot> loadEquipmentSlot(String value) {
        if (value.isBlank()) return Optional.empty();
        try {
            return Optional.of(EquipmentSlot.valueOf(value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Sound> loadSound(String value) {
        if (value.isBlank()) return Optional.empty();
        try {
            NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
            return key == null ? Optional.empty() : Optional.of(Registry.SOUNDS.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<NamespacedKey> loadNamespacedKey(String value) {
        if (value.isBlank()) return Optional.empty();
        try {
            return Optional.ofNullable(NamespacedKey.fromString(value.toLowerCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Collection<EntityType>> loadAllowedEntities(Object allowedEntitiesObj) {
        Collection<EntityType> entityTypes = new ArrayList<>();

        if (allowedEntitiesObj instanceof Collection<?> collection) {
            for (Object obj : collection) {
                if (obj instanceof String entityString) {
                    this.loadEntityType(entityString).ifPresent(entityTypes::add);
                }
            }
        } else if (allowedEntitiesObj instanceof String entityString) {
            this.loadEntityType(entityString).ifPresent(entityTypes::add);
        }

        return entityTypes.isEmpty() ? Optional.empty() : Optional.of(entityTypes);
    }

    private Optional<Tag<EntityType>> loadAllowedEntityTags(Object allowedEntitiesObj) {
        if (allowedEntitiesObj instanceof Collection<?> collection) {
            for (Object obj : collection) {
                if (obj instanceof String entityString) {
                    Optional<Tag<EntityType>> tag = this.loadEntityTag(entityString);
                    if (tag.isPresent()) return tag;
                }
            }
        } else if (allowedEntitiesObj instanceof String entityString) {
            return this.loadEntityTag(entityString);
        }

        return Optional.empty();
    }

    private Optional<EntityType> loadEntityType(String entityString) {
        NamespacedKey key = NamespacedKey.fromString(entityString.toLowerCase(Locale.ROOT));
        if (key == null) return Optional.empty();

        try {
            return Optional.of(Registry.ENTITY_TYPE.getOrThrow(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Tag<EntityType>> loadEntityTag(String entityString) {
        NamespacedKey key = NamespacedKey.fromString(entityString.toLowerCase(Locale.ROOT));
        if (key == null) return Optional.empty();

        try {
            Tag<EntityType> tag = Bukkit.getTag("entity-types", key, EntityType.class);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}