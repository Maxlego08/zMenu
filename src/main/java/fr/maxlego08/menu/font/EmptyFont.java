package fr.maxlego08.menu.font;

import fr.maxlego08.menu.api.font.FontImage;
import org.jspecify.annotations.NonNull;

public class EmptyFont implements FontImage {

    @Override
    public @NonNull String replace(@NonNull String string) {
        return string;
    }
}
