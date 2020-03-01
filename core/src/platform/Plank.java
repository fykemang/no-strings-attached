package platform;

import obstacle.BoxObstacle;

public class Plank extends BoxObstacle {
    public static final String PLANK_NAME = "plank";
    private int id;
    public Plank(float x, float y, float width, float height, int id) {
        super(x, y, width, height);
        setName(PLANK_NAME);
        this.id = id;
    }

    public int getPlankParentID() {
        return id;
    }
}
