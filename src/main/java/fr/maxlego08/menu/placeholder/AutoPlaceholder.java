package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import org.bukkit.OfflinePlayer;

public class AutoPlaceholder {

    private final String startWith;
    private final ReturnBiConsumer<OfflinePlayer, String, String> biConsumer;

    /**
     * @param startWith
     * @param biConsumer
     */
    public AutoPlaceholder(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer) {
        super();
        this.startWith = startWith;
        this.biConsumer = biConsumer;
    }

    /**
     * @return the startWith
     */
    public String getStartWith() {
        return startWith;
    }

    /**
     * @return the biConsumer
     */
    public ReturnBiConsumer<OfflinePlayer, String, String> getBiConsumer() {
        return biConsumer;
    }

    public String accept(OfflinePlayer offlinePlayer, String value) {
        return this.biConsumer.accept(offlinePlayer, value);
    }

}
