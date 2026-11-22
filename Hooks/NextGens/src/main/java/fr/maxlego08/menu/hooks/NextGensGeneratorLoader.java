package fr.maxlego08.menu.hooks;

import com.muhammaddaffa.nextgens.NextGens;
import com.muhammaddaffa.nextgens.generators.Generator;
import com.muhammaddaffa.nextgens.generators.managers.GeneratorManager;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("NextGens")
public class NextGensGeneratorLoader extends MaterialLoader {
    private NextGens nextGens = null;
    private GeneratorManager generatorManager =null;

    public NextGensGeneratorLoader() {
        super("nextgens_generators");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        this.loadNextGens(); // Try to fix Circular dependency
        Generator generator = this.generatorManager.getGenerator(materialString);
        return generator != null ? generator.item() : null;
    }

    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }

    private void loadNextGens() {
        if (this.nextGens != null) return;
        this.nextGens = NextGens.getInstance();
        this.generatorManager = this.nextGens.getGeneratorManager();
    }
}
