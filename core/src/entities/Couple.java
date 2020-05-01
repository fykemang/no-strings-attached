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
    private final NpcPerson l;
    private final NpcPerson r;
    private NpcRope trampoline;
    private final Stone leftTile;
    private final Stone rightTile;

    public Couple(float x1, float y1, float x2, float y2, String type1, String type2, TextureRegion avatar1, TextureRegion avatar2, Vector2 drawScale,
                  Stone leftTile, Stone rightTile, int id) {
        this.drawScale = drawScale;
        this.l = createAvatar(x1, y1, avatar1, true);
        this.r = createAvatar(x2, y2, avatar2, false);
        if(leftTile.isRotating){
            leftTile.setRotationData(r.getPosition(), l.getPosition());

        }else if(rightTile.isRotating){
            rightTile.setRotationData(l.getPosition(), r.getPosition());
        }
        l.setType(type1);
        r.setType(type2);
        l.setCouple(r);
        r.setCouple(l);
        l.setIsFacingRight(true);
        r.setIsFacingRight(false);
        this.trampoline = new NpcRope(x1 + l.getWidth() / 1.5f - 0.1f, y1 + 0.1f, x2 - r.getWidth() / 1.5f + 0.2f, y2 + 0.1f, 4f / drawScale.y, id, 0.2f);
        this.trampoline.setDrawScale(drawScale);
        Filter trampolineFilter = new Filter();
        trampolineFilter.categoryBits = CollisionFilterConstants.CATEGORY_NPC_ROPE.getID();
        trampolineFilter.maskBits = CollisionFilterConstants.MASK_NPC_ROPE.getID();
        trampoline.setFilterDataAll(trampolineFilter);
        this.bodies.add(trampoline);
        this.bodies.add(l);
        this.bodies.add(r);
        this.leftTile = leftTile;
        this.rightTile = rightTile;
        setName("couples" + id);
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public NpcPerson createAvatar(float x, float y, TextureRegion t, boolean l) {
        float dWidth = t instanceof FilmStrip ? t.getRegionWidth() / drawScale.x / 2.2f : t.getRegionWidth() / drawScale.x;
        float dHeight = t.getRegionHeight() / drawScale.y;
        NpcPerson avatar = new NpcPerson(x, y, dWidth, dHeight, "npc", "npcSensor", l);
        avatar.setAttached(true);
        avatar.setBodyType(BodyDef.BodyType.KinematicBody);
        setDensity(10f);
        setLinearDamping(100f);
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
        anchor2.x = -trampoline.lWidth / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));

        jointDef.bodyA = trampoline.getLastLink();
        jointDef.bodyB = r.getBody();
        anchor1.x = r.getWidth() / 2;
        anchor2.x = -trampoline.lWidth / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));
        return true;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        l.setLinearVelocity(leftTile.getLinearVelocity());
        r.setLinearVelocity(rightTile.getLinearVelocity());
        if (leftTile.isRotating) {
            leftTile.setPosData(l.getPosition());
            if (l.left) {
                trampoline.moveStart(l.getCloserAttachPoint(), false);
                trampoline.moveEnd(r.getCloserAttachPoint(), false);
            } else {
                trampoline.moveStart(r.getCloserAttachPoint(), false);
                trampoline.moveEnd(l.getCloserAttachPoint(), false);
            }
        }else if(rightTile.isRotating){
            rightTile.setPosData(r.getPosition());
            if (l.left) {
                trampoline.moveStart(l.getCloserAttachPoint(), false);
                trampoline.moveEnd(r.getCloserAttachPoint(), false);
            } else {
                trampoline.moveStart(r.getCloserAttachPoint(), false);
                trampoline.moveEnd(l.getCloserAttachPoint(), false);
            }

        }
    }

    public void breakBond(NpcRope leftFragment, NpcRope rightFragment) {
        l.setAttached(false);
        r.setAttached(false);
        this.bodies.add(leftFragment);
        this.bodies.add(rightFragment);
    }

    public NpcRope getRope() {
        return l.isAttached() ? trampoline : null;
    }

}
