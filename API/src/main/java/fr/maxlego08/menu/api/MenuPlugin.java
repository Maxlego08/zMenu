package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.scheduler.ZScheduler;

public interface MenuPlugin {

    ZScheduler getScheduler();

    InventoryManager getInventoryManager();

    ButtonManager getButtonManager();

    PatternManager getPatternManager();

}
