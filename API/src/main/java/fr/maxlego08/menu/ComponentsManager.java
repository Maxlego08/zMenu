package fr.maxlego08.menu;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ComponentsManager {

    void registerComponent(@NotNull ItemComponentLoader loader);

    @NotNull
    Optional<ItemComponentLoader> getLoader(@NotNull String key) throws IllegalArgumentException;
}
