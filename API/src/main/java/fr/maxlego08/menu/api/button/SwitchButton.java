package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;

import java.util.Map;

public interface SwitchButton {

    /**
     * Get the map of buttons per key.
     *
     */
    Map<String, Button> getSwitchButton();

    /**
     * Get the key.
     *
     */
    String getKey();

    /**
     * Get the button with the key after placeholder apply.
     *
     * @param player The player for placeholder key.
     */
    Button perform(Player player);
}
