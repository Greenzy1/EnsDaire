package org.byauth.data;

public record LevelData(int level, long xp) {
    public long getRequiredXp() {
        return (long) (Math.pow(level, 2) * 100);
    }

    public double getProgress() {
        return (double) xp / getRequiredXp();
    }
}
