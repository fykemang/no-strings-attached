/*
 * RopeBridge.java
 *
 * The class is a classic example of how to subclass ComplexPhysicsObject.
 * You have to implement the createJoints() method to stick in all of the
 * joints between objects.
 *
 * This is one of the files that you are expected to modify. Please limit changes to
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package platform;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.*;
import root.GameCanvas;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A bridge with planks connected by revolute joints.
 * <p>
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Rope extends ComplexObstacle {
    /**
     * The debug name for the entire obstacle
     */
    private static final String ROPE_NAME = "rope";
    /**
     * The debug name for each plank
     */
    private static final String PLANK_NAME = "plank";
    /**
     * The density of each plank in the bridge
     */
    private static final float BASIC_DENSITY = 1.0f;

    // Dimension information
    /**
     * The size of the entire bridge
     */
    protected Vector2 dimension;
    /**
     * The size of a single plank
     */
    protected Vector2 planksize;

    /* The length of each link */
    protected float linksize = 1.0f;
    /**
     * The spacing between each link
     */
    protected float spacing = 0.0f;


    private CatmullRomSpline<Vector2> splineCurve;

    Vector2[] contPoints;

    private final int K = 100;

    private Vector2[] POINTS = new Vector2[K];
    private BoxObstacle[] planks;

    public RopeState state;

    public enum RopeState{
        LEFT_BROKEN, RIGHT_BROKEN, COMPLETE
    }


    public Rope(BoxObstacle[] planks, RopeState state){
        this.state = state;
        this.planks = planks;
        bodies.addAll(planks);
        for (int i = 0; i < K; i++) {
            POINTS[i] = new Vector2();
        }
        contPoints = new Vector2[bodies.size + 3];

        for (int i = 0; i < contPoints.length; i++) {
            contPoints[i] = new Vector2();
        }
        setCurrentSplineCurve();
    }

    /**
     * Creates a new rope bridge with the given anchors.
     *
     * @param x0      The x position of the left anchor
     * @param y0      The y position of the left anchor
     * @param x1      The x position of the right anchor
     * @param y1      The y position of the right anchor
     * @param lwidth  The plank length
     * @param lheight The bridge thickness
     */
    public Rope(float x0, float y0, float x1, float y1, float lwidth, float lheight, int id) {
        super(x0, y0);
        setName(ROPE_NAME + id);
        state = RopeState.COMPLETE;

        planksize = new Vector2(lwidth, lheight);
        linksize = planksize.x;

        // Compute the bridge length
        dimension = new Vector2(x1 - x0, y1 - y0);
        float length = dimension.len();
        Vector2 norm = new Vector2(dimension);
        norm.nor();

        // If too small, only make one plank.
        int nLinks = (int) (length / linksize);
        if (nLinks <= 1) {
            nLinks = 1;
            linksize = length;
            spacing = 0;
        } else {
            spacing = length - nLinks * linksize;
            spacing /= (nLinks - 1);
        }

        planks = new BoxObstacle[nLinks];
        // Create the planks
        planksize.x = linksize;
        Vector2 pos = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (linksize + spacing) + linksize / 2.0f;
            pos.set(norm);
            pos.scl(t);
            pos.add(x0, y0);
            Plank plank = new Plank(pos.x, pos.y, planksize.x, planksize.y, id);
            plank.setDensity(BASIC_DENSITY);
            bodies.add(plank);
            planks[i] = plank;
        }

        for (int i = 0; i < K; i++) {
            POINTS[i] = new Vector2();
        }

        contPoints = new Vector2[bodies.size + 4];

        for (int i = 0; i < contPoints.length; i++) {
            contPoints[i] = new Vector2();
        }
        setCurrentSplineCurve();
    }

    /**
     * Creates the joints for this object.
     * <p>
     * This method is executed as part of activePhysics. This is the primary method to
     * override for custom physics objects.
     *
     * @param world Box2D world to store joints
     * @return true if object allocation succeeded
     */
    @Override
    protected boolean createJoints(World world) {
        assert bodies.size > 0;

        if (state != RopeState.COMPLETE) return true;
        Vector2 anchor1 = new Vector2(linksize / 2, 0);
        Vector2 anchor2 = new Vector2(-linksize / 2, 0);

        // Create the leftmost anchor
        // Normally, we would do this in constructor, but we have
        // reasons to not add the anchor to the bodies list.
        Vector2 pos = bodies.get(0).getPosition();
        pos.x -= linksize / 2;

        // Definition for a revolute joint
        RevoluteJointDef jointDef = new RevoluteJointDef();
        // Link the planks together
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        for (int i = 0; i < bodies.size - 1; i++) {
            // Look at what we did above and join the planks
            Obstacle curr = bodies.get(i);
            Obstacle next = bodies.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            Joint joint = world.createJoint(jointDef);
            joints.add(joint);
        }

        // Create the rightmost anchor
        Obstacle last = bodies.get(bodies.size - 1);

        pos = last.getPosition();
        pos.x += linksize / 2;
        return true;
    }

    /**
     * Destroys the physics Body(s) of this object if applicable,
     * removing them from the world.
     *
     * @param world Box2D world that stores body
     */
    public void deactivatePhysics(World world) {
        super.deactivatePhysics(world);
    }

    /**
     * Sets the texture for the individual planks
     *
     * @param texture the texture for the individual planks
     */
    public void setTexture(TextureRegion texture) {
        for (Obstacle body : bodies) {
            ((SimpleObstacle) body).setTexture(texture);
        }
    }

    /**
     * Returns the texture for the individual planks
     *
     * @return the texture for the individual planks
     */
    public TextureRegion getTexture() {
        if (bodies.size == 0) {
            return null;
        }
        return ((SimpleObstacle) bodies.get(0)).getTexture();
    }


    private void extractContPoints() {
        int startIndex = state == RopeState.RIGHT_BROKEN ? 1 : 2;
        int endIndex = state == RopeState.LEFT_BROKEN ? contPoints.length-2  : contPoints.length - 3;

        for (int i = startIndex; i <= endIndex; i++) {
            int cur = state == RopeState.RIGHT_BROKEN ? i-1 : i-2;
            Vector2 pos = bodies.get(cur).getPosition();
            contPoints[i].set(pos.x * drawScale.x, pos.y * drawScale.y);
        }
        if (state == RopeState.LEFT_BROKEN){
            contPoints[contPoints.length -1] = contPoints[contPoints.length -2];
        }
        if (state == RopeState.RIGHT_BROKEN){
            contPoints[0] = contPoints[1];
        }
    }

    private void setCurrentSplineCurve() {
        extractContPoints();
        if (splineCurve == null)
            splineCurve = new CatmullRomSpline<>(contPoints, false);
        else
            splineCurve.set(contPoints, false);

    }



    public Rope[] cut(final Vector2 pos, World w){
        Rope[] cutRopes = new Rope[2];
        Arrays.sort(planks, new Comparator<Obstacle>() {
            @Override
            public int compare(Obstacle o1, Obstacle o2) {
                return (int)(o1.getPosition().dst2(pos) - o2.getPosition().dst2(pos));
            }
        });
        assert planks.length >=2;
        Body bodyA = planks[0].getBody();
        Body bodyB = planks[1].getBody();
        for(Joint j: joints){
            if ((j.getBodyA() == bodyA && j.getBodyB() == bodyB) ||
                    (j.getBodyA() == bodyB && j.getBodyB() == bodyA)){
                w.destroyJoint(j);
            }

        }
        ArrayList<BoxObstacle> left = new ArrayList<>();
        ArrayList<BoxObstacle> right = new ArrayList<>();
        for (Obstacle obstacle: bodies){
            left.add((BoxObstacle) obstacle);
            if (obstacle.getBody() == bodyA || obstacle.getBody() == bodyB  )
                break;
        }
        for(int i = left.size(); i < bodies.size; i++){
            right.add((BoxObstacle) bodies.get(i));
        }
        Rope l =  new Rope(left.toArray(new BoxObstacle[left.size()]), RopeState.LEFT_BROKEN);
        l.setStart(contPoints[0], true);
        l.setDrawScale(this.drawScale);
        cutRopes[0] = l;

        Rope r =  new Rope(right.toArray(new BoxObstacle[right.size()]), RopeState.RIGHT_BROKEN);
        r.setEnd(contPoints[contPoints.length -1], true);
        r.setDrawScale(this.drawScale);
        cutRopes[1] = r;

        this.bodyinfo.active = false;
        return cutRopes;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {

        // Delegate to components
        setCurrentSplineCurve();
        canvas.drawCatmullRom(splineCurve, K, POINTS);
    }

    /**
     *
     * @return retrieve the last link in the rope
     */
    public Body getLastLink() {
        return (bodies.size > 0 ? bodies.get(bodies.size - 1).getBody() : null);
    }

    public void setStart(Vector2 start, boolean scaled) {
        if (!scaled) {
            contPoints[0].set(start.x * drawScale.x, start.y * drawScale.y);
            contPoints[1].set(start.x * drawScale.x, start.y * drawScale.y);
        }
        else {
            contPoints[0].set(start.x, start.y);
            contPoints[1].set(start.x, start.y);
        }
    }

    public void setEnd(Vector2 end, boolean scaled) {
        if (!scaled) {
            contPoints[contPoints.length - 1].set(end.x * drawScale.x, end.y * drawScale.y);
            contPoints[contPoints.length - 2].set(end.x * drawScale.x, end.y * drawScale.y);
        }
        else {
            contPoints[contPoints.length - 1].set(end.x, end.y);
            contPoints[contPoints.length - 2].set(end.x, end.y);
        }
    }
}
