package fr.maxlego08.menu.api.text;

import fr.robie.minimessageflow.MiniMessageFlow;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Where {@link TextService#get()} finds the service: the one zMenu installed, or a plain one made on first use.
 */
final class TextServices {

    static final Style ITALIC = Style.style(TextDecoration.ITALIC.withState(true));
    static final Style NOT_ITALIC = Style.style(TextDecoration.ITALIC.withState(false));

    private static volatile TextService installed;

    private TextServices() {
    }

    static @NotNull TextService get() {
        TextService service = installed;
        if (service != null) {
            return service;
        }
        synchronized (TextServices.class) {
            if (installed == null) {
                installed = new PlainTextService();
            }
            return installed;
        }
    }

    static void install(@NotNull TextService service) {
        installed = Objects.requireNonNull(service, "service cannot be null");
    }

    /**
     * The service before zMenu installs its own: MiniMessageFlow without the Paper integration, with zMenu's options.
     */
    private static final class PlainTextService extends AbstractTextService {
        PlainTextService() {
            super(build());
        }

        private static @NotNull MiniMessageFlow build() {
            MiniMessageFlow.Builder builder = MiniMessageFlow.builder();
            TextService.options(builder);
            return builder.build();
        }
    }
}
