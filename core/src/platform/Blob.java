package platform;

import obstacle.WheelObstacle;

public class Blob extends WheelObstacle {
    public static final String BLOB_NAME = "blob";
    private int id;
    private final float STRING_CONSTANT = 0.5f;

    public Blob(float x, float y, float r, int id) {
        super(x, y, r);
        setName(BLOB_NAME);
        this.id = id;
    }

    public float getK() {
        return STRING_CONSTANT;
    }

    @Override
    protected void createFixtures() {

        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixture
        fixture.shape = shape;

        fixture.density = 2f;
        fixture.restitution = 0.4f;
        fixture.friction = 1f;
        body.setFixedRotation(true);
        geometry = body.createFixture(fixture);
        markDirty(false);
    }

    public int getPlankParentID() {
        return id;
    }
}
