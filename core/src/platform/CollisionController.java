package platform;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import obstacle.Obstacle;

public class CollisionController implements ContactListener {
    /**
     * Mark set to handle more sophisticated collision callbacks
     */
    private ObjectSet<Fixture> sensorFixtures;
    private DudeModel mainDude;


    public CollisionController(DudeModel mainDude) {
        this.sensorFixtures = new ObjectSet<>();
        this.mainDude = mainDude;
    }

//    public boolean checkForRopeCollision(Obstacle obs) {
//        if (obs.getName().equals("rope")) {
//            Rope rope = (Rope) obs;
//            Iterable<Obstacle> links = rope.getBodies();
//            for (Obstacle link : links) {
//                BoxObstacle l = (BoxObstacle) link;
//                float linkWidth = l.getWidth();
//                Vector2 linkPos = link.getPosition();
//
//                normal.set(mainDude.getPosition()).sub(linkPos);
//                float normDist = normal.len();
//                float impactDist =
//
//            }
//            return true;
//        }
//        return false;
//    }

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

            if (bd1.getName().equals(Plank.PLANK_NAME) && bd2.getName().equals(mainDude.getName())) {
                mainDude.setCanCut(true);
                mainDude.setClosestCouple(((Plank) bd1).getPlankParentID());
            }

            if (bd1.getName().equals(mainDude.getName()) && bd2.getName().equals(Plank.PLANK_NAME)) {
                mainDude.setCanCut(true);
                mainDude.setClosestCouple(((Plank) bd2).getPlankParentID());
            }

            // See if we have landed on the ground.
            if ((mainDude.getSensorName().equals(fd2) && mainDude != bd1) ||
                    (mainDude.getSensorName().equals(fd1) && mainDude != bd2)) {
                mainDude.setGrounded(true);
                sensorFixtures.add(mainDude == bd1 ? fix2 : fix1); // Could have more than one ground
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

        if (bd1.getName().equals(mainDude.getName()) && bd2.getName().equals(Plank.PLANK_NAME) ||
                bd2.getName().equals(mainDude.getName()) && bd1.getName().equals(Plank.PLANK_NAME)) {
            mainDude.setCanCut(false);
        }


        if ((mainDude.getSensorName().equals(fd2) && mainDude != bd1) ||
                (mainDude.getSensorName().equals(fd1) && mainDude != bd2)) {
            sensorFixtures.remove(mainDude == bd1 ? fix2 : fix1);
            if (sensorFixtures.size == 0) {
                mainDude.setGrounded(false);
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
