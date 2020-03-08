package platform;

import obstacle.WheelObstacle;

public class Plank extends WheelObstacle {
    public static final String PLANK_NAME = "plank";
    private int id;

    public Plank(float x, float y, float r, int id) {
        super(x, y, r);
        setName(PLANK_NAME);
        this.id = id;
    }

    @Override
    protected void createFixtures(){

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
