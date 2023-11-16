package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ZRegexPermissible extends ZUtils implements Permissible {

    private final String regex;
    private final String placeholder;

    public ZRegexPermissible(String regex, String placeholder) {
        this.regex = regex;
        this.placeholder = placeholder;
    }

    @Override
    public boolean hasPermission(Player player) {

        String value = papi(this.placeholder, player);
        String regex = papi(this.regex, player);

        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(value).matches();
    }

    @Override
    public boolean isValid() {
        return this.regex != null && this.placeholder != null;
    }
}
