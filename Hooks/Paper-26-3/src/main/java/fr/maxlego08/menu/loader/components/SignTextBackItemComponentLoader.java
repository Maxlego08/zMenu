package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.SignTextComponent;

@AutoComponentLoader
@SinceVersion("26.3")
public final class SignTextBackItemComponentLoader extends SignTextItemComponentLoader {

    public SignTextBackItemComponentLoader(MenuPlugin plugin) {
        super("sign-text-back", SignTextComponent.Side.BACK, plugin);
    }
}
