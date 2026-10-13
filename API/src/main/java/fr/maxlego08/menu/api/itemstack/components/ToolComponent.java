package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableToolRule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The {@code tool} component: how fast the item mines, what it costs in durability, and the rules
 * for specific blocks. When several rules match a block, the first one in the list wins.
 */
@SuppressWarnings("unused")
public class ToolComponent extends ItemComponent {

    private final @NotNull ResolvableFloat defaultMiningSpeed;
    private final @NotNull ResolvableInt damagePerBlock;
    private final @NotNull ResolvableBoolean canDestroyBlocksInCreative;
    private final @NotNull List<ResolvableToolRule> rules;

    public ToolComponent(@NotNull ResolvableFloat defaultMiningSpeed, @NotNull ResolvableInt damagePerBlock, @NotNull ResolvableBoolean canDestroyBlocksInCreative,
                         @NotNull List<ResolvableToolRule> rules) {
        this.defaultMiningSpeed = defaultMiningSpeed;
        this.damagePerBlock = damagePerBlock;
        this.canDestroyBlocksInCreative = canDestroyBlocksInCreative;
        this.rules = rules;
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

    /**
     * @return the rules, in the order they are checked
     */
    public @NotNull List<ResolvableToolRule> getRules() {
        return this.rules;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Tool.Builder tool = Tool.tool();

        Resolvable.applyResolvable(context, this.defaultMiningSpeed, tool::defaultMiningSpeed);
        Resolvable.applyResolvable(context, this.damagePerBlock, tool::damagePerBlock);
        Resolvable.applyResolvable(context, this.canDestroyBlocksInCreative, tool::canDestroyBlocksInCreative);
        Resolvable.applyResolvable(context, this.rules, tool::addRules);

        itemStack.setData(DataComponentTypes.TOOL, tool.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (!Objects.equals(this.defaultMiningSpeed.serialize(), 1.0f)) map.put("default-mining-speed", this.defaultMiningSpeed.serialize());
        if (!Objects.equals(this.damagePerBlock.serialize(), 1)) map.put("damage-per-block", this.damagePerBlock.serialize());
        if (!Boolean.TRUE.equals(this.canDestroyBlocksInCreative.serialize())) map.put("can-destroy-blocks-in-creative", this.canDestroyBlocksInCreative.serialize());
        if (!this.rules.isEmpty()) map.put("rules", Resolvable.serializeList(this.rules));
        return map;
    }

}
