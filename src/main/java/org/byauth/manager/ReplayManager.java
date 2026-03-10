package org.byauth.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.service.Service;

import java.util.*;

public class ReplayManager implements Service {

    private final EnsDaire plugin;
    private final Map<UUID, List<ReplayFrame>> recording = new HashMap<>();
    private boolean isRecording = false;

    public ReplayManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public record ReplayFrame(Location location, boolean isCrouching, boolean isSprinting) {}

    @Override
    public void init() {}

    public void startRecording() {
        isRecording = true;
        recording.clear();
    }

    public void recordFrame(Player player) {
        if (!isRecording) return;
        var frames = recording.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        frames.add(new ReplayFrame(player.getLocation().clone(), player.isSneaking(), player.isSprinting()));
    }

    public void stopRecording() {
        isRecording = false;
    }

    public List<ReplayFrame> getFrames(UUID uuid) {
        return recording.getOrDefault(uuid, Collections.emptyList());
    }

    @Override
    public void terminate() {
        recording.clear();
    }
}
