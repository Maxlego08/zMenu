package fr.maxlego08.menu.zcore;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.utils.ItemStackPlatformHelper;
import fr.maxlego08.menu.hooks.ComponentMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ComponentItemStackPlatformHelper extends ItemStackPlatformHelper {
    private final ComponentMeta componentMeta;

    public ComponentItemStackPlatformHelper(ComponentMeta componentMeta) {
        super();
        this.componentMeta = componentMeta;
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
        return this.componentMeta.getMiniMessage(itemStack.getItemMeta().displayName());
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
        List<Component> lore = itemStack.getItemMeta().lore();
        if (lore == null || lore.isEmpty()) {
            return Collections.emptyList();
        }
        return lore.stream()
                .map(this.componentMeta::getMiniMessage)
                .toList();
    }

}
