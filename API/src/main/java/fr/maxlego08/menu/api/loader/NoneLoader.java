package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class NoneLoader extends ButtonLoader {

    private final Class<? extends Button> clazz;

    public NoneLoader(@NotNull Plugin plugin,@NotNull Class<? extends Button> clazz, String name) {
        super(plugin, name);
        this.clazz = clazz;
    }

    @Override
    @Nullable
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        try {
            return this.clazz.getConstructor(Plugin.class).newInstance(this.plugin);
        } catch (Exception exception) {
            try {
                return this.clazz.getDeclaredConstructor().newInstance();
            } catch (Exception exception2) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
