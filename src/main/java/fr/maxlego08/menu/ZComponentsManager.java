package fr.maxlego08.menu;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.loader.components.BlockStateItemComponentLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    ZComponentsManager(){
        this.registerComponent(new BlockStateItemComponentLoader());
    }

    @Override
    public void registerComponent(@NotNull ItemComponentLoader loader) {
        String componentName = loader.getComponentName();
        if (this.components.containsKey(componentName)) {
            throw new IllegalArgumentException("Component with name " + componentName + " is already registered.");
        }
        this.components.put(componentName, loader);
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String key) {
        return Optional.ofNullable(this.components.get(key));
    }
}
