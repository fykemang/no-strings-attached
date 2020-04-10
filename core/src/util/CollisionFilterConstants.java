package util;

public enum CollisionFilterConstants {
    CATEGORY_PLAYER_ROPE(0x0001),
    CATEGORY_PLATFORM(0x0010),
    CATEGORY_NPC(0x0011),
    MASK_PLAYER_ROPE(0x0000);

    private int id;

    public int getID() {
        return this.id;
    }

    CollisionFilterConstants(int id) {
        this.id = id;
    }
}
