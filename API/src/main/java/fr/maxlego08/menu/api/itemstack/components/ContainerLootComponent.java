package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableLong;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ContainerLootComponent extends ItemComponent {

    private final @Nullable ResolvableEnum<LootTables> resolvableLootTable;
    private final @NotNull ResolvableLong seed;

    public ContainerLootComponent(@Nullable ResolvableEnum<LootTables> resolvableLootTable, @NotNull ResolvableLong seed) {
        this.resolvableLootTable = resolvableLootTable;
        this.seed = seed;
    }

    public @Nullable ResolvableEnum<LootTables> getResolvableLootTable() {
        return this.resolvableLootTable;
    }

    public @NotNull ResolvableLong getSeed() {
        return this.seed;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        LootTables lootTable = Resolvable.resolve(context, this.resolvableLootTable);
        if (lootTable == null) return;

        SeededContainerLoot.Builder containerLoot = SeededContainerLoot.seededContainerLoot(lootTable.getKey());
        this.applyResolvable(context, containerLoot::seed, this.seed);

        itemStack.setData(DataComponentTypes.CONTAINER_LOOT, containerLoot.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.resolvableLootTable != null) map.put("loot-table", this.resolvableLootTable.serialize());
        if (this.seed.isDynamic() || !Long.valueOf(0L).equals(this.seed.getResolvedValue())) {
            map.put("seed", this.seed.serialize());
        }
        return map;
    }

}
