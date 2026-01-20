package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class ToolComponent extends ItemComponent {

    private final float defaultMiningSpeed;
    private final int damagePerBlock;
    private final boolean canDestroyBlocksInCreative;
    private final List<ZToolRule<Material>> materialRules;
    private final List<ZToolRule<Collection<Material>>> materialGroups;
    private final List<ZToolRule<Tag<Material>>> tagRules;

    public ToolComponent(float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative,
                         List<ZToolRule<Material>> materialRules, List<ZToolRule<Collection<Material>>> materialGroups,
                         List<ZToolRule<Tag<Material>>> tagRules) {
        this.defaultMiningSpeed = defaultMiningSpeed;
        this.damagePerBlock = damagePerBlock;
        this.canDestroyBlocksInCreative = canDestroyBlocksInCreative;
        this.materialRules = materialRules;
        this.materialGroups = materialGroups;
        this.tagRules = tagRules;
    }

    public float getDefaultMiningSpeed() {
        return this.defaultMiningSpeed;
    }

    public int getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public boolean isCanDestroyBlocksInCreative() {
        return this.canDestroyBlocksInCreative;
    }

    public List<ZToolRule<Material>> getMaterialRules() {
        return this.materialRules;
    }

    public List<ZToolRule<Collection<Material>>> getMaterialGroups() {
        return this.materialGroups;
    }

    public List<ZToolRule<Tag<Material>>> getTagRules() {
        return this.tagRules;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
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
