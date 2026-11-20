package fr.maxlego08.menu.hooks.text;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.text.AbstractTextService;
import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.paper.GuavaCacheFactory;
import fr.robie.minimessageflow.paper.PaperFlow;
import fr.robie.minimessageflow.paper.PlaceholderApi;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * zMenu's {@link TextService}, built when the plugin is created, through MiniMessageFlow's Paper integration:
 * <ul>
 *   <li>PlaceholderAPI when it is installed ({@code %vault_eco_balance%}, {@code <papi:...>}, zMenu's
 *   {@code %zmenu_...%} expansion), its values kept per player for {@code cache-placeholder-api} when
 *   {@code enable-cache-placeholder-api} is on, read at each value so a reload applies at once;</li>
 *   <li>the viewer and server placeholders ({@code %viewer_name%}, {@code <server_online>}...);</li>
 *   <li>a failing placeholder logged once to the plugin's logger;</li>
 *   <li>Guava caches, expiring after {@code component-cache.expire-minutes};</li>
 *   <li>zMenu's options ({@link TextService#options}).</li>
 * </ul>
 * MiniPlaceholders is off. Subclass it to change how the instance is built ({@link #builder}).
 */
public class PaperTextService extends AbstractTextService {

    public PaperTextService(@NotNull Plugin plugin) {
        super(builder(plugin).build());
    }

    protected PaperTextService(@NotNull MiniMessageFlow flow) {
        super(flow);
    }

    /**
     * The builder of zMenu's instance, for a subclass to change before building.
     *
     * @param plugin the plugin the instance logs to
     * @return the builder
     */
    protected static @NotNull PaperFlow.Builder builder(@NotNull Plugin plugin) {
        return PaperFlow.builder(plugin)
                .placeholderApi(PlaceholderApi.builder()
                        .cache(PaperTextService::placeholderApiCache)
                        .cacheFactory(GuavaCacheFactory.create()))
                .miniPlaceholders(false)
                .placeholders(true)
                .logErrors(true)
                .configure(builder -> {
                    TextService.options(builder);
                    builder.cacheFactory(GuavaCacheFactory.builder().expireAfterAccess(PaperTextService::expiry).build());
                });
    }

    /**
     * How long PlaceholderAPI's values are kept: {@code cache-placeholder-api} (milliseconds) when
     * {@code enable-cache-placeholder-api} is true.
     */
    private static @Nullable Duration placeholderApiCache() {
        return Configuration.enableCachePlaceholderAPI && Configuration.cachePlaceholderAPI > 0
                ? Duration.ofMillis(Configuration.cachePlaceholderAPI) : null;
    }

    private static @Nullable Duration expiry() {
        return Configuration.componentCacheExpireMinutes > 0 ? Duration.ofMinutes(Configuration.componentCacheExpireMinutes) : null;
    }
}
