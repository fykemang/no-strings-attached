package root;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import entities.Person;
import obstacle.Obstacle;

import java.util.Stack;

/**
 * Callback to handle if the player needs to attach to an NPC
 */
public class RopeQueryCallback implements QueryCallback {
    private final Person player;
    /**
     * Hold all npcs which we detect
     */
    private final Stack<Person> npcCache;

    public RopeQueryCallback(Person player) {
        this.player = player;
        npcCache = new Stack<>();
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        Obstacle obstacle = (Obstacle) body.getUserData();

        if (obstacle.getName().equals("npc")) {
            Person p = (Person) obstacle;
            if (!p.isAttached()) {
                npcCache.add((Person) obstacle);
            }
        }

        return true;
    }

    /**
     * Iterate through {@code npcCache} to find
     * the target the {@code player} should latch onto
     */
    public boolean selectTarget() {
        Vector2 playerPosition = player.getPosition();
        Person closest = null;

        if (!npcCache.empty()) {
            closest = npcCache.pop();
            Vector2 npcPosition = closest.getPosition();
            float dst = playerPosition.dst(npcPosition);
            float closestDst = dst;

            while (!npcCache.empty()) {
                Person npc = npcCache.pop();
                npcPosition = npc.getPosition();
                boolean isPlayerFacingNpc = (playerPosition.x - npcPosition.x > 0 && !player.isFacingRight()) || (playerPosition.x - npcPosition.x < 0 && player.isFacingRight());
                dst = playerPosition.dst(npcPosition);
                if (isPlayerFacingNpc && dst < closestDst) {
                    closest = npc;
                    closestDst = dst;
                }
            }
        }

        player.setTarget(closest);
        return closest != null;
    }
}
