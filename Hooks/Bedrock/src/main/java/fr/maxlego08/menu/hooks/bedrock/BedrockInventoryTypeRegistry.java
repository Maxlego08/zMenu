package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.CustomBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.ModalBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.SimpleBedrockInventoryTypeLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class BedrockInventoryTypeRegistry {
    private static final BedrockInventoryTypeRegistry instance;

    static {
        instance = new BedrockInventoryTypeRegistry();
        instance.register(BedrockType.MODAL, new ModalBedrockInventoryTypeLoader());
        instance.register(BedrockType.SIMPLE, new SimpleBedrockInventoryTypeLoader());
        instance.register(BedrockType.CUSTOM, new CustomBedrockInventoryTypeLoader());
    }

    private final Map<BedrockType, BedrockInventoryTypeLoader<?>> loaders = new EnumMap<>(BedrockType.class);

    public static BedrockInventoryTypeRegistry getInstance() {
        return instance;
    }

    public void register(BedrockType type, BedrockInventoryTypeLoader<?> loader) {
        this.loaders.put(type, loader);
    }

    public Optional<BedrockInventoryTypeLoader<?>> get(BedrockType type) {
        return Optional.ofNullable(this.loaders.get(type));
    }
}
