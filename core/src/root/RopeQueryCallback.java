package root;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import obstacle.Obstacle;
import entities.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback to handle if the player needs to attach to an NPC
 */
public class RopeQueryCallback implements QueryCallback {
    private Person player;
    /**
     * Hold all npcs which we detect
     */
    private List<Person> npcCache;

    public RopeQueryCallback(Person player) {
        this.player = player;
        npcCache = new ArrayList<>();
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        Obstacle obstacle = (Obstacle) body.getUserData();

        if (obstacle.getName().equals("npc")) {
            npcCache.add((Person) obstacle);
        }

        return true;
    }

    /**
     * Iterate through {@code npcCache} to find
     * the target the {@code player} should latch onto
     */
    public void selectTarget() {
        Vector2 playerPosition = player.getPosition();
        Person closest = null;
        float closestDst = Float.MAX_VALUE;
        for (Person npc : npcCache) {
            Vector2 npcPosition = npc.getPosition();
            boolean isPlayerFacingNpc = (playerPosition.x - npcPosition.x > 0 && !player.isFacingRight()) || (playerPosition.x - npcPosition.x < 0 && player.isFacingRight());
            float dst = playerPosition.dst(npcPosition);
            if (isPlayerFacingNpc && dst < closestDst) {
                closest = npc;
                closestDst = dst;
            }
        }
        player.setTarget(closest);
    }
}
