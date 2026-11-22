package fr.maxlego08.menu.zcore.utils;

import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.CoreScalarResolver;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.schema.CoreSchema;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * YAML 1.2 core schema that still reads the YAML 1.1 booleans Bukkit accepted ({@code yes/no/on/off} in lower,
 * capitalized or upper case), so files written for Bukkit keep their meaning. Used to dump too, so a string
 * such as {@code "yes"} stays quoted.
 */
public final class LegacyBooleanSchema extends CoreSchema {

    // Same set as SnakeYAML 1.x, the parser behind Bukkit's YamlConfiguration
    private static final Pattern LEGACY_BOOL = Pattern.compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
    public static final LegacyBooleanSchema INSTANCE = new LegacyBooleanSchema();

    // Added after the core resolvers: addImplicitResolvers is package-private in the engine
    private final ScalarResolver resolver = new CoreScalarResolver() {{
        this.addImplicitResolver(Tag.BOOL, LEGACY_BOOL, "yYnNoO");
    }};
    private final Map<Tag, ConstructNode> constructors;

    private LegacyBooleanSchema() {
        this.constructors = new HashMap<>(super.getSchemaTagConstructors());
        this.constructors.put(Tag.BOOL, node -> parse(((ScalarNode) node).getValue()));
    }

    /**
     * Reads a YAML 1.1 boolean.
     *
     * @return the boolean, or {@code null} when the text is not one
     */
    public static Boolean parse(String value) {
        if (value == null || !LEGACY_BOOL.matcher(value).matches()) return null;
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.equals("true") || lower.equals("yes") || lower.equals("on");
    }

    /**
     * Loader settings reading YAML 1.1 booleans, without creating the file or updating it.
     */
    public static LoaderSettings loaderSettings() {
        return LoaderSettings.builder().setSchema(INSTANCE).setCreateFileIfAbsent(false).setAutoUpdate(false).build();
    }

    public static DumperSettings dumperSettings() {
        return DumperSettings.builder().setSchema(INSTANCE).build();
    }

    @Override
    public ScalarResolver getScalarResolver() {
        return this.resolver;
    }

    @Override
    public Map<Tag, ConstructNode> getSchemaTagConstructors() {
        return this.constructors;
    }
}
