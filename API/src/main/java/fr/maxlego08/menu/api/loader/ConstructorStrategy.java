package fr.maxlego08.menu.api.loader;

import org.bukkit.plugin.Plugin;

@FunctionalInterface
public interface ConstructorStrategy<P extends Plugin> {
    Object instantiate(Class<?> clazz, P plugin) throws Exception;
}
