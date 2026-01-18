package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PaperProvidesBannerPatternsComponent(
    TagKey<PatternType> patterns
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, this.patterns);
    }
}
