package fr.maxlego08.menu.website;

import fr.maxlego08.menu.zcore.utils.storage.Persist;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public class Token implements Saveable {

	public static String token;

	@Override
	public void save(Persist persist) {
		persist.save(this, "token");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, Token.class, "token");
	}

}
