package fr.maxlego08.menu.website;

import fr.maxlego08.menu.zcore.utils.storage.Persist;

public class Token {

    public static String token;

    /**
     * static Singleton instance.
     */
    private static volatile Token instance;

    /**
     * Private constructor for singleton.
     */
    private Token() {
    }

    /**
     * Return a singleton instance of Token.
     */
    public static Token getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Token.class) {
                if (instance == null) {
                    instance = new Token();
                }
            }
        }
        return instance;
    }

    public void save(Persist persist) {
        persist.save(this, "token");
    }

    public void load(Persist persist) {
        persist.loadOrSaveDefault(this, Token.class, "token");
    }

}
