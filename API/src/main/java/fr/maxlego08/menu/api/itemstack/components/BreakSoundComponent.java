package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BreakSoundComponent extends ItemComponent {
    private final @Nullable ResolvableSound breakSoundResolvable;

    public BreakSoundComponent(@Nullable ResolvableSound breakSoundResolvable) {
        this.breakSoundResolvable = breakSoundResolvable;
    }

    public @Nullable ResolvableSound getBreakSound() {
        return this.breakSoundResolvable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            this.applyResolvable(context, itemMeta::setBreakSound, this.breakSoundResolvable);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
