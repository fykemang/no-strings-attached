package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.*;
import root.GameCanvas;

import java.util.ArrayList;

public class PlayerRope extends ComplexObstacle {


    /**
     * The debug name for the entire obstacle
     */
    private static final String ROPE_NAME = "player_rope";
    /**
     * The density of each plank in the bridge
     */
    private static final float BASIC_DENSITY = 0.7f;

    // Dimension information
    /**
     * The size of the entire bridge
     */
    protected Vector2 dimension;
    /**
     * The spacing between each link
     */
    protected float spacing;
    private int ropeID;
    protected float length;

    private float lwidth;
    private float lheight;
    private CatmullRomSpline<Vector2> splineCurve;
    private String PLANK_NAME = "plank";

    Vector2[] contPoints;

    private final int K = 100;

    private final Vector2[] POINTS = new Vector2[K];
    private ArrayList<BoxObstacle> layer = new ArrayList<>();

    public PlayerRope(float x0, float y0, float x1, float y1, int id, float ropeLength) {
        super(x0, y0);
        setName(ROPE_NAME + id);
        this.lwidth = 0.2f;
        this.ropeID = id;
        dimension = new Vector2(x1 - x0, y1 - y0);
        this.length = ropeLength;
        initializePlanks();
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
    public PlayerRope(float x0, float y0, float x1, float y1, int id) {
        super(x0, y0);
        setName(ROPE_NAME + id);
        this.ropeID = id;
        // Compute the bridge length
        dimension = new Vector2(x1 - x0, y1 - y0);
        this.length = dimension.len();
        initializePlanks();
    }

    private void initializePlanks() {
        Vector2 norm = new Vector2(dimension);
        norm.nor();
        // If too small, only make one plank.
        int nLinks = (int) (length / lwidth) - 4;
        if (nLinks <= 1) {
            nLinks = 1;
            lwidth = length;
            spacing = 0;
        } else {
            spacing = length - nLinks * lwidth;
            spacing /= (nLinks - 1);
        }

        Vector2 pos = new Vector2();
        for (int i = 0; i < nLinks; i++) {
            float t = i * (lwidth + spacing) + lwidth / 2.0f;
            pos.set(norm);
            pos.scl(t);
            pos.add(getX(), getY());
            BoxObstacle plank = new BoxObstacle(pos.x, pos.y,lwidth * 2.5f, lwidth);
            plank.setDensity(BASIC_DENSITY);
            plank.setName(PLANK_NAME);
            bodies.add(plank);
            layer.add(plank);
        }

        for (int i = 0; i < K; i++) {
            POINTS[i] = new Vector2();
        }

        contPoints = new Vector2[layer.size() + 2];

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
        assert layer.size() > 0;
        float anc = this.lwidth / 2;
        Vector2 anchor1 = new Vector2(anc / 2, 0);
        Vector2 anchor2 = new Vector2(-anc / 2, 0);

        RevoluteJointDef jointDef = new RevoluteJointDef();

        for (int i = 0; i < layer.size()-1; i++) {
            Obstacle curr = layer.get(i);
            Obstacle next = layer.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            jointDef.localAnchorA.set(anchor1);
            jointDef.localAnchorB.set(anchor2);
            jointDef.collideConnected = false;
            Joint joint = world.createJoint(jointDef);
            joints.add(joint);
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
        int startIndex = 1;
        int endIndex = contPoints.length - 1;

        for (int i = startIndex; i < endIndex; i++) {
//            int cur = state == RopeState.RIGHT_BROKEN ? i : i - 1;
            Vector2 pos = layer.get(i - 1).getPosition();
            contPoints[i].set(pos.x * drawScale.x, pos.y * drawScale.y);
        }


        contPoints[0] = contPoints[1];

        contPoints[contPoints.length - 1] = contPoints[contPoints.length - 2];
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
        return layer.size() > 0 ? layer.get(layer.size() - 1).getBody() : null;
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
