package org.byauth.manager;

import org.byauth.EnsDaire;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorManager {

    private final EnsDaire plugin;
    private final Map<UUID, EditorState> editorStates = new HashMap<>();

    public EditorManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public enum EditorType {
        ARENA_SETTINGS, LOOT_SETTINGS, COSMETIC_SETTINGS, GENERAL_CONFIG
    }

    public static class EditorState {
        public EditorType type;
        public String targetId; // Arena name, item id, etc.
        public int page = 0;

        public EditorState(EditorType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }
    }

    public void setState(Player player, EditorType type, String targetId) {
        editorStates.put(player.getUniqueId(), new EditorState(type, targetId));
    }

    public EditorState getState(Player player) {
        return editorStates.get(player.getUniqueId());
    }

    public void clearState(Player player) {
        editorStates.remove(player.getUniqueId());
    }
}
