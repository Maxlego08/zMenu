package fr.maxlego08.menu.zcore.enums;

import java.util.Locale;

public enum Folder {

	UTILS,

	;
	

	public String toFolder(){
		return this.name().toLowerCase(Locale.ROOT);
	}
	
}
