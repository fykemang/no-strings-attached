package root;

import com.badlogic.gdx.physics.box2d.QueryCallback;

public abstract class AABBQueryCallback implements QueryCallback {
    public AABBQueryCallback() {
        reset();
    }

    public abstract void reset();
}
