package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableUUID;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ResolvableProfileResolvable implements Resolvable<ResolvableProfile> {

    private final @Nullable ResolvableString name;
    private final @Nullable ResolvableUUID uuid;
    private final @Nullable Collection<@NotNull ProfilePropertyEntry> properties;
    private final @Nullable ResolvableNamespacedKey texture;
    private final @Nullable ResolvableNamespacedKey cape;
    private final @Nullable ResolvableNamespacedKey elytra;
    private final @Nullable ResolvableEnum<PlayerTextures.SkinModel> model;

    public ResolvableProfileResolvable(
            @Nullable ResolvableString name,
            @Nullable ResolvableUUID uuid,
            @Nullable Collection<@NotNull ProfilePropertyEntry> properties,
            @Nullable ResolvableNamespacedKey texture,
            @Nullable ResolvableNamespacedKey cape,
            @Nullable ResolvableNamespacedKey elytra,
            @Nullable ResolvableEnum<PlayerTextures.SkinModel> model
    ) {
        this.name = name;
        this.uuid = uuid;
        this.properties = properties;
        this.texture = texture;
        this.cape = cape;
        this.elytra = elytra;
        this.model = model;
    }

    public ResolvableProfileResolvable(@NotNull ResolvableString name) {
        this(name, null, null, null, null, null, null);
    }

    @Override
    public @Nullable ResolvableProfile resolve(@NotNull BuildContext context) {
        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

        Resolvable.applyResolvable(context, this.name, builder::name);

        Resolvable.applyResolvable(context, this.uuid, builder::uuid);

        if (this.properties != null) {
            List<ProfileProperty> resolved = new ArrayList<>();
            for (ProfilePropertyEntry entry : this.properties) {
                ProfileProperty prop = entry.resolve(context);
                if (prop != null) {
                    resolved.add(prop);
                }
            }
            if (!resolved.isEmpty()) {
                builder.addProperties(resolved);
            }
        }

        ResolvableProfile.SkinPatch skinPatch = this.resolveSkinPatch(context);
        if (skinPatch != null) {
            builder.skinPatch(skinPatch);
        }

        return builder.build();
    }

    private @Nullable ResolvableProfile.SkinPatch resolveSkinPatch(@NotNull BuildContext context) {

        if (this.texture == null && this.cape == null && this.elytra == null && this.model == null) {
            return null;
        }

        ResolvableProfile.SkinPatchBuilder builder = ResolvableProfile.SkinPatch.skinPatch();

        Resolvable.applyResolvable(context, this.texture, builder::body);
        Resolvable.applyResolvable(context, this.cape, builder::cape);
        Resolvable.applyResolvable(context, this.elytra, builder::elytra);
        Resolvable.applyResolvable(context, this.model, builder::model);

        return builder.build();
    }

    public static final class ProfilePropertyEntry {
        private final @NotNull ResolvableString name;
        private final @NotNull ResolvableString value;
        private final @Nullable ResolvableString signature;

        public ProfilePropertyEntry(@NotNull ResolvableString name, @NotNull ResolvableString value, @Nullable ResolvableString signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }

        public @Nullable ProfileProperty resolve(@NotNull BuildContext context) {
            String resolvedName = this.name.resolve(context);
            String resolvedValue = this.value.resolve(context);
            if (resolvedName == null || resolvedValue == null) return null;
            return new ProfileProperty(resolvedName, resolvedValue, Resolvable.resolve(context, this.signature));
        }
    }
}
