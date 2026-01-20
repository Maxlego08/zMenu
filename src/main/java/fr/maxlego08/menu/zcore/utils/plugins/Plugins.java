package fr.maxlego08.menu.zcore.utils.plugins;

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
	ZMENUPLUS("zMenuPlus"),
    GEYSER("Geyser-Spigot"),
    FLOODGATE("floodgate"),
    NEXTGENS("NextGens"),
    MYTHICMOBS("MythicMobs"),
  
  ;

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

}
