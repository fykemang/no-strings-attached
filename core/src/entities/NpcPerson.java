package entities;

import com.badlogic.gdx.math.Vector2;

public class NpcPerson extends Person {

    private final Vector2 leftAttachPt;
    private final Vector2 rightAttachPt;
    private String type;
    private NpcPerson couple;
    public boolean left;
    public boolean flip;

    public NpcPerson getCouple() {
        return couple;
    }

    /**
     * Creates a new dude avatar at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x          Initial x position of the avatar center
     * @param y          Initial y position of the avatar center
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param name
     * @param sensorName
     */
    public NpcPerson(float x, float y, float width, float height, String name, String sensorName, boolean left) {
        super(x, y, width, height, name, sensorName);
        this.leftAttachPt = new Vector2(getX() + getWidth() / 1.5f - 0.2f, getY() + 0.1f);
        this.rightAttachPt = new Vector2(getX() - getWidth() / 1.5f + 0.2f, getY() + 0.1f);
        this.left = left;
        this.type = "";
        flip = false;
        this.setName("npc");
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setCouple(NpcPerson couple) {
        this.couple = couple;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        Vector2 cpPos = couple.getPosition();
        leftAttachPt.set(getX() + getWidth() / 1.5f - 0.2f, getY() + 0.1f);
        rightAttachPt.set(getX() - getWidth() / 1.5f + 0.2f, getY() + 0.1f);
        boolean temp = leftAttachPt.dst2(cpPos) <= rightAttachPt.dst2(cpPos);
        flip = temp != left;
        left = temp;
    }

    public Vector2 getCloserAttachPoint() {
        if (left) {
            return leftAttachPt;
        } else {
            return rightAttachPt;
        }
    }
}
