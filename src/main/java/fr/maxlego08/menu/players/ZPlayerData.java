package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.api.variable.VariableService;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A view of a player's variables, the {@link VariableScope#PLAYER} scope of the
 * {@link VariableService}. It holds no value itself.
 */
public class ZPlayerData implements PlayerData {

    private final VariableService variables;
    private final UUID uniqueId;

    public ZPlayerData(@NotNull VariableService variables, @NotNull UUID uniqueId) {
        super();
        this.variables = variables;
        this.uniqueId = uniqueId;
    }

    static @NotNull Data toData(@NotNull Variable variable) {
        return new ZData(variable.ref().name(), variable.text(), variable.expiresAt());
    }

    private VariableRef ref(String key) {
        return VariableRef.player(this.uniqueId, key);
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NonNull Collection<Data> getDatas() {
        List<Data> datas = new ArrayList<>();
        for (Variable variable : this.variables.getCachedAll(VariableScope.PLAYER, this.uniqueId.toString())) {
            datas.add(toData(variable));
        }
        return Collections.unmodifiableCollection(datas);
    }

    @Override
    public void addData(@NonNull Data data) {
        Object value = data.getValue();
        this.variables.set(this.ref(data.getKey()), value == null ? "" : value.toString(), data.getExpiredAt());
    }

    @Override
    public void removeData(@NonNull Data data) {
        this.removeData(data.getKey());
    }

    @Override
    public void removeData(@NonNull String key) {
        this.variables.delete(this.ref(key));
    }

    @Override
    public boolean containsKey(@NonNull String key) {
        return this.variables.getCached(this.ref(key)).isPresent();
    }

    /**
     * The value of a key. The returned {@link Data} is a copy: changing it changes nothing until it
     * is given to {@code StorageManager#upsertData} or {@link #addData(Data)}.
     */
    @Override
    public @NonNull Optional<Data> getData(@NonNull String key) {
        return this.variables.getCached(this.ref(key)).map(ZPlayerData::toData);
    }
}
