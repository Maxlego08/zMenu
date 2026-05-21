package fr.maxlego08.menu.itemstack.components.paper;

import com.destroystokyo.paper.profile.ProfileProperty;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class PaperProfileComponent extends ItemComponent {
    private final @NotNull Placeholder placeholder;
    private final @Nullable String name;
    private final @Nullable String uuid;
    private final @Nullable Collection<@NotNull ProfileProperty> properties;
    private final @Nullable ResolvableProfile.SkinPatch skinPatch;

    public PaperProfileComponent(@Nullable String name, @Nullable String uuid, @Nullable Collection<@NotNull ProfileProperty> properties, @Nullable ResolvableProfile.SkinPatch skinPatch) {
        this.placeholder = Placeholder.Placeholders.getPlaceholder();
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
        this.skinPatch = skinPatch;
    }

    public PaperProfileComponent(@NotNull String name) {
        this(name, null, null, null);
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

        if (this.name != null) {
            builder.name(this.placeholder.setPlaceholders(context.getPlayer(), context.getPlaceholders().parse(this.name)));
        }

        if (this.uuid != null) {
            String uuidString = this.uuid;
            try {
                UUID uuid = UUID.fromString(this.placeholder.setPlaceholders(context.getPlayer(), context.getPlaceholders().parse(uuidString)));
                builder.uuid(uuid);
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (this.properties != null) {
            builder.addProperties(this.properties);
        }

        if (this.skinPatch != null) {
            builder.skinPatch(this.skinPatch);
        }

        itemStack.setData(DataComponentTypes.PROFILE, builder.build());
    }
}
