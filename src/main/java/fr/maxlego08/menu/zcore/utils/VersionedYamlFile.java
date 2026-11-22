package fr.maxlego08.menu.zcore.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Block;
import dev.dejvokep.boostedyaml.block.Comments;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.updater.MergeRule;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.conversion.PrimitiveConversions;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Opens a root file of the plugin, such as {@code config.yml}, versioned by {@value #VERSION_ROUTE}.
 *
 * <ul>
 *     <li>missing: the defaults are copied as they are;</li>
 *     <li>without version or with an older one: the file is saved as {@code <name>.bak-v<version or 0>}, then the
 *     missing keys are added with their comments, the keys the defaults no longer have are removed (except under
 *     the ignored routes) and the YAML 1.1 booleans ({@code yes}, {@code off}...) are written {@code true/false};</li>
 *     <li>same version: the file is left untouched;</li>
 *     <li>newer version: the file is left untouched and a warning is logged.</li>
 * </ul>
 * Values whose type does not match the defaults are only reported, the reader falls back on its own default.
 * {@link YamlDocument#reload()} reloads the returned document.
 */
public final class VersionedYamlFile {

    public static final String VERSION_ROUTE = "config-version";

    private VersionedYamlFile() {
    }

    /**
     * @param file          the file of the data folder
     * @param defaults      opens the defaults, which must contain {@value #VERSION_ROUTE}
     * @param ignoredRoutes sections with free keys, kept as the user wrote them
     * @param migrations    changes to apply when updating to a version, by version
     */
    public static YamlDocument open(File file, Supplier<InputStream> defaults, Logger logger, Set<String> ignoredRoutes, Map<String, Consumer<YamlDocument>> migrations) throws IOException {
        if (!file.exists()) {
            File parent = file.getAbsoluteFile().getParentFile();
            if (parent != null) parent.mkdirs();
            try (InputStream inputStream = defaults.get()) {
                Files.copy(inputStream, file.toPath());
            }
        }

        YamlDocument document;
        try (InputStream inputStream = defaults.get()) {
            document = YamlDocument.create(file, inputStream, GeneralSettings.DEFAULT, LegacyBooleanSchema.loaderSettings(), LegacyBooleanSchema.dumperSettings(), UpdaterSettings.DEFAULT);
        }

        int target = document.getDefaults().getInt(VERSION_ROUTE);
        // getOptional, unlike getOptionalInt, does not fall back on the defaults
        Integer current = PrimitiveConversions.toInt(document.getOptional(VERSION_ROUTE).orElse(null)).orElse(null);
        if (current != null && current > target) {
            logger.warning(file.getName() + " has " + VERSION_ROUTE + " " + current + ", newer than this zMenu version (" + target + "): the file is not updated.");
        } else if (current == null || current < target) {
            int from = current == null ? 0 : current;
            if (current == null) removeDuplicatedHeader(document);
            Files.copy(file.toPath(), new File(file.getAbsoluteFile().getParentFile(), file.getName() + ".bak-v" + from).toPath(), StandardCopyOption.REPLACE_EXISTING);

            String version = String.valueOf(target);
            UpdaterSettings.Builder settings = UpdaterSettings.builder()
                    .setVersioning(new BasicVersioning(VERSION_ROUTE))
                    .setKeepAll(false)
                    // A value whose type changed is the user's, it is reported below instead of being replaced
                    .setMergeRule(MergeRule.MAPPING_AT_SECTION, true)
                    .setMergeRule(MergeRule.SECTION_AT_MAPPING, true)
                    .addIgnoredRoutes(version, ignoredRoutes, '.');
            migrations.forEach(settings::addCustomLogic);
            document.update(settings.build());
            document.set(VERSION_ROUTE, target); // BasicVersioning writes it as a string
            document.save();
            logger.info(file.getName() + " updated from version " + from + " to " + target + ", previous file saved as " + file.getName() + ".bak-v" + from);
        }

        validate(file.getName(), document, document.getDefaults(), ignoredRoutes, logger);
        return document;
    }

    /**
     * The header of a file without version is on its first key, while the defaults have it on {@value #VERSION_ROUTE}:
     * the comment lines both start with are removed from the first key, so the header is not written twice.
     */
    private static void removeDuplicatedHeader(YamlDocument document) {
        Iterator<Object> keys = document.getKeys().iterator();
        if (!keys.hasNext()) return;
        Block<?> first = document.getBlock(String.valueOf(keys.next()));
        List<CommentLine> header = Comments.get(first, NodeRole.KEY, Comments.Position.BEFORE);
        List<CommentLine> defaults = Comments.get(document.getDefaults().getBlock(VERSION_ROUTE), NodeRole.KEY, Comments.Position.BEFORE);
        if (header == null || defaults == null) return;
        int common = 0;
        while (common < header.size() && common < defaults.size()
                && header.get(common).getValue().equals(defaults.get(common).getValue())
                && header.get(common).getCommentType() == defaults.get(common).getCommentType()) {
            common++;
        }
        Comments.set(first, NodeRole.KEY, Comments.Position.BEFORE, header.subList(common, header.size()));
    }

    private static void validate(String fileName, Section document, Section defaults, Set<String> ignoredRoutes, Logger logger) {
        for (Map.Entry<String, Object> entry : defaults.getStringRouteMappedValues(true).entrySet()) {
            String route = entry.getKey();
            if (ignoredRoutes.stream().anyMatch(ignored -> route.equals(ignored) || route.startsWith(ignored + "."))) continue;
            Object expected = entry.getValue();
            Object value = document.getOptional(route).orElse(null);
            if (value == null || expected == null) continue;
            if (expected instanceof Boolean && !(value instanceof Boolean)
                    || expected instanceof Number && !(value instanceof Number)
                    || expected instanceof List && !(value instanceof List)
                    || expected instanceof Section && !(value instanceof Section)) {
                logger.warning(fileName + ": " + route + " is '" + value + "', expected a " + expected.getClass().getSimpleName() + " like '" + expected + "'. The default value is used.");
            }
        }
    }
}
