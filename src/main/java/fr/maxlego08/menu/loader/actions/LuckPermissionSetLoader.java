package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.luckperms.LuckpermAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LuckPermissionSetLoader extends ActionLoader {

    public LuckPermissionSetLoader() {
        super("permission-set", "permission set", "set permission", "set-permission");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String permission = accessor.getString("permission");
        boolean value = accessor.getBoolean("value", true);
        long expiration = accessor.getLong("expiration", -1L);
        TimeUnit timeUnit = TimeUnit.SECONDS;
        String timeUnitStr = accessor.getString("time-unit");
        if (timeUnitStr != null) {
            try {
                timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new LuckpermAction(permission, value, expiration, timeUnit);
    }
}
