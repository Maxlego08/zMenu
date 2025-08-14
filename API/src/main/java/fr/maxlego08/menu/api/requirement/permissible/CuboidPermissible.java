package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.cuboid.Region;

import java.util.List;

public abstract class CuboidPermissible extends Permissible {

    public CuboidPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    /**
     * @return the region of this cuboid permissible
     */
    public abstract Region getRegion();

}
