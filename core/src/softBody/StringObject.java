package softBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import root.GameCanvas;

public class StringObject extends SimModel {
    private Spring springForce;

    public Spring getSpringForce() {
        return springForce;
    }

    public void setSpringForce(Spring springForce) {
        this.springForce = springForce;
    }

    public StringObject(float mass, SimObjectType s, Vector2 currPosition) {
        super(mass, s, currPosition);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
//        float restlength =
        System.out.println(currPosition);

        if (texture != null) {
//            System.out.println("here");
            canvas.draw(texture, Color.RED, origin.x, origin.y, currPosition.x * drawScale.x,
                    currPosition.y * drawScale.y, 0f, springForce.getRestlength() / texture.getRegionWidth(), 1);

        }
    }
}
