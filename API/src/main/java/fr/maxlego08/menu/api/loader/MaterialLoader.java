package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.ZMenuKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * Documentation: <a href="https://docs.zmenu.dev/api/create-material-loader">here</a>
 * <p>The MaterialLoader interface defines methods for loading an {@link ItemStack} from a configuration.</p>
 */
public abstract class MaterialLoader implements Keyed {

    private final String key;

    public MaterialLoader(@NotNull String key) {
        this.key = key;
    }

    /**
     * Returns the key used to load the ItemStack.
     *
     * @return The key.
     */
    @Contract(pure = true)
    @NotNull
    public String getKey() {
        return this.key;
    }

    /**
     * The key of this loader in {@link fr.maxlego08.menu.api.registry.Registries#MATERIAL_LOADER},
     * read from {@link #getKey()} like the prefix of a material written in a file
     * ({@code itemsadder} in {@code itemsadder:ruby}).
     *
     * @return the key
     */
    @Override
    public @NotNull Key key() {
        return ZMenuKeys.parse(this.key);
    }

    /**
     * Loads an ItemStack based on the provided configuration.
     *
     * @param player         The player.
     * @param configuration  The current configuration.
     * @param path           The current path.
     * @param materialString The material as a String.
     * @return The loaded ItemStack.
     * @deprecated use {@link #load(Player, YamlDocument, String, String)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Nullable
    public abstract ItemStack load(@NotNull Player player, @Nullable YamlConfiguration configuration,@NotNull String path,@NotNull String materialString);

    /**
     * Loads an ItemStack based on the provided document. zMenu calls this method, override it.
     * By default it reads a Bukkit copy with the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     *
     * @param player         The player.
     * @param configuration  The current document.
     * @param path           The current path.
     * @param materialString The material as a String.
     * @return The loaded ItemStack.
     */
    @Nullable
    public ItemStack load(@NotNull Player player, @Nullable YamlDocument configuration, @NotNull String path, @NotNull String materialString) {
        return this.load(player, configuration == null ? null : YamlSections.toBukkit(configuration), path, materialString);
    }

}
