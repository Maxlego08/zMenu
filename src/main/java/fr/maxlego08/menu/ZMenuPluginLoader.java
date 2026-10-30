package fr.maxlego08.menu;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ZMenuPluginLoader implements PluginLoader {

    private static final String JEDIS_VERSION = "5.2.0";
    private static final String POSTGRESQL_VERSION = "42.7.4";
    private static final String MONGODB_VERSION = "5.2.0";

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", this.getMavenCentralMirror()
        ).build());
        resolver.addDependency(new Dependency(
                new DefaultArtifact("org.mariadb.jdbc:mariadb-java-client:3.5.6"),
                null
        ));

        resolver.addDependency(new Dependency(
                new DefaultArtifact("org.reflections:reflections:0.10.2"),
                null
        ));

        Set<String> backends = configuredBackends(classpathBuilder.getContext().getDataDirectory().resolve("config.yml"));
        if (backends.contains("REDIS")) {
            resolver.addDependency(new Dependency(
                    new DefaultArtifact("redis.clients:jedis:" + JEDIS_VERSION),
                    null
            ));
        }
        if (backends.contains("MONGODB") || backends.contains("MONGO")) {
            resolver.addDependency(new Dependency(
                    new DefaultArtifact("org.mongodb:mongodb-driver-sync:" + MONGODB_VERSION),
                    null
            ));
        }
        if (backends.contains("POSTGRESQL") || backends.contains("POSTGRES")) {
            resolver.addDependency(new Dependency(
                    new DefaultArtifact("org.postgresql:postgresql:" + POSTGRESQL_VERSION),
                    null
            ));
        }

        classpathBuilder.addLibrary(resolver);
    }

    /**
     * The backends {@code config.yml} names in {@code storage-type}, {@code messaging-type} and
     * {@code migration-target}. The Redis client and the PostgreSQL and MongoDB drivers are only
     * downloaded when one of them asks for it.
     */
    static Set<String> configuredBackends(Path configFile) {
        if (!Files.exists(configFile)) return Set.of();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile.toFile());
        Set<String> backends = new HashSet<>();
        for (String key : new String[]{"storage-type", "messaging-type", "migration-target"}) {
            backends.add(configuration.getString(key, "").toUpperCase(Locale.ROOT));
        }
        return backends;
    }

    private @NonNull String getMavenCentralMirror() {
        try {
            return MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR;
        } catch (NoSuchFieldError e) {
            String central = System.getenv("PAPER_DEFAULT_CENTRAL_REPOSITORY");
            if (central == null) {
                central = System.getProperty("org.bukkit.plugin.java.LibraryLoader.centralURL");
            }
            if (central == null) {
                central = "https://maven-central.storage-download.googleapis.com/maven2";
            }
            return central;
        }
    }
}