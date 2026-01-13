package fr.maxlego08.menu.zcore.utils.plugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Plugins {
	
	VAULT("Vault"),
	ESSENTIALS("Essentials"),
	EXECUTABLE_ITEMS("ExecutableItems"),
	EXECUTABLE_BLOCKS("ExecutableBlocks"),
	HEADDATABASE("HeadDatabase"), 
	ZHEAD("zHead"),
	PLACEHOLDER("PlaceholderAPI"),
	CITIZENS("Citizens"),
	TRANSLATIONAPI("TranslationAPI"),
	ZTRANSLATOR("zTranslator"),
	ORAXEN("Oraxen"),
	ITEMSADDER("ItemsAdder"),
	SLIMEFUN("Slimefun"),
	NOVA("Nova"),
	ECO("eco"),
	ZITEMS("zItems"),
	HMCCOSMETICS("HMCCosmetics"),
	JOBS("Jobs"),
	LUCKPERMS("LuckPerms"),
	CRAFTENGINE("CraftEngine"),
	NEXO("Nexo"),
	MAGICCOSMETICS("MagicCosmetics"),
    NEXTGENS("NextGens"),
    MYTHICMOBS("MythicMobs"),
	ZMENUPLUS("zMenuPlus"),
    BREWERYX("BreweryX"),
    PACKETEVENTS("packetevents")
    ;
    private static final Map<Plugins, Boolean> presenceCache = new ConcurrentHashMap<>();
	private final String name;

	Plugins(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

    public boolean isPresent() {
        return presenceCache.computeIfAbsent(this, plugin -> {
            return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
        });
    }
    public boolean isEnabled() {
        Plugin bukkitPlugin = Bukkit.getServer().getPluginManager().getPlugin(name);
        return bukkitPlugin != null && bukkitPlugin.isEnabled();
    }

}
