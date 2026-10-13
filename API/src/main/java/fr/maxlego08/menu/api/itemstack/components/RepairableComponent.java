package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.TypedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepairableComponent extends ItemComponent {
    private final @NotNull Repairable repairable;

    public RepairableComponent(@NotNull Repairable repairable) {
        this.repairable = repairable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.REPAIRABLE, this.repairable);
    }

    @Override
    public @Nullable Object serialize() {
        List<String> items = new ArrayList<>();
        for (TypedKey<ItemType> key : this.repairable.types().values()) {
            items.add(key.key().asString());
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("items", items.size() == 1 ? items.getFirst() : items);
        return map;
    }
}
