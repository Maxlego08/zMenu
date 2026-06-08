package fr.maxlego08.menu.zcore.utils.storage;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.enums.Folder;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Locale;

public class Persist extends ZUtils {

    private final ZPlugin plugin;

    public Persist(ZPlugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //

    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase(Locale.ROOT);
    }

    public static String getName(Object o) {
        return getName(o.getClass());
    }

    public static String getName(Type type) {
        return getName(type.getClass());
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object?
    // ------------------------------------------------------------ //

    public File getFile(String name) {
        return new File(this.plugin.getDataFolder(), name + ".json");
    }

    public File getFile(Class<?> clazz) {
        return this.getFile(getName(clazz));
    }

    public File getFile(Object obj) {
        return this.getFile(getName(obj));
    }

    public File getFile(Type type) {
        return this.getFile(getName(type));
    }

    // NICE WRAPPERS

    public <T> T loadOrSaveDefault(T def, Class<T> clazz) {
        return this.loadOrSaveDefault(def, clazz, this.getFile(clazz));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, String name) {
        return this.loadOrSaveDefault(def, clazz, this.getFile(name));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, Folder folder, String name) {
        return this.loadOrSaveDefault(def, clazz, this.getFile(folder.toFolder() + File.separator + name));
    }

    public <T> T loadOrSaveDefault(T def, Class<T> clazz, File file) {
        if (!file.exists()) {
            Logger.info("Creating default: " + file, LogType.SUCCESS);
            this.save(def, file);
            return def;
        }

        T loaded = this.load(clazz, file);

        if (loaded == null) {
            Logger.info("Using default as I failed to load: " + file, LogType.WARNING);

            /*
             * Create new config backup
             */

            File backup = new File(file.getPath() + "_bad");
            if (backup.exists())
                backup.delete();
            Logger.info("Backing up copy of bad file to: " + backup, LogType.WARNING);

            file.renameTo(backup);

            return def;
        } else {

            if (Configuration.enableLogStorageFile) {
                Logger.info(file.getPath() + " loaded successfully !", LogType.SUCCESS);
            }

        }

        return loaded;
    }

    // SAVE

    public boolean save(Object instance) {
        return this.save(instance, this.getFile(instance));
    }

    public boolean save(Object instance, String name) {
        return this.save(instance, this.getFile(name));
    }

    public boolean save(Object instance, Folder folder, String name) {
        return this.save(instance, this.getFile(folder.toFolder() + File.separator + name));
    }

    public boolean save(Object instance, File file) {

        try {

            boolean b = DiscUtils.writeCatch(file, this.plugin.getGson().toJson(instance));
            if (Configuration.enableLogStorageFile) {
                Logger.info(file.getAbsolutePath() + " successfully saved !", LogType.SUCCESS);
            }
            return b;

        } catch (Exception e) {
            Logger.info("cannot save file " + file.getAbsolutePath(), LogType.ERROR);
            e.printStackTrace();

            return false;
        }
    }

    // LOAD BY CLASS

    public <T> T load(Class<T> clazz) {
        return this.load(clazz, this.getFile(clazz));
    }

    public <T> T load(Class<T> clazz, String name) {
        return this.load(clazz, this.getFile(name));
    }

    public <T> T load(Class<T> clazz, File file) {
        String content = DiscUtils.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return this.plugin.getGson().fromJson(content, clazz);
        } catch (Exception ex) { // output the error message rather than full
            // stack trace; error parsing the file, most
            // likely
            Logger.info(ex.getMessage(), LogType.ERROR);
        }

        return null;
    }

    // LOAD BY TYPE
    public <T> T load(Type typeOfT, String name) {
        return this.load(typeOfT, this.getFile(name));
    }

    public <T> T load(Type typeOfT, File file) {
        String content = DiscUtils.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return this.plugin.getGson().fromJson(content, typeOfT);
        } catch (Exception ex) { // output the error message rather than full
            // stack trace; error parsing the file, most
            // likely
            Logger.info(ex.getMessage(), LogType.ERROR);
        }

        return null;
    }

}
