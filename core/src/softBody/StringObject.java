package softBody;

import com.badlogic.gdx.math.Vector2;

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

}
