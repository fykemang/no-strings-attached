package root;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import entities.Item;
import obstacle.Obstacle;
import entities.Blob;
import entities.Person;
import entities.NpcRope;

/**
 * ContactListener that detects and handles collisions in the Box2D World
 */
public class CollisionController implements ContactListener {
    /**
     * Mark set to handle more sophisticated collision callbacks
     */
    private ObjectSet<Fixture> sensorFixtures;
    private Person player;
    private Vector2 trampolineForce;


    public CollisionController(Person player) {
        this.sensorFixtures = new ObjectSet<>();
        this.player = player;
        this.trampolineForce = new Vector2();
    }

    /**
     * Callback method for the start of a collision
     * <p>
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    public void beginContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();
        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle) body1.getUserData();
            Obstacle bd2 = (Obstacle) body2.getUserData();

            if (bd1.getName().equals("player_rope") && bd2 != player) {
                bd1.markRemoved(true);
            }

            if (bd2.getName().equals("player_rope") && bd1 != player) {
                NpcRope rope = (NpcRope) bd2;
                if (bd1.getName().equals("npc") && rope.isBroken()) {
                    player.setTarget((Person) bd1);
                }
                bd2.markRemoved(true);
            }

            if (player.getSensorName().equals(fd1) && bd2.getName().equals(Blob.BLOB_NAME)) {
                player.setCanCut(true);
                player.setClosestCoupleID(((Blob) bd2).getPlankParentID());
                Vector2 norm = ((Blob) bd2).getNorm();
                player.setTrampolineDir(norm);
                player.setOnString(true);
            }

            if ("gatesensor".equals(fd1) && bd2.getName().equals(player.getName())) {
                player.atGate();
            }



            if ("gatesensor".equals(fd2) && bd1.getName().equals(player.getName())) {
                player.atGate();

            }

            if (player.getSensorName().equals(fd2) && bd1.getName().equals(Blob.BLOB_NAME)) {
                player.setCanCut(true);
                player.setClosestCoupleID(((Blob) bd1).getPlankParentID());
                Vector2 norm = ((Blob) bd2).getNorm();
                player.setTrampolineDir(norm);
                player.setOnString(true);

            }

            if (bd1 == player && ("item_sensor").equals(fd2) && !((Item) bd2).getState()) {
                player.addItem(bd2.getName());
                ((Item) bd2).setState(true);
                bd2.markRemoved(true);
            }
            if (bd2 == player && ("item_sensor").equals(fd1) && !((Item) bd1).getState()) {
                player.addItem(bd1.getName());
                ((Item) bd1).setState(true);
                bd1.markRemoved(true);
            }

            if ((bd1 == player && bd2.getName().equals("spike")) || (bd2 == player && bd1.getName().equals("spike"))) {
                player.kill();
            }


            // See if we have landed on the ground.
            if ((player.getSensorName().equals(fd2) && player != bd1) ||
                    (player.getSensorName().equals(fd1) && player != bd2)) {
                player.setGrounded(true);
                sensorFixtures.add(player == bd1 ? fix2 : fix1); // Could have more than one ground
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback method for the end of a collision
     * <p>
     * This method is called when two objects cease to touch.  The main use of this method
     * is to determine when the characer is NOT on the ground.  This is how we prevent
     * double jumping.
     */
    public void endContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        Obstacle bd1 = (Obstacle) body1.getUserData();
        Obstacle bd2 = (Obstacle) body2.getUserData();

        if ((player.getSensorName().equals(fd1) && bd2.getName().equals(Blob.BLOB_NAME)) ||
                (player.getSensorName().equals(fd2) && bd1.getName().equals(Blob.BLOB_NAME))) {
            player.setCanCut(false);
            player.setIsTrampolining(true);
        }

        if ((player.getSensorName().equals(fd2) && player != bd1) ||
                (player.getSensorName().equals(fd1) && player != bd2)) {
            sensorFixtures.remove(player == bd1 ? fix2 : fix1);
            if (sensorFixtures.size == 0) {
                player.setGrounded(false);
            }
        }
    }

    /**
     * Unused ContactListener method
     */
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Unused ContactListener method
     */
    public void preSolve(Contact contact, Manifold oldManifold) {
    }
}
