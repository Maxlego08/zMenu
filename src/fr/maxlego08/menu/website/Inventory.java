package fr.maxlego08.menu.website;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Inventory {
    private final int id;
    private final int folderId;
    private final int userId;
    private final String fileName;
    private final String name;
    private final int size;
    private final int updateInterval;
    private final int clearInventory;
    private final String createdAt;
    private final String updatedAt;

    public Inventory(int id, int folderId, int userId, String fileName, String name, int size, int updateInterval, int clearInventory, String createdAt, String updatedAt) {
        this.id = id;
        this.folderId = folderId;
        this.userId = userId;
        this.fileName = fileName;
        this.name = name;
        this.size = size;
        this.updateInterval = updateInterval;
        this.clearInventory = clearInventory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public int getId() {
        return id;
    }

    public int getFolderId() {
        return folderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getClearInventory() {
        return clearInventory;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
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