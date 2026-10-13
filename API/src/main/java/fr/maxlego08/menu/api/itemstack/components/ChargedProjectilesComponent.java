package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ChargedProjectilesComponent extends ItemComponent {
    private final @NotNull List<@NotNull MenuItemStack> projectiles;

    public ChargedProjectilesComponent(@NotNull List<@NotNull MenuItemStack> projectiles) {
        this.projectiles = projectiles;
    }

    public @NotNull List<@NotNull MenuItemStack> getProjectiles() {
        return this.projectiles;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ChargedProjectiles.Builder chargedProjectiles = ChargedProjectiles.chargedProjectiles();
        for (MenuItemStack menuItemStack : this.projectiles) {
            chargedProjectiles.add(menuItemStack.build(player));
        }
        itemStack.setData(DataComponentTypes.CHARGED_PROJECTILES, chargedProjectiles.build());
    }

    @Override
    public @Nullable Object serialize() {
        List<Map<String, Object>> items = new ArrayList<>(this.projectiles.size());
        for (MenuItemStack menuItemStack : this.projectiles) {
            items.add(menuItemStack.serializeToMap());
        }
        return items;
    }
}
