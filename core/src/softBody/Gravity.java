package softBody;

import com.badlogic.gdx.math.Vector2;

public class Gravity implements ForceGenerator {
    private Vector2 acceleration;

    public Vector2 getAcceleration() {
        return this.acceleration;
    }

    public void setAcceleration(Vector2 a) {
        this.acceleration = a;
    }

    public float getAccelerationX() {
        return this.acceleration.x;
    }

    public void setAccelerationX(float ax) {
        this.acceleration.x = ax;
    }

    public float getAccelerationY() {
        return this.acceleration.y;
    }

    public void setAccelerationY(float ay) {
        this.acceleration.y = ay;
    }

    public Gravity() {
        this.acceleration = new Vector2(0, -9.81f);
    }

    public Gravity(Vector2 a) {
        this.acceleration = a;
    }

    @Override
    public void applyForce(SimObject s) {
        float ax = acceleration.x * s.getMass(), ay = acceleration.y * s.getMass();
        float fx = s.getResultantForce().x, fy = s.getResultantForce().y;
        s.setResultantForce(fx + ax, fy + ay);
    }
}
