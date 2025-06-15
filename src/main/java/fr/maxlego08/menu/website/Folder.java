package fr.maxlego08.menu.website;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Folder {
    private final int id;
    private final String name;
    private final int userId;
    private final Integer parentId;
    private final String createdAt;
    private final String updatedAt;
    private final List<Inventory> inventories;

    public Folder(int id, String name, int userId, Integer parentId, String createdAt, String updatedAt, List<Inventory> inventories) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.inventories = inventories;
    }

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    @Override
    public String toString() {
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
