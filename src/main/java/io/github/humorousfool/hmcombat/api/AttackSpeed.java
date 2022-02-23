package io.github.humorousfool.hmcombat.api;

public enum AttackSpeed
{
    FAST(0, 0, -1, "Fast"),
    MEDIUM(1, 300, 1, "Medium"),
    SLOW(2, 500, 3, "Slow"),
    VERY_SLOW(3, 1000, 5, "Very Slow");

    AttackSpeed(int id, int attackCooldown, int effectLevel, String title)
    {
        this.title = title;
        this.id = id;
        this.attackCooldown = attackCooldown;
        this.effectLevel = effectLevel;
    }

    public final String title;
    public final int id;
    public final int attackCooldown;
    public final int effectLevel;

    public static AttackSpeed fromInteger(int id)
    {
        return switch (id) {
            case 0 -> FAST;
            case 1 -> MEDIUM;
            case 2 -> SLOW;
            case 3 -> VERY_SLOW;
            default -> FAST;
        };

    }
}
