package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BedrockAction;

import java.io.File;
import java.util.List;

public class BedrockLoader extends ActionLoader {
    private final ZMenuPlugin plugin;
    private final BedrockManager bedrockManager;

    public BedrockLoader(ZMenuPlugin plugin, BedrockManager bedrockManager) {
        super("bedrock");
        this.plugin = plugin;
        this.bedrockManager = bedrockManager;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String bedrock = accessor.getString("bedrock");
        String plugin = accessor.getString("plugin", null);
        List<String> arguments = accessor.getStringList("arguments");
        return new BedrockAction(this.bedrockManager, this.plugin.getCommandManager(), bedrock, plugin, arguments);
    }
}
