package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoCanvasPackTarget;
import fr.maxlego08.menu.api.annotations.AutoGlyphProvider;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.RegistryManager;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.hooks.dialogs.canvas.check.CanvasChecker;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.MinecraftGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.ProgressGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.ZMenuGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateActions;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.ZCanvasStateService;
import fr.maxlego08.menu.hooks.dialogs.canvas.target.NoneCanvasPackTarget;
import fr.maxlego08.menu.hooks.dialogs.canvas.target.StandaloneCanvasPackTarget;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextFonts;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.VanillaTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.vanilla.VanillaAssets;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.WidgetLoaders;
import fr.maxlego08.menu.hooks.dialogs.click.DialogClicks;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Draws dialog canvases and generates the resource pack they need.
 * <p>
 * The pack is generated from the canvases of the loaded dialogs: when dialogs are loaded, when the
 * vanilla assets finish downloading, and when a pack plugin builds its own pack and imports zMenu's files.
 */
public final class ZCanvasManager implements CanvasManager, Listener {

    private static final MinecraftVersion GEOMETRIC_CLICKS = MinecraftVersion.parse("1.21.11");
    private static final long STALE_PACK_STATUS_MILLIS = Duration.ofMinutes(5).toMillis();
    private static final UUID UNKNOWN_PACK = new UUID(0L, 0L);

    private final MenuPlugin plugin;
    private final Path resourcePackFolder;
    private final VanillaAssets vanillaAssets;
    private final TextFonts textFonts;
    private final CanvasPackGenerator generator;
    private final Object generationLock = new Object();
    private final Map<UUID, PackStatus> packStatus = new ConcurrentHashMap<>();
    private final ZCanvasStateService stateService = new ZCanvasStateService();
    private final Set<String> reportedMissing = ConcurrentHashMap.newKeySet();
    private final Map<String, Optional<ResolvedGlyph>> glyphs = new ConcurrentHashMap<>();
    private final AtomicBoolean downloadingVanillaAssets = new AtomicBoolean();
    private final DialogClicks clicks;

    private volatile CanvasLayout layout = CanvasLayout.EMPTY;
    private volatile ZGeneratedPack pack;
    private volatile CanvasSettings settings;
    private volatile String reportedTargetProblem;
    private volatile boolean sentByZMenu;
    private volatile Map<String, List<String>> problems = Map.of();

    public ZCanvasManager(@NotNull MenuPlugin plugin, @NotNull DialogClicks clicks) {
        this.plugin = plugin;
        this.clicks = clicks;
        this.resourcePackFolder = plugin.getDataFolder().toPath().resolve("resource-pack").toAbsolutePath().normalize();
        this.vanillaAssets = new VanillaAssets(plugin.getDataFolder().toPath().resolve("cache"), Bukkit.getMinecraftVersion());
        this.textFonts = new TextFonts(new VanillaTextFont(this.vanillaAssets));
        this.generator = new CanvasPackGenerator(this.textFonts);
        this.settings = CanvasSettings.read(plugin.getConfig());
    }

