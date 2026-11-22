package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;


/**
 * Represents an item and associated player actions that, when interacted with, trigger opening a specific menu.
 */
public class OpenWithItem {
    private final MenuItemStack menuItemStack;
    private final List<Action> actions;
    private final ItemStackSimilar itemStackSimilar;

    public OpenWithItem(@NotNull MenuItemStack menuItemStack,@NotNull List<Action> actions,@NotNull ItemStackSimilar itemStackSimilar) {
        this.menuItemStack = menuItemStack;
        this.actions = actions;
        this.itemStackSimilar = itemStackSimilar;
    }

    @NotNull
    public MenuItemStack getItemStack() {
        return this.menuItemStack;
    }

    @NotNull
    public List<Action> getActions() {
        return this.actions;
    }

    public boolean shouldTrigger(@NotNull PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return false;
        }

        if (!this.actions.contains(event.getAction())) {
            return false;
        }

        ItemStack itemStackA = this.menuItemStack.build(event.getPlayer());
        if (itemStackA == null) {
            return false;
        }
        return this.itemStackSimilar.isSimilar(itemStackA, item);
    }

    /**
     * @deprecated use {@link #serialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public void serialize(@NotNull ConfigurationSection section) {
        this.menuItemStack.serialize(section.createSection("item"));
        List<String> actionNames = new ArrayList<>(this.actions.size());
        for (Action action : this.actions) {
            actionNames.add(action.name());
        }
        section.set("actions", actionNames);
        if (!"full".equals(this.itemStackSimilar.getName())) section.set("type", this.itemStackSimilar.getName());
    }

    /**
     * Writes this object's keys into a section.
     * By default it writes through the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    public void serialize(@NotNull Section section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }
}