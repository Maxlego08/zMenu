package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.cumulus.component.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BedrockBuilderInput<T extends Plugin> {
    protected final T plugin;
    protected final MetaUpdater metaUpdater;

    public BedrockBuilderInput(T plugin, @Nullable MetaUpdater metaUpdater) {
        this.plugin = plugin;
        this.metaUpdater = metaUpdater;
    }

    public abstract DialogInputType getType();
    public abstract @NotNull Component build(@NotNull Player player, @NotNull InputButton button, @NotNull Placeholders placeholders);
}