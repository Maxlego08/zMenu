package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.ResolvableColor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public abstract class AbstractColorItemComponentLoader extends ItemComponentLoader {

    public AbstractColorItemComponentLoader(@NotNull String componentName) {
        super(componentName);
    }


    @Nullable
    protected ResolvableColor parseColor(@NotNull Object obj) {
        return ResolvableColor.of(obj);
    }

}
