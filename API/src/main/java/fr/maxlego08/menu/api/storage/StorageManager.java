package fr.maxlego08.menu.api.storage;

import org.bukkit.event.Listener;

public interface StorageManager extends Listener {

    void loadDatabase();

    boolean isEnable();

}
