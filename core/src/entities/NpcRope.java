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
package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
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
public class NpcRope extends Rope {
    /**
     * The debug name for the entire obstacle
     */
    private static final String ROPE_NAME = "rope";
    /**
     * The density of each plank in the bridge
     */
    private static final float NPC_ROPE_DENSITY = 1.0f;

    private Vector2 approxNorm;

    private ArrayList<WheelObstacle> upperLayer;
    private ArrayList<WheelObstacle> lowerLayer;
    public RopeState state;
    private boolean destroy = false;

    public enum RopeState {
        LEFT_BROKEN, RIGHT_BROKEN, COMPLETE
    }

    public NpcRope(ArrayList<WheelObstacle> upper, ArrayList<WheelObstacle> lower, RopeState state) {
        super();
        this.state = state;
        this.lowerLayer = lower;
        this.upperLayer = upper;
        for (WheelObstacle o : upper) {
            bodies.add(o);
        }
        for (WheelObstacle o : lower) {
            bodies.add(o);
        }
        for (int i = 0; i < MAX_DRAW_POINTS; i++) {
            points[i] = new Vector2();
        }
        contPoints = new Vector2[upper.size() + 2];

        for (int i = 0; i < contPoints.length; i++) {
            contPoints[i] = new Vector2();
        }
        if (state==RopeState.COMPLETE) {
            float dx = contPoints[contPoints.length - 1].x - contPoints[0].x;
            float dy = contPoints[contPoints.length - 1].y - contPoints[0].y;

            approxNorm = new Vector2(-dy, dx);
            approxNorm.nor();
        }
        setCurrentSplineCurve();
    }

    public NpcRope(float x0, float y0, float x1, float y1, float lheight, int id, float blobDiameter, float ropeLength) {
        super(x0, y0, x1, y1, ROPE_NAME + id, NPC_ROPE_DENSITY, ropeLength, blobDiameter, id);
        state = RopeState.COMPLETE;
    }

    /**
     * Creates a new rope bridge with the given anchors.
     *
     * @param x0      The x position of the left anchor
     * @param y0      The y position of the left anchor
     * @param x1      The x position of the right anchor
     * @param y1      The y position of the right anchor
     * @param lheight The bridge thickness
     */
    public NpcRope(float x0, float y0, float x1, float y1, float lheight, int id, float lWidth) {
        super(x0, y0, x1, y1, ROPE_NAME + id, NPC_ROPE_DENSITY, new Vector2(x1 - x0, y1 - y0).len(), lWidth, id);
        state = RopeState.COMPLETE;
    }

    void initializeSegments() {
        upperLayer = new ArrayList<>();
        lowerLayer = new ArrayList<>();
        Vector2 norm = new Vector2(dimension);
        float blobRadius = lWidth / 2;
        norm.nor();
        // If too small, only make one plank.
        int nLinks = (int) (length / lWidth) - 4;
        if (nLinks <= 1) {
            nLinks = 1;
            lWidth = length;
            spacing = 0;
        } else {
            spacing = length - nLinks * lWidth;
            spacing /= (nLinks - 1);
        }

        Vector2 pos = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (lWidth + spacing) + lWidth / 2.0f;
            pos.set(norm);
            pos.scl(t);
            pos.add(getX(), getY());
            Blob blob = new Blob(pos.x, pos.y, blobRadius, id);
            blob.setDensity(density);
            bodies.add(blob);
            upperLayer.add(blob);
        }

        Vector2 pos2 = new Vector2();
        for (int i = 0; i < nLinks - 1; i++) {
            float t = i * (lWidth + spacing) + lWidth / 2.0f;
            pos2.set(norm);
            pos2.scl(t);
            pos2.add(getX(), getY());
            Blob blob = new Blob(pos2.x, pos2.y - 0.2f, blobRadius, id);
            blob.setDensity(density);
            bodies.add(blob);
            lowerLayer.add(blob);
        }


        for (int i = 0; i < MAX_DRAW_POINTS; i++) {
            points[i] = new Vector2();
        }

        contPoints = new Vector2[upperLayer.size() + 2];

        for (int i = 0; i < contPoints.length; i++) {
            contPoints[i] = new Vector2();
        }
        float dx = contPoints[contPoints.length - 1].x - contPoints[0].x;
        float dy = contPoints[contPoints.length - 1].y - contPoints[0].y;

