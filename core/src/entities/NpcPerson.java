package entities;

public class NpcPerson extends Person {
    private String type;
    private NpcPerson couple;
    public boolean left;
    public boolean flip;
    private float anchorX;

    public float getAnchorX() {
        return anchorX;
    }

    public float getAnchorY() {
        return anchorY;
    }

    private float anchorY;

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
        this.left = left;
        this.type = "";
        flip = false;
        this.setName("npc");
    }

    private void setAnchors() {
        switch (type) {
            case "welcome":
                anchorX = getWidth() / 2;
                anchorY = left ? -0.1f : 0.1f;
                break;
            case "cozy":
                anchorX = getWidth() / 2 - 0.45f;
                anchorY = left ? -0.29f : 0.29f;
                break;
            case "cheese":
                anchorX = getWidth() / 2 - 0.45f;
                anchorY = left ? -0.05f : 0.05f;
                break;

            default:
                anchorX = getWidth() / 2 - 0.45f;
                anchorY = left ? -0.12f : 0.12f;
                break;
        }
    }

    public void setType(String type) {
        this.type = type;
        setAnchors();
    }

    public String getType() {
        return this.type;
    }

    public void setCouple(NpcPerson couple) {
        this.couple = couple;
    }

}
