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
package root;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;
import obstacle.Obstacle;
import obstacle.PolygonObstacle;
import platform.Character;
import platform.*;
import util.FilmStrip;
import util.PooledList;
import util.ScreenListener;
import util.SoundController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Gameplay specific controller for the platformer game.
 * <p>
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameMode implements Screen {
    Random rand = new Random();
    /**
     * Exit code for quitting the game
     */
    public static final int EXIT_QUIT = 0;
    /**
     * Exit code for advancing to next level
     */
    public static final int EXIT_NEXT = 1;
    /**
     * Exit code for jumping back to previous level
     */
    public static final int EXIT_PREV = 2;

    public static final int EXIT_INTO_GAME = 3;
    /**
     * How many frames after winning/losing do we continue?
     */
    public static final int EXIT_COUNT = 120;
    /**
     * The amount of time for a physics engine step.
     */
    public static final float WORLD_STEP = 1 / 60.0f;
    /**
     * Number of velocity iterations for the constrain solvers
     */
    public static final int WORLD_VELOC = 6;
    /**
     * Number of position iterations for the constrain solvers
     */
    public static final int WORLD_POSIT = 2;
    /**
     * Width of the game world in Box2d units
     */
    protected static final float DEFAULT_WIDTH = 32.0f;
    /**
     * Height of the game world in Box2d units
     */
    protected static final float DEFAULT_HEIGHT = 18.0f;

    private final float EPSILON = 0.5f;
    /**
     * Background
     */
    private static final String BKG_SUN = "platform/sun_background.png";

    private static final String BKG_CITY = "platform/city_background.png";

    private static final String BKG_CLOUD = "platform/cloud_background.png";

    private static final String BKG_SKY = "platform/background_sky.png";
    /**
     * The texture file for the player (no animation)
     */

    private static final String PLAYER_IDLE = "platform/pc_idle.png";

    private static final String PLAYER_IDLE_ANIMATION = "platform/pc_idle_256x256.png";

    private static final String PLAYER_LEFT = "platform/pc_left.png";

    private static final String PLAYER_RIGHT = "platform/pc_right.png";

    private static final String PLAYER_JUMP = "platform/pc_jump_up_256x256.png";

    private static final String PLAYER_FALL = "platform/pc_jump_down_256x256.png";

    private static final String NPC_COZY = "platform/cozy.png";

    private static final String NPC_CHEESE = "platform/cheese.png";

    private static final String NPC_NERVY = "platform/nervy.png";

    private static final String NPC_SPIKY = "platform/spiky.png";

    private static final String NPC_HEYO = "platform/heyo.png";

    private static final String NPC_WELCOME = "platform/welcome.png";
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
    /**
     * The folder with all levels
     */
    private static final String TEST_LEVEL = "levels/test_level.json";
    private static final String CROSSHAIR_FILE = "platform/crosshair.png";

    /**
     * File to texture for walls and platforms
     */
    private static final String EARTH_FILE = "shared/earthtile.png";
    /**
     * File to texture for the win door
     */
    private static final String GOAL_FILE = "shared/goaldoor.png";
    private static final String PLAYER_WALKING_ANIMATION_FILE = "platform/pc_walk.png";
    /**
     * Retro font for displaying messages
     */
    private static String FONT_FILE = "shared/RetroGame.ttf";

    private static int FONT_SIZE = 64;
    /**
     * Track asset loading from all instances and subclasses
     */
    protected AssetState worldAssetState = AssetState.EMPTY;
    /**
     * Track all loaded assets (for unloading purposes)
     */
    protected Array<String> assets;
    /**
     * The texture for walls and platforms
     */
    protected TextureRegion earthTile;
    /**
     * The texture for the exit condition
     */
    protected TextureRegion goalTile;
    /**
     * The font for giving messages to the player
     */
    protected BitmapFont displayFont;
    /**
     * Reference to the game canvas
     */
    protected GameCanvas canvas;
    /**
     * All the objects in the world.
     */
    protected PooledList<Obstacle> objects = new PooledList<Obstacle>();
    /**
     * Queue for adding objects
     */
    protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
    /**
     * The Box2D world
     */
    protected World world;
    /**
     * The boundary of the world
     */
    protected Rectangle bounds;
    /**
     * The world scale
     */
    protected Vector2 scale;

    /**
     * Texture assets for character avatar
     */
    private TextureRegion playerTexture;
    private TextureRegion playerJumpTexture;
    private TextureRegion playerFallTexture;

    private TextureRegion npcCozyTexture;
    private TextureRegion npcCheeseTexture;
    private TextureRegion npcNervyTexture;
    private TextureRegion npcheyoTexture;
    private TextureRegion npcspikyTexture;
    private TextureRegion npcwelcomeTexture;

    private ArrayList<TextureRegion> npcs = new ArrayList<>();

    private TextureRegion backgroundTexture;

    private FilmStrip playerIdleAnimation;

    private FilmStrip playerWalkingAnimation;
    /**
     * Texture asset for the bullet
     */
    private TextureRegion bulletTexture;
    /**
     * Texture asset for the bridge plank
     */
    private TextureRegion bridgeTexture;
    private TextureRegion crosshairTexture;

    /**
     * Track asset loading from all instances and subclasses
     */
    private AssetState platformAssetState = AssetState.EMPTY;
    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;
    /**
     * Whether or not this is an active controller
     */
    private boolean active;
    /**
     * Whether we have completed this level
     */
    private boolean complete;
    /**
     * Whether we have failed at this world (and need a reset)
     */
    private boolean failed;
    /**
     * Whether or not debug mode is active
     */
    private boolean debug;
    /**
     * Countdown active for winning or losing
     */
    private int countdown;
    private TextureRegion cityTexture;
    private TextureRegion skyTexture;
    private TextureRegion cloudTexture;
    private TextureRegion sunTexture;

    /**
     * Creates a new game world
     * <p>
     * The game world is scaled so that the screen coordinates do not agree
     * with the Box2d coordinates.  The bounds are in terms of the Box2d
     * world, not the screen.
     *
     * @param bounds  The game bounds in Box2d coordinates
     * @param gravity The gravitational force on this Box2d world
     */
    protected GameMode(Rectangle bounds, Vector2 gravity) {
        assets = new Array<>();
        world = new World(gravity, false);
        this.bounds = new Rectangle(bounds);
        this.scale = new Vector2(1, 1);
        complete = false;
        failed = false;
        debug = false;
        active = false;
        countdown = -1;
    }

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
        manager.load(PLAYER_IDLE, Texture.class);
        assets.add(PLAYER_IDLE);
        manager.load(PLAYER_JUMP, Texture.class);
        assets.add(PLAYER_JUMP);
        manager.load(PLAYER_FALL, Texture.class);
        assets.add(PLAYER_FALL);
        manager.load(BARRIER_FILE, Texture.class);
        assets.add(BARRIER_FILE);

        manager.load(NPC_CHEESE, Texture.class);
        assets.add(NPC_CHEESE);
        manager.load(NPC_COZY, Texture.class);
        assets.add(NPC_COZY);
        manager.load(NPC_NERVY, Texture.class);
        assets.add(NPC_NERVY);
        manager.load(NPC_SPIKY, Texture.class);
        assets.add(NPC_SPIKY);
        manager.load(NPC_HEYO, Texture.class);
        assets.add(NPC_HEYO);
        manager.load(NPC_WELCOME, Texture.class);
        assets.add(NPC_WELCOME);



        manager.load(BULLET_FILE, Texture.class);
        assets.add(BULLET_FILE);
        manager.load(ROPE_FILE, Texture.class);
        assets.add(ROPE_FILE);
        manager.load(CROSSHAIR_FILE, Texture.class);
        assets.add(CROSSHAIR_FILE);
        manager.load(EARTH_FILE, Texture.class);
        assets.add(EARTH_FILE);
        manager.load(GOAL_FILE, Texture.class);
        assets.add(GOAL_FILE);
        manager.load(BKG_CLOUD, Texture.class);
        assets.add(BKG_CLOUD);
        manager.load(BKG_SKY, Texture.class);
        assets.add(BKG_SKY);
        manager.load(BKG_SUN, Texture.class);
        assets.add(BKG_SUN);
        manager.load(BKG_CITY, Texture.class);
        assets.add(BKG_CITY);
        manager.load(PLAYER_IDLE_ANIMATION, Texture.class);
        assets.add(PLAYER_IDLE_ANIMATION);
        manager.load(PLAYER_WALKING_ANIMATION_FILE, Texture.class);
        assets.add(PLAYER_WALKING_ANIMATION_FILE);

        // Load Sound Assets
        manager.load(JUMP_FILE, Sound.class);
        assets.add(JUMP_FILE);
        manager.load(PEW_FILE, Sound.class);
        assets.add(PEW_FILE);
        manager.load(POP_FILE, Sound.class);
        assets.add(POP_FILE);


        manager.load(TEST_LEVEL, Level.class);
        assets.add(TEST_LEVEL);

        if (worldAssetState != AssetState.EMPTY) {
            return;
        }

        worldAssetState = AssetState.LOADING;

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);
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

        if (manager.isLoaded(TEST_LEVEL)) {
            levels.add(manager.get(TEST_LEVEL, Level.class));
        }


        playerTexture = createTexture(manager, PLAYER_IDLE, false);
        playerIdleAnimation = createFilmStrip(manager, PLAYER_IDLE_ANIMATION, 1,24,24);
        playerJumpTexture = createTexture(manager, PLAYER_JUMP, false);
        playerFallTexture = createTexture(manager, PLAYER_FALL, false);
        bridgeTexture = createTexture(manager, ROPE_FILE, false);
        bulletTexture = createTexture(manager, BULLET_FILE, false);
        crosshairTexture = createTexture(manager, CROSSHAIR_FILE, false);
        playerWalkingAnimation = createFilmStrip(manager, PLAYER_WALKING_ANIMATION_FILE, 1, 17, 17);
        cityTexture = createTexture(manager, BKG_CITY, false);
        skyTexture = createTexture(manager, BKG_SKY, false);
        cloudTexture = createTexture(manager, BKG_CLOUD, false);
        sunTexture = createTexture(manager, BKG_SUN, false);
        npcCheeseTexture = createTexture(manager, NPC_CHEESE, false);
        npcCozyTexture = createTexture(manager, NPC_COZY, false);
        npcNervyTexture = createTexture(manager, NPC_NERVY, false);
        npcheyoTexture = createTexture(manager, NPC_HEYO, false);
        npcspikyTexture = createTexture(manager, NPC_SPIKY, false);
        npcwelcomeTexture = createTexture(manager, NPC_WELCOME, false);
        npcs.add(npcCheeseTexture); npcs.add(npcCozyTexture); npcs.add(npcNervyTexture);npcs.add(npcheyoTexture);
        npcs.add(npcspikyTexture);npcs.add(npcwelcomeTexture);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        if (worldAssetState == AssetState.LOADING) {// Allocate the tiles
            earthTile = createTexture(manager, EARTH_FILE, true);
            goalTile = createTexture(manager, GOAL_FILE, true);// Allocate the font
            if (manager.isLoaded(FONT_FILE)) {
                displayFont = manager.get(FONT_FILE, BitmapFont.class);
            } else {
                displayFont = null;
            }
            worldAssetState = AssetState.COMPLETE;
        }

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
     * Friction of most platforms
     */
    private static final float BASIC_FRICTION = 0.4f;
    /**
     * The restitution for all physics objects
     */
    private static final float BASIC_RESTITUTION = 0.1f;
    /**
     * The volume for sound effects
     */
    private static final float EFFECT_VOLUME = 0.8f;
    /**
     * Offset for bullet when firing
     */
    private static final float BULLET_OFFSET = 0.2f;
    /**
     * The speed of the bullet after firing
     */
    private static final float BULLET_SPEED = 40.0f;
    private static final float MAX_BULLET_OFFSET_Y = 0.8f;

    // Physics objects for the game
    /**
     * Reference to the player avatar
     */
    private Character player;

    private List<Level> levels;

    private RopeJointDef jointDef;

    /**
     * Creates and initialize a new instance of the platformer game
     * <p>
     * The game has default gravity and other settings
     */
    public GameMode() {
        this(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT), new Vector2(0, DEFAULT_GRAVITY));
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(new CollisionController(player));
        this.levels = new ArrayList<>();
        jointDef = new RopeJointDef();
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
        world.setContactListener(new CollisionController(player));
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
        float dWidth = goalTile.getRegionWidth() / scale.x;
        float dHeight = goalTile.getRegionHeight() / scale.y;

        Level testLevel = levels.get(0);

        Vector2 playerPos = testLevel.getPlayerPos();
        List<Tile> tiles = testLevel.getTiles();
        List<float[]> couples = testLevel.getCouples();


        for (int i = 0; i < tiles.size(); i++) {
            createTile(tiles.get(i).getCorners(), 0, 0, "tile" + i);
        }

        // Create main dude
        dWidth = playerTexture.getRegionWidth() / scale.x;
        dHeight = playerTexture.getRegionHeight() / scale.y;
        player = new Character(playerPos.x, playerPos.y, dWidth, dHeight, "mainDude", "mainDudeSensor");
        player.setDrawScale(scale);
        player.setTexture(playerTexture);
        addObject(player);

        for (int i = 0; i < couples.size(); i++) {
            float[] curr = couples.get(i);
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
        int n1 = rand.nextInt(npcs.size());
        int n2 = rand.nextInt(npcs.size());
        while (n2 == n1) n2 = rand.nextInt(npcs.size());
        TextureRegion randTex1 = npcs.get(n1);
        TextureRegion randTex2 = npcs.get(n2);
//        System.out.println("n1 " + n1 + "n2 " + n2);
        Couple couple = new Couple(x1, y1, x2, y2, randTex1, randTex2, bridgeTexture, scale, id);
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
        boolean result = true;
        InputController input = InputController.getInstance();
        input.readInput(bounds, scale);
        if (listener != null) {// Toggle debug
            if (input.didDebug()) {
                debug = !debug;
            }// Handle resets
            if (input.didReset()) {
                reset();
            }// Now it is time to maybe switch screens.
            if (input.didExit()) {
                listener.exitScreen(this, EXIT_QUIT);
                result = false;
            } else if (input.didAdvance()) {
                listener.exitScreen(this, EXIT_NEXT);
                result = false;
            } else if (input.didRetreat()) {
                listener.exitScreen(this, EXIT_PREV);
                result = false;
            } else if (countdown > 0) {
                countdown--;
            } else if (countdown == 0) {
                if (failed) {
                    reset();
                } else if (complete) {
                    listener.exitScreen(this, EXIT_NEXT);
                    result = false;
                }
            }
        }

        if (!result) {
            return false;
        }

        if (!isFailure() && player.getY() < -1) {
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
        player.setMovement(InputController.getInstance().getHorizontal() * player.getForce());
        player.setJumping(InputController.getInstance().didPrimary());
        player.setShooting(InputController.getInstance().didTertiary());
        player.applyForce();


        if (player.isJumping()) {
            player.setTexture(playerJumpTexture);
           // SoundController.getInstance().play(JUMP_FILE, JUMP_FILE, false, EFFECT_VOLUME);
        }

        if (player.getVY() > EPSILON * 3f) {
            player.setTexture(playerJumpTexture);

        }else if (player.getVY() < -EPSILON * 3f) {
            player.setTexture(playerFallTexture);

        }else if (player.getVX() > EPSILON && player.isGrounded()) {
            player.setTexture(playerWalkingAnimation);

        }else if (player.getVX() < -EPSILON && player.isGrounded()) {
            player.setTexture(playerWalkingAnimation);

        }else if (player.isGrounded()){
            player.setTexture(playerIdleAnimation);

        }


        // Add a bullet if we fire
        if (player.isShooting()) {
            if (InputController.getInstance().getCrossHair().x - player.getX() >= 0) {
                player.setMovement(0.01f);
            } else {
                player.setMovement(-0.01f);
            }
            createBullet();
        }

        if (player.isShooting() && player.getSwingJoint() != null) {
            world.destroyJoint(player.getSwingJoint());
            player.setSwingJoint(null);
            player.setTarget(null);
        }

        // Cutting the rope
        if (InputController.getInstance().didSecondary() && player.canCut()) {
            int coupleID = player.getClosestCoupleID();
            for (Obstacle obs : objects) {
                if (obs.getName().equals("couples" + coupleID)) {
                    Rope[] ropes = ((Couple) obs).getRope().cut(player.getPosition(), world);
                    if (ropes != null) {
                        ((Couple) obs).breakBond(ropes[0], ropes[1]);
                    }
                }
            }
        }

        if (player.getTarget() != null && player.getSwingJoint() == null) {
            jointDef.bodyA = player.getBody();
            jointDef.bodyB = player.getTarget().getBody();
            jointDef.maxLength = 4.0f;
            Joint swingJoint = world.createJoint(jointDef);
            player.setSwingJoint(swingJoint);
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }

    /**
     * Add a new bullet to the world and send it in the right direction.
     */
    private void createBullet() {
        Vector2 crossHairLocation = InputController.getInstance().getCrossHair();
        Vector2 playerPosition = player.getPosition();
        crossHairLocation.sub(playerPosition);
        float firingAngle = (float) Math.atan(crossHairLocation.y / crossHairLocation.x);
        float offsetX = player.isFacingRight() ? BULLET_OFFSET : -BULLET_OFFSET;
        float offsetY = (float) (Math.tan(firingAngle) * offsetX);
        if (offsetY > MAX_BULLET_OFFSET_Y) {
            offsetY = MAX_BULLET_OFFSET_Y;
        } else if (offsetY < -MAX_BULLET_OFFSET_Y) {
            offsetY = -MAX_BULLET_OFFSET_Y;
        }

        float radius = bulletTexture.getRegionWidth() / (2.0f * scale.x);
        Projectile projectile = new Projectile(player.getX() + offsetX, player.getY() + offsetY, radius, 60);
        projectile.setName("player_rope");
        projectile.setDrawScale(scale);
        projectile.setTexture(bulletTexture);
        projectile.setBullet(true);
        projectile.setGravityScale(0);

        // Compute position and velocity
        float speed = player.isFacingRight() ? BULLET_SPEED : -BULLET_SPEED;
        float vx = (float) (speed * Math.cos(firingAngle));
        float vy = (float) (speed * Math.sin(firingAngle));
        projectile.setVX(vx);
        projectile.setVY(vy);
        addQueuedObject(projectile);

        SoundController.getInstance().play(PEW_FILE, PEW_FILE, false, EFFECT_VOLUME);
    }

    public void draw(float dt) {
        canvas.begin();
        float camera = player.getX() * scale.x;
        canvas.drawWrapped(skyTexture, 0f * camera, 0f, skyTexture.getRegionWidth() / 2, skyTexture.getRegionHeight() / 2);
        canvas.drawWrapped(sunTexture, 0f * camera, 0f, sunTexture.getRegionWidth() / 2, sunTexture.getRegionHeight() / 2);
        canvas.drawWrapped(cityTexture, -0.1f * camera, 0f, cityTexture.getRegionWidth() / 2, cityTexture.getRegionHeight() / 2);
        canvas.drawWrapped(cloudTexture, -0.5f * camera, 0f, cloudTexture.getRegionWidth() / 2, cloudTexture.getRegionHeight() / 2);

        canvas.end();
        canvas.moveCamera(player.getX() * scale.x, player.getY() * scale.y);

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

    /**
     * Returns a newly loaded texture region for the given file.
     * <p>
     * This helper methods is used to set texture settings (such as scaling, and
     * whether or not the texture should repeat) after loading.
     *
     * @param manager Reference to global asset manager.
     * @param file    The texture (region) file
     * @param repeat  Whether the texture should be repeated
     * @return a newly loaded texture region for the given file.
     */
    protected TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return region;
        }
        return null;
    }

    /**
     * Returns a newly loaded filmstrip for the given file.
     * <p>
     * This helper methods is used to set texture settings (such as scaling, and
     * the number of animation frames) after loading.
     *
     * @param manager Reference to global asset manager.
     * @param file    The texture (region) file
     * @param rows    The number of rows in the filmstrip
     * @param cols    The number of columns in the filmstrip
     * @param size    The number of frames in the filmstrip
     * @return a newly loaded texture region for the given file.
     */
    protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size) {
        if (manager.isLoaded(file)) {
            FilmStrip strip = new FilmStrip(manager.get(file, Texture.class), rows, cols, size);
            strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return strip;
        }
        return null;
    }

    /**
     * Unloads the assets for this game.
     * <p>
     * This method erases the static variables.  It also deletes the associated textures
     * from the asset manager. If no assets are loaded, this method does nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void unloadContent(AssetManager manager) {
        for (String s : assets) {
            if (manager.isLoaded(s)) {
                manager.unload(s);
            }
        }
    }

    /**
     * Returns true if debug mode is active.
     * <p>
     * If true, all objects will display their physics bodies.
     *
     * @return true if debug mode is active.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets whether debug mode is active.
     * <p>
     * If true, all objects will display their physics bodies.
     *
     * @param value whether debug mode is active.
     */
    public void setDebug(boolean value) {
        debug = value;
    }

    /**
     * Returns true if the level is completed.
     * <p>
     * If true, the level will advance after a countdown
     *
     * @return true if the level is completed.
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Sets whether the level is completed.
     * <p>
     * If true, the level will advance after a countdown
     *
     * @param value whether the level is completed.
     */
    public void setComplete(boolean value) {
        if (value) {
            countdown = EXIT_COUNT;
        }
        complete = value;
    }

    /**
     * Returns true if the level is failed.
     * <p>
     * If true, the level will reset after a countdown
     *
     * @return true if the level is failed.
     */
    public boolean isFailure() {
        return failed;
    }

    /**
     * Sets whether the level is failed.
     * <p>
     * If true, the level will reset after a countdown
     *
     * @param value whether the level is failed.
     */
    public void setFailure(boolean value) {
        if (value) {
            countdown = EXIT_COUNT;
        }
        failed = value;
    }

    /**
     * Returns the canvas associated with this controller
     * <p>
     * The canvas is shared across all controllers
     *
     * @return the canvas associated with this controller
     */
    public GameCanvas getCanvas() {
        return canvas;
    }

    /**
     * Sets the canvas associated with this controller
     * <p>
     * The canvas is shared across all controllers.  Setting this value will compute
     * the drawing scale from the canvas size.
     *
     * @param canvas the canvas associated with this controller
     */
    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
        this.scale.x = canvas.getWidth() / bounds.getWidth();
        this.scale.y = canvas.getHeight() / bounds.getHeight();
    }

    /**
     * Dispose of all (non-static) resources allocated to this mode.
     */
    public void dispose() {
        for (Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        world.dispose();
        objects = null;
        addQueue = null;
        bounds = null;
        scale = null;
        world = null;
        canvas = null;
    }

    /**
     * Adds a physics object in to the insertion queue.
     * <p>
     * Objects on the queue are added just before collision processing.  We do this to
     * control object creation.
     * <p>
     * param obj The object to add
     */
    public void addQueuedObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        addQueue.add(obj);
    }

    /**
     * Immediately adds the object to the physics world
     * <p>
     * param obj The object to add
     */
    protected void addObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
        obj.activatePhysics(world);
    }

    /**
     * Returns true if the object is in bounds.
     * <p>
     * This assertion is useful for debugging the physics.
     *
     * @param obj The object to check.
     * @return true if the object is in bounds.
     */
    public boolean inBounds(Obstacle obj) {
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
        boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
        return horiz && vert;
    }

    /**
     * Processes physics
     * <p>
     * Once the update phase is over, but before we draw, we are ready to handle
     * physics.  The primary method is the step() method in world.  This implementation
     * works for all applications and should not need to be overwritten.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void postUpdate(float dt) {
        // Add any objects created by actions
        while (!addQueue.isEmpty()) {
            addObject(addQueue.poll());
        }

        // Turn the physics engine crank.
        world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);

        // Garbage collect the deleted objects.
        // Note how we use the linked list nodes to delete O(1) in place.
        // This is O(n) without copying.
        Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
        while (iterator.hasNext()) {
            PooledList<Obstacle>.Entry entry = iterator.next();
            Obstacle obj = entry.getValue();
            if (obj.isRemoved()) {
                obj.deactivatePhysics(world);
                entry.remove();
            } else {
                // Note that update is called last!
                obj.update(dt);
            }
        }
    }

    /**
     * Called when the Screen is resized.
     * <p>
     * This can happen at any point during a non-paused state but will never happen
     * before a call to show().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        // IGNORE FOR NOW
    }

    /**
     * Called when the Screen should render itself.
     * <p>
     * We defer to the other methods update() and draw().  However, it is VERY important
     * that we only quit AFTER a draw.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void render(float delta) {
        if (active) {
            if (preUpdate(delta)) {
                update(delta); // This is the one that must be defined.
                postUpdate(delta);
            }
            draw(delta);
        }
    }

    /**
     * Called when the Screen is paused.
     * <p>
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        // TODO Auto-generated method stub
    }

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    public void show() {
        // Useless if called in outside animation loop
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
    }

    /**
     * Sets the ScreenListener for this mode
     * <p>
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    /**
     * Tracks the asset state.  Otherwise subclasses will try to load assets
     */
    protected enum AssetState {
        /**
         * No assets loaded
         */
        EMPTY,
        /**
         * Still loading assets
         */
        LOADING,
        /**
         * Assets are complete
         */
        COMPLETE
    }
}