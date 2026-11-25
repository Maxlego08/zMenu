package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SignTextComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SignText;
import net.kyori.adventure.text.Component;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

public abstract class SignTextItemComponentLoader extends ItemComponentLoader {

    private final SignTextComponent.Side side;
    private final MetaUpdater metaUpdater;

    protected SignTextItemComponentLoader(@NotNull String componentName, @NotNull SignTextComponent.Side side, @NotNull MenuPlugin plugin) {
        super(componentName);
        this.side = side;
        this.metaUpdater = plugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        if (!(this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater)) {
            Logger.info("The " + this.getComponentName() + " component at " + path + " in " + file.getName() + " needs enable-mini-message-format, it is ignored.", Logger.LogType.WARNING);
            return null;
        }

        List<String> lines = YamlSections.getStringList(componentSection, "messages");
        if (lines.size() > SignTextComponent.LINES) {
            Logger.info("The " + this.getComponentName() + " component at " + path + " in " + file.getName() + " has more than " + SignTextComponent.LINES + " messages, the others are ignored.", Logger.LogType.WARNING);
            lines = lines.subList(0, SignTextComponent.LINES);
        }

        List<ResolvableComponent> messages = new ArrayList<>(lines.size());
        for (String line : lines) {
            messages.add(ResolvableComponent.auto(line, paperMetaUpdater));
        }

        ResolvableDyeColor color = ResolvableDyeColor.autoOrNull(componentSection.get("color"));
        ResolvableBoolean hasGlowingText = this.asResolvableBoolean(componentSection, "has-glowing-text");

        return new SignTextComponent(this.side, messages, color, hasGlowingText);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!(this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater)) return null;

        SignText signText = itemStack.getData(this.side == SignTextComponent.Side.FRONT ? DataComponentTypes.SIGN_TEXT_FRONT : DataComponentTypes.SIGN_TEXT_BACK);
        if (signText == null || signText.lines().size() > SignTextComponent.LINES) return null;

        List<ResolvableComponent> messages = new ArrayList<>(signText.lines().size());
        for (Component line : signText.lines()) {
            ResolvableComponent message = toResolvableComponent(line, paperMetaUpdater);
            if (message == null) return null;
            messages.add(message);
        }

        return new SignTextComponent(this.side, messages, ResolvableDyeColor.of(signText.color()), ResolvableBoolean.of(signText.hasGlowingText()));
    }
}
