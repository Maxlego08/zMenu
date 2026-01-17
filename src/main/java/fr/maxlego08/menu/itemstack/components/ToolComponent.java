package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZToolRule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public record ToolComponent(
    float defaultMiningSpeed,
    int damagePerBlock,
    boolean canDestroyBlocksInCreative,
    List<ZToolRule<Material>> materialRules,
    List<ZToolRule<Collection<Material>>> materialGroups,
    List<ZToolRule<Tag<Material>>> tagRules
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.ToolComponent tool = itemMeta.getTool();

            tool.setDefaultMiningSpeed(this.defaultMiningSpeed);
            tool.setDamagePerBlock(this.damagePerBlock);
            tool.setCanDestroyBlocksInCreative(this.canDestroyBlocksInCreative);

            for (ZToolRule<Material> rule : this.materialRules) {
                tool.addRule(rule.getData(), rule.getSpeed(), rule.isCorrectForDrop());
            }

            for (ZToolRule<Collection<Material>> rule : this.materialGroups) {
                tool.addRule(rule.getData(), rule.getSpeed(), rule.isCorrectForDrop());
            }

            for (ZToolRule<Tag<Material>> rule : this.tagRules) {
                tool.addRule(rule.getData(), rule.getSpeed(), rule.isCorrectForDrop());
            }

            itemStack.setItemMeta(itemMeta);
        }
    }
}
