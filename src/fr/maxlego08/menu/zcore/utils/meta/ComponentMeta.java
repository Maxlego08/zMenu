package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ComponentMeta extends ZUtils implements MetaUpdater {

    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();

    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
        Component component = MINI_MESSAGE.deserialize(color(text));
        itemMeta.displayName(component);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
        List<Component> components = color(lore).stream().map(MINI_MESSAGE::deserialize).toList();
        itemMeta.lore(components);
    }
}