    /**
     * Registers the built-in glyph providers, targets and elements, then the providers and targets of the
     * plugin hooks, and starts downloading the vanilla assets if needed.
     */
    public void enable() {
        RegistryManager registries = this.registries();

        WritableRegistry<GlyphProvider> glyphProviderWritableRegistry = registries.writable(Registries.GLYPH_PROVIDER);

        glyphProviderWritableRegistry.register(this.plugin, new ZMenuGlyphProvider(this.resourcePackFolder));
        glyphProviderWritableRegistry.register(this.plugin, new MinecraftGlyphProvider(this.vanillaAssets));
        glyphProviderWritableRegistry.register(this.plugin, new PanelGlyphProvider(this.resourcePackFolder));
        glyphProviderWritableRegistry.register(this.plugin, new ProgressGlyphProvider(this.resourcePackFolder));

        WritableRegistry<CanvasPackTarget> canvasPackTargetWritableRegistry = registries.writable(Registries.CANVAS_PACK_TARGET);

        canvasPackTargetWritableRegistry.register(this.plugin, new NoneCanvasPackTarget());
        StandaloneCanvasPackTarget standalone = new StandaloneCanvasPackTarget(this.plugin, this);
        canvasPackTargetWritableRegistry.register(this.plugin, standalone);

        Bukkit.getPluginManager().registerEvents(standalone, this.plugin);

        WritableRegistry<CanvasElementLoader> canvasElementLoaderWritableRegistry = registries.writable(Registries.CANVAS_ELEMENT);

        canvasElementLoaderWritableRegistry.register(this.plugin, new ImageElementLoader());
        canvasElementLoaderWritableRegistry.register(this.plugin, new TextElementLoader());
        canvasElementLoaderWritableRegistry.register(this.plugin, new ButtonElementLoader());
        canvasElementLoaderWritableRegistry.register(this.plugin, new PanelElementLoader());
        canvasElementLoaderWritableRegistry.register(this.plugin, new ProgressElementLoader());
        canvasElementLoaderWritableRegistry.register(this.plugin, new LayoutElementLoader(LayoutEngine.Direction.ROW));
        canvasElementLoaderWritableRegistry.register(this.plugin, new LayoutElementLoader(LayoutEngine.Direction.COLUMN));
        canvasElementLoaderWritableRegistry.register(this.plugin, new ObjectElementLoaders.Head());
        canvasElementLoaderWritableRegistry.register(this.plugin, new ObjectElementLoaders.Sprite());
        canvasElementLoaderWritableRegistry.register(this.plugin, new ItemElementLoader());

        for (CanvasElementLoader loader : WidgetLoaders.loaders()) canvasElementLoaderWritableRegistry.register(this.plugin, loader);
        for (ActionLoader loader : CanvasStateActions.loaders(this.plugin)) {
            registries.writable(Registries.ACTION).register(this.plugin, loader, loader.aliases());
        }
        this.plugin.registerPlaceholder("canvas_", (offlinePlayer, argument) -> {
            int separator = argument.lastIndexOf(':');
            if (separator <= 0) return "";
            String value = this.stateService.get(offlinePlayer.getUniqueId(), argument.substring(0, separator), argument.substring(separator + 1));
            return value == null ? "" : value;
        });

        ClassRegistry<GlyphProvider, MenuPlugin> providers = ClassRegistry.<GlyphProvider, MenuPlugin>of(GlyphProvider.class, provider -> {
            glyphProviderWritableRegistry.register(this.plugin, provider);
            if (provider instanceof Listener listener) Bukkit.getPluginManager().registerEvents(listener, this.plugin);
        }).tryConstructor((clazz, owner) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(owner)).tryNoArgsConstructor().errorLogger(Logger::error);
        VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoGlyphProvider.class, providers);

        ClassRegistry<CanvasPackTarget, MenuPlugin> targets = ClassRegistry.<CanvasPackTarget, MenuPlugin>of(CanvasPackTarget.class, target -> {
            canvasPackTargetWritableRegistry.register(this.plugin, target);
            if (target instanceof Listener listener) Bukkit.getPluginManager().registerEvents(listener, this.plugin);
        }).tryConstructor((clazz, owner) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(owner)).tryNoArgsConstructor().errorLogger(Logger::error);
        VersionFilter.scanAndRegister("fr.maxlego08.menu", this.plugin, AutoCanvasPackTarget.class, targets);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * Starts downloading the vanilla assets when the canvases need them (the default font, a {@code minecraft:}
     * image or the focus outline shader) and they are not on disk yet, then generates the pack again.
     *
     * @param needed True if the canvases of the loaded dialogs use the vanilla assets.
     */
    private void downloadVanillaAssets(boolean needed) {
        if (!needed || !this.settings.downloadVanillaAssets() || this.vanillaAssets.isReady()) return;
        if (!this.downloadingVanillaAssets.compareAndSet(false, true)) return;
        this.plugin.getScheduler().runAsync(task -> {
            try {
                if (this.vanillaAssets.download()) {
                    this.plugin.getScheduler().runNextTick(next -> this.regenerate("the vanilla assets are ready"));
                }
            } finally {
                this.downloadingVanillaAssets.set(false);
            }
        });
    }

