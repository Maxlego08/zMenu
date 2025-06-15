package fr.maxlego08.menu.zcore.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeSafeCache {
    private final Map<Class<?>, List<Object>> cache;

    public TypeSafeCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    // Ajoute un objet à la cache
    public void add(Object object) {
        Class<?> type = object.getClass();
        cache.computeIfAbsent(type, k -> new ArrayList<>()).add(object);
    }

    // Récupère tous les objets d'un type donné
    public @NotNull <T> List<T> get(Class<T> type) {
        return (List<T>) cache.getOrDefault(type, new ArrayList<>());
    }

    // Efface tous les objets d'un type donné
    public void clear(Class<?> type) {
        cache.put(type, new ArrayList<>());
    }

    // Efface tous les objets de la cache
    public void clearAll() {
        cache.clear();
    }

    // Supprime un objet spécifique de la cache
    public boolean remove(Object object) {
        Class<?> type = object.getClass();
        List<Object> list = cache.get(type);
        if (list != null) {
            return list.remove(object);
        }
        return false;
    }
}