        approxNorm = new Vector2(-dy, dx);
        approxNorm.nor();
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
        float blobRadius = lWidth / 2;
        if (state != RopeState.COMPLETE) return true;
        Vector2 anchor1 = new Vector2(blobRadius / 2, 0);
        Vector2 anchor2 = new Vector2(-blobRadius / 2, 0);

        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.dampingRatio = 1f;
        jointDef.frequencyHz = 18f;

        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);

        for (int i = 0; i < upperLayer.size() - 2; i++) {
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


        Obstacle curr1 = upperLayer.get(upperLayer.size() - 1);
        Obstacle next1 = lowerLayer.get(lowerLayer.size() - 1);
        jointDef.bodyA = curr1.getBody();
        jointDef.bodyB = next1.getBody();
        Joint joint1 = world.createJoint(jointDef);
        joints.add(joint1);

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
            jointDef.length = 0.15f;
            Obstacle currl = lowerLayer.get(i);
            Obstacle nextl = lowerLayer.get(i + 1);
            jointDef.bodyA = currl.getBody();
            jointDef.bodyB = nextl.getBody();
            Joint jointl = world.createJoint(jointDef);
            joints.add(jointl);
        }

        for (int i = 0; i < upperLayer.size() - 1; i++) {
            jointDef.length = 0.2f;
            Obstacle top = upperLayer.get(i);
            Obstacle bottom = lowerLayer.get(i);
            jointDef.bodyA = top.getBody();
            jointDef.bodyB = bottom.getBody();
            Joint joint2 = world.createJoint(jointDef);
            joints.add(joint2);
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


    void extractContPoints() {
        int startIndex = 1;
        int endIndex = contPoints.length - 1;

        for (int i = startIndex; i < endIndex; i++) {
//            int cur = state == RopeState.RIGHT_BROKEN ? i : i - 1;
            Vector2 pos = upperLayer.get(i - 1).getPosition();
            contPoints[i].set(pos.x * drawScale.x, pos.y * drawScale.y);
        }


        contPoints[0] = contPoints[1];

        contPoints[contPoints.length - 1] = contPoints[contPoints.length - 2];
    }


    public NpcRope[] cut(final Vector2 pos, World w, float h) {
        if ((this.state == RopeState.RIGHT_BROKEN || this.state == RopeState.LEFT_BROKEN)) {
            return null;
        }
        NpcRope[] cutNpcRopes = new NpcRope[2];
        WheelObstacle closest = null;
        int index = 0;
        for (int i = 0; i < upperLayer.size(); i++) {
            WheelObstacle cur = upperLayer.get(i);
            if (closest == null) {
                closest = cur;
                index = i;
            } else {
                if (isCloser(cur, closest, pos, h)) {
                    closest = cur;
                    index = i;
                }
            }
        }

        int lowIdx = index >= lowerLayer.size() ? lowerLayer.size() - 1 : index;
        w.destroyBody(upperLayer.get(index).getBody());
        w.destroyBody(lowerLayer.get(lowIdx).getBody());

        upperLayer.remove(index);
        lowerLayer.remove(lowIdx);

        ArrayList<WheelObstacle> leftUpper = new ArrayList<>(upperLayer.subList(0, index));
        ArrayList<WheelObstacle> leftLower = new ArrayList<>(lowerLayer.subList(0, lowIdx));
        ArrayList<WheelObstacle> rightUpper = new ArrayList<>(upperLayer.subList(index, upperLayer.size()));
        ArrayList<WheelObstacle> rightLower = new ArrayList<>(lowerLayer.subList(lowIdx, lowerLayer.size()));

        NpcRope l = new NpcRope(leftUpper, leftLower, RopeState.LEFT_BROKEN);
        l.setStart(contPoints[0], true);
        l.setDrawScale(this.drawScale);
        cutNpcRopes[0] = l;

        NpcRope r = new NpcRope(rightUpper, rightLower, RopeState.RIGHT_BROKEN);
        r.setEnd(contPoints[contPoints.length - 1], true);
        r.setDrawScale(this.drawScale);
        cutNpcRopes[1] = r;

        this.bodyinfo.active = false;
        return cutNpcRopes;
    }


    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    @Override
    public void draw(GameCanvas canvas) {
        if(tint.a< 0.03f)
            return;
        // Delegate to components
        setCurrentSplineCurve();
        if (state != RopeState.COMPLETE) {
            tint.set(tint.r, tint.g, tint.b, tint.a * 0.975f);
        } else {
            float dx = contPoints[contPoints.length - 1].x - contPoints[0].x;
            float dy = contPoints[contPoints.length - 1].y - contPoints[0].y;
            approxNorm.set(-dy, dx);
            approxNorm.nor();
            setNorms();
        }
        canvas.drawCatmullRom(splineCurve, tint, MAX_DRAW_POINTS, points);
    }

    private void setNorms() {
        for (WheelObstacle b : upperLayer) {
            ((Blob) b).setNorm(approxNorm);
        }
        for (WheelObstacle b : lowerLayer) {
            ((Blob) b).setNorm(approxNorm);
        }
    }

    /**
     * @return retrieve the last link in the rope
     */
    public Body getLastLink() {
        return upperLayer.size() > 0 ? upperLayer.get(upperLayer.size() - 1).getBody() : null;
    }

    public void moveStart(Vector2 start, boolean scaled) {
        upperLayer.get(0).setPosition(start);
        setStart(start, scaled);
    }

    public void moveEnd(Vector2 end, boolean scaled) {
        upperLayer.get(upperLayer.size() - 1).setPosition(end);
        setEnd(end, scaled);
    }

    public boolean isBroken() {
        return this.state != RopeState.COMPLETE;
    }
}
