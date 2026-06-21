package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperCustomModelDataComponent;
import fr.maxlego08.menu.loader.components.SpigotCustomModelDataItemComponentLoader;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
@PaperOnly
public class PaperCustomModelDataComponentLoader extends SpigotCustomModelDataItemComponentLoader {

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        //TODO: refaire
        //         if (componentSection == null) return null;
//         List<ResolvableFloat> floats = this.getFloats(componentSection);
//
//         List<ResolvableBoolean> booleans = this.getBooleans(componentSection);
//
//         List<ResolvableString> strings = this.getStrings(componentSection);
//
//         List<ResolvableColor> colorList = this.getColors(componentSection);
//
//         if (colorList.isEmpty() && booleans.isEmpty() && floats.isEmpty() && strings.isEmpty()) {
//             return null;
//         }

        CustomModelData.Builder builder = CustomModelData.customModelData();
//         if (!colorList.isEmpty()) {
//             builder.addColors(colorList);
//         }
//         if (!booleans.isEmpty()) {
//             builder.addFlags(booleans);
//         }
//         if (!floats.isEmpty()) {
//             builder.addFloats(floats);
//         }
//         if (!strings.isEmpty()) {
//             builder.addStrings(strings);
//         }
        return new PaperCustomModelDataComponent(builder.build());
    }
}
