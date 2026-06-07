package fr.maxlego08.menu.hooks;

import com.nexomc.nexo.glyphs.GlyphTag;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;

public class NexoTagResolverLoader {

    public static void register(PaperMetaUpdater paperMetaUpdater) {
        paperMetaUpdater.withTagResolver(GlyphTag.INSTANCE.getRESOLVER());
        paperMetaUpdater.buildMiniMessage();
    }
}
