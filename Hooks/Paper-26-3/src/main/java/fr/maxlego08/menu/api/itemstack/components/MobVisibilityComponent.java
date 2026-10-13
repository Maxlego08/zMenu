package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MobVisibility;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MobVisibilityComponent extends ItemComponent {
    private final Resolvable<RegistryKeySet<EntityType>> targetingEntityTypes;
    private final ResolvableFloat visibility;

    public MobVisibilityComponent(Resolvable<RegistryKeySet<EntityType>> targetingEntityTypes, ResolvableFloat visibility) {
        this.targetingEntityTypes = targetingEntityTypes;
        this.visibility = visibility;
    }

    public Resolvable<RegistryKeySet<EntityType>> getTargetingEntityTypes() {
        return this.targetingEntityTypes;
    }

    public ResolvableFloat getVisibility() {
        return this.visibility;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        RegistryKeySet<EntityType> entityTypes = Resolvable.resolve(context, this.targetingEntityTypes);
        Float resolvedVisibility = Resolvable.resolve(context, this.visibility);
        if (entityTypes == null || resolvedVisibility == null) return;
        itemStack.setData(DataComponentTypes.MOB_VISIBILITY, MobVisibility.mobVisibility(entityTypes, resolvedVisibility));
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("targeting-entity-types", this.targetingEntityTypes.serialize());
        map.put("visibility", this.visibility.serialize());
        return map;
    }
}
