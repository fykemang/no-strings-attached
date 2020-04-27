package root;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import entities.Blob;
import entities.Person;
import obstacle.Obstacle;

public class CuttingCallback extends AABBQueryCallback {
    private Person player;
    private Blob closestBlob;
    private float closestDist;

    public CuttingCallback() {
        super();
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        Obstacle obstacle = (Obstacle) body.getUserData();
        if (obstacle.getName() != null && obstacle.getName().equals("blob")) {
            Blob blob = (Blob) obstacle;
            Vector2 playerPosition = player.getPosition();
            Vector2 blobPosition = blob.getPosition();
            float dst = playerPosition.dst(blobPosition);
            if (dst < closestDist) {
                closestBlob = blob;
                closestDist = dst;
            }
        }
        return true;
    }

    public void setPlayer(Person player) {
        this.player = player;
    }

    public int getClosestBlobID() {
        return closestBlob != null ? closestBlob.getParentRopeID() : -1;
    }

    @Override
    public void reset() {
        closestBlob = null;
        closestDist = Float.MAX_VALUE;
    }
}
