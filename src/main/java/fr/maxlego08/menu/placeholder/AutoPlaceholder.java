package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import org.bukkit.OfflinePlayer;

public record AutoPlaceholder(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer) {

    /**
     * @param startWith
     * @param biConsumer
     */
    public AutoPlaceholder {
    }

    /**
     * @return the startWith
     */
    @Override
    public String startWith() {
        return startWith;
    }

    /**
     * @return the biConsumer
     */
    @Override
    public ReturnBiConsumer<OfflinePlayer, String, String> biConsumer() {
        return biConsumer;
    }

    public String accept(OfflinePlayer offlinePlayer, String value) {
        return this.biConsumer.accept(offlinePlayer, value);
    }

}
