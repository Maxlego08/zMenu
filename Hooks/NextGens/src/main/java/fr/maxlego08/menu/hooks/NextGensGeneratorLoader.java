package fr.maxlego08.menu.hooks;

import com.muhammaddaffa.nextgens.NextGens;
import com.muhammaddaffa.nextgens.generators.Generator;
import com.muhammaddaffa.nextgens.generators.managers.GeneratorManager;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NextGensGeneratorLoader extends MaterialLoader {
    private NextGens nextGens = null;
    private GeneratorManager generatorManager =null;

    public NextGensGeneratorLoader() {
        super("nextgens_generators");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        this.loadNextGens(); // Try to fix Circular dependency
        Generator generator = generatorManager.getGenerator(materialString);
        return generator != null ? generator.item() : null;
    }

    private void loadNextGens() {
        if (this.nextGens != null) return;
        this.nextGens = NextGens.getInstance();
        this.generatorManager = nextGens.getGeneratorManager();
    }
}
