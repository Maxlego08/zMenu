package fr.maxlego08.menu.itemstack.paper.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeathProtectionComponent implements ItemComponent {
    private final List<ConsumeEffect> effects;

    public DeathProtectionComponent(@NotNull List<@NotNull ConsumeEffect> effects) {
        this.effects = effects;
    }

    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(this.effects));
    }
}
