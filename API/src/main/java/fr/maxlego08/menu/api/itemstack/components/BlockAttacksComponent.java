package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableDamageReduction;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableItemDamageFunction;
import fr.maxlego08.menu.api.utils.resolvable.paper.TagKeyResolvable;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockAttacksComponent extends ItemComponent {
    private final ResolvableFloat blockDelaySeconds;
    private final ResolvableFloat disableCooldownScale;
    private final ResolvableNamespacedKey blockSound;
    private final ResolvableNamespacedKey disableSound;
    private final TagKeyResolvable<DamageType> bypassedBy;
    private final ResolvableItemDamageFunction itemDamageFunction;
    private final List<ResolvableDamageReduction> damageReductions;

    public BlockAttacksComponent(ResolvableFloat blockDelaySeconds, ResolvableFloat disableCooldownScale, ResolvableNamespacedKey blockSound, ResolvableNamespacedKey disableSound, TagKeyResolvable<DamageType> bypassedBy, ResolvableItemDamageFunction itemDamageFunction, List<ResolvableDamageReduction> damageReductions) {
        this.blockDelaySeconds = blockDelaySeconds;
        this.disableCooldownScale = disableCooldownScale;
        this.blockSound = blockSound;
        this.disableSound = disableSound;
        this.bypassedBy = bypassedBy;
        this.itemDamageFunction = itemDamageFunction;
        this.damageReductions = damageReductions;
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks();

        Resolvable.applyResolvable(context, this.blockDelaySeconds, builder::blockDelaySeconds);
        Resolvable.applyResolvable(context, this.disableCooldownScale, builder::disableCooldownScale);
        Resolvable.applyResolvable(context, this.blockSound, builder::blockSound);
        Resolvable.applyResolvable(context, this.disableSound, builder::disableSound);

        Resolvable.applyResolvable(context, this.bypassedBy, builder::bypassedBy);

        Resolvable.applyResolvable(context, this.itemDamageFunction, builder::itemDamage);

        Resolvable.applyResolvable(context, this.damageReductions, builder::damageReductions);

        itemStack.setData(DataComponentTypes.BLOCKS_ATTACKS, builder.build());
    }
}
