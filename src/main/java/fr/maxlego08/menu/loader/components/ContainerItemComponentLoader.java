package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ResolvableContainerSlot;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ContainerComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyContainerComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class ContainerItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public ContainerItemComponentLoader(MenuPlugin plugin) {
        super("container", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);

        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<ResolvableContainerSlot> contents = new ArrayList<>();
        int index = 0;
        for (var rawMap : mapList){
            @SuppressWarnings("unchecked")
            Map<String, Object> itemMap = (Map<String, Object>) rawMap;
            MenuItemStack menuItemStack = this.loadItemStack(itemMap, file);
            if (menuItemStack != null) {
                ResolvableInt slotResolvable;
                if (itemMap.containsKey("slot")) {
                    slotResolvable = ResolvableInt.of(itemMap, "slot", 0);
                } else {
                    slotResolvable = ResolvableInt.of(index++);
                }
                contents.add(new ResolvableContainerSlot(menuItemStack, slotResolvable));
            }

        }
        if (contents.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new ContainerComponent(contents)
                : new LegacyContainerComponent(contents);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemContainerContents containerContents = itemStack.getData(DataComponentTypes.CONTAINER);
        if (containerContents == null) return null;

        List<ResolvableContainerSlot> contents = new ArrayList<>();
        List<ItemStack> items = containerContents.contents();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (item.isEmpty()) continue;

            MenuItemStack menuItemStack = this.convertItemStack(item);
            if (menuItemStack == null) return null;
            contents.add(new ResolvableContainerSlot(menuItemStack, ResolvableInt.of(slot)));
        }
        return contents.isEmpty() ? null : new ContainerComponent(contents);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return null;
        if (!(blockStateMeta.getBlockState() instanceof Container container)) return null;

        List<ResolvableContainerSlot> contents = new ArrayList<>();
        ItemStack[] items = container.getInventory().getContents();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item == null || item.isEmpty()) continue;

            MenuItemStack menuItemStack = this.convertItemStackFromItemMeta(item);
            if (menuItemStack == null) return null;
            contents.add(new ResolvableContainerSlot(menuItemStack, ResolvableInt.of(slot)));
        }
        return contents.isEmpty() ? null : new LegacyContainerComponent(contents);
    }
}
