package fr.maxlego08.menu.api.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Pagination<T> {

    /**
     * Paginates a list in reverse order.
     *
     * @param list          the list of elements
     * @param inventorySize the size of each page
     * @param page          the current page (1-based index)
     * @return a sublist corresponding to the requested page
     */
    public List<T> paginateReverse(List<T> list, int inventorySize, int page) {
        if (list == null || list.isEmpty() || inventorySize <= 0) return Collections.emptyList();
        if (page <= 0) page = 1;

        List<T> currentList = new ArrayList<>();
        int idStart = list.size() - ((page - 1) * inventorySize) - 1;
        int idEnd = Math.max(idStart - inventorySize + 1, 0);

        if (idStart < 0) return Collections.emptyList();

        for (int i = idStart; i >= idEnd; i--) {
            currentList.add(list.get(i));
        }
        return currentList;
    }

    /**
     * Paginates a list in normal order.
     *
     * @param list the list of elements
     * @param size the size of each page
     * @param page the current page (1-based index)
     * @return a sublist corresponding to the requested page
     */
    public List<T> paginate(List<T> list, int size, int page) {
        if (list == null || list.isEmpty() || size <= 0) return Collections.emptyList();
        if (page <= 0) page = 1;

        int idStart = (page - 1) * size;
        if (idStart >= list.size()) return Collections.emptyList();

        int idEnd = Math.min(idStart + size, list.size());

        return list.subList(idStart, idEnd);
    }

    /**
     * Paginates a map's values in reverse order.
     *
     * @param map  the map of elements
     * @param size the size of each page
     * @param page the current page (1-based index)
     * @return a sublist corresponding to the requested page
     */
    public List<T> paginateReverse(Map<?, T> map, int size, int page) {
        return (map == null || map.isEmpty()) ? Collections.emptyList() : paginateReverse(new ArrayList<>(map.values()), size, page);
    }

    /**
     * Paginates a map's values in normal order.
     *
     * @param map  the map of elements
     * @param size the size of each page
     * @param page the current page (1-based index)
     * @return a sublist corresponding to the requested page
     */
    public List<T> paginate(Map<?, T> map, int size, int page) {
        return (map == null || map.isEmpty()) ? Collections.emptyList() : paginate(new ArrayList<>(map.values()), size, page);
    }
}
