package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.ToolComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZToolRule;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class SpigotToolItemComponentLoader extends ItemComponentLoader {

    public SpigotToolItemComponentLoader() {
        super("tool");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration,
                                        @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        float defaultMiningSpeed = (float) componentSection.getDouble("default_mining_speed", 1.0);
        int damagePerBlock = componentSection.getInt("damage_per_block", 1);
        boolean canDestroyBlocksInCreative = componentSection.getBoolean("can_destroy_blocks_in_creative", true);

        List<Map<?, ?>> rawRulesList = componentSection.getMapList("rules");
        List<ZToolRule<Material>> materialRules = new ArrayList<>();
        List<ZToolRule<Collection<Material>>> materialsRules = new ArrayList<>();
        List<ZToolRule<Tag<Material>>> tagRules = new ArrayList<>();

        for (var rawRuleMap : rawRulesList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> ruleMap = (Map<String, Object>) rawRuleMap;

            float miningSpeed = ((Number) ruleMap.getOrDefault("speed", 1.0)).floatValue();
            boolean correctForDrops = (boolean) ruleMap.getOrDefault("correct_for_drops", false);

            Object blocks = ruleMap.get("blocks");

            if (blocks instanceof String blockString) {
                processBlockString(blockString, miningSpeed, correctForDrops, materialRules, tagRules);
            } else if (blocks instanceof List<?> blockList) {
                processBlockList(blockList, miningSpeed, correctForDrops, materialsRules, tagRules);
            }
        }

        return new ToolComponent(defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative,
                materialRules, materialsRules, tagRules);
    }

    private void processBlockString(String blockString, float miningSpeed, boolean correctForDrops,
                                    List<ZToolRule<Material>> materialRules,
                                    List<ZToolRule<Tag<Material>>> tagRules) {
        parseNamespacedKey(blockString).ifPresent(key -> {
            getTag(key).ifPresentOrElse(
                    tag -> tagRules.add(new ZToolRule<>(tag, miningSpeed, correctForDrops)),
                    () -> getMaterial(key).ifPresent(
                            material -> materialRules.add(new ZToolRule<>(material, miningSpeed, correctForDrops))
                    )
            );
        });
    }

    private void processBlockList(List<?> blockList, float miningSpeed, boolean correctForDrops,
                                  List<ZToolRule<Collection<Material>>> materialsRules,
                                  List<ZToolRule<Tag<Material>>> tagRules) {
        List<Material> materials = new ArrayList<>();

        for (Object blockObj : blockList) {
            if (!(blockObj instanceof String blockName)) continue;

            parseNamespacedKey(blockName).ifPresent(key -> {
                Optional<Tag<Material>> tagOpt = getTag(key);

                if (tagOpt.isPresent()) {
                    if (!materials.isEmpty()) {
                        materialsRules.add(new ZToolRule<>(new ArrayList<>(materials), miningSpeed, correctForDrops));
                        materials.clear();
                    }
                    tagRules.add(new ZToolRule<>(tagOpt.get(), miningSpeed, correctForDrops));
                } else {
                    getMaterial(key).ifPresent(materials::add);
                }
            });
        }

        if (!materials.isEmpty()) {
            materialsRules.add(new ZToolRule<>(materials, miningSpeed, correctForDrops));
        }
    }

    private Optional<NamespacedKey> parseNamespacedKey(String keyString) {
        return Optional.ofNullable(NamespacedKey.fromString(keyString));
    }

    private Optional<Tag<Material>> getTag(NamespacedKey key) {
        return Optional.ofNullable(Bukkit.getTag("items", key, Material.class));
    }

    private Optional<Material> getMaterial(NamespacedKey key) {
        try {
            return Optional.of(Registry.MATERIAL.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}