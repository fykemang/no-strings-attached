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
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import obstacle.ComplexObstacle;
import obstacle.Obstacle;
import obstacle.SimpleObstacle;
import obstacle.WheelObstacle;
import root.GameCanvas;

import java.util.ArrayList;

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
     * The density of each plank in the bridge
     */
    private static final float BASIC_DENSITY = 1.0f;

    // Dimension information
    /**
     * The size of the entire bridge
     */
    protected Vector2 dimension;
    /**
     * The size of a single bloc
     */
    protected Vector2 blobSize;
    /**
     * The length of each link
     */
    protected float linkSize;
    /**
     * The spacing between each link
     */
    protected float spacing;
    protected float length;

    private CatmullRomSpline<Vector2> splineCurve;

    Vector2[] contPoints;

    private final int K = 100;

    private Vector2[] POINTS = new Vector2[K];
    private ArrayList<WheelObstacle> upperLayer = new ArrayList<>();
    private ArrayList<WheelObstacle> lowerLayer = new ArrayList<>();
    private boolean shouldDraw;
    public RopeState state;

    public enum RopeState {
        LEFT_BROKEN, RIGHT_BROKEN, COMPLETE
    }

    public Rope(ArrayList<WheelObstacle> upper, ArrayList<WheelObstacle> lower, RopeState state) {
        this.shouldDraw = true;
        this.state = state;
        this.lowerLayer = lower;
        this.upperLayer = upper;
        for (WheelObstacle o : upper) {
            bodies.add(o);
        }
        for (WheelObstacle o : lower) {
            bodies.add(o);
        }
        for (int i = 0; i < K; i++) {
            POINTS[i] = new Vector2();
        }
        contPoints = new Vector2[bodies.size / 2 + 3];

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
        this.shouldDraw = true;
        setName(ROPE_NAME + id);
        state = RopeState.COMPLETE;
        blobSize = new Vector2(0.2f, 0.2f);
        linkSize = 0.2f;

        // Compute the bridge length
        dimension = new Vector2(x1 - x0, y1 - y0);
        this.length = dimension.len();
        Vector2 norm = new Vector2(dimension);
        norm.nor();

        // If too small, only make one plank.
        int nLinks = (int) (length / linkSize) - 4;
        if (nLinks <= 1) {
            nLinks = 1;
            linkSize = length;
            spacing = 0;
        } else {
            spacing = length - nLinks * linkSize;
            spacing /= (nLinks - 1);
        }

        blobSize.x = linkSize;
        Vector2 pos = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (linkSize + spacing) + linkSize / 2.0f;
            pos.set(norm);
            pos.scl(t);
            pos.add(x0, y0);
            Blob blob = new Blob(pos.x, pos.y, 0.1f, id);
            blob.setDensity(BASIC_DENSITY);
            bodies.add(blob);
            upperLayer.add(blob);
        }

        Vector2 pos2 = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (linkSize + spacing) + linkSize / 2.0f;
            pos2.set(norm);
            pos2.scl(t);
            pos2.add(x0, y0);
            Blob blob = new Blob(pos2.x, pos2.y - 0.2f, 0.1f, id);
            blob.setDensity(BASIC_DENSITY);
            bodies.add(blob);
            lowerLayer.add(blob);
        }


        for (int i = 0; i < K; i++) {
            POINTS[i] = new Vector2();
        }

        contPoints = new Vector2[upperLayer.size() + 4];

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
        assert upperLayer.size() > 0;
        linkSize = 0.1f;
        if (state != RopeState.COMPLETE) return true;
        Vector2 anchor1 = new Vector2(linkSize / 2, 0);
        Vector2 anchor2 = new Vector2(-linkSize / 2, 0);

        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.dampingRatio = 1f;
        jointDef.frequencyHz = 18f;

        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        for (int i = 0; i < upperLayer.size() - 1; i++) {
            jointDef.length = 0.05f;
            // Look at what we did above and join the planks
            Obstacle curr = upperLayer.get(i);
            Obstacle next = upperLayer.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            Joint joint = world.createJoint(jointDef);
            joints.add(joint);
        }

        for (int i = 0; i < lowerLayer.size() - 1; i++) {
            jointDef.length = 0.2f;
            Obstacle currl = lowerLayer.get(i);
            Obstacle nextl = lowerLayer.get(i + 1);
            jointDef.bodyA = currl.getBody();
            jointDef.bodyB = nextl.getBody();
            Joint jointl = world.createJoint(jointDef);
            joints.add(jointl);
        }

        for (int i = 0; i < upperLayer.size(); i++) {
            jointDef.length = 0.2f;
            Obstacle top = upperLayer.get(i);
            Obstacle bottom = lowerLayer.get(i);
            jointDef.bodyA = top.getBody();
            jointDef.bodyB = bottom.getBody();
            Joint joint2 = world.createJoint(jointDef);
            joints.add(joint2);
        }

        for (int i = 0; i < upperLayer.size() - 1; i++) {
            jointDef.length = 0.3f;
            // Look at what we did above and join the planks
            Obstacle curr = upperLayer.get(i);
            Obstacle next = lowerLayer.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            Joint joint = world.createJoint(jointDef);
            joints.add(joint);

            Obstacle curr1 = upperLayer.get(i + 1);
            Obstacle next1 = lowerLayer.get(i);
            jointDef.bodyA = curr1.getBody();
            jointDef.bodyB = next1.getBody();
            Joint joint1 = world.createJoint(jointDef);
            joints.add(joint1);
        }

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
        int endIndex = state == RopeState.LEFT_BROKEN ? contPoints.length - 2 : contPoints.length - 3;

        for (int i = startIndex; i <= endIndex; i++) {
            int cur = state == RopeState.RIGHT_BROKEN ? i - 1 : i - 2;
            Vector2 pos = upperLayer.get(cur).getPosition();
            contPoints[i].set(pos.x * drawScale.x, pos.y * drawScale.y);
        }

        if (state == RopeState.LEFT_BROKEN) {
            contPoints[contPoints.length - 1] = contPoints[contPoints.length - 2];
        }

        if (state == RopeState.RIGHT_BROKEN) {
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

    private boolean isCloser(WheelObstacle a, WheelObstacle b, Vector2 pos) {
        return a.getPosition().dst2(pos) < b.getPosition().dst2(pos);
    }

    public Rope[] cut(final Vector2 pos, World w) {
        if ((this.state == RopeState.RIGHT_BROKEN || this.state == RopeState.LEFT_BROKEN)) {
            return null;
        }
        Rope[] cutRopes = new Rope[2];
        WheelObstacle closest = null;
        int index = 0;
        for (int i = 0; i < upperLayer.size(); i++) {
            WheelObstacle cur = upperLayer.get(i);
            if (closest == null) {
                closest = cur;
                index = i;
            } else {
                if (isCloser(cur, closest, pos)) {
                    closest = cur;
                    index = i;
                }
            }
        }

        w.destroyBody(upperLayer.get(index).getBody());
        w.destroyBody(lowerLayer.get(index).getBody());
        upperLayer.get(index).markRemoved(true);
        lowerLayer.get(index).markRemoved(true);

        upperLayer.remove(index);
        lowerLayer.remove(index);

        ArrayList<WheelObstacle> leftUpper = new ArrayList<>(upperLayer.subList(0, index));
        ArrayList<WheelObstacle> leftLower = new ArrayList<>(lowerLayer.subList(0, index));

        ArrayList<WheelObstacle> rightUpper = new ArrayList<>(upperLayer.subList(index + 1, upperLayer.size()));
        ArrayList<WheelObstacle> rightLower = new ArrayList<>(lowerLayer.subList(index + 1, lowerLayer.size()));

        Rope l = new Rope(leftUpper, leftLower, RopeState.LEFT_BROKEN);
        l.setStart(contPoints[0], true);
        l.setDrawScale(this.drawScale);
        cutRopes[0] = l;

        Rope r = new Rope(rightUpper, rightLower, RopeState.RIGHT_BROKEN);
        r.setEnd(contPoints[contPoints.length - 1], true);
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
     * @return retrieve the last link in the rope
     */
    public Body getLastLink() {
        return upperLayer.size() > 0 ? upperLayer.get(upperLayer.size() - 1).getBody() : null;
    }

    public void setStart(Vector2 start, boolean scaled) {
        if (!scaled) {
            contPoints[0].set(start.x * drawScale.x, start.y * drawScale.y);
            contPoints[1].set(start.x * drawScale.x, start.y * drawScale.y);
        } else {
            contPoints[0].set(start.x, start.y);
            contPoints[1].set(start.x, start.y);
        }
    }

    public void setEnd(Vector2 end, boolean scaled) {
        if (!scaled) {
            contPoints[contPoints.length - 1].set(end.x * drawScale.x, end.y * drawScale.y);
            contPoints[contPoints.length - 2].set(end.x * drawScale.x, end.y * drawScale.y);
        } else {
            contPoints[contPoints.length - 1].set(end.x, end.y);
            contPoints[contPoints.length - 2].set(end.x, end.y);
        }
    }

    public float getLength() {
        return length;
    }
}
