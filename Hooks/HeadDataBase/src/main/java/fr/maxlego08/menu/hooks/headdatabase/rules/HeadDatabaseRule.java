package fr.maxlego08.menu.hooks.headdatabase.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeadDatabaseRule extends AbstractPluginItemRule {
    private final HeadDatabaseAPI headDatabaseAPI;

    protected HeadDatabaseRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
        this.headDatabaseAPI = new HeadDatabaseAPI();
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        return headDatabaseAPI.getItemID(itemStack);
    }
}
