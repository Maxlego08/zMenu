package fr.maxlego08.menu.hooks.denizen.rules;

import com.denizenscript.denizen.objects.ItemTag;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DenizenRule extends AbstractPluginItemRule {

    public DenizenRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        return new ItemTag(itemStack).getScriptName();
    }
}
