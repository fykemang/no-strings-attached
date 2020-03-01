package softBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import root.GameCanvas;

public class StringObject extends SimModel {
    private Spring springForce;
    private float angle;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Spring getSpringForce() {
        return springForce;
    }

    public void setSpringForce(Spring springForce) {
        this.springForce = springForce;
    }

    public StringObject(float mass, SimObjectType s, Vector2 currPosition) {
        super(mass, s, currPosition);
        this.angle = 0f;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture, Color.RED, origin.x, origin.y, currPosition.x * drawScale.x,
                    currPosition.y * drawScale.y, angle, springForce.getRestlength() / texture.getRegionWidth(),
                    1);

        }
    }
}
