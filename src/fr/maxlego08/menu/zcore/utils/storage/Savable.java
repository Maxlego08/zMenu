package fr.maxlego08.menu.zcore.utils.storage;

public interface Savable {

    /**
     * @param persist
     */
    void save(Persist persist);

    /**
     * @param persist
     */
    void load(Persist persist);
}
