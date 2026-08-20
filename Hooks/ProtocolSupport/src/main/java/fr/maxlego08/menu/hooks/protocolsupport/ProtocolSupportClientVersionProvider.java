package fr.maxlego08.menu.hooks.protocolsupport;

import fr.maxlego08.menu.api.utils.version.ClientVersionProvider;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

public class ProtocolSupportClientVersionProvider implements ClientVersionProvider {

    private Method getProtocolVersion;
    private Method getVersionName;
    private boolean resolved;

    @Override
    @NotNull
    public String getName() {
        return "ProtocolSupport";
    }

    @Override
    public boolean isAvailable() {
        this.resolve();
        return this.getProtocolVersion != null && this.getVersionName != null;
    }

    private void resolve() {
        if (this.resolved) return;
        this.resolved = true;
        try {
            Class<?> apiClass = Class.forName("protocolsupport.api.ProtocolSupportAPI");
            this.getProtocolVersion = apiClass.getMethod("getProtocolVersion", Player.class);
            this.getVersionName = this.getProtocolVersion.getReturnType().getMethod("getName");
        } catch (Throwable throwable) {
            this.getProtocolVersion = null;
            this.getVersionName = null;
        }
    }

    @Override
    @NotNull
    public Optional<MinecraftVersion> getClientVersion(@NotNull Player player) {
        if (!this.isAvailable()) return Optional.empty();
        try {
            Object protocolVersion = this.getProtocolVersion.invoke(null, player);
            if (protocolVersion == null) return Optional.empty();

            Object name = this.getVersionName.invoke(protocolVersion);
            if (!(name instanceof String versionName) || versionName.isBlank()) return Optional.empty();

            return Optional.of(MinecraftVersion.parse(versionName));
        } catch (Throwable throwable) {
            return Optional.empty();
        }
    }
}
