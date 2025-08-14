package fr.maxlego08.menu.hooks.dialogs.utils.loader;

import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;

import java.io.File;
import java.util.List;

public interface DialogActionIntLoader {
    List<String> getActionNames();
    DialogAction load(String path, TypedMapAccessor typedMapAccessor, File file);



}
