/*
 * PlatformController.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package platform;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import obstacle.Obstacle;
import obstacle.PolygonObstacle;
import root.InputController;
import root.WorldController;
import util.SoundController;

/**
 * Gameplay specific controller for the platformer game.
 * <p>
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class PlatformController extends WorldController {
    private Affine2 local;

    private Vector2 lastLocation;

    /**
     * The texture file for the character avatar (no animation)
     */
    private static final String DUDE_FILE = "platform/pc_idle.png";

    private static final String DUDE_LEFT = "platform/pc_left.png";

    private static final String DUDE_RIGHT = "platform/pc_right.png";

    private static final String DUDE_JUMP = "platform/pc_jump.png";

    private static final String DUDE_FALL = "platform/pc_fall.png";
    /**
     * The texture file for the spinning barrier
     */
    private static final String BARRIER_FILE = "platform/barrier.png";
    /**
     * The texture file for the bullet
     */
    private static final String BULLET_FILE = "platform/bullet.png";
    /**
     * The texture file for the bridge plank
     */
    private static final String ROPE_FILE = "platform/ropebridge.png";

    /**
     * The sound file for a jump
     */
    private static final String JUMP_FILE = "platform/jump.mp3";
    /**
     * The sound file for a bullet fire
     */
    private static final String PEW_FILE = "platform/pew.mp3";
    /**
     * The sound file for a bullet collision
     */
    private static final String POP_FILE = "platform/plop.mp3";

    private static final String BKG_FILE = "platform/background.png";

    /**
     * Texture asset for character avatar
     */
    private TextureRegion avatarTexture;
    private TextureRegion leftTexture;
    private TextureRegion rightTexture;
    private TextureRegion jumpTexture;
    private TextureRegion fallTexture;
    private TextureRegion backgroundTexture;


    /**
     * Texture asset for the bridge plank
     */
    private TextureRegion bridgeTexture;

    /**
     * Track asset loading from all instances and subclasses
     */
    private AssetState platformAssetState = AssetState.EMPTY;

    /**
     * Preloads the assets for this controller.
     * <p>
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        if (platformAssetState != AssetState.EMPTY) {
            return;
        }

        platformAssetState = AssetState.LOADING;
        manager.load(DUDE_FILE, Texture.class);
        assets.add(DUDE_FILE);
        manager.load(DUDE_LEFT, Texture.class);
        assets.add(DUDE_LEFT);
        manager.load(DUDE_RIGHT, Texture.class);
        assets.add(DUDE_RIGHT);
        manager.load(DUDE_JUMP, Texture.class);
        assets.add(DUDE_JUMP);
        manager.load(DUDE_FALL, Texture.class);
        assets.add(DUDE_FALL);
        manager.load(BARRIER_FILE, Texture.class);
        assets.add(BARRIER_FILE);
        manager.load(BULLET_FILE, Texture.class);
        assets.add(BULLET_FILE);
        manager.load(ROPE_FILE, Texture.class);
        assets.add(ROPE_FILE);

        manager.load(JUMP_FILE, Sound.class);
        assets.add(JUMP_FILE);

        manager.load(BKG_FILE, Texture.class);
        assets.add(BKG_FILE);
        manager.load(PEW_FILE, Sound.class);
        assets.add(PEW_FILE);
        manager.load(POP_FILE, Sound.class);
        assets.add(POP_FILE);

        local = new Affine2();

        super.preLoadContent(manager);
    }

    /**
     * Load the assets for this controller.
     * <p>
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        if (platformAssetState != AssetState.LOADING) {
            return;
        }

        avatarTexture = createTexture(manager, DUDE_FILE, false);
        backgroundTexture = createTexture(manager, BKG_FILE, false);
        leftTexture = createTexture(manager, DUDE_LEFT, false);
        rightTexture = createTexture(manager, DUDE_RIGHT, false);
        jumpTexture = createTexture(manager, DUDE_JUMP, false);
        fallTexture = createTexture(manager, DUDE_FALL, false);
        bridgeTexture = createTexture(manager, ROPE_FILE, false);

        local = new Affine2();

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        super.loadContent(manager);
        platformAssetState = AssetState.COMPLETE;
    }

    // Physics constants for initialization
    /**
     * The new heavier gravity for this world (so it is not so floaty)
     */
    private static final float DEFAULT_GRAVITY = -14.7f;
    /**
     * The density for most physics objects
     */
    private static final float BASIC_DENSITY = 0.0f;
    /**
     * The density for a bullet
     */
    private static final float HEAVY_DENSITY = 10.0f;
    /**
     * Friction of most platforms
     */
    private static final float BASIC_FRICTION = 0.4f;
    /**
     * The restitution for all physics objects
     */
    private static final float BASIC_RESTITUTION = 0.1f;

    /**
     * Offset for bullet when firing
     */
    private static final float BULLET_OFFSET = 0.2f;
    /**
     * The speed of the bullet after firing
     */
    private static final float BULLET_SPEED = 20.0f;
    /**
     * The volume for sound effects
     */
    private static final float EFFECT_VOLUME = 0.8f;

    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.
    // Wall vertices
    private Wall[] walls;

    // Other game objects
    /**
     * The position of the main dude
     */
    private Vector2 mainDudePos;

    // Physics objects for the game
    /**
     * Reference to the character avatar
     */
    private DudeModel mainDude;

    /**
     * Creates and initialize a new instance of the platformer game
     * <p>
     * The game has default gravity and other settings
     */
    public PlatformController() {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(new CollisionController(mainDude));
    }

    /**
     * Resets the status of the game so that we can play again.
     * <p>
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity());

        for (Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        world.dispose();

        world = new World(gravity, false);
        setComplete(false);
        setFailure(false);
        populateLevel();
        world.setContactListener(new CollisionController(mainDude));
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
        PlatformLoader loader = new PlatformLoader(
                "test.json");
        walls = loader.getTiles();
        mainDudePos = loader.getCharacterPos();
        // Add level goal
        float dwidth = goalTile.getRegionWidth() / scale.x;
        float dheight = goalTile.getRegionHeight() / scale.y;

        for (int i = 0; i < walls.length; i++) {
            createTile(walls[i].getIndices(), 0, 0, "tile" + i);
        }
        // Create main dude
        dwidth = avatarTexture.getRegionWidth() / scale.x;
        dheight = avatarTexture.getRegionHeight() / scale.y;
        mainDude = new DudeModel(mainDudePos.x, mainDudePos.y, dwidth, dheight, "mainDude", "mainDudeSensor");
        mainDude.setDrawScale(scale);
        mainDude.setTexture(avatarTexture);
        addObject(mainDude);
        lastLocation = mainDude.getPosition();

        float[][] couples = loader.getCouples();
        for (int i = 0; i < couples.length; i++) {
            float[] curr = couples[i];
            createCouple(curr[0], curr[1], curr[2], curr[3], i);
        }
    }

    public void createTile(float[] points, float x, float y, String name) {
        PolygonObstacle tile = new PolygonObstacle(points, x, y);
        tile.setBodyType(BodyDef.BodyType.StaticBody);
        tile.setDensity(BASIC_DENSITY);
        tile.setFriction(BASIC_FRICTION);
        tile.setRestitution(BASIC_RESTITUTION);
        tile.setDrawScale(scale);
        tile.setTexture(earthTile);
        tile.setName(name);
        addObject(tile);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void createCouple(float x1, float y1, float x2, float y2, int id) {
        float[] points = new float[]{0.15f, 0.25f, 0.15f, 1f, 0.75f, 1f, 0.75f, 0.25f};
        createTile(points, x1, y1 - 1f, "tile");
        createTile(points, x2, y2 - 1f, "tile");
        Couple couple = new Couple(x1, y1, x2, y2, avatarTexture, bridgeTexture, scale, id);
        addObject(couple);
    }

    /**
     * Returns whether to process the update loop
     * <p>
     * At the start of the update loop, we check if it is time
     * to switch to a new game mode.  If not, the update proceeds
     * normally.
     *
     * @param dt Number of seconds since last animation frame
     * @return whether to process the update loop
     */
    public boolean preUpdate(float dt) {
        if (!super.preUpdate(dt)) {
            return false;
        }

        if (!isFailure() && mainDude.getY() < -1) {
            setFailure(true);
            return false;
        }

        return true;
    }

    /**
     * The core gameplay loop of this world.
     * <p>
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class game.WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Process actions in object model
        mainDude.setMovement(InputController.getInstance().getHorizontal() * mainDude.getForce());
        mainDude.setJumping(InputController.getInstance().didPrimary());

        mainDude.applyForce();
        if (mainDude.isJumping()) {
            mainDude.setTexture(jumpTexture);
            SoundController.getInstance().play(JUMP_FILE, JUMP_FILE, false, EFFECT_VOLUME);
        }

        if (mainDude.goingUp) {
            mainDude.setTexture(jumpTexture);
        }

        if (!mainDude.goingUp) {
            mainDude.setTexture(fallTexture);
        }

        if (mainDude.isGrounded()) {
            mainDude.setTexture(avatarTexture);
        }

//        if (! mainDude.isJumping() && ! mainDude.isGrounded()) {
//            mainDude.setTexture(fallTexture);
//        }

        if (mainDude.getMovement() < 0) {
            mainDude.setTexture(leftTexture);
        }

        if (mainDude.getMovement() > 0) {
            mainDude.setTexture(rightTexture);
        }

        if (InputController.getInstance().didSecondary() && mainDude.canCut()) {
            int coupleID = mainDude.getClosestCouple();
            for (Obstacle obs : objects) {
                // System.out.println(obs.getName());
                if (obs.getName().equals("couples" + coupleID)) {
                    Rope[] ropes = ((Couple) obs).getRope().cut(mainDude.getPosition(), world);
                    ((Couple) obs).breakBond(ropes[0], ropes[1]);

                }
            }
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }

    public void draw(float dt) {
        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.end();

        canvas.begin();
        for (Obstacle obj : objects) {
            obj.draw(canvas);
        }
        canvas.end();

        if (isDebug()) {
            canvas.beginDebug();
            for (Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
        }

    }


}