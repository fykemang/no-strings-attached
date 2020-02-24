package softBody;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import softBody.SimObjectType;

public class SimModel extends SimObject {
    public SimModel(float mass, SimObjectType s, Vector2 currPosition) {
        super(mass, s);
        this.setCurrPosition(currPosition);
    }

    @Override
    public void update(float dt) {
        Vector2 a = this.getAcceleration();
        Vector2 v = this.getCurrVelocity();
        this.setCurrPosition(this.currPosition.x + v.x * dt, this.currPosition.y + v.y * dt);
        this.setVelocity(v.x + a.x * dt, v.y + a.y * dt);
    }
}
