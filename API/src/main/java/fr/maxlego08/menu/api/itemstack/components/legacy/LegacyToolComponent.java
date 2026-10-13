package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.ToolComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableToolRule;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LegacyToolComponent extends ToolComponent {

    public LegacyToolComponent(@NotNull ResolvableFloat defaultMiningSpeed, @NotNull ResolvableInt damagePerBlock, @NotNull ResolvableBoolean canDestroyBlocksInCreative,
                               @NotNull List<ResolvableToolRule> rules) {
        super(defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative, rules);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.ToolComponent tool = itemMeta.getTool();
        this.applyResolvable(context, tool::setDefaultMiningSpeed, this.getDefaultMiningSpeed());
        this.applyResolvable(context, tool::setDamagePerBlock, this.getDamagePerBlock());
        tool.setRules(new ArrayList<>());
        for (ResolvableToolRule rule : this.getRules()) {
            this.addRule(context, tool, rule);
        }
        itemMeta.setTool(tool);

        itemStack.setItemMeta(itemMeta);
    }

    private void addRule(@NotNull BuildContext context, @NotNull org.bukkit.inventory.meta.components.ToolComponent tool, @NotNull ResolvableToolRule rule) {
        RegistryKeySet<BlockType> blocks = rule.blocks().resolve(context);
        if (blocks == null) return;
        Float speed = Resolvable.resolve(context, rule.speed());
        Boolean correctForDrops = Resolvable.resolve(context, rule.correctForDrops());

        if (blocks instanceof io.papermc.paper.registry.tag.Tag<BlockType> blockTag) {
            Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, this.toNamespacedKey(blockTag.tagKey().key()), Material.class);
            if (tag != null) tool.addRule(tag, speed, correctForDrops);
            return;
        }

        List<Material> materials = new ArrayList<>();
        for (TypedKey<BlockType> block : blocks.values()) {
            Material material = Registry.MATERIAL.get(this.toNamespacedKey(block.key()));
            if (material != null && material.isBlock()) materials.add(material);
        }
        if (!materials.isEmpty()) tool.addRule(materials, speed, correctForDrops);
    }

    private @NotNull NamespacedKey toNamespacedKey(@NotNull Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }
}
