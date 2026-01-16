package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.EquippableComponent;
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
import java.util.Optional;

public class EquippableItemComponentLoader extends ItemComponentLoader {

    public EquippableItemComponentLoader() {
        super("equippable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        Optional<EquipmentSlot> slot = loadEquipmentSlot(componentSection.getString("slot", ""));
        Optional<Sound> equipSound = loadSound(componentSection.getString("equip_sound", ""));
        Optional<NamespacedKey> assetId = loadNamespacedKey(componentSection.getString("asset_id", ""));

        boolean dispensable = componentSection.getBoolean("dispensable", true);
        boolean swappable = componentSection.getBoolean("swappable", true);
        boolean damageOnHurt = componentSection.getBoolean("damage_on_hurt", true);
        boolean equipOnInteract = componentSection.getBoolean("equip_on_interact", false);

        Optional<NamespacedKey> cameraOverlay = loadNamespacedKey(componentSection.getString("camera_overlay", ""));
        boolean canBeSheared = componentSection.getBoolean("can_be_sheared", false);
        Optional<Sound> shearingSound = loadSound(componentSection.getString("shearing_sound", ""));

        Optional<Collection<EntityType>> allowedEntities = loadAllowedEntities(componentSection.get("allowed_entities"));
        Optional<Tag<EntityType>> allowedEntityTags = loadAllowedEntityTags(componentSection.get("allowed_entities"));

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
            return Optional.of(EquipmentSlot.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Sound> loadSound(String value) {
        if (value.isBlank()) return Optional.empty();
        try {
            NamespacedKey key = NamespacedKey.fromString(value.toLowerCase());
            return key == null ? Optional.empty() : Optional.of(Registry.SOUNDS.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<NamespacedKey> loadNamespacedKey(String value) {
        if (value.isBlank()) return Optional.empty();
        try {
            return Optional.ofNullable(NamespacedKey.fromString(value.toLowerCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Collection<EntityType>> loadAllowedEntities(Object allowedEntitiesObj) {
        Collection<EntityType> entityTypes = new ArrayList<>();

        if (allowedEntitiesObj instanceof Collection<?> collection) {
            for (Object obj : collection) {
                if (obj instanceof String entityString) {
                    loadEntityType(entityString).ifPresent(entityTypes::add);
                }
            }
        } else if (allowedEntitiesObj instanceof String entityString) {
            loadEntityType(entityString).ifPresent(entityTypes::add);
        }

        return entityTypes.isEmpty() ? Optional.empty() : Optional.of(entityTypes);
    }

    private Optional<Tag<EntityType>> loadAllowedEntityTags(Object allowedEntitiesObj) {
        if (allowedEntitiesObj instanceof Collection<?> collection) {
            for (Object obj : collection) {
                if (obj instanceof String entityString) {
                    Optional<Tag<EntityType>> tag = loadEntityTag(entityString);
                    if (tag.isPresent()) return tag;
                }
            }
        } else if (allowedEntitiesObj instanceof String entityString) {
            return loadEntityTag(entityString);
        }

        return Optional.empty();
    }

    private Optional<EntityType> loadEntityType(String entityString) {
        NamespacedKey key = NamespacedKey.fromString(entityString.toLowerCase());
        if (key == null) return Optional.empty();

        try {
            return Optional.of(Registry.ENTITY_TYPE.getOrThrow(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Tag<EntityType>> loadEntityTag(String entityString) {
        NamespacedKey key = NamespacedKey.fromString(entityString.toLowerCase());
        if (key == null) return Optional.empty();

        try {
            Tag<EntityType> tag = Bukkit.getTag("entity_types", key, EntityType.class);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}