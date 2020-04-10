package entities;

import obstacle.WheelObstacle;

public class Blob extends WheelObstacle {
    public static final String BLOB_NAME = "blob";
    private int id;

    public Blob(float x, float y, float radius, int id) {
        super(x, y, radius);
        setName(BLOB_NAME);
        this.id = id;
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
