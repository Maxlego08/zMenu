package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.ResolvableContainerSlot;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ContainerComponent extends ItemComponent {
    private final List<@NotNull ResolvableContainerSlot> contents;

    public ContainerComponent(List<@NotNull ResolvableContainerSlot> contents) {
        this.contents = contents;
    }

    public List<@NotNull ResolvableContainerSlot> getContents() {
        return this.contents;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        List<ItemStack> items = new ArrayList<>();
        ItemContainerContents current = itemStack.getData(DataComponentTypes.CONTAINER);
        if (current != null) items.addAll(current.contents());

        for (ResolvableContainerSlot slot : this.contents) {
            Integer resolvedSlot = slot.getSlot().resolve(context);
            if (resolvedSlot == null || resolvedSlot < 0) continue;

            while (items.size() <= resolvedSlot) {
                items.add(ItemStack.empty());
            }
            items.set(resolvedSlot, slot.getItemStack().build(context));
        }

        itemStack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(items));
    }

    @Override
    public @Nullable Object serialize() {
        List<Map<String, Object>> items = new ArrayList<>(this.contents.size());
        for (ResolvableContainerSlot slot : this.contents) {
            Map<String, Object> item = new LinkedHashMap<>(slot.getItemStack().serializeToMap());
            item.put("slot", slot.getSlot().serialize());
            items.add(item);
        }
        return items;
    }
}
