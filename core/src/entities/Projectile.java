package entities;

import obstacle.WheelObstacle;

public class Projectile extends WheelObstacle {
    private final int maxAge;
    private int age;

    public Projectile(float x, float y, float radius, int maxAge) {
        super(x, y, radius);
        age = -1;
        this.maxAge = maxAge;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (age == maxAge) {
            markRemoved(true);
        } else {
            age++;
        }
    }
}
