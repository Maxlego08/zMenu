package fr.maxlego08.menu.zcore.utils.storage;

public interface Saveable {

    /**
     * @param persist
     */
    void save(Persist persist);

    /**
     * @param persist
     */
    void load(Persist persist);
}
