package softBody;

import com.badlogic.gdx.math.Vector2;
import softBody.SimObjectType;

public class SimModel extends SimObject {
    public SimModel(float mass, SimObjectType s, Vector2 currPosition) {
        super(mass, s);
        this.setCurrPosition(currPosition);
    }

    @Override
    public void update(float dt) {
        Vector2 v = this.getCurrVelocity();
        Vector2 f = this.getResultantForce();
        this.setCurrPosition(this.currPosition.x + v.x * dt, this.currPosition.y + v.y * dt);
    }
}
