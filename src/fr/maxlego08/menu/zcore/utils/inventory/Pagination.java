package fr.maxlego08.menu.zcore.utils.inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Pagination<T> {

    /**
     * Allows you to sort a list of items according to the number of items and the page
     * Here the pagination will allow to invert the list of elements
     * The system can be used to create inventories with several pages for example
     *
     * @param list - List of element
     * @param inventorySize - Pagination size
     * @param page - Current pagination page
     */
    public List<T> paginateReverse(List<T> list, int inventorySize, int page) {
        if (page <= 0) page = 1;

        int idStart = Math.max(0, list.size() - (page - 1) * inventorySize - 1);
        int idEnd = Math.max(-1, idStart - inventorySize);

        List<T> currentList = new ArrayList<>();
        for (int i = idStart; i > idEnd; i--) {
            currentList.add(list.get(i));
        }

        return currentList;
    }

    /**
     * Allows you to sort a list of items according to the number of items and the page
     * The system can be used to create inventories with several pages for example
     *
     * @param list - List of element
     * @param size - Pagination size
     * @param page - Current pagination page
     */
    public List<T> paginate(List<T> list, int size, int page) {
        if (page <= 0) page = 1;

        int idStart = (page - 1) * size;
        int idEnd = Math.min(list.size(), idStart + size);

        return new ArrayList<>(list.subList(idStart, idEnd));
    }

    /**
     * The pagination will be done on the values of the map
     *
     * @param map  of element
     * @param size map size
     * @param page current page
     */
    public List<T> paginateReverse(Map<?, T> map, int size, int page) {
        return paginateReverse(new ArrayList<>(new LinkedHashMap<>(map).values()), size, page);
    }

    /**
     * The pagination will be done on the values of the map
     *
     * @param map  of element
     * @param inventorySize need size
     * @param page current page
     */
    public List<T> paginate(Map<?, T> map, int inventorySize, int page) {
        return paginate(new ArrayList<>(new LinkedHashMap<>(map).values()), inventorySize, page);
    }

}
