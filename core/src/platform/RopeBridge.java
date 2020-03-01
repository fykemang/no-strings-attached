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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.*;
import root.GameCanvas;
import softBody.StringObject;

/**
 * A bridge with planks connected by revolute joints.
 * <p>
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class RopeBridge extends ComplexObstacle {
    /**
     * The debug name for the entire obstacle
     */
    private static final String BRIDGE_NAME = "bridge";
    /**
     * The debug name for each plank
     */
    private static final String PLANK_NAME = "barrier";
    /**
     * The debug name for each anchor pin
     */
    private static final String BRIDGE_PIN_NAME = "pin";
    /**
     * The radius of each anchor pin
     */
    private static final float BRIDGE_PIN_RADIUS = 0.1f;
    /**
     * The density of each plank in the bridge
     */
    private static final float BASIC_DENSITY = 1.0f;

    // Invisible anchor objects
    /**
     * The left side of the bridge
     */
    private WheelObstacle start = null;
    /**
     * The right side of the bridge
     */
    private WheelObstacle finish = null;

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

    private StringObject rope;

    Vector2[] contPoints;

    private final int K = 100;

    private Vector2[] POINTS = new Vector2[K];

    /**
     * Creates a new rope bridge at the given position.
     * <p>
     * This bridge is straight horizontal. The coordinates given are the
     * position of the leftmost anchor.
     *
     * @param x       The x position of the left anchor
     * @param y       The y position of the left anchor
     * @param width   The length of the bridge
     * @param lwidth  The plank length
     * @param lheight The bridge thickness
     */
    public RopeBridge(float x, float y, float width, float lwidth, float lheight) {
        this(x, y, x + width, y, lwidth, lheight);
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
    public RopeBridge(float x0, float y0, float x1, float y1, float lwidth, float lheight) {
        super(x0, y0);
        setName(BRIDGE_NAME);

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
//            spacing = 0;
        } else {
//            spacing = length - nLinks * linksize;
//            spacing /= (nLinks - 1);
        }

        // Create the planks
        planksize.x = linksize;
        Vector2 pos = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (linksize + spacing) + linksize / 2.0f;
            pos.set(norm);
            pos.scl(t);
            pos.add(x0, y0);
            BoxObstacle plank = new BoxObstacle(pos.x, pos.y, planksize.x, planksize.y);
            plank.setName(PLANK_NAME + i);
            plank.setDensity(BASIC_DENSITY);
            bodies.add(plank);
        }

        for (int i = 0; i < K; i ++){
            POINTS[i] = new Vector2();
        }
        contPoints = new Vector2[bodies.size+2];
        for (int i = 0; i < contPoints.length; i++){
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
    protected boolean createJoints(World world) {
        assert bodies.size > 0;

        Vector2 anchor1 = new Vector2();
        Vector2 anchor2 = new Vector2(-linksize / 2, 0);

        // Create the leftmost anchor
        // Normally, we would do this in constructor, but we have
        // reasons to not add the anchor to the bodies list.
        Vector2 pos = bodies.get(0).getPosition();
        pos.x -= linksize / 2;
        contPoints[0].set(pos.x * drawScale.x, pos.y * drawScale.y);
        start = new WheelObstacle(pos.x, pos.y, BRIDGE_PIN_RADIUS);
        start.setName(BRIDGE_PIN_NAME + 0);
        start.setDensity(BASIC_DENSITY);
        start.setBodyType(BodyDef.BodyType.StaticBody);
        start.activatePhysics(world);

        // Definition for a revolute joint
        RevoluteJointDef jointDef = new RevoluteJointDef();

        // Initial joint
        jointDef.bodyA = start.getBody();
        jointDef.bodyB = bodies.get(0).getBody();
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        jointDef.collideConnected = false;
        Joint joint = world.createJoint(jointDef);
        joints.add(joint);

        // Link the planks together
        anchor1.x = linksize / 2;
        for (int i = 0; i < bodies.size - 1; i++) {
            //#region INSERT CODE HERE
            // Look at what we did above and join the planks
            Obstacle curr = bodies.get(i);
            Obstacle next = bodies.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            jointDef.localAnchorA.set(anchor1);
            joint = world.createJoint(jointDef);
            joints.add(joint);
            //#endregion
        }

        // Create the rightmost anchor
        Obstacle last = bodies.get(bodies.size - 1);

        pos = last.getPosition();
        pos.x += linksize / 2;
        finish = new WheelObstacle(pos.x, pos.y, BRIDGE_PIN_RADIUS);
        finish.setName(BRIDGE_PIN_NAME + 1);
        finish.setDensity(BASIC_DENSITY);
        finish.setBodyType(BodyDef.BodyType.StaticBody);
        finish.activatePhysics(world);

        // Final joint
        anchor2.x = 0;
        jointDef.bodyA = last.getBody();
        jointDef.bodyB = finish.getBody();
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joint = world.createJoint(jointDef);
        joints.add(joint);

        contPoints[contPoints.length-1].set(pos.x * drawScale.x, pos.y * drawScale.y);
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
        if (start != null) {
            start.deactivatePhysics(world);
        }
        if (finish != null) {
            finish.deactivatePhysics(world);
        }
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


    private void extractContPoints(){

        for (int i = 1; i < contPoints.length-1; i++) {
            Vector2 pos = bodies.get(i-1).getPosition();
            contPoints[i].set(pos.x * drawScale.x , pos.y * drawScale.y);
        }
    }

    private void setCurrentSplineCurve(){
        extractContPoints();
        if (splineCurve == null)
            splineCurve = new CatmullRomSpline<>(contPoints, true);
        else
            splineCurve.set(contPoints, true);

    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {

        for (Obstacle obj : bodies) {

            obj.draw(canvas);
        }
        // Delegate to components
        setCurrentSplineCurve();
        canvas.drawCatmullRom(splineCurve, K, POINTS);

    }
}