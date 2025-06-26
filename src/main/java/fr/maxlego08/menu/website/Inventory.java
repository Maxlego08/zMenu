package fr.maxlego08.menu.website;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public record Inventory(int id, int folderId, int userId, String fileName, String name, int size, int updateInterval, int clearInventory, String createdAt, String updatedAt) {

    public static Inventory fromMap(Map<String, Object> map) {
        int id = ((Number) map.get("id")).intValue();
        int folderId = ((Number) map.get("folder_id")).intValue();
        int userId = ((Number) map.get("user_id")).intValue();
        String fileName = (String) map.get("file_name");
        String name = (String) map.get("name");
        int size = ((Number) map.get("size")).intValue();
        int updateInterval = ((Number) map.get("update_interval")).intValue();
        int clearInventory = ((Number) map.get("clear_inventory")).intValue();
        String createdAt = (String) map.get("created_at");
        String updatedAt = (String) map.get("updated_at");

        return new Inventory(id, folderId, userId, fileName, name, size, updateInterval, clearInventory, createdAt, updatedAt);
    }

    @Override
    public @NotNull String toString() {
        return "Inventory{" + "id=" + id + ", folderId=" + folderId + ", userId=" + userId + ", fileName='" + fileName + '\'' + ", name='" + name + '\'' + ", size=" + size + ", updateInterval=" + updateInterval + ", clearInventory=" + clearInventory + ", createdAt='" + createdAt + '\'' + ", updatedAt='" + updatedAt + '\'' + '}';
    }

    public String toCreateDate() {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss");
        LocalDateTime createdDate = LocalDateTime.parse(this.createdAt, inputFormat);
        return createdDate.format(outputFormat);
    }

    public String toUpdateDate() {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss");
        LocalDateTime updateDate = LocalDateTime.parse(this.updatedAt, inputFormat);
        return updateDate.format(outputFormat);
    }
}