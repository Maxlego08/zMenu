package fr.maxlego08.menu.website;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Folder(int id, String name, int userId, Integer parentId, String createdAt, String updatedAt, List<Inventory> inventories) {

    public static Folder fromMap(Map<String, Object> map) {
        int id = ((Number) map.get("id")).intValue();
        String name = (String) map.get("name");
        int userId = ((Number) map.get("user_id")).intValue();
        int parentId = map.containsKey("parent_id") ? map.get("parent_id") != null ? ((Number) map.get("parent_id")).intValue() : -1 : -1;
        String createdAt = (String) map.get("created_at");
        String updatedAt = (String) map.get("updated_at");

        // Convertir la liste des inventaires
        List<Map<String, Object>> inventoriesMap = (List<Map<String, Object>>) map.get("inventories");
        List<Inventory> inventories = new ArrayList<>();
        for (Map<String, Object> inventoryMap : inventoriesMap) {
            Inventory inventory = Inventory.fromMap(inventoryMap);
            inventories.add(inventory);
        }

        return new Folder(id, name, userId, parentId, createdAt, updatedAt, inventories);
    }

    @Override
    public @NotNull String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", parentId=" + parentId +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", inventories=" + inventories +
                '}';
    }
}
