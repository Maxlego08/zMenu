package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.itemstack.ZToolRule;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
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

    private final @NotNull ResolvableFloat defaultMiningSpeed;
    private final @NotNull ResolvableInt damagePerBlock;
    private final @NotNull ResolvableBoolean canDestroyBlocksInCreative;
    private final List<ZToolRule<Material>> materialRules;
    private final List<ZToolRule<Collection<Material>>> materialGroups;
    private final List<ZToolRule<Tag<Material>>> tagRules;

    public ToolComponent(@NotNull ResolvableFloat defaultMiningSpeed, @NotNull ResolvableInt damagePerBlock, @NotNull ResolvableBoolean canDestroyBlocksInCreative,
                         List<ZToolRule<Material>> materialRules, List<ZToolRule<Collection<Material>>> materialGroups,
                         List<ZToolRule<Tag<Material>>> tagRules) {
        this.defaultMiningSpeed = defaultMiningSpeed;
        this.damagePerBlock = damagePerBlock;
        this.canDestroyBlocksInCreative = canDestroyBlocksInCreative;
        this.materialRules = materialRules;
        this.materialGroups = materialGroups;
        this.tagRules = tagRules;
    }

    public @NotNull ResolvableFloat getDefaultMiningSpeed() {
        return this.defaultMiningSpeed;
    }

    public @NotNull ResolvableInt getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public @NotNull ResolvableBoolean isCanDestroyBlocksInCreative() {
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
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.ToolComponent tool = itemMeta.getTool();

        Resolvable.applyResolvable(context, this.defaultMiningSpeed, tool::setDefaultMiningSpeed);
        Resolvable.applyResolvable(context, this.damagePerBlock, tool::setDamagePerBlock);
        Resolvable.applyResolvable(context, this.canDestroyBlocksInCreative, tool::setCanDestroyBlocksInCreative);

        for (ZToolRule<Material> rule : this.materialRules) {
            tool.addRule(rule.data(), rule.speed(), rule.correctForDrop());
        }
        for (ZToolRule<Collection<Material>> rule : this.materialGroups) {
            tool.addRule(rule.data(), rule.speed(), rule.correctForDrop());
        }
        for (ZToolRule<Tag<Material>> rule : this.tagRules) {
            tool.addRule(rule.data(), rule.speed(), rule.correctForDrop());
        }

        itemStack.setItemMeta(itemMeta);
    }

}
