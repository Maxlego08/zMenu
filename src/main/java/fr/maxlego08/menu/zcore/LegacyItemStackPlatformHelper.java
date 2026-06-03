package fr.maxlego08.menu.zcore;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.utils.ItemStackPlatformHelper;
import fr.maxlego08.menu.zcore.utils.meta.ClassicMeta;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LegacyItemStackPlatformHelper extends ItemStackPlatformHelper {
    private final ClassicMeta classicMeta;

    public LegacyItemStackPlatformHelper(ClassicMeta classicMeta) {
        super();
        this.classicMeta = classicMeta;
    }


    @Override
    public boolean hasDisplayName(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack cannot be null");
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName();
    }

    @Override
    public @Nullable String getDisplayName(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack cannot be null");
        if (!itemStack.hasItemMeta()) {
            return null;
        }
        return itemStack.getItemMeta().getDisplayName().replace("§", "&");
    }

    @Override
    public boolean hasLore(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack cannot be null");
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    @Override
    public @NotNull List<@NotNull String> getLore(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack cannot be null");
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return Collections.emptyList();
        }

        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore == null || lore.isEmpty()) {
            return Collections.emptyList();
        }
        return lore.stream().map(ChatColor::stripColor).toList();
    }
}
