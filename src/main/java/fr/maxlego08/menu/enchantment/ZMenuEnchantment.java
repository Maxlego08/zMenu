package fr.maxlego08.menu.enchantment;

import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public record ZMenuEnchantment(Enchantment enchantment, List<String> aliases) implements MenuEnchantment {

}
