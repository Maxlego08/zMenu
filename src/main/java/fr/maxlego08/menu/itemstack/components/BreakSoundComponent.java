package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BreakSoundComponent extends ItemComponent {
    private final Sound breakSound;

    public BreakSoundComponent(@NotNull Sound breakSound) {
        this.breakSound = breakSound;
    }

    public @NotNull Sound getBreakSound() {
        return breakSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.setBreakSound(this.breakSound);
        itemStack.setItemMeta(itemMeta);

    }
}
