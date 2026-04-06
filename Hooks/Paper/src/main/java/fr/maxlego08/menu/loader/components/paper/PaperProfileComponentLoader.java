package fr.maxlego08.menu.loader.components.paper;

import com.destroystokyo.paper.profile.ProfileProperty;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperProfileComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PaperProfileComponentLoader extends ItemComponentLoader {

    public PaperProfileComponentLoader() {
        super("profile");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            String name = configuration.getString(this.normalizePath(path));
            return name != null ? new PaperProfileComponent(name) : null;
        }

        String uuid = componentSection.getString("id");
        String name = componentSection.getString("name");
        Collection<ProfileProperty> properties = this.loadProperties(componentSection.getMapList("properties"));
        ResolvableProfile.SkinPatch skinPatch = this.loadSkinPatch(componentSection);

        return new PaperProfileComponent(name, uuid, properties, skinPatch);
    }

    private @Nullable Collection<ProfileProperty> loadProperties(@NotNull List<Map<?, ?>> rawProperties) {
        if (rawProperties.isEmpty()) return null;

        List<ProfileProperty> properties = new ArrayList<>(rawProperties.size());
        for (Map<?, ?> raw : rawProperties) {
            String name = (String) raw.get("name");
            String value = (String) raw.get("value");
            String signature = (String) raw.get("signature");
            if (name != null && value != null) {
                properties.add(new ProfileProperty(name, value, signature));
            }
        }
        return properties;
    }

    private @Nullable ResolvableProfile.SkinPatch loadSkinPatch(@NotNull ConfigurationSection section) {
        String texture = section.getString("texture");
        String cape = section.getString("cape");
        String elytra = section.getString("elytra");
        String model = section.getString("model");

        if (texture == null && cape == null && elytra == null && model == null) return null;

        ResolvableProfile.SkinPatchBuilder builder = ResolvableProfile.SkinPatch.skinPatch();

        if (texture != null)
            this.applyKey(texture, "texture", builder::body);
        if (cape != null)
            this.applyKey(cape,"cape", builder::cape);
        if (elytra != null)
            this.applyKey(elytra,"elytra", builder::elytra);
        if (model != null) builder.model(this.parseSkinModel(model));

        return builder.build();
    }

    private void applyKey(@NotNull String raw, @NotNull String fieldName, @NotNull java.util.function.Consumer<Key> applier) {
        try {
            applier.accept(Key.key(raw));
        } catch (Exception ignored) {
            if (Configuration.enableDebug) {
                Logger.info("Invalid " + fieldName + " key for profile component: " + raw);
            }
        }
    }

    private @NotNull PlayerTextures.SkinModel parseSkinModel(@NotNull String model) {
        return model.equalsIgnoreCase("slim")
                ? PlayerTextures.SkinModel.SLIM
                : PlayerTextures.SkinModel.CLASSIC;
    }
}