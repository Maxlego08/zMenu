package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import org.bukkit.plugin.Plugin;

public interface MenuPlugin extends Plugin {

    ZScheduler getScheduler();

    InventoryManager getInventoryManager();

    ButtonManager getButtonManager();

    PatternManager getPatternManager();

    <T> T getProvider(Class<T> headManagerClass);
}
