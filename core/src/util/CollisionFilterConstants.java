package util;

public enum CollisionFilterConstants {
    CATEGORY_PLAYER_ROPE((short) 0x0001),
    CATEGORY_PLATFORM((short) 0x0002),
    CATEGORY_NPC((short) 0x0003),
    CATEGORY_PLAYER((short) 0x0004),
    CATEGORY_NPC_ROPE((short) 0x0005),
    MASK_PLAYER_ROPE((short) 0x0000),
    MASK_PLAYER((short) (CATEGORY_PLATFORM.getID() | CATEGORY_NPC.getID() | CATEGORY_NPC_ROPE.getID())),
    MASK_SWINGING_PLAYER((short) (CATEGORY_PLATFORM.getID() | CATEGORY_NPC.getID())),
    MASK_NPC((short) (CATEGORY_PLATFORM.getID() | CATEGORY_PLAYER.getID())),
    MASK_NPC_PLATFORM((short) (CATEGORY_PLAYER.getID() | CATEGORY_NPC.getID())),
    MASK_NPC_ROPE(CATEGORY_PLAYER.getID());


    private short id;

    public short getID() {
        return this.id;
    }

    CollisionFilterConstants(short id) {
        this.id = id;
    }
}
