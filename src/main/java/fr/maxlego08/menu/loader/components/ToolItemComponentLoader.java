package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ToolComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyToolComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKeySet;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableToolRule;
import fr.maxlego08.menu.api.utils.resolvable.paper.TagKeySetResolvable;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21")
public class ToolItemComponentLoader extends ItemComponentLoader {

    public ToolItemComponentLoader() {
        super("tool");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration,
                                         @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableFloat defaultMiningSpeed = this.asResolvableFloat(componentSection, "default-mining-speed", 1.0f);
        ResolvableInt damagePerBlock = this.asResolvableInt(componentSection, "damage-per-block", 1);
        ResolvableBoolean canDestroyBlocksInCreative = this.asResolvableBoolean(componentSection, "can-destroy-blocks-in-creative", true);

        Integer fixedDamagePerBlock = damagePerBlock.getResolvedValue();
        if (fixedDamagePerBlock != null && fixedDamagePerBlock < 0) {
            Logger.info("damage-per-block of the tool component at " + path + " in " + file.getName() + " must not be negative, 1 is used.", Logger.LogType.WARNING);
            damagePerBlock = ResolvableInt.of(1);
        }

        List<ResolvableToolRule> rules = new ArrayList<>();
        for (Map<?, ?> rawRule : componentSection.getMapList("rules")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rule = (Map<String, Object>) rawRule;
            this.loadRule(rule, rules, path, file);
        }

        return MinecraftVersion.isServerAtLeast("1.21.5")
                ? new ToolComponent(defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative, rules)
                : new LegacyToolComponent(defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative, rules);
    }

    private void loadRule(Map<String, Object> rule, List<ResolvableToolRule> rules, String path, File file) {
        // Optional, as in Minecraft: an unset value overrides nothing
        ResolvableFloat speed = ResolvableFloat.of(rule, "speed", null);
        ResolvableBoolean correctForDrops = ResolvableBoolean.of(rule, "correct-for-drops", null);

        Object blocks = rule.get("blocks");
        if (blocks instanceof String block) {
            Resolvable<RegistryKeySet<BlockType>> blockSet = this.isTag(block) ? TagKeySetResolvable.of(RegistryKey.BLOCK, block) : ResolvableRegistryKeySet.typedKeySet(RegistryKey.BLOCK, block);
            rules.add(new ResolvableToolRule(blockSet, speed, correctForDrops));
        } else if (blocks instanceof List<?> blockList) {
            List<String> group = new ArrayList<>();
            for (Object entry : blockList) {
                if (entry == null) continue;
                String block = entry.toString();
                if (block.startsWith("#")) {
                    if (!group.isEmpty()) {
                        rules.add(new ResolvableToolRule(ResolvableRegistryKeySet.typedKeySet(RegistryKey.BLOCK, group), speed, correctForDrops));
                        group = new ArrayList<>();
                    }
                    rules.add(new ResolvableToolRule(TagKeySetResolvable.of(RegistryKey.BLOCK, block), speed, correctForDrops));
                } else {
                    group.add(block);
                }
            }
            if (!group.isEmpty()) rules.add(new ResolvableToolRule(ResolvableRegistryKeySet.typedKeySet(RegistryKey.BLOCK, group), speed, correctForDrops));
        } else {
            Logger.info("A rule of the tool component at " + path + " in " + file.getName() + " has no blocks, it is ignored.", Logger.LogType.WARNING);
        }
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.5")) return null;
        Tool tool = itemStack.getData(DataComponentTypes.TOOL);
        if (tool == null) return null;
        List<ResolvableToolRule> rules = new ArrayList<>();
        for (Tool.Rule rule : tool.rules()) {
            Resolvable<RegistryKeySet<BlockType>> blocks = ResolvableRegistryKeySet.of(rule.blocks());
            if (blocks == null) return null;
            ResolvableFloat speed = rule.speed() == null ? null : ResolvableFloat.of(rule.speed());
            Boolean correctForDrops = rule.correctForDrops().toBoolean();
            rules.add(new ResolvableToolRule(blocks, speed, correctForDrops == null ? null : ResolvableBoolean.of(correctForDrops)));
        }
        return new ToolComponent(ResolvableFloat.of(tool.defaultMiningSpeed()), ResolvableInt.of(tool.damagePerBlock()), ResolvableBoolean.of(tool.canDestroyBlocksInCreative()), rules);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasTool()) return null;
        org.bukkit.inventory.meta.components.ToolComponent tool = itemMeta.getTool();
        if (tool.getDamagePerBlock() < 0) return null;

        List<ResolvableToolRule> rules = new ArrayList<>();
        for (org.bukkit.inventory.meta.components.ToolComponent.ToolRule rule : tool.getRules()) {
            Resolvable<RegistryKeySet<BlockType>> blocks = this.toBlocks(rule);
            if (blocks == null) return null;
            ResolvableFloat speed = rule.getSpeed() == null ? null : ResolvableFloat.of(rule.getSpeed());
            ResolvableBoolean correctForDrops = rule.isCorrectForDrops() == null ? null : ResolvableBoolean.of(rule.isCorrectForDrops());
            rules.add(new ResolvableToolRule(blocks, speed, correctForDrops));
        }
        return new LegacyToolComponent(ResolvableFloat.of(tool.getDefaultMiningSpeed()), ResolvableInt.of(tool.getDamagePerBlock()), ResolvableBoolean.of(true), rules);
    }

    private @Nullable Resolvable<RegistryKeySet<BlockType>> toBlocks(@NotNull org.bukkit.inventory.meta.components.ToolComponent.ToolRule rule) {
        if (rule.serialize().get("blocks") instanceof String tag && tag.startsWith("#")) {
            return TagKeySetResolvable.of(RegistryKey.BLOCK, tag);
        }
        Collection<Material> materials = rule.getBlocks();
        if (materials.isEmpty()) return null;
        List<String> blocks = new ArrayList<>(materials.size());
        for (Material material : materials) {
            blocks.add(material.getKey().asString());
        }
        return ResolvableRegistryKeySet.typedKeySet(RegistryKey.BLOCK, blocks);
    }

    /**
     * A tag is written with a {@code #}. Without it, a single value naming an existing block tag is
     * still read as that tag, as zMenu always did.
     */
    private boolean isTag(String value) {
        if (value.startsWith("#")) return true;
        if (Resolvable.isExpression(value)) return false;
        NamespacedKey key = NamespacedKey.fromString(value);
        return key != null && Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class) != null;
    }
}
