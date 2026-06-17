package fr.maxlego08.menu.hooks.bedrock;

import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.registry.Registry;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.CustomBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.ModalBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.inventory.SimpleBedrockInventoryTypeLoader;

public class BedrockInventoryTypeRegistry extends Registry<BedrockType, BedrockInventoryTypeLoader<?>> {
    private static final BedrockInventoryTypeRegistry instance;

    static {
        instance = new BedrockInventoryTypeRegistry();
        instance.register(BedrockType.MODAL, new ModalBedrockInventoryTypeLoader());
        instance.register(BedrockType.SIMPLE, new SimpleBedrockInventoryTypeLoader());
        instance.register(BedrockType.CUSTOM, new CustomBedrockInventoryTypeLoader());
    }

    public static BedrockInventoryTypeRegistry getInstance() {
        return instance;
    }
}
