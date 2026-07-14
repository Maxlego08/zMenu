package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class PotionContentsComponent extends ItemComponent {
    private final @Nullable ResolvableRegistryEntry<PotionType> basePotionType;
    private final @Nullable Resolvable<String> customName;
    private final @Nullable ResolvableColor color;
    private final @NotNull List<ResolvablePotionEffect> potionEffects;

    public PotionContentsComponent(@Nullable ResolvableRegistryEntry<PotionType> basePotionType, @Nullable Resolvable<String> customName, @Nullable ResolvableColor color, @NotNull List<ResolvablePotionEffect> potionEffects) {
        this.basePotionType = basePotionType;
        this.customName = customName;
        this.color = color;
        this.potionEffects = potionEffects;
    }

    public @Nullable ResolvableRegistryEntry<PotionType> getBasePotionType() {
        return this.basePotionType;
    }

    public @Nullable Resolvable<String> getCustomName() {
        return this.customName;
    }

    public @Nullable ResolvableColor getColor() {
        return this.color;
    }

    public @NotNull List<ResolvablePotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        PotionContents.Builder builder = PotionContents.potionContents();

        Resolvable.applyResolvable(context, this.basePotionType, builder::potion);
        Resolvable.applyResolvable(context, this.color, builder::customColor);
        Resolvable.applyResolvable(context, this.customName, builder::customName);
        Resolvable.applyResolvable(context, this.potionEffects, builder::addCustomEffects);

        itemStack.setData(DataComponentTypes.POTION_CONTENTS, builder.build());
    }
}
