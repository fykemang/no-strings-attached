package platform;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import obstacle.ComplexObstacle;
import util.FilmStrip;

/**
 * A obstacle made up of two dudes and a trampoline
 */
public class Couple extends ComplexObstacle {
    private NpcPerson l;
    private NpcPerson r;
    private Rope trampoline;
    private Rope trampLeft;
    private Rope trampRight;
    private TextureRegion trampolineTexture;
    private Stone leftTile;
    private Stone rightTile;

    final short CATEGORY_NPC = 0x0001;  // 0000000000000001 in binary
    final short CATEGORY_TILE = 0x0002; // 0000000000000010 in binary
    final short CATEGORY_ROPE = 0x0004; // 0000000000000100 in binary
//    final short MASK_ROPE = CATEGORY_MONSTER | CATEGORY_SCENERY;
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
    public Couple(float x1, float y1, float x2, float y2, TextureRegion avatar1, TextureRegion avatar2, TextureRegion trampolineTexture, Vector2 drawScale,
                  Stone leftTile, Stone rightTile, int id) {
        this.drawScale = drawScale;
        this.trampolineTexture = trampolineTexture;
        this.l = createAvatar(x1, y1, avatar1);
        this.r = createAvatar(x2, y2, avatar2);

        l.setCouple(r);
        r.setCouple(l);
        this.trampoline = new Rope(x1 + l.getWidth() / 1.5f + 0.1f, y1 + 0.1f, x2 - r.getWidth() / 1.5f - 0.1f, y2 + 0.1f, 0.2f, trampolineTexture.getRegionHeight() / drawScale.y, id);
        this.trampoline.setTexture(trampolineTexture);
        this.trampoline.setDrawScale(drawScale);
        this.bodies.add(trampoline);
        this.bodies.add(l);
        this.bodies.add(r);
        this.leftTile = leftTile;
        this.rightTile = rightTile;
        setName("couples" + id);



//        Filter RopeFilter = new Filter();
//        RopeFilter.categoryBits = CATEGORY_ROPE;
//        playerRopeFilter.maskBits = CollisionFilterConstants.MASK_PLAYER_ROPE.getID();
//        playerRope.setFilterDataAll(playerRopeFilter);
//        playerRope.setName("rope");
//        playerRope.setDrawScale(scale);
//        addObject(playerRope);

    }

    /**
     * @param x
     * @param y
     * @return
     */
    public NpcPerson createAvatar(float x, float y, TextureRegion t) {
        float dWidth = t instanceof FilmStrip ? t.getRegionWidth() / drawScale.x / 2.2f : t.getRegionWidth() / drawScale.x;
        float dHeight = t.getRegionHeight() / drawScale.y;
        NpcPerson avatar = new NpcPerson(x, y, dWidth, dHeight, "npc", "npcSensor");
        avatar.setBodyType(BodyDef.BodyType.KinematicBody);

        setDensity(10f);
        setLinearDamping(100f);
        avatar.setFriction(100f);
        avatar.setPosition(x + avatar.getWidth() / 2 + 0.15f, y + avatar.getHeight() / 2);
        avatar.setDrawScale(drawScale);
        avatar.setTexture(t);
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
        anchor2.x = -trampoline.linkSize / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));

        jointDef.bodyA = trampoline.getLastLink();
        jointDef.bodyB = r.getBody();
        anchor1.x = r.getWidth() / 2;
        anchor2.x = -trampoline.linkSize / 2;
        jointDef.localAnchorA.set(anchor1);
        jointDef.localAnchorB.set(anchor2);
        joints.add(world.createJoint(jointDef));
        return true;
    }

    @Override
    public void update(float dt){
        super.update(dt);
        l.setLinearVelocity(leftTile.getLinearVelocity());
        r.setLinearVelocity(rightTile.getLinearVelocity());
        l.setAngularVelocity(leftTile.getAngularVelocity());
        r.setAngularVelocity(rightTile.getAngularVelocity());
//        trampoline.setStart(,false);
        if(l.getX() <= r.getX()){
            trampoline.setStart(l.getCloserAttachPoint(),false);
            trampoline.setEnd(r.getCloserAttachPoint(), false);
        }else{
            System.out.println("here");
            trampoline.moveEnd(l.getCloserAttachPoint(),false);
            trampoline.moveStart(r.getCloserAttachPoint(), false);
        }
    }

    public void breakBond(Rope l, Rope r) {
        this.trampLeft = l;
        this.trampRight = r;
        this.bodies.add(l);
        this.bodies.add(r);
    }

    public Rope getRope() {
        return trampoline;
    }
}
