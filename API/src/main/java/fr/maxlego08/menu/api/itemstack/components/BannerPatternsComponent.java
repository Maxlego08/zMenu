package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableBannerPattern;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class BannerPatternsComponent extends ItemComponent {
    private final @Nullable List<ResolvableBannerPattern> resolvablePatterns;

    public BannerPatternsComponent(@Nullable List<ResolvableBannerPattern> resolvablePatterns) {
        this.resolvablePatterns = resolvablePatterns;
    }

    @Nullable
    public List<ResolvableBannerPattern> getPatterns() {
        return this.resolvablePatterns;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.resolvablePatterns, patterns -> itemStack.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(patterns)));
    }

    @Override
    public @Nullable Object serialize() {
        return this.resolvablePatterns == null ? null : Resolvable.serializeList(this.resolvablePatterns);
    }

}
