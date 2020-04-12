package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.ComplexObstacle;
import util.CollisionFilterConstants;
import util.FilmStrip;

/**
 * A obstacle made up of two dudes and a trampoline
 */
public class Couple extends ComplexObstacle {
    private Person l;
    private Person r;
    private Rope trampoline;
    private Rope trampLeft;
    private Rope trampRight;
    private TextureRegion trampolineTexture;
    private boolean isCut;

    public enum CoupleState {BROKEN, PAIRED}

    private CoupleState state;

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param avatar1
     * @param avatar2
     * @param trampolineTexture
     * @param drawScale
     */
    public Couple(float x1, float y1, float x2, float y2, TextureRegion avatar1, TextureRegion avatar2, TextureRegion trampolineTexture, Vector2 drawScale, int id) {
        this.drawScale = drawScale;
        this.trampolineTexture = trampolineTexture;
        this.l = createAvatar(x1, y1, avatar1);
        this.r = createAvatar(x2, y2, avatar2);
        this.trampoline = new Rope(x1 + l.getWidth() / 1.5f + 0.1f, y1 + 0.1f, x2 - r.getWidth() / 1.5f - 0.1f, y2 + 0.1f, trampolineTexture.getRegionHeight() / drawScale.y, id, 0.2f);
        this.trampoline.setTexture(trampolineTexture);
        this.trampoline.setDrawScale(drawScale);
        this.trampoline.setStart(l.getPosition().add(l.getWidth() / 1.5f, 0.1f), false);
        this.trampoline.setEnd(r.getPosition().add(-r.getWidth() / 1.5f, 0.1f), false);
        Filter trampolineFilter = new Filter();
        trampolineFilter.categoryBits = CollisionFilterConstants.CATEGORY_NPC_ROPE.getID();
        trampolineFilter.maskBits = CollisionFilterConstants.MASK_NPC_ROPE.getID();
        trampoline.setFilterDataAll(trampolineFilter);
        this.bodies.add(trampoline);
        this.bodies.add(l);
        this.bodies.add(r);
        assert (l.isAttached());
        assert (r.isAttached());
        setName("couples" + id);
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public Person createAvatar(float x, float y, TextureRegion t) {
        float dWidth = t instanceof FilmStrip ? t.getRegionWidth() / drawScale.x / 2.2f : t.getRegionWidth() / drawScale.x;
        float dHeight = t.getRegionHeight() / drawScale.y;
        Person avatar = new Person(x, y, dWidth, dHeight, "npc", "npcSensor");
        avatar.setAttached(true);
        avatar.setBodyType(BodyDef.BodyType.KinematicBody);
        avatar.setPosition(x + avatar.getWidth() / 2 + 0.15f, y + avatar.getHeight() / 2);
        avatar.setDrawScale(drawScale);
        avatar.setTexture(t);
        Filter avatarFilter = new Filter();
        avatarFilter.categoryBits = CollisionFilterConstants.CATEGORY_NPC.getID();
        avatarFilter.maskBits = CollisionFilterConstants.MASK_NPC.getID();
        avatar.setFilterData(avatarFilter);
        return avatar;
    }

    @Override
    protected boolean createJoints(World world) {
        Vector2 anchor1 = new Vector2();
        Vector2 anchor2 = new Vector2();
        RevoluteJointDef jointDef = new RevoluteJointDef();

        jointDef.bodyA = l.getBody();
        jointDef.bodyB = trampoline.getBody();
        anchor1.x = l.getWidth() / 2;
        anchor2.x = -trampoline.blobDiameter / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));

        jointDef.bodyA = trampoline.getLastLink();
        jointDef.bodyB = r.getBody();
        anchor1.x = r.getWidth() / 2;
        anchor2.x = -trampoline.blobDiameter / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));
        return true;
    }

    public void breakBond(Rope leftFragment, Rope rightFragment) {
        this.trampLeft = leftFragment;
        this.trampRight = rightFragment;
        this.bodies.add(leftFragment);
        this.bodies.add(rightFragment);
        l.setAttached(false);
        r.setAttached(false);
    }

    public Rope getRope() {
        return trampoline;
    }

}
