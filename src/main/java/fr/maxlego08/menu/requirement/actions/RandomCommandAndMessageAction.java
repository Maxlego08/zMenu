package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks one entry at random and runs it. Each entry is a
 * {@link ConsoleCommandAndMessageAction} or a {@link PlayerCommandAndMessageAction}.
 */
public class RandomCommandAndMessageAction extends ActionHelper {

    private final String serializedType;
    private final Map<String, CommandAndMessageAction> entries;
    private final List<CommandAndMessageAction> choices;

    public RandomCommandAndMessageAction(String serializedType, Map<String, CommandAndMessageAction> entries) {
        this.serializedType = serializedType;
        this.entries = entries;
        this.choices = new ArrayList<>(entries.values());
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (this.choices.isEmpty()) return;
        CommandAndMessageAction choice = this.choices.get(ThreadLocalRandom.current().nextInt(this.choices.size()));
        choice.execute(player, button, inventory, placeholders);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", this.serializedType);
        this.entries.forEach((key, entry) -> {
            Map<String, Object> properties = new LinkedHashMap<>();
            entry.serializeProperties(properties);
            properties.remove("type");
            map.put(key, properties);
        });
    }
}
