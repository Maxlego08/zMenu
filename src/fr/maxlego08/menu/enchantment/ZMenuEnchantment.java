package fr.maxlego08.menu.enchantment;

import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class ZMenuEnchantment implements MenuEnchantment {

    private final Enchantment enchantment;
    private final List<String> aliases;

    public ZMenuEnchantment(Enchantment enchantment, List<String> aliases) {
        this.enchantment = enchantment;
        this.aliases = aliases;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
