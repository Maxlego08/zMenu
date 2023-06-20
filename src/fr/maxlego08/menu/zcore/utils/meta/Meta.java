package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;

public class Meta {

	public static MetaUpdater meta;

	static {

		if (!Config.enableMiniMessageFormat || !NMSUtils.isHexColor()) {
			meta = new ClassicMeta();
		} else {
			try {
				Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
				meta = new ComponentMeta();
			} catch (Exception e) {
				meta = new ClassicMeta();
			}
		}

	}

}
