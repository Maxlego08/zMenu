package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class SqlAnimationStore implements AnimationStore {

    private final SqlDatabase database;

    SqlAnimationStore(SqlDatabase database) {
        this.database = database;
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Long>> load(@NotNull UUID playerId) {
        return this.database.read(() -> {
            Map<String, Long> played = new HashMap<>();
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.PLAYER_ANIMATIONS, table -> table.where("player_id", playerId))) {
                String animationKey = SqlRows.text(row, "animation_key");
                if (animationKey != null) played.put(animationKey, SqlRows.number(row, "played_at"));
            }
            return played;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> markPlayed(@NotNull UUID playerId, @NotNull String animationKey, long playedAt) {
        return this.database.write(() -> this.database.requestHelper().upsert(Tables.PLAYER_ANIMATIONS, table -> {
            table.uuid("player_id", playerId).primary();
            table.string("animation_key", animationKey).primary();
            table.bigInt("played_at", playedAt);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_ANIMATIONS, table -> {
            table.where("player_id", playerId);
            if (animationKey != null) table.where("animation_key", animationKey);
        }));
    }
}
