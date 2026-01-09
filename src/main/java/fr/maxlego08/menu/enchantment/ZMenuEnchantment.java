package fr.maxlego08.menu.enchantment;

import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ZMenuEnchantment(@NotNull Enchantment enchantment,@NotNull List<String> aliases) implements MenuEnchantment {

}
