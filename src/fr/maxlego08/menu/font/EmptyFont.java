package fr.maxlego08.menu.font;

import fr.maxlego08.menu.api.font.FontImage;

public class EmptyFont implements FontImage {

    @Override
    public String replace(String string) {
        return string;
    }
}
