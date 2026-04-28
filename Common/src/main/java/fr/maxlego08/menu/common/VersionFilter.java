package fr.maxlego08.menu.common;

import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.api.annotations.UntilVersion;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.PlatformType;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;

public class VersionFilter {

    private VersionFilter() {}

    public static boolean passes(@NotNull Class<?> clazz) {
        if (clazz.isAnnotationPresent(PaperOnly.class) && !PlatformType.isPaper()) {
            if (Configuration.enableDebug) {
                Logger.info("Class " + clazz.getSimpleName() + " is annotated with @PaperOnly but the server is not running Paper or Folia. Skipping.", Logger.LogType.INFO);
            }
            return false;
        }
        if (clazz.isAnnotationPresent(SpigotOnly.class) && PlatformType.isPaper()) {
            if (Configuration.enableDebug) {
                Logger.info("Class " + clazz.getSimpleName() + " is annotated with @SpigotOnly but the server is running Paper or Folia. Skipping.", Logger.LogType.INFO);
            }
            return false;
        }

        MinecraftVersion serverVersion = MinecraftVersion.getCurrentVersion();

        SinceVersion since = clazz.getAnnotation(SinceVersion.class);
        if (since != null) {
            MinecraftVersion minimum = MinecraftVersion.parse(since.value());
            if (!serverVersion.isAtLeast(minimum)) {
                if (Configuration.enableDebug) {
                    Logger.info("Class " + clazz.getSimpleName() + " is annotated with @SinceVersion(" + since.value() + ") but the server value is " + serverVersion + ". Skipping.", Logger.LogType.INFO);
                }
                return false;
            }
        }

        UntilVersion until = clazz.getAnnotation(UntilVersion.class);
        if (until != null) {
            MinecraftVersion maximum = MinecraftVersion.parse(until.value());
            if (!serverVersion.isAtMost(maximum)) {
                if (Configuration.enableDebug) {
                    Logger.info("Class " + clazz.getSimpleName() + " is annotated with @UntilVersion(" + until.value() + ") but the server value is " + serverVersion + ". Skipping.", Logger.LogType.INFO);
                }
                return false;
            }
        }

        return true;

    }

}
