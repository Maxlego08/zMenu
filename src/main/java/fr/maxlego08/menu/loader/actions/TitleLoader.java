package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.TitleAction;

import java.io.File;

public class TitleLoader extends ActionLoader {

    public TitleLoader() {
        super("title");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {

        String title = accessor.getString("title");
        String subtitle = accessor.getString("subtitle");
        long start = accessor.getLong("start");
        long duration = accessor.getLong("duration");
        long end = accessor.getLong("end");

        return new TitleAction(title, subtitle, start, duration, end);
    }
}