    private @NotNull RegistryManager registries() {
        return this.plugin.getInventoryManager().getRegistries();
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolveGlyph(@NotNull String reference) {
        return this.glyphs.computeIfAbsent(reference, this::resolveUncached);
    }

    private @NotNull Optional<ResolvedGlyph> resolveUncached(@NotNull String reference) {
        Optional<GlyphProvider> provider = this.providerFor(reference);
        if (provider.isEmpty()) return Optional.empty();
        int separator = reference.indexOf(':');
        String path = separator < 0 ? reference : reference.substring(separator + 1);
        try {
            return provider.get().resolve(path);
        } catch (RuntimeException | LinkageError exception) {
            Logger.info("The glyph " + reference + " cannot be resolved: " + exception, Logger.LogType.WARNING);
            return Optional.empty();
        }
    }

    private @NotNull Optional<GlyphProvider> providerFor(@NotNull String reference) {
        int separator = reference.indexOf(':');
        String name = separator < 0 ? ZMenuKeys.NAMESPACE : reference.substring(0, separator);
        return this.registries().registry(Registries.GLYPH_PROVIDER).get(ZMenuKeys.parse(name)).filter(GlyphProvider::isAvailable);
    }

    @Override
    public @NotNull CanvasBuilder newCanvas(int width, int height) {
        return new ZCanvasBuilder(width, height, this.layout, this.textFonts, this::reportMissing);
    }

    @Override
    public @NotNull CanvasBuilder newCanvas(int width, int height, @NotNull Player viewer) {
        boolean rowClicks = this.plugin.getClientVersionManager().getClientVersion(viewer).isBefore(GEOMETRIC_CLICKS);
        return new ZCanvasBuilder(width, height, this.layout, this.textFonts, this::reportMissing, rowClicks);
    }

    @Override
    public @NotNull ClickEvent callback(@NotNull Player viewer, @NotNull Consumer<Player> action) {
        return this.clicks.add(viewer.getUniqueId(), (player, view) -> action.accept(player), DialogClicks.UNLIMITED_USES,
                this.settings.clickLifetimeSeconds() * 1000L);
    }

    @Override
    public void resetClicks(@NotNull Player viewer) {
        this.clicks.reset(viewer.getUniqueId());
    }

    private void reportMissing(@NotNull String what) {
        if (this.reportedMissing.add(what)) {
            Logger.info("A dialog canvas cannot draw the " + what + ": it is not in the generated resource pack. It is skipped until the pack is generated again.", Logger.LogType.WARNING);
        }
    }

    @Override
    public @NotNull Optional<CanvasElementLoader> getElementLoader(@NotNull String type) {
        return this.registries().registry(Registries.CANVAS_ELEMENT).get(ZMenuKeys.parse(type));
    }

    @Override
    public void regenerate(@NotNull String reason) {
        this.generate(reason, true);
    }

    @Override
    public @NotNull Optional<GeneratedPack> generateForPackBuild(@NotNull String reason) {
        return Optional.ofNullable(this.generate(reason, false));
    }

    private @Nullable ZGeneratedPack generate(@NotNull String reason, boolean allowRebuild) {
        synchronized (this.generationLock) {
            try {
                this.settings = CanvasSettings.read(this.plugin.getConfig());
                this.glyphs.clear();
                Optional<CanvasPackTarget> target = this.getActiveTarget();
                this.vanillaAssets.useOverrides(path -> path.startsWith("assets/zmenu/") ? Optional.empty() : target.flatMap(value -> value.readPackFile(path)));
                this.registries().registry(Registries.GLYPH_PROVIDER).values().forEach(GlyphProvider::reload);
                this.textFonts.load(this.resourcePackFolder);

                AtomicBoolean vanillaGlyph = new AtomicBoolean();
                CanvasRequest request = new CanvasRequest(reference -> {
                    if (reference.startsWith(Key.MINECRAFT_NAMESPACE + ":")) vanillaGlyph.set(true);
                    return this.resolveGlyph(reference);
                }, this::providerFor);
                CanvasChecker checker = new CanvasChecker(request, this.textFonts::get, this::formatForCheck);
                this.canvasBodies().forEach((dialog, bodies) -> bodies.forEach(body -> checker.check(dialog, body)));
                this.reportProblems(checker.problems());

                CodepointLock lock = CodepointLock.load(this.resourcePackFolder.resolve("codepoints.lock"));
                CanvasPackGenerator.Result result = this.generator.generate(request, lock);
                lock.save();

                SortedMap<String, byte[]> files = new TreeMap<>(result.files());
                boolean vanillaFont = request.texts().stream().anyMatch(text -> text.startsWith(VanillaTextFont.NAME + "@"));
                boolean focusOutline = this.settings.focusOutline() != CanvasSettings.FocusOutline.OFF && !files.isEmpty();
                this.downloadVanillaAssets(vanillaGlyph.get() || vanillaFont || focusOutline);
                this.addFocusOutlineShader(files);
                String contentHash = PackOutput.contentHash(files);
                ZGeneratedPack previous = this.pack;
                boolean changed = previous == null || !previous.contentHash().equals(contentHash);

                Path assets = this.resourcePackFolder.resolve("output");
                PackOutput.writeFolder(assets, files);

                SortedMap<String, byte[]> zipFiles = new TreeMap<>(files);
                int format = PackFormat.forVersion(Bukkit.getMinecraftVersion());
                zipFiles.put("pack.mcmeta", PackFormat.mcmeta(format, "zMenu dialog canvases").toString().getBytes(StandardCharsets.UTF_8));
                Path zip = this.resourcePackFolder.resolve("zmenu-pack.zip");
                String sha1 = PackOutput.writeZip(zip, zipFiles);

                ZGeneratedPack generated = new ZGeneratedPack(assets, zip, sha1, contentHash, changed, files.isEmpty());
                this.layout = result.layout();
                checker.lines().forEach((body, lines) -> body.useLines(generated, lines.base(), lines.overlay()));
                this.pack = generated;
                this.reportedMissing.clear();

                if (Configuration.enableInformationMessage && changed) {
                    Logger.info("Generated the dialog canvas resource pack (" + files.size() + " files, " + request.glyphs().size() + " image positions and "
                            + request.texts().size() + " text fonts) because " + reason);
                    if (!checker.variantCounts().isEmpty()) {
                        Logger.info("Pack variants by dialog: " + String.join(", ", checker.variantCounts().entrySet().stream()
                                .map(entry -> entry.getKey() + " " + entry.getValue()).toList()));
                    }
                }
                this.deliver(generated, allowRebuild);
                return generated;
            } catch (IOException | RuntimeException exception) {
                Logger.info("Could not generate the dialog canvas resource pack: " + exception.getMessage(), Logger.LogType.ERROR);
                if (Configuration.enableDebug) Logger.error(exception);
                return null;
            }
        }
    }

    private void addFocusOutlineShader(@NotNull SortedMap<String, byte[]> files) {
        if (this.settings.focusOutline() == CanvasSettings.FocusOutline.OFF || files.isEmpty()) return;
        List<int[]> canvases = new ArrayList<>();
        this.canvasBodies().values().forEach(bodies -> bodies.forEach(body ->
                canvases.add(new int[]{body.getCanvasWidth().fixedOrFallback(), body.getCanvasHeight().fixedOrFallback()})));
        Optional<String> vertex = this.vanillaAssets.text(FocusOutlineShader.VERTEX_PATH);
        Optional<String> fragment = this.vanillaAssets.text(FocusOutlineShader.FRAGMENT_PATH);
        Optional<Map<String, String>> shaders = vertex.isEmpty() || fragment.isEmpty()
                ? Optional.empty()
                : FocusOutlineShader.patch(vertex.get(), fragment.get(), canvases, this.settings.focusOutline() == CanvasSettings.FocusOutline.DEBUG);
        if (shaders.isPresent()) {
            shaders.get().forEach((path, source) -> files.put(path, source.getBytes(StandardCharsets.UTF_8)));
        } else {
            this.reportTargetProblem("dialog-canvas.hide-focus-outline is on, but the gui shader of the Minecraft " + this.vanillaAssets.getVersion()
                    + " client is not downloaded yet or has changed: the click outline stays");
        }
    }

    private void deliver(@NotNull ZGeneratedPack generated, boolean mayRebuild) {
        Optional<CanvasPackTarget> optional = this.getActiveTarget();
        this.sentByZMenu = optional.map(target -> target.key().equals(ZMenuKeys.key(StandaloneCanvasPackTarget.NAME))).orElse(false);
        if (optional.isEmpty()) return;
        CanvasPackTarget target = optional.get();
        boolean outdated = target.install(generated);
        if (!mayRebuild || !outdated) return;
        if (this.settings.autoRebuild()) {
            target.requestRebuild();
        } else {
            Logger.info("The dialog canvas files changed: " + target.rebuildHint() + " so players get them");
        }
    }

    private @NotNull Map<String, List<DialogCanvasBody>> canvasBodies() {
        Map<String, List<DialogCanvasBody>> bodies = new LinkedHashMap<>();
        DialogManager dialogManager = this.plugin.getDialogManager();
        if (dialogManager == null) return bodies;
        for (DialogInventory dialog : dialogManager.getDialogs()) {
            List<DialogCanvasBody> canvases = new ArrayList<>();
            for (BodyButton body : dialog.getBodyButtons()) collect(body, canvases);
            if (!canvases.isEmpty()) bodies.computeIfAbsent(dialog.getFileName(), key -> new ArrayList<>()).addAll(canvases);
        }
        return bodies;
    }

    private @NotNull Component formatForCheck(@NotNull String text) {
        return this.plugin.getMetaUpdater() instanceof PaperMetaUpdater metaUpdater ? metaUpdater.getComponent(text) : Component.text(text);
    }

    private void reportProblems(@NotNull Map<String, List<String>> found) {
        if (found.equals(this.problems)) return;
        this.problems = found;
        found.forEach((dialog, list) -> {
            Logger.info("The canvases of the dialog " + dialog + " have " + list.size() + (list.size() == 1 ? " problem:" : " problems:"), Logger.LogType.WARNING);
            for (String problem : list) Logger.info("  - " + problem, Logger.LogType.WARNING);
        });
    }

    private static void collect(@Nullable Button button, @NotNull List<DialogCanvasBody> bodies) {
        while (button != null) {
            if (button instanceof DialogCanvasBody canvasBody) bodies.add(canvasBody);
            button = button.getElseButton();
        }
    }

    @Override
    public @NotNull Optional<GeneratedPack> getGeneratedPack() {
        return Optional.ofNullable(this.pack);
    }

    @Override
    public @NotNull Optional<CanvasPackTarget> getActiveTarget() {
        List<CanvasPackTarget> targets = this.registries().registry(Registries.CANVAS_PACK_TARGET).values();
        String name = this.settings.target();

        if (CanvasSettings.AUTO.equalsIgnoreCase(name)) {
            Key none = ZMenuKeys.key(NoneCanvasPackTarget.NAME);
            Key standalone = ZMenuKeys.key(StandaloneCanvasPackTarget.NAME);
            List<CanvasPackTarget> hosts = targets.stream()
                    .filter(target -> !target.key().equals(none) && !target.key().equals(standalone))
                    .filter(CanvasPackTarget::isAvailable)
                    .toList();
            if (hosts.size() == 1) return Optional.of(hosts.getFirst());
            if (hosts.size() > 1) {
                this.reportTargetProblem("Several resource pack plugins are installed (" + String.join(", ", hosts.stream().map(target -> target.key().value()).toList())
                        + "): choose the one zMenu adds its dialog canvas files to with dialog-canvas.resource-pack.target in config.yml");
                return targets.stream().filter(target -> target.key().equals(none)).findFirst();
            }
            Optional<CanvasPackTarget> standaloneTarget = targets.stream().filter(target -> target.key().equals(standalone)).filter(CanvasPackTarget::isAvailable).findFirst();
            return standaloneTarget.isPresent() ? standaloneTarget : targets.stream().filter(target -> target.key().equals(none)).findFirst();
        }

        Optional<CanvasPackTarget> target = this.registries().registry(Registries.CANVAS_PACK_TARGET).get(ZMenuKeys.parse(name));
        if (target.isEmpty() || !target.get().isAvailable()) {
            this.reportTargetProblem("The dialog canvas pack target " + name + " is unknown or its plugin is not installed, zMenu's files are left in " + this.resourcePackFolder);
            return Optional.empty();
        }
        return target;
    }

    private void reportTargetProblem(@NotNull String message) {
        if (message.equals(this.reportedTargetProblem)) return;
        this.reportedTargetProblem = message;
        Logger.info(message, Logger.LogType.WARNING);
    }

    @Override
    public boolean canSeeCanvas(@NotNull Player player) {
        if (this.isBedrockPlayer(player)) return false;
        return this.settings.requirePack().showsCanvas(this.getResourcePackStatus(player.getUniqueId()).orElse(null), this.sentByZMenu);
    }

    @Override
    public boolean isBedrockPlayer(@NotNull Player player) {
        BedrockManager bedrockManager = this.plugin.getBedrockManager();
        if (bedrockManager == null) return false;
        try {
            return bedrockManager.isBedrockPlayer(player);
        } catch (LinkageError error) {
            return false;
        }
    }

    @Override
    public void reportResourcePack(@NotNull UUID player, @Nullable UUID pack, boolean loaded) {
        UUID packId = pack == null ? UNKNOWN_PACK : pack;
        this.packStatus.compute(player, (uuid, current) -> {
            Map<UUID, Boolean> packs = new HashMap<>(current == null ? Map.of() : current.packs());
            packs.put(packId, loaded);
            return new PackStatus(Map.copyOf(packs), System.currentTimeMillis());
        });
    }

    @Override
    public @NotNull Optional<Boolean> getResourcePackStatus(@NotNull UUID player) {
        PackStatus status = this.packStatus.get(player);
        if (status == null) return Optional.empty();
        if (this.sentByZMenu) return Optional.ofNullable(status.packs().get(StandaloneCanvasPackTarget.PACK_ID));
        return Optional.of(!status.packs().containsValue(false));
    }

    /**
     * What a player's client last answered to each resource pack.
     *
     * @param packs      By pack id, true if the pack loaded, false if it was declined or failed.
     * @param reportedAt When the last answer was reported, to forget players who left during the configuration phase.
     */
    private record PackStatus(@NotNull Map<UUID, Boolean> packs, long reportedAt) {
    }

    @Override
    public @NotNull Map<String, List<String>> getProblems() {
        return this.problems;
    }

    @Override
    public @NotNull CanvasStateService getStateService() {
        return this.stateService;
    }

    @Override
    public void reopen(@NotNull Player player, @NotNull String dialog) {
        DialogManager dialogManager = this.plugin.getDialogManager();
        if (dialogManager == null) return;
        dialogManager.getDialog(dialog).ifPresent(inventory -> this.plugin.getScheduler().runAtEntity(player, task -> {
            this.stateService.markReopen(player.getUniqueId(), inventory.getFileName());
            try {
                dialogManager.openDialog(player, inventory);
            } finally {
                this.stateService.cancelReopen(player.getUniqueId(), inventory.getFileName());
            }
        }));
    }

    @Override
    public @NotNull Path getResourcePackFolder() {
        return this.resourcePackFolder;
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (Configuration.enableDebug) {
            Logger.info("[Canvas] Resource pack " + event.getID() + " of " + event.getPlayer().getName() + " in the world: " + event.getStatus());
        }
        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED -> this.reportResourcePack(event.getPlayer().getUniqueId(), event.getID(), true);
            case DECLINED, FAILED_DOWNLOAD, INVALID_URL, FAILED_RELOAD, DISCARDED -> this.reportResourcePack(event.getPlayer().getUniqueId(), event.getID(), false);
            default -> {
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.packStatus.remove(event.getPlayer().getUniqueId());
        this.stateService.clear(event.getPlayer().getUniqueId());
        long now = System.currentTimeMillis();
        this.packStatus.entrySet().removeIf(entry -> now - entry.getValue().reportedAt() > STALE_PACK_STATUS_MILLIS && Bukkit.getPlayer(entry.getKey()) == null);
    }
}
