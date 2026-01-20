package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JukeboxPlayableComponent extends ItemComponent {
    private final @NotNull org.bukkit.inventory.meta.components.JukeboxPlayableComponent jukeboxPlayableComponent;

    public JukeboxPlayableComponent(@NotNull org.bukkit.inventory.meta.components.JukeboxPlayableComponent jukeboxPlayableComponent) {
        this.jukeboxPlayableComponent = jukeboxPlayableComponent;
    }

    public @NotNull org.bukkit.inventory.meta.components.JukeboxPlayableComponent getJukeboxPlayableComponent() {
        return this.jukeboxPlayableComponent;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setJukeboxPlayable(this.jukeboxPlayableComponent);
            itemStack.setItemMeta(itemMeta);
        }
    }
}

