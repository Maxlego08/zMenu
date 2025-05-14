package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MathPlaceholders extends ZUtils {

    public void register(MenuPlugin plugin){
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("math_", (player, args) -> String.valueOf(new ExpressionBuilder(plugin.parse(player, args.replace("{", "%").replace("}", "%"))).build().evaluate()));
        placeholder.register("formatted_math_", (player, args) -> format(new ExpressionBuilder(plugin.parse(player, args.replace("{", "%").replace("}", "%"))).build().evaluate()));
    }

}
