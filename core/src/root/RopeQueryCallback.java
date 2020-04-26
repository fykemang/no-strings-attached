package root;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import entities.NpcPerson;
import entities.Person;
import obstacle.Obstacle;

/**
 * Callback to handle if the player needs to attach to an NPC
 */
public class RopeQueryCallback extends AABBQueryCallback {
    private Person player;
    private NpcPerson closestNpc;
    private float closestDist;

    public RopeQueryCallback() {
        super();
    }

    public void setPlayer(Person player) {
        this.player = player;
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        Obstacle obstacle = (Obstacle) body.getUserData();
        if (obstacle.getName().equals("npc")) {
            NpcPerson npc = (NpcPerson) obstacle;
            Vector2 playerPosition = player.getPosition();
            Vector2 npcPosition = npc.getPosition();
            if (!npc.isAttached()) {
                boolean isPlayerFacingNpc = (playerPosition.x - npcPosition.x > 0 && !player.isFacingRight()) || (playerPosition.x - npcPosition.x < 0 && player.isFacingRight());
                float dst = playerPosition.dst(npcPosition);
                if (isPlayerFacingNpc && dst < closestDist) {
                    closestNpc = npc;
                    closestDist = dst;
                }
            }
        }

        return true;
    }

    public NpcPerson getClosestNpc() {
        return closestNpc;
    }

    @Override
    public void reset() {
        closestDist = Float.MAX_VALUE;
        closestNpc = null;
    }
}
