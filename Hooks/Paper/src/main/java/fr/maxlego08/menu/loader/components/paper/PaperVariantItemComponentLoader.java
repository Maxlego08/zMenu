package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.common.factory.VariantItemComponentLoaderFactory;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.spigot.SpigotVariantItemComponentLoader;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

/**
 * Paper-specific variant component loader that uses Paper's RegistryAccess API
 * for better performance and compatibility.
 */
public class PaperVariantItemComponentLoader extends SpigotVariantItemComponentLoader implements VariantItemComponentLoaderFactory {

    public PaperVariantItemComponentLoader(VariantComponent variantFactory) {
        super(variantFactory);
    }

    @Override
    public @NotNull ItemComponentLoader getLoaderChicken() {
        return new Chicken();
    }

    @Override
    public @NotNull ItemComponentLoader getLoaderCow() {
        return new Cow();
    }
    @Override
    public @NotNull ItemComponentLoader getLoaderPig() {
        return new Pig();
    }


    private static abstract class PaperRegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
        private final RegistryKey<T> registryKey;
        private final Function<T, ItemComponent> componentFactory;

        protected PaperRegistryVariantLoader(String path, RegistryKey<T> registryKey, Function<T, ItemComponent> componentFactory) {
            super(path);
            this.registryKey = registryKey;
            this.componentFactory = componentFactory;
        }

        @Override
        public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
            path = normalizePath(path);
            String value = configuration.getString(path);
            if (value == null) return null;
            NamespacedKey key = NamespacedKey.fromString(value);
            if (key == null) return null;
            try {
                T registryValue = RegistryAccess.registryAccess().getRegistry(this.registryKey).getOrThrow(key);
                return componentFactory.apply(registryValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public class Chicken extends PaperRegistryVariantLoader<org.bukkit.entity.Chicken.Variant> {
        public Chicken() {
            super("chicken/variant", RegistryKey.CHICKEN_VARIANT, variantFactory::createChicken);
        }
    }

    public class Cow extends PaperRegistryVariantLoader<org.bukkit.entity.Cow.Variant> {
        public Cow() {
            super("cow/variant", RegistryKey.COW_VARIANT, variantFactory::createCow);
        }
    }

    public class Pig extends PaperRegistryVariantLoader<org.bukkit.entity.Pig.Variant> {
        public Pig() {
            super("pig/variant", RegistryKey.PIG_VARIANT, variantFactory::createPig);
        }
    }
}