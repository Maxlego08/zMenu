package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZPlayerNamePermissible;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class PlayerNamePermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public PlayerNamePermissibleLoader(ButtonManager buttonManager) {
        super("playername");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);
        return new ZPlayerNamePermissible(accessor.getString("playerName", accessor.getString("playername", accessor.getString("player-name"))), denyActions, successActions);
    }
}
