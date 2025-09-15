package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface BedrockInventory extends Inventory {
    String getContent(Player player);

    List<BedrockButton> getBedrockButtons();

    List<BedrockButton> getBedrockButtons(Player player);

    List<InputButton> getInputButtons();

    List<InputButton> getInputButtons(Player player);

    List<Requirement> getRequirements();

    BedrockType getBedrockType();

    //TODO getOpenWithItem not implemented but need
    @Override
    default OpenWithItem getOpenWithItem() {
        return null;
    }

    /**
     * Set unUsed méthod of Inventory Interface, don't used in BedrockInventory
     *
     */

    @Override
    default int size() {
        return 9;
    }

    @Override
    default InventoryType getType() {
        return null;
    }

    @Override
    default boolean shouldCancelItemPickup() {
        return false;
    }

    @Override
    default Collection<Button> getButtons() {
        return Collections.emptyList();
    }

    @Override
    default Collection<Pattern> getPatterns() {
        return Collections.emptyList();
    }

    @Override
    default <T extends Button> List<T> getButtons(Class<T> type) {
        return Collections.emptyList();
    }

    @Override
    default int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects) {
        return 1;
    }

    @Override
    default List<Button> sortButtons(int page, Object... objects) {
        return Collections.emptyList();
    }

    @Override
    default List<Button> sortPatterns(Pattern pattern, int page, Object... objects) {
        return Collections.emptyList();
    }

    @Override
    default InventoryResult openInventory(Player player, InventoryEngine InventoryEngine) {
        return null;
    }

    @Override
    default void postOpenInventory(Player player, InventoryEngine InventoryEngine) {};

    @Override
    default void closeInventory(Player player, InventoryEngine InventoryEngine) {}

    @Override
    default MenuItemStack getFillItemStack() {
        return null;
    }

    @Override
    default int getUpdateInterval() {
        return 0;
    }

    @Override
    default boolean cleanInventory() {
        return false;
    }

    @Override
    default Map<String, String> getTranslatedNames() {
        return null;
    }
}
