/*
 * DudeModel.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import obstacle.CapsuleObstacle;
import obstacle.Obstacle;
import root.GameCanvas;
import util.FilmStrip;

import java.util.ArrayList;

/**
 * Player avatar for the platform game.
 * <p>
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Person extends CapsuleObstacle {
    // Physics constants
    /**
     * The density of the character
     */
    private static final float PLAYER_DENSITY = 1.5f;
    /**
     * The factor to multiply by the input
     */
    private static final float PLAYER_FORCE = 20.0f;
    /**
     * The amount to slow the character down
     */
    private static final float PLAYER_DAMPING = 100.0f;
    /**
     * The dude is not a slippery one
     */
    private static final float PLAYER_FRICTION = 0.05f;
    /**
     * The maximum horizontal character speed
     */
    private static final float PLAYER_MAX_HORIZONTAL_SPEED = 4.5f;
    /**
     * The maximum vertical character speed
     */
    private static final float PLAYER_MAX_VERTICAL_SPEED = 13f;
    /**
     * The impulse for the character jump
     */
    private static final float PLAYER_JUMP = 10f;

    private static final float FRICTION = 0.6f;

    private static final float EPSILON = 0.03f;

    private static final float VERTICAL_EPSILON = 10f;
    /**
     * Cooldown (in animation frames) for jumping
     */
    private static final int JUMP_COOLDOWN = 30;
    /**
     * Height of the sensor attached to the player's feet
     */
    private static final float SENSOR_HEIGHT = 0.05f;

    // This is to fit the image to a tigher hitbox
    /**
     * The amount to shrink the body fixture (vertically) relative to the image
     */
    private static final float VSHRINK = 0.22f;
    /**
     * The amount to shrink the body fixture (horizontally) relative to the image
     */
    private static final float HSHRINK = 0.22f;
    /**
     * The amount to shrink the sensor fixture (horizontally) relative to the image
     */
    private static final float SSHRINK = 0.5f;
    /**
     * Cooldown (in animation frames) for shooting
     */
    private static final int SHOOT_COOLDOWN = 40;

    private int frameCount = 0;

    private boolean won;

    private boolean collectedAll;

    /**
     * The current horizontal movement of the character
     */
    private float horizontalMovement;
    /**
     * How long until we can jump again
     */
    private int jumpCooldown;
    /**
     * Whether we are actively jumping
     */
    private boolean isJumping;
    /**
     * Whether we are actively cutting
     */
    private boolean isCutting;

    private boolean isAlive;

    private boolean isTrampolining;

    private boolean isOnNpc;

    private boolean isGod;

    private NpcPerson onNpc;
    /**
     * Whether our feet are on the ground
     */
    private boolean isGrounded;
    /**
     * Ground sensor to represent our feet
     */
    private Fixture sensorFixture;
    /**
     * Whether we are actively shooting
     */
    private boolean isShooting;
    /**
     * How long until we can shoot again
     */
    private int shootCooldown;
    private PolygonShape sensorShape;
    private boolean canCut;

    public boolean isFallingBack() {
        return fallingBack;
    }

    public void setFallingBack(boolean fallingBack) {
        this.fallingBack = fallingBack;
    }

    private final String sensorName;
    private Person target;
    private NpcPerson canSwingTo;
    private final Vector2 trampolineDir;
    private final Vector2 trampolineForce;
    private static final float MAX_TRAMPOLINE = 1.55f;
    private static final float MIN_TRAMPOLINE = 0.1f;
    private final ArrayList<String> inventory;
    private boolean isAttached;
    private boolean released;
    private Color tint = new Color(Color.WHITE);
    private boolean fallingBack;

    /**
     * Which direction is the character facing
     */
    private boolean isFacingRight;
    private boolean isWalking;
    private float verticalMovement;
    /**
     * Cache for internal force calculations
     */
    private final Vector2 forceCache = new Vector2();
    private Joint swingJoint;
    private final Vector2 temp = new Vector2();

    private boolean onString = false;
    private boolean turned = false;

    /**
     * Returns left/right movement of this character.
     * <p>
     * This is the result of input times dude force.
     *
     * @return left/right movement of this character.
     */
    public float getHorizontalMovement() {
        return horizontalMovement;
    }

    /**
     * Sets left/right movement of this character.
     * <p>
     * This is the result of input times dude force.
     *
     * @param value left/right movement of this character.
     */
    public void setHorizontalMovement(float value) {
        // Change facing if appropriate
        if (value < 0) {
            turned = isFacingRight;
            isFacingRight = false;
            horizontalMovement = value;
        } else if (value > 0) {
            turned = !isFacingRight;
            isFacingRight = true;
            horizontalMovement = value;
        }

        if (isWalking && value == 0) {
            horizontalMovement = Math.abs(horizontalMovement * FRICTION) < EPSILON ? 0 : horizontalMovement * FRICTION;
        }

        isWalking = horizontalMovement != 0;
    }

    public void setVerticalMovement(float verticalMovement) {
        this.verticalMovement = verticalMovement;
    }

    public void addItem(String s) {
        inventory.add(s);
    }

    /**
     * Returns true if the dude is actively jumping.
     *
     * @return true if the dude is actively jumping.
     */
    public boolean isJumping() {
        return isJumping && isGrounded && jumpCooldown <= 0;
    }

    /**
     * Sets whether the dude is actively jumping.
     *
     * @param value whether the dude is actively jumping.
     */
    public void setJumping(boolean value) {
        isJumping = value;
    }

    /**
     * Returns true if the dude is on the ground.
     *
     * @return true if the dude is on the ground.
     */
    public boolean isGrounded() {
        return isGrounded;
    }

    /**
     * Returns true if the player is attempting to cut
     *
     * @return true if the player is cutting
     */
    public boolean isCutting() {
        return isCutting;
    }

    public void setIsFacingRight(boolean t) {
        isFacingRight = t;
    }

    public void setCutting(boolean isCutting) {
        this.isCutting = isCutting;
    }

    /**
     * Returns true if this character is facing right
     *
     * @return true if this character is facing right
     */
    public boolean isFacingRight() {
        return isFacingRight;
    }

    /**
     * Sets whether the dude is on the ground.
     *
     * @param value whether the dude is on the ground.
     */
    public void setGrounded(boolean value) {
        isGrounded = value;
    }

    /**
     * Returns how much force to apply to get the dude moving
     * <p>
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the dude moving
     */
    public float getForce() {
        return PLAYER_FORCE;
    }

    /**
     * Returns ow hard the brakes are applied to get a dude to stop moving
     *
     * @return ow hard the brakes are applied to get a dude to stop moving
     */
    public float getDamping() {
        return PLAYER_DAMPING;
    }

    /**
     * Returns the upper limit on dude left-right movement.
     * <p>
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on dude left-right movement.
     */
    public float getMaxHorizontalSpeed() {
        return PLAYER_MAX_HORIZONTAL_SPEED;
    }

    public float getMaxVerticalSpeed() {
        return PLAYER_MAX_VERTICAL_SPEED;
    }

    public void setOnString(boolean onString) {
        this.onString = onString;
    }

    public void setIsTrampolining(boolean isTrampolining) {
        this.isTrampolining = isTrampolining;
    }

    /**
     * Returns the name of the ground sensor
     * <p>
     * This is used by ContactListener
     *
     * @return the name of the ground sensor
     */
    public String getSensorName() {
        return sensorName;
    }

    /**
     * Creates a new dude avatar at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x      Initial x position of the avatar center
     * @param y      Initial y position of the avatar center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public Person(float x, float y, float width, float height, String name, String sensorName) {
        super(x, y, width * HSHRINK, height * VSHRINK);
        setDensity(PLAYER_DENSITY);
        setFriction(PLAYER_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        trampolineDir = new Vector2();

        isAlive = true;
        isCutting = false;
        // Gameplay attributes
        isGrounded = false;
        isJumping = false;
        isWalking = false;
        isTrampolining = false;
        isOnNpc = false;
        onNpc = null;
        this.sensorName = sensorName;
        this.inventory = new ArrayList<>();
        trampolineForce = new Vector2();
        isAttached = false;
        jumpCooldown = 0;
        setName(name);
    }

    public NpcPerson getOnNpc() {
        return onNpc;
    }

    public void setOnNpc(NpcPerson n) {
        this.onNpc = n;
    }

    public void setIsOnNpc(boolean b) {
        this.isOnNpc = b;
    }

    public boolean isOnNpc() {
        return isOnNpc;
    }

    public boolean isAttached() {
        return isAttached;
    }

    public void setTrampolineDir(Vector2 v) {
        if (isTrampolining)
            return;
        trampolineDir.set(v.x, v.y);
        temp.set(-getLinearVelocity().x, -getLinearVelocity().y);

    }

    public void calculateTrampolineForce() {
        float magnitude = temp.dot(trampolineDir) / trampolineDir.len();
        if (magnitude < 3.5f)
            return;

        this.trampolineForce.set(magnitude * trampolineDir.x, magnitude * trampolineDir.y);
        float len = trampolineForce.len();
        if (len > MAX_TRAMPOLINE) {
            trampolineForce.scl(MAX_TRAMPOLINE / len);
        } else if (len < MIN_TRAMPOLINE) {
            trampolineForce.scl(MIN_TRAMPOLINE / len);
        }
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     * <p>
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        // Ground Sensor
        // -------------
        // We only allow the dude to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the dude is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = PLAYER_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(SSHRINK * getWidth() * .75f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;
        sensorDef.filter.maskBits = getFilterData().maskBits;
        sensorDef.filter.categoryBits = getFilterData().categoryBits;
        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;
    }

    /**
     * Applies the force to the body of this dude
     * <p>
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        float horizontalMovement = getHorizontalMovement();
        if (isGod) {
            body.setLinearVelocity(horizontalMovement, verticalMovement);
        } else {
            Vector2 linearVelocity = body.getLinearVelocity();
            if (linearVelocity.x > 0 && horizontalMovement < 0) {
                body.setLinearVelocity(0.1f, linearVelocity.y);
            } else if (linearVelocity.x < 0 && horizontalMovement > 0) {
                body.setLinearVelocity(-0.1f, linearVelocity.y);
            }

            // Don't want to be moving. Damp out player motion
            if (horizontalMovement == 0f && !isAttached) {
                forceCache.set(-getDamping() * getVX(), 0);
                body.applyForce(forceCache, getPosition(), true);
            }

            // Velocity too high, clamp it
            if (Math.abs(getVX()) >= getMaxHorizontalSpeed()) {
                setVX(Math.signum(getVX()) * getMaxHorizontalSpeed());
            }

            if (Math.abs(getVY()) >= getMaxVerticalSpeed()) {
                setVY(Math.signum(getVY()) * getMaxVerticalSpeed());
            }

            float vertical = PLAYER_JUMP;

            if (isTrampolining) {
                calculateTrampolineForce();
                forceCache.set(trampolineForce.x, trampolineForce.y);
                body.applyLinearImpulse(forceCache, getPosition(), true);
                isTrampolining = false;
            }


            if (isAttached) {
                horizontalMovement = horizontalMovement * 5f;
            } else if (released) {
                horizontalMovement = getVX() * 15f + getHorizontalMovement();
            }

            forceCache.set(horizontalMovement, 0);
            body.applyForce(forceCache, getPosition(), true);

            // Jump!
            if (isJumping()) {
                forceCache.set(0, vertical);
                body.applyLinearImpulse(forceCache, getPosition(), true);
            }
            released = false;
        }

    }


    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     * <p>
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        frameCount++;
        int frameRate = 3;
        if (horizontalMovement != 0) {
            int temp = Math.abs(((int) (frameRate * 0.16f / horizontalMovement)));
        }

        if (horizontalMovement == 0) {
            frameRate = 7;
        }

        if (isJumping()) {
            jumpCooldown = JUMP_COOLDOWN;
        } else {
            jumpCooldown = Math.max(0, jumpCooldown - 1);
        }

        if (!isShooting) {
            shootCooldown = Math.max(0, shootCooldown - 1);
        }

        if (texture instanceof FilmStrip && frameCount % frameRate == 0 && (isGrounded() || getName().equals("npc"))) {
            frameCount = 0;
            if (!((FilmStrip) texture).getShouldFreeze()) {
                ((FilmStrip) texture).setNextFrame();
            }
        }

        if (won) {
            setLinearVelocity(Vector2.Zero);
        }

        super.update(dt);
    }

    public void resetShootCooldown() {
        shootCooldown = SHOOT_COOLDOWN;
    }

    /**
     * @param canCut whether the character can cut a trampoline rope
     */
    public void setCanCut(boolean canCut) {
        this.canCut = canCut;
    }

    public boolean canCut() {
        return canCut;
    }

    public void setCanSwingTo(NpcPerson canSwingTo) {
        this.canSwingTo = canSwingTo;
    }

    public NpcPerson getCanSwingTo() {
        return canSwingTo;
    }

    /**
     * Returns true if the dude is actively firing.
     *
     * @return true if the dude is actively firing.
     */
    public boolean isShooting() {
        return isShooting;
    }

    /**
     * Sets whether the dude is actively firing.
     *
     * @param value whether the dude is actively firing.
     */
    public void setShooting(boolean value) {
        isShooting = value;
    }

    public void setTarget(Person target) {
        this.target = target;
    }

    public Obstacle getTarget() {
        return target;
    }

    public void setSwingJoint(Joint swingJoint) {
        this.swingJoint = swingJoint;
    }

    public Joint getSwingJoint() {
        return swingJoint;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (won()) {
            tint.set(tint.r, tint.g, tint.b, tint.a * 0.97f);
        } else {
            tint.set(Color.WHITE);
        }
        canvas.draw(texture, tint, origin.x, origin.y, getX() * drawScale.x,
                getY() * drawScale.y, getAngle(), (isFacingRight ? 1 : -1) * HSHRINK, VSHRINK);
    }

    public void kill() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }


    public boolean isWalking() {
        return isWalking;
    }

    public boolean isRising() {
        return getVY() > VERTICAL_EPSILON;
    }

    public boolean isFalling() {
        return getVY() < -VERTICAL_EPSILON;
    }

    public ArrayList<String> getInventory() {
        return inventory;
    }

    public void atGate() {
        if (collectedAll) {
            won = true;
        }
    }

    public void setCollectedAll(boolean all) {
        collectedAll = all;
    }

    public boolean won() {
        return won;
    }

    /**
     * Draws the outline of the physics body.
     * <p>
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }

    public void setAttached(boolean isAttached) {
        released = this.isAttached && !isAttached;
        this.isAttached = isAttached;
    }

    public void setGodMode(boolean isGod) {
        this.isGod = isGod;
    }

    public boolean isGodModeActivated() {
        return this.isGod;
    }

}