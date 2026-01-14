package fr.maxlego08.menu;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.loader.components.BlockStateItemComponentLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    ZComponentsManager(){
        this.registerComponent(new BlockStateItemComponentLoader());
    }

    @Override
    public void registerComponent(@NotNull ItemComponentLoader loader) {
        List<String> componentNames = loader.getComponentNames();
        for (String name : componentNames) {
            if (this.components.containsKey(name)) {
                throw new IllegalArgumentException("Component with name '" + name + "' is already registered.");
            }
            this.components.put(name, loader);
        }
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        return Optional.ofNullable(this.components.get(name));
    }
}
