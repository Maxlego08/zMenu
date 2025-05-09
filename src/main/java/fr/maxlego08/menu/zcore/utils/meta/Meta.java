package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;

public class Meta {

    public static MetaUpdater meta;

    static {

        if (!Config.enableMiniMessageFormat || !NMSUtils.isComponentColor()) {
            meta = new ClassicMeta();
        } else {
            try {
                Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
                meta = new ComponentMeta();
                Logger.info("Use ComponentMeta");
            } catch (Exception ignored) {
                meta = new ClassicMeta();
                Logger.info("Use ClassicMeta");
            }
        }

    }

}
