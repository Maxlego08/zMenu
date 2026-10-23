package fr.maxlego08.menu.common.utils;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClickTypeParser {

    private ClickTypeParser() {
    }

    /**
     * Transforms a list of names into click types. {@code ALL} or {@code ANY} adds the click types of
     * the configuration, as does an empty list.
     *
     * @param names the click types as written in a file
     * @return the click types
     */
    public static List<ClickType> parse(List<String> names) {
        List<ClickType> clickTypes = new ArrayList<>();
        names.forEach(clickType -> {
            if (clickType == null) return;
            if (clickType.equalsIgnoreCase("all") || clickType.equalsIgnoreCase("any")) {
                clickTypes.addAll(Configuration.allClicksType);
            } else {
                try {
                    clickTypes.add(ClickType.valueOf(clickType.toUpperCase(Locale.ROOT)));
                } catch (Exception ignored) {
                    Logger.info(clickType + " click type was not found.", LogType.ERROR);
                }
            }
        });

        if (clickTypes.isEmpty()) {
            clickTypes.addAll(Configuration.allClicksType);
        }

        return clickTypes;
    }
}
