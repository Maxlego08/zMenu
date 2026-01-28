package fr.maxlego08.menu.common.context;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZBuildContext implements BuildContext {

    private final Player player;
    private final boolean useCache;
    private final Placeholders placeholders;

    private ZBuildContext(@NotNull Builder builder) {
        this.player = builder.player;
        this.useCache = builder.useCache;
        this.placeholders = builder.placeholders;
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isUseCache() {
        return this.useCache;
    }

    @Override
    public @NotNull Placeholders getPlaceholders() {
        return this.placeholders;
    }

    public static class Builder {
        private Player player;
        private boolean useCache = true;
        private final Placeholders placeholders = new Placeholders();

        @Contract("_ -> this")
        public Builder player(@Nullable Player player) {
            this.player = player;
            return this;
        }

        @Contract("_ -> this")
        public Builder useCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        @Contract("_ -> this")
        public Builder placeholders(@NotNull Placeholders placeholders) {
            this.placeholders.merge(placeholders);
            return this;
        }

        @Contract("-> new")
        public ZBuildContext build() {
            return new ZBuildContext(this);
        }
    }
}
