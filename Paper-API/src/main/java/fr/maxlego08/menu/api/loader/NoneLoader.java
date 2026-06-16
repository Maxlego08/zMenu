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
            for (var constructor : this.clazz.getConstructors()) {
                var params = constructor.getParameterTypes();
                if (params.length == 1 && params[0].isInstance(this.plugin)) {
                    return (Button) constructor.newInstance(this.plugin);
                }
            }
            return this.clazz.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
