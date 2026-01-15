package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaperItemUtils {
    private static PaperMetaUpdater metaUpdater;

    public static void initialize(@NotNull PaperMetaUpdater paperMetaUpdater) {
        metaUpdater = paperMetaUpdater;
    }

    public static void setCustomName(@NotNull ItemStack itemStack, @NotNull String name) {
        itemStack.setData(DataComponentTypes.CUSTOM_NAME, metaUpdater.getComponent(name));
    }
}
