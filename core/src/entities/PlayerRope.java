package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.BoxObstacle;
import obstacle.Obstacle;
import obstacle.SimpleObstacle;
import root.GameCanvas;

import java.util.ArrayList;

public class PlayerRope extends Rope {
    private static final float PLAYER_ROPE_DENSITY = 0.6f;
    private static final float SEGMENT_LENGTH = 0.21f;
    private ArrayList<BoxObstacle> segmentLayer;

    public PlayerRope(float x0, float y0, float x1, float y1, float ropeLength) {
        super(x0, y0, x1, y1, "player_rope", PLAYER_ROPE_DENSITY, ropeLength, SEGMENT_LENGTH, -1);
    }

    public float getNPC() {
        float x = contPoints[0].x;
        return x / drawScale.x;
    }

    @Override
    protected void initializeSegments() {
        Vector2 norm = new Vector2(dimension);
        segmentLayer = new ArrayList<>();
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
            BoxObstacle plank = new BoxObstacle(pos.x, pos.y, lWidth * 2.5f, lWidth);
            plank.setDensity(density);
            bodies.add(plank);
            segmentLayer.add(plank);
        }

        for (int i = 0; i < MAX_DRAW_POINTS; i++) {
            points[i] = new Vector2();
        }

        contPoints = new Vector2[segmentLayer.size() + 2];

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
        assert segmentLayer.size() > 0;
        float anc = this.lWidth/2;
        Vector2 anchor1 = new Vector2(anc / 2, 0);
        Vector2 anchor2 = new Vector2(-anc / 2, 0);

        RevoluteJointDef jointDef = new RevoluteJointDef();

        for (int i = 0; i < segmentLayer.size() - 1; i++) {
            Obstacle curr = segmentLayer.get(i);
            Obstacle next = segmentLayer.get(i + 1);
            jointDef.bodyA = curr.getBody();
            jointDef.bodyB = next.getBody();
            jointDef.localAnchorA.set(anchor1);
            jointDef.localAnchorB.set(anchor2);
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

    @Override
    void extractContPoints() {
        int startIndex = 1;
        int endIndex = contPoints.length - 1;

        for (int i = startIndex; i < endIndex; i++) {
            Vector2 pos = segmentLayer.get(i - 1).getPosition();
            contPoints[i].set(pos.x * drawScale.x, pos.y * drawScale.y);
        }

        contPoints[0] = contPoints[1];
        contPoints[contPoints.length - 1] = contPoints[contPoints.length - 2];
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
        canvas.drawCatmullRom(splineCurve, tint, MAX_DRAW_POINTS, points);
    }

    /**
     * @return retrieve the last link in the rope
     */
    @Override
    public Body getLastLink() {
        return segmentLayer.size() > 0 ? segmentLayer.get(segmentLayer.size() - 1).getBody() : null;
    }
}
