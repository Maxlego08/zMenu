package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.animation.TitleAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface TitleAnimationManager {

    boolean registerLoader(@NotNull String id, @NotNull TitleAnimationLoader loader);

    Optional<TitleAnimationLoader> getLoader(@NotNull String id);

    Optional<TitleAnimationLoader> getFirstLoader();
}
