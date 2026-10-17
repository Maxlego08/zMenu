package fr.maxlego08.menu;

import fr.maxlego08.menu.api.TitleAnimationManager;
import fr.maxlego08.menu.api.animation.TitleAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ZTitleAnimationManager implements TitleAnimationManager {
    private final Map<String, TitleAnimationLoader> loaders = new LinkedHashMap<>();

    public ZTitleAnimationManager() {
    }

    @Override
    public boolean registerLoader(@NotNull String id, @NotNull TitleAnimationLoader loader) {
        if (this.loaders.containsKey(id)) {
            return false;
        }
        this.loaders.put(id, loader);
        return true;
    }

    @Override
    public Optional<TitleAnimationLoader> getLoader(@NotNull String id) {
        TitleAnimationLoader loader = this.loaders.get(id);
        if (loader != null) return Optional.of(loader);
        return this.loaders.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(id)).map(Map.Entry::getValue).findFirst();
    }

    @Override
    public Optional<TitleAnimationLoader> getFirstLoader() {
        return this.loaders.values().stream().findFirst();
    }
}
