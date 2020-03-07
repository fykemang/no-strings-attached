package platform;

import obstacle.BoxObstacle;
import obstacle.WheelObstacle;

public class Plank extends WheelObstacle {
    public static final String PLANK_NAME = "plank";
    private int id;

    public Plank(float x, float y, float r, int id) {
        super(x, y, r);
        setName(PLANK_NAME);
        this.id = id;
    }

    public int getPlankParentID() {
        return id;
    }
}
