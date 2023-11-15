package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Action;

import java.io.File;
import java.util.Map;

public interface ActionLoader {

    String getKey();

    Action load(String path, Map<String, Object> map, File file);
}
