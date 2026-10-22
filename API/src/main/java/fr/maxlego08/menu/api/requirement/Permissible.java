package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a condition that can be checked to determine if a player has permission.
 */
public abstract class Permissible {
    private final List<Action> denyActions;
    private final List<Action> successActions;

    public Permissible(@NotNull List<Action> denyActions,@NotNull List<Action> successActions) {
        this.denyActions = denyActions;
        this.successActions = successActions;
    }

    /**
     * Checks if the player has permission to interact with the button in the given inventory and placeholders.
     * <p>
     * This method is called when a player attempts to interact with a button in an inventory.
     * <p>
     * If this method returns false, the button will not be interactable by the player and the deny actions will be performed.
     *
     * @param player          The player who is attempting to interact with the button.
     * @param button          The button that the player is attempting to interact with.
     * @param inventoryEngine The inventory that the button is in.
     * @param placeholders    The placeholders that are currently active in the inventory.
     * @return True if the player has permission; otherwise, false.
     */
    public abstract boolean hasPermission(@NotNull Player player, @Nullable Button button,@NotNull InventoryEngine inventoryEngine,@NotNull Placeholders placeholders);

    /**
     * Checks if the permissible is valid.
     * This method is used to ensure the integrity of the permissible condition.
     *
     * @return True if the permissible is valid; otherwise, false.
     */
    public abstract boolean isValid();

    /**
     * Gets the list of actions performed if the player doesn't have permission.
     *
     * @return List of deny actions.
     */
    @Contract(pure = true)
    @NotNull
    public List<Action> getDenyActions() {
        return this.denyActions;
    }

    /**
     * Gets the list of actions performed if the player has permission.
     *
     * @return List of success actions.
     */
    @Contract(pure = true)
    @NotNull
    public List<Action> getSuccessActions() {
        return this.successActions;
    }

    /**
     * Writes this permissible in the zMenu format, as one entry of a requirements list.
     * <p>
     * The {@code deny} and {@code success} actions are written here; subclasses write their own
     * fields in {@link #serializeProperties}. Empty action lists are left out.
     *
     * @return The permissible as a map, ready to be put in a YAML list.
     * @throws UnsupportedOperationException If this permissible type cannot be serialized.
     */
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        this.serializeProperties(map);
        if (!this.denyActions.isEmpty()) map.put("deny", serializeActions(this.denyActions));
        if (!this.successActions.isEmpty()) map.put("success", serializeActions(this.successActions));
        return map;
    }

    /**
     * Writes the fields specific to this permissible, starting with its {@code type} key.
     *
     * @param map The map to write into.
     * @throws UnsupportedOperationException If this permissible type cannot be serialized.
     */
    protected void serializeProperties(@NotNull Map<String, Object> map) {
        throw new UnsupportedOperationException("The permissible " + this.getClass().getName() + " cannot be serialized");
    }

    /**
     * Serializes a list of actions, in order.
     *
     * @param actions The actions to serialize.
     * @return One map per action.
     */
    @NotNull
    public static List<Map<String, Object>> serializeActions(@NotNull List<Action> actions) {
        List<Map<String, Object>> serialized = new ArrayList<>(actions.size());
        for (Action action : actions) {
            serialized.add(action.serialize());
        }
        return serialized;
    }
}
