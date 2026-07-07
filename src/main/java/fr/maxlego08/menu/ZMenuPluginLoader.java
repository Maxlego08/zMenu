package fr.maxlego08.menu;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class ZMenuPluginLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
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
}