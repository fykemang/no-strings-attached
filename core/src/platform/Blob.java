package platform;

import com.badlogic.gdx.math.Vector2;
import obstacle.WheelObstacle;

public class Blob extends WheelObstacle {
    public static final String BLOB_NAME = "blob";
    private int id;
    private Vector2 norm;

    public Blob(float x, float y, float r, int id) {
        super(x, y, r);
        setName(BLOB_NAME);
        this.id = id;
        norm = new Vector2();
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

    public void setNorm(Vector2 n) {
        norm.set(n);
    }

    public Vector2 getNorm() {
        return norm;
    }

    public int getPlankParentID() {
        return id;
    }
}
