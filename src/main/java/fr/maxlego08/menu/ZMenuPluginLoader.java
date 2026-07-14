package fr.maxlego08.menu;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jspecify.annotations.NonNull;

public class ZMenuPluginLoader implements PluginLoader {

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

        classpathBuilder.addLibrary(resolver);
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