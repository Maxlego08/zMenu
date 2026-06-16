package fr.maxlego08.menu.api.storage;

import org.jetbrains.annotations.NotNull;

/**
 * Defines table name constants used for player inventory/data storage in the zMenu plugin.
 */
public interface Tables {

    @NotNull String PLAYER_OPEN_INVENTORIES = "%prefix%player_open_inventories";
    @NotNull String PLAYER_DATAS = "%prefix%player_datas";
    @NotNull String PLAYER_INVENTORIES = "%prefix%player_inventories";

}
