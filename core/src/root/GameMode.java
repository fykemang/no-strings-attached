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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import entities.*;
import obstacle.Obstacle;
import util.*;

import java.util.*;

/**
 * Gameplay specific controller for the game.
 * <p>
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameMode extends Mode implements Screen {
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

    public static final int EXIT_RESET = 12;

    public static final int EXIT_INTO_GAME = 3;

    public static final int EXIT_INTO_NEXT = 6;
    /**
     * How many frames after winning/losing do we continue?
     */
    public static final int EXIT_COUNT = 60;
    /**
     * The amount of time for a physics engine step.
     */
    public static final float WORLD_STEP = 0.016f;
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
    // time after game ends
    private float timeSeconds = 0f;
    // wait time
    private final float period = 2f;

    private Vector2 lastpos;
    private Vector2 lastviewport;

    boolean isZoomed = true;


    /**
     * Player animations
     */
    private static final String PLAYER_IDLE_ANIMATION = "player/player_idle_animation.png";
    private static final String PLAYER_WALKING_ANIMATION_FILE = "player/player_walk_animation.png";
    private static final String PLAYER_SWING_FORWARD = "player/player_swing_forward.png";
    private static final String PLAYER_SWING_FREE = "player/player_swing_free.png";
    private static final String PLAYER_SWING_BACK = "player/player_swing_back.png";
    private static final String PLAYER_JUMP_UP = "player/player_jump_up.png";
    private static final String PLAYER_JUMP_DOWN = "player/player_jump_down.png";
    private static final String PLAYER_ENTER = "player/player_enter.png";
    private static final String PLAYER_EXIT = "player/player_exit.png";
    private static final String PLAYER_DEATH = "player/player_death.png";

    /**
     * NPC animations
     */
    private static final String NPC_COZY = "entities/cozy_idle.png";
    private static final String NPC_CHEESE = "entities/cheese_idle.png";
    private static final String NPC_NERVY = "entities/nervy_idle.png";
    private static final String NPC_SPIKY = "entities/spiky_idle.png";
    private static final String NPC_HEYO = "entities/heyo_idle.png";
    private static final String NPC_WELCOME = "entities/welcome_idle.png";
    private static final String NPC_COZY_SHOCK = "entities/cozy_shock.png";
    private static final String NPC_CHEESE_SHOCK = "entities/cheese_shock.png";
    private static final String NPC_NERVY_SHOCK = "entities/nervy_shock.png";
    private static final String NPC_SPIKY_SHOCK = "entities/spiky_shock.png";
    private static final String NPC_HEYO_SHOCK = "entities/heyo_shock.png";
    private static final String NPC_WELCOME_SHOCK = "entities/welcome_shock.png";
    private static final String EXCLAMATION = "entities/exclamation.png";
    private static final String TARGET = "entities/target.png";

    /**
     * Texture file for the exit door
     */
    private static final String GATE = "entities/door.png";
    /**
     * Texture files for items
     */
    private static final String NEEDLE = "entities/needles.png";
    private static final String YARN = "entities/yarn.png";
    private static final String GREY_YARN = "entities/grey_yarn.png";
    private static final String BUTTON = "entities/buttons.png";
    private static final String FABRIC_1 = "entities/fabric1.png";
    private static final String FABRIC_2 = "entities/fabric2.png";
    private static final String SPOOL = "entities/spool.png";
    private static final String STUFFING = "entities/stuffing.png";
    private static final String[] cityItems = {"entities/needles.png", "entities/yarn.png", "entities/yarn.png"};
    private static final String[] villageItems = {"entities/spool.png", "entities/yarn.png", "entities/yarn.png"};
    private static final String[] forestItems = {"entities/fabric1.png", "entities/fabric2.png", "entities/buttons.png"};
    private static final String[] mountainItems = {"entities/stuffing.png", "entities/stuffing.png", "entities/stuffing.png"};

    /**
     * Texture files for baskets (progress bar)
     */
    private static final String BASKET_EMPTY = "ui/basket_0.png";

    private static final String CUT_INDICATOR_FILE = "entities/scissor.png";
    /**
     * The sound effects
     */
    private static final String JUMP_FILE = "sounds/jump.mp3";
    private static final String COLLECT_FILE = "sounds/itemcollect.mp3";
    private static final String WIN_FILE = "sounds/door_open.mp3";
    private static final String LOSE_FILE = "sounds/win-reverse.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";
    private static final String SNIP_FILE = "sounds/snip.mp3";
    private static final String LAND_FILE = "sounds/landing.mp3";
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String TRAMP_LAND_FILE = "sounds/trampoline_jump.mp3";
    private static final String TRAMP_JUMP_FILE = "sounds/trampoline_jump.mp3";
    private static final String HANG_FILE = "sounds/swing.mp3";
    private static final String WALKING_CITY_FILE = "sounds/walking_city.mp3";
    private static final String WALKING_VILLAGE_FILE = "sounds/walking_village.mp3";
    private static final String WALKING_FOREST_FILE = "sounds/walking_forest.mp3";
    private static final String WALKING_MT_FILE = "sounds/walking_mountain.mp3";
    private static final String AMBIENCE_CITY_FILE = "music/ambience_city.mp3";

    /**
     * File to texture for walls and platforms
     */
    private static final String SPIKE_FILE = "entities/spikes.png";
    private static final String SPIKE_VERT = "entities/spikes_vert.png";
    private static final String UI_GreyYarn = "ui/ui_uncollected_item.png";
    private static final String UI_RedYarn = "ui/ui_collected_item.png";
    /**
     * File to texture for restarting button
     */
    private static final String RESTART_FILE = "ui/restart.png";
    /**
     * File to texture for escape button
     */
    private static final String ESC_FILE = "ui/pause.png";
    /**
     * Retro font for displaying messages
     */
    private static final String FONT_FILE = "ui/BalooThambi.ttf";
    private static final int FONT_SIZE = 64;

    /**
     * The textures for walls and platforms
     */
    protected TextureRegion spikeTile;
    protected TextureRegion spikeVertTile;
    protected TextureRegion UI_restart;
    protected TextureRegion UI_exit;
    protected FilmStrip door;
    private Stage stage;
    private float volume = 0.5f * GDXRoot.musicVol;
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
    protected PooledList<Obstacle> objects = new PooledList<>();

    /**
     * Queue for adding objects
     */
    protected PooledList<Obstacle> addQueue = new PooledList<>();
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
    private final Random rand;
    /**
     * Texture assets for NPCs
     */
    private FilmStrip npcCozyTexture;
    private FilmStrip npcCheeseTexture;
    private FilmStrip npcNervyTexture;
    private FilmStrip npcHeyoTexture;
    private FilmStrip npcSpikyTexture;
    private FilmStrip npcWelcomeTexture;
    private FilmStrip npcCozyShockTexture;
    private FilmStrip npcCheeseShockTexture;
    private FilmStrip npcNervyShockTexture;
    private FilmStrip npcHeyoShockTexture;
    private FilmStrip npcSpikyShockTexture;
    private FilmStrip npcWelcomeShockTexture;
    private FilmStrip exclamationTexture;
    private TextureRegion targetTexture;
    /**
     * Texture assets for items
     */
    private TextureRegion buttonTexture;
    private TextureRegion needleTexture;
    private TextureRegion yarnTexture;
    private TextureRegion greyYarnTexture;
    private TextureRegion fabric1Texture;
    private TextureRegion fabric2Texture;
    private TextureRegion spoolTexture;
    private TextureRegion stuffingTexture;
    private TextureRegion basketEmptyTexture;
    private TextureRegion forestMushroom;

    private TextureRegion cutIndicatorTexture;
    /**
     * List of all unique NPC textures
     */
    private final String[] npcTypes = new String[]{"cheese", "cozy", "heyo", "nervy", "spiky", "welcome"};
    private final Map<String, TextureRegion> npcs;
    private final Map<String, TextureRegion> npcShock;
    /**
     * List of all unique item textures
     */
    private final ArrayList<TextureRegion> itemTexture = new ArrayList<>();
    /**
     * List of item objects
     */
    private ArrayList<float[]> items;
    /**
     * FilmStrip objects to show player animations
     */
    private FilmStrip playerIdleAnimation;
    private FilmStrip playerSwingForwardAnimation;
    //    private FilmStrip playerSwingBackAnimation;
    private FilmStrip playerWalkingAnimation;
    private FilmStrip playerEnterAnimation;
    private FilmStrip playerExitAnimation;
    private FilmStrip playerJumpUpAnimation;
    private FilmStrip playerJumpDownAnimation;
    private FilmStrip playerDeathAnimation;

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;
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

    private GameState gameState;

    /**
     * Files for music assets
     */
    private final String CITY_MUSIC_FILE = "music/donewithwork.mp3";
    private final String VILLAGE_MUSIC_FILE = "music/capstone.mp3";
    private final String FOREST_MUSIC_FILE = "music/harp.mp3";
    private final String MOUNTAIN_MUSIC_FILE = "music/mountain_theme.mp3";
    private final String OPENING_CUTSCENE_FILE = "music/cutscene.mp3";
    private final String ENDING_CUTSCENE_FILE = "music/youshoulddosomereflecting.mp3";
    private final String TRANSITION_CUTSCENE_FILE = "music/goodnight.mp3";
    /**
     * Music object played in the game
     */
    private Music music;
    private Music walkingMusic;
    /**
     * Sound objects played in the game
     */
    private Sound jumpSound;
    private Sound collectSound;
    private Sound loseSound;
    private Sound clickSound;
    private Sound winSound;
    private Sound snipSound;
    private Sound landSound;
    private Sound trampolineLandSound;
    private Sound trampolineJumpSound;
    private Sound swingSound;
    private boolean didPlayWin;
    private boolean didPlayLose;
    private boolean didPlayJump;
    private boolean didPlayCollect;
    private boolean didPlayLand;
    private boolean didPlaySwing;
    private boolean didPlayWalk;

    /**
     * Files for region tiles
     */
    private static final String CITY_TILE_FILE = "entities/city-brick.png";
    private static final String VILLAGE_TILE_FILE = "entities/village-tile.png";
    private static final String FOREST_TILE_FILE = "entities/forest-leaves.png";
    private static final String FOREST_MUSHROOM_FILE = "entities/forest-mushroom.png";
    private static final String FOREST_SPIKES_FILE = "entities/forest-spikes.png";
    private static final String FOREST_SPIKES_VERT_FILE = "entities/forest-spikes-vert.png";
    private static final String VILLAGE_SPIKES_FILE = "entities/village-obstacle.png";
    private static final String VILLAGE_SPIKES_VERT_FILE = "entities/village-obstacle-vert.png";
    private static final String MOUNTAIN_TILE_FILE = "entities/earthtile.png";
    protected TextureRegion forestSpikeTile;
    protected TextureRegion forestSpikeVertTile;
    protected TextureRegion villageSpikeTile;
    protected TextureRegion villageSpikeVertTile;
    private ArrayList<TextureRegion> billboards;

    /**
     * Tile texture used in the game
     */
    private TextureRegion tileTexture;
    /**
     * Files for backgrounds
     */
    private final String[] CITY_BKG_FILES_LAYER_A = new String[]{"background/citylayer1.png", "background/citylayer2.png"};
    private final String[] CITY_BKG_FILES_LAYER_B = new String[]{"background/citylayer4.png", "background/citylayer5.png", "background/citylayer6.png", "background/citylayer7.png", "background/citylayer8.png", "background/citylayer9.png"};
    private final String[] CITY_BKG_FILES_LAYER_C = new String[]{"background/citylayer3.png"};
    private final String[] VILLAGE_BKG_FILES_LAYER_A = new String[]{"background/village3-1.png", "background/village3-2.png", "background/village3-3.png"};
    private final String[] VILLAGE_BKG_FILES_LAYER_B = new String[]{"background/village3-5.png", "background/village3-6.png"};
    private final String[] VILLAGE_BKG_FILES_LAYER_C = new String[]{"background/village3-4.png"};
    //    private final String[] FOREST_BKG_FILES_LAYER_A = new String[]{"background/forest-1.png", "background/forest-2.png", "background/forest-3.png"};
//    private final String[] FOREST_BKG_FILES_LAYER_B = new String[]{"background/forest-5.png", "background/forest-6.png", "background/forest-7.png"};
//    private final String[] FOREST_BKG_FILES_LAYER_C = new String[]{"background/forest-4.png"};
    private final String[] FOREST_BKG_FILES_LAYER_A = new String[]{"background/forest-layer1.png", "background/forest-layer2.png", "background/forest-layer3.png"};
    private final String[] FOREST_BKG_FILES_LAYER_B = new String[]{};
    private final String[] FOREST_BKG_FILES_LAYER_C = new String[]{};
    private final String[] MT_BKG_FILES_LAYER_A = new String[]{"background/sky-1.png"};
    private final String[] MT_BKG_FILES_LAYER_B = new String[]{"background/sky-2.png", "background/sky-3.png", "background/sky-4.png"};
    private final String[] MT_BKG_FILES_LAYER_C = new String[]{};
    /**
     * TextureRegions used in the game
     */
    private final List<TextureRegion> stillBackgroundTextures;
    private final List<TextureRegion> slightMoveBackgroundTextures;
    private final List<TextureRegion> movingBackgroundTextures;
    private Level level;
    private List<TextBox> textBoxes;

    private final String[] LEVEL1_T = new String[]{"billboard/level1-jump.png", "billboard/level1-move.png", "billboard/level1-collectibles.png", "billboard/level1-door.png"};
    private final String[] LEVEL2_T = new String[]{"billboard/level2-Z.png", "billboard/level2-trampoline.png", "billboard/Level2-extra.png"};
    final String[] LEVEL3_T = new String[]{"billboard/Level3-extra.png"};
    final String[] LEVEL4_T = new String[]{"billboard/Level4-space.png", "billboard/Level4-shift.png", "billboard/Level4-extra.png"};

    private double physicsStepAccumulator = 0.0;

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
        items = new ArrayList<>();
        level = null;
        stillBackgroundTextures = new ArrayList<>();
        slightMoveBackgroundTextures = new ArrayList<>();
        movingBackgroundTextures = new ArrayList<>();
        world = new World(gravity, false);
        rand = new Random();
        this.bounds = new Rectangle(bounds);
        this.scale = new Vector2(1, 1);
        complete = false;
        failed = false;
        debug = false;
        countdown = -1;
        this.npcs = new HashMap<>();
        this.npcShock = new HashMap<>();
    }


    public void preloadContent(AssetManager manager) {
        if (assetState != AssetState.EMPTY) {
            return;
        }
        assetState = AssetState.LOADING;
        manager.load(UI_GreyYarn, Texture.class);
        assets.add(UI_GreyYarn);
        manager.load(UI_RedYarn, Texture.class);
        assets.add(UI_RedYarn);
        manager.load(GATE, Texture.class);
        assets.add(GATE);
        manager.load(NEEDLE, Texture.class);
        assets.add(NEEDLE);
        manager.load(BUTTON, Texture.class);
        assets.add(BUTTON);
        manager.load(YARN, Texture.class);
        assets.add(YARN);
        manager.load(GREY_YARN, Texture.class);
        assets.add(GREY_YARN);
        manager.load(FABRIC_1, Texture.class);
        assets.add(FABRIC_1);
        manager.load(FABRIC_2, Texture.class);
        assets.add(FABRIC_2);
        manager.load(SPOOL, Texture.class);
        assets.add(SPOOL);
        manager.load(STUFFING, Texture.class);
        assets.add(STUFFING);
        manager.load(BASKET_EMPTY, Texture.class);
        assets.add(BASKET_EMPTY);
        manager.load(CITY_TILE_FILE, Texture.class);
        assets.add(CITY_TILE_FILE);
        manager.load(VILLAGE_TILE_FILE, Texture.class);
        assets.add(VILLAGE_TILE_FILE);
        manager.load(FOREST_TILE_FILE, Texture.class);
        assets.add(FOREST_TILE_FILE);
        manager.load(MOUNTAIN_TILE_FILE, Texture.class);
        assets.add(MOUNTAIN_TILE_FILE);
        manager.load(FOREST_SPIKES_FILE, Texture.class);
        assets.add(FOREST_SPIKES_FILE);
        manager.load(FOREST_SPIKES_VERT_FILE, Texture.class);
        assets.add(FOREST_SPIKES_VERT_FILE);
        manager.load(VILLAGE_SPIKES_FILE, Texture.class);
        assets.add(VILLAGE_SPIKES_FILE);
        manager.load(VILLAGE_SPIKES_VERT_FILE, Texture.class);
        assets.add(VILLAGE_SPIKES_VERT_FILE);
        manager.load(SPIKE_FILE, Texture.class);
        assets.add(SPIKE_FILE);
        manager.load(SPIKE_VERT, Texture.class);
        assets.add(SPIKE_VERT);
        manager.load(RESTART_FILE, Texture.class);
        assets.add(RESTART_FILE);
        manager.load(ESC_FILE, Texture.class);
        assets.add(ESC_FILE);
        manager.load(FOREST_MUSHROOM_FILE, Texture.class);
        assets.add(FOREST_MUSHROOM_FILE);
        for (String s : CITY_BKG_FILES_LAYER_A) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : CITY_BKG_FILES_LAYER_B) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : CITY_BKG_FILES_LAYER_C) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : LEVEL1_T) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : LEVEL2_T) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : LEVEL3_T) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : LEVEL4_T) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : VILLAGE_BKG_FILES_LAYER_A) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : VILLAGE_BKG_FILES_LAYER_B) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : VILLAGE_BKG_FILES_LAYER_C) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : FOREST_BKG_FILES_LAYER_A) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : FOREST_BKG_FILES_LAYER_B) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : FOREST_BKG_FILES_LAYER_C) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : MT_BKG_FILES_LAYER_A) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : MT_BKG_FILES_LAYER_B) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        for (String s : MT_BKG_FILES_LAYER_C) {
            assets.add(s);
            manager.load(s, Texture.class);
        }
        manager.load(CUT_INDICATOR_FILE, Texture.class);
        assets.add(CUT_INDICATOR_FILE);
        // Load Player Animations
        manager.load(PLAYER_IDLE_ANIMATION, Texture.class);
        assets.add(PLAYER_IDLE_ANIMATION);
        manager.load(PLAYER_WALKING_ANIMATION_FILE, Texture.class);
        assets.add(PLAYER_WALKING_ANIMATION_FILE);
        manager.load(PLAYER_ENTER, Texture.class);
        assets.add(PLAYER_ENTER);
        manager.load(PLAYER_EXIT, Texture.class);
        assets.add(PLAYER_EXIT);
        manager.load(PLAYER_JUMP_UP, Texture.class);
        assets.add(PLAYER_JUMP_UP);
        manager.load(PLAYER_JUMP_DOWN, Texture.class);
        assets.add(PLAYER_JUMP_DOWN);
        manager.load(PLAYER_SWING_FORWARD, Texture.class);
        assets.add(PLAYER_SWING_FORWARD);
        manager.load(PLAYER_SWING_FREE, Texture.class);
        assets.add(PLAYER_SWING_FREE);
        manager.load(PLAYER_SWING_BACK, Texture.class);
        assets.add(PLAYER_SWING_BACK);
        manager.load(PLAYER_DEATH, Texture.class);
        assets.add(PLAYER_DEATH);

        // Load NPC Animations
        manager.load(NPC_CHEESE, Texture.class);
        assets.add(NPC_CHEESE);
        manager.load(NPC_CHEESE_SHOCK, Texture.class);
        assets.add(NPC_CHEESE_SHOCK);
        manager.load(NPC_COZY, Texture.class);
        assets.add(NPC_COZY);
        manager.load(NPC_COZY_SHOCK, Texture.class);
        assets.add(NPC_COZY_SHOCK);
        manager.load(NPC_NERVY, Texture.class);
        assets.add(NPC_NERVY);
        manager.load(NPC_NERVY_SHOCK, Texture.class);
        assets.add(NPC_NERVY_SHOCK);
        manager.load(NPC_SPIKY, Texture.class);
        assets.add(NPC_SPIKY);
        manager.load(NPC_SPIKY_SHOCK, Texture.class);
        assets.add(NPC_SPIKY_SHOCK);
        manager.load(NPC_HEYO, Texture.class);
        assets.add(NPC_HEYO);
        manager.load(NPC_HEYO_SHOCK, Texture.class);
        assets.add(NPC_HEYO_SHOCK);
        manager.load(NPC_WELCOME, Texture.class);
        assets.add(NPC_WELCOME);
        manager.load(NPC_WELCOME_SHOCK, Texture.class);
        assets.add(NPC_WELCOME_SHOCK);
        manager.load(EXCLAMATION, Texture.class);
        assets.add(EXCLAMATION);
        manager.load(TARGET, Texture.class);
        assets.add(TARGET);

        // Load Sound Assets
        loadAsset(JUMP_FILE, Sound.class, manager);
        loadAsset(COLLECT_FILE, Sound.class, manager);
        loadAsset(WIN_FILE, Sound.class, manager);
        loadAsset(LOSE_FILE, Sound.class, manager);
        loadAsset(CLICK_FILE, Sound.class, manager);
        loadAsset(SNIP_FILE, Sound.class, manager);
        loadAsset(HOVER_FILE, Sound.class, manager);
        loadAsset(LAND_FILE, Sound.class, manager);
        loadAsset(TRAMP_LAND_FILE, Sound.class, manager);
        loadAsset(TRAMP_JUMP_FILE, Sound.class, manager);
        loadAsset(HANG_FILE, Sound.class, manager);
//        loadAsset(SWING_FILE, Music.class, manager);

        // Load Music
        manager.load(CITY_MUSIC_FILE, Music.class);
        assets.add(CITY_MUSIC_FILE);
        manager.load(VILLAGE_MUSIC_FILE, Music.class);
        assets.add(VILLAGE_MUSIC_FILE);
        manager.load(FOREST_MUSIC_FILE, Music.class);
        assets.add(FOREST_MUSIC_FILE);
        manager.load(MOUNTAIN_MUSIC_FILE, Music.class);
        assets.add(MOUNTAIN_MUSIC_FILE);
        manager.load(OPENING_CUTSCENE_FILE, Music.class);
        assets.add(OPENING_CUTSCENE_FILE);
        manager.load(ENDING_CUTSCENE_FILE, Music.class);
        assets.add(ENDING_CUTSCENE_FILE);
        manager.load(TRANSITION_CUTSCENE_FILE, Music.class);
        assets.add(TRANSITION_CUTSCENE_FILE);
        manager.load(WALKING_CITY_FILE, Music.class);
        assets.add(WALKING_CITY_FILE);
        manager.load(WALKING_VILLAGE_FILE, Music.class);
        assets.add(WALKING_VILLAGE_FILE);
        manager.load(WALKING_FOREST_FILE, Music.class);
        assets.add(WALKING_FOREST_FILE);
        manager.load(WALKING_MT_FILE, Music.class);
        assets.add(WALKING_MT_FILE);

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);
    }

    public void initializeContent(AssetManager manager) {
        billboards = new ArrayList<>();
        String type = level.getType();
        stillBackgroundTextures.clear();
        slightMoveBackgroundTextures.clear();
        movingBackgroundTextures.clear();
        itemTexture.clear();

        switch (level.getLevel()) {
            case 1:
                for (String s : LEVEL1_T) {
                    billboards.add(createTexture(manager, s, false));
                }
                break;
            case 2:
                for (String s : LEVEL2_T) {
                    billboards.add(createTexture(manager, s, false));
                }
                break;
            case 3:
                for (String s : LEVEL3_T) {
                    billboards.add(createTexture(manager, s, false));
                }
                break;
            case 4:
                for (String s : LEVEL4_T) {
                    billboards.add(createTexture(manager, s, false));
                }
                break;
        }

        switch (type) {
            case "city":
                music = manager.get(CITY_MUSIC_FILE, Music.class);
                tileTexture = createTexture(manager, CITY_TILE_FILE, false);
                for (String s : CITY_BKG_FILES_LAYER_A) {
                    stillBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : CITY_BKG_FILES_LAYER_B) {
                    slightMoveBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : CITY_BKG_FILES_LAYER_C) {
                    movingBackgroundTextures.add(createTexture(manager, s, false));
                }
                if (level.getLevel() == 1) {
                    itemTexture.add(createTexture(manager, BASKET_EMPTY, false));
                } else {
                    for (String s : cityItems) {
                        itemTexture.add(createTexture(manager, s, false));
                    }
                }
                walkingMusic = manager.get(WALKING_CITY_FILE, Music.class);
                break;
            case "village":
                music = manager.get(VILLAGE_MUSIC_FILE, Music.class);
                tileTexture = createTexture(manager, VILLAGE_TILE_FILE, false);
                for (String s : VILLAGE_BKG_FILES_LAYER_A) {
                    stillBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : VILLAGE_BKG_FILES_LAYER_B) {
                    slightMoveBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : VILLAGE_BKG_FILES_LAYER_C) {
                    movingBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : villageItems) {
                    itemTexture.add(createTexture(manager, s, false));
                }
                walkingMusic = manager.get(WALKING_VILLAGE_FILE, Music.class);
                break;
            case "forest":
                music = manager.get(FOREST_MUSIC_FILE, Music.class);
                tileTexture = createTexture(manager, FOREST_TILE_FILE, false);
                for (String s : FOREST_BKG_FILES_LAYER_A) {
                    stillBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : FOREST_BKG_FILES_LAYER_B) {
                    slightMoveBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : FOREST_BKG_FILES_LAYER_C) {
                    movingBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : forestItems) {
                    itemTexture.add(createTexture(manager, s, false));
                }
                walkingMusic = manager.get(WALKING_FOREST_FILE, Music.class);
                break;
            case "mountain":
                music = manager.get(MOUNTAIN_MUSIC_FILE, Music.class);
                tileTexture = createTexture(manager, MOUNTAIN_TILE_FILE, false);
                for (String s : MT_BKG_FILES_LAYER_A) {
                    stillBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : MT_BKG_FILES_LAYER_B) {
                    slightMoveBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : MT_BKG_FILES_LAYER_C) {
                    movingBackgroundTextures.add(createTexture(manager, s, false));
                }
                for (String s : mountainItems) {
                    itemTexture.add(createTexture(manager, s, false));
                }
                walkingMusic = manager.get(WALKING_MT_FILE, Music.class);
        }
        music.setVolume(0.5f * GDXRoot.musicVol);
        music.play();
        music.setLooping(true);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }
        playerSwingForwardAnimation = createFilmStrip(manager, PLAYER_SWING_FORWARD, 1, 7, 7, false);
        playerIdleAnimation = createFilmStrip(manager, PLAYER_IDLE_ANIMATION, 1, 24, 24, true);
        playerIdleAnimation.setFrameDuration(0.09f);
        playerEnterAnimation = createFilmStrip(manager, PLAYER_ENTER, 1, 21, 21, true);
        playerExitAnimation = createFilmStrip(manager, PLAYER_EXIT, 1, 15, 15, true);
        playerExitAnimation.setFrameDuration(0.045f);
        playerJumpUpAnimation = createFilmStrip(manager, PLAYER_JUMP_UP, 1, 8, 8, false);
        playerJumpDownAnimation = createFilmStrip(manager, PLAYER_JUMP_DOWN, 1, 14, 14, false);
        playerDeathAnimation = createFilmStrip(manager, PLAYER_DEATH, 1, 24, 24, false);
        playerDeathAnimation.setFrameDuration(0.1f);
        playerWalkingAnimation = createFilmStrip(manager, PLAYER_WALKING_ANIMATION_FILE, 1, 17, 17, true);
        playerWalkingAnimation.setFrameDuration(0.045f);
        npcCheeseTexture = createFilmStrip(manager, NPC_CHEESE, 1, 49, 49, true);
        npcCheeseTexture.setFrameDuration(0.1f);
        npcCozyTexture = createFilmStrip(manager, NPC_COZY, 1, 33, 33, true);
        npcCozyTexture.setFrameDuration(0.1f);
        npcNervyTexture = createFilmStrip(manager, NPC_NERVY, 1, 33, 33, true);
        npcNervyTexture.setFrameDuration(0.2f);
        npcHeyoTexture = createFilmStrip(manager, NPC_HEYO, 1, 4, 4, true);
        npcHeyoTexture.setFrameDuration(0.2f);
        npcSpikyTexture = createFilmStrip(manager, NPC_SPIKY, 1, 16, 16, true);
        npcSpikyTexture.setFrameDuration(0.1f);
        npcWelcomeTexture = createFilmStrip(manager, NPC_WELCOME, 1, 7, 7, true);
        npcWelcomeTexture.setFrameDuration(0.13f);
        npcHeyoShockTexture = createFilmStrip(manager, NPC_HEYO_SHOCK, 1, 9, 9, true);
        npcHeyoShockTexture.setFrameDuration(0.1f);
        npcCheeseShockTexture = createFilmStrip(manager, NPC_CHEESE_SHOCK, 1, 9, 9, true);
        npcCheeseShockTexture.setFrameDuration(0.1f);
        npcCozyShockTexture = createFilmStrip(manager, NPC_COZY_SHOCK, 1, 12, 12, true);
        npcCozyShockTexture.setFrameDuration(0.1f);
        npcNervyShockTexture = createFilmStrip(manager, NPC_NERVY_SHOCK, 1, 21, 21, true);
        npcNervyShockTexture.setFrameDuration(0.1f);
        npcSpikyShockTexture = createFilmStrip(manager, NPC_SPIKY_SHOCK, 1, 17, 17, true);
        npcSpikyShockTexture.setFrameDuration(0.1f);
        npcWelcomeShockTexture = createFilmStrip(manager, NPC_WELCOME_SHOCK, 1, 13, 13, true);
        npcWelcomeShockTexture.setFrameDuration(0.1f);
        exclamationTexture = createFilmStrip(manager, EXCLAMATION, 1, 5, 5, true);
        targetTexture = createTexture(manager, TARGET, false);
        npcs.put("cheese", npcCheeseTexture);
        npcs.put("cozy", npcCozyTexture);
        npcs.put("nervy", npcNervyTexture);
        npcs.put("heyo", npcHeyoTexture);
        npcs.put("spiky", npcSpikyTexture);
        npcs.put("welcome", npcWelcomeTexture);
        npcShock.put("cheese", npcCheeseShockTexture);
        npcShock.put("cozy", npcCozyShockTexture);
        npcShock.put("nervy", npcNervyShockTexture);
        npcShock.put("heyo", npcHeyoShockTexture);
        npcShock.put("spiky", npcSpikyShockTexture);
        npcShock.put("welcome", npcWelcomeShockTexture);
        yarnTexture = createTexture(manager, YARN, false);
        greyYarnTexture = createTexture(manager, GREY_YARN, false);
        basketEmptyTexture = createTexture(manager, BASKET_EMPTY, false);
        door = createFilmStrip(manager, GATE, 1, 11, 11, false);
        cutIndicatorTexture = createTexture(manager, CUT_INDICATOR_FILE, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, LAND_FILE);
        sounds.allocate(manager, HOVER_FILE);
        sounds.allocate(manager, COLLECT_FILE);
        sounds.allocate(manager, WIN_FILE);
        sounds.allocate(manager, LOSE_FILE);
        sounds.allocate(manager, CLICK_FILE);
        sounds.allocate(manager, SNIP_FILE);
        sounds.allocate(manager, TRAMP_JUMP_FILE);
        sounds.allocate(manager, TRAMP_LAND_FILE);
        sounds.allocate(manager, HANG_FILE);
        trampolineLandSound = manager.get(TRAMP_LAND_FILE);
        trampolineJumpSound = manager.get(TRAMP_JUMP_FILE);
        landSound = manager.get(LAND_FILE);
        swingSound = manager.get(HANG_FILE);
        jumpSound = manager.get(JUMP_FILE);
        collectSound = manager.get(COLLECT_FILE);
        winSound = manager.get(WIN_FILE);
        loseSound = manager.get(LOSE_FILE);
        clickSound = manager.get(CLICK_FILE);
        snipSound = manager.get(SNIP_FILE);
        forestSpikeTile = createTexture(manager, FOREST_SPIKES_FILE, false);
        forestMushroom = createTexture(manager, FOREST_MUSHROOM_FILE, false);
        forestSpikeVertTile = createTexture(manager, FOREST_SPIKES_VERT_FILE, false);
        villageSpikeTile = createTexture(manager, VILLAGE_SPIKES_FILE, false);
        villageSpikeVertTile = createTexture(manager, VILLAGE_SPIKES_VERT_FILE, false);
        spikeTile = createTexture(manager, SPIKE_FILE, false);
        spikeVertTile = createTexture(manager, SPIKE_VERT, false);
        UI_restart = createTexture(manager, RESTART_FILE, false);
        UI_exit = createTexture(manager, ESC_FILE, false);
        if (manager.isLoaded(FONT_FILE)) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_FILE));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 20;
            displayFont = generator.generateFont(parameter);
            //  displayFont = manager.get(FONT_FILE, BitmapFont.class);
        } else {
            displayFont = null;
        }
        assetState = AssetState.COMPLETE;
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
     * References to physics objects for the game
     */
    private Person player;

    private RopeJointDef ropeJointDef;

    private RevoluteJointDef revoluteJointDef;

    private PlayerRope playerRope;

    private RopeQueryCallback ropeQueryCallback;

    private CuttingCallback cuttingCallback;

    private Level currentlevel;

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
        ropeJointDef = new RopeJointDef();
        revoluteJointDef = new RevoluteJointDef();
        ropeQueryCallback = new RopeQueryCallback();
        cuttingCallback = new CuttingCallback();
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
        ropeQueryCallback.setPlayer(player);
        ropeQueryCallback.reset();
        cuttingCallback.setPlayer(player);
        cuttingCallback.reset();
        gameState = GameState.PLAYING;
        playerDeathAnimation.setFrame(0);
        playerSwingForwardAnimation.refresh();
        playerJumpUpAnimation.refresh();
        playerJumpDownAnimation.refresh();
        didPlaySwing = false;
        didPlayLand = false;
        door.refresh();
        didPlayWin = false;
        didPlayLose = false;
        didPlayCollect = false;
        didPlayJump = false;
        didPlayWalk = false;
        volume = 0.5f * GDXRoot.musicVol;
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
        currentlevel = level;
        Vector2 playerPos = level.getPlayerPos();
        List<Tile> tiles = level.getTiles();
        List<Tile> spikes = level.getSpikes();
        items = (ArrayList<float[]>) level.getItems();
        List<NpcData> npcData = level.getNpcData();
        this.textBoxes = level.getText();


        // Create main dude
        float dWidth = playerIdleAnimation.getRegionWidth() / 2.2f / scale.x;
        float dHeight = playerIdleAnimation.getRegionHeight() / scale.y;
        player = new Person(playerPos.x, playerPos.y, dWidth, dHeight, "player", "playerSensor");
        Filter playerFilter = new Filter();
        playerFilter.categoryBits = CollisionFilterConstants.CATEGORY_PLAYER.getID();
        playerFilter.maskBits = CollisionFilterConstants.MASK_PLAYER.getID();
        player.setFilterData(playerFilter);
        player.setDrawScale(scale);
        player.setTexture(playerIdleAnimation);
        float[] points = new float[]{0f, 0f, 0f, 0.01f, 0.01f, 0.01f, 0.01f, 0f};

        float xpos = player.getX() * scale.x > 350 ? player.getX() * scale.x : 350;
        float ypos = player.getY() * scale.y > 240 ? player.getY() * scale.y : 240;
        lastpos = new Vector2(xpos, ypos);
        lastviewport = new Vector2(canvas.getWidth() * 0.6f, canvas.getHeight() * 0.6f);
        canvas.moveCamera(lastpos.x, lastpos.y);
        canvas.changeViewport(lastviewport.x, lastviewport.y);
        isZoomed = true;
        direction = null;
        targetViewPort = null;
        // Create exit door
        createGate(points, level.getExitPos().x, level.getExitPos().y, door);
        //add player
        addObject(player);
        // Create NPCs
        for (int i = 0; i < npcData.size(); i += 2) {
            NpcData curr = npcData.get(i);
            NpcData next = npcData.get(i + 1);
            createCouple(curr, next, i);
        }

        // Create items
        for (int i = 0; i < items.size(); i++) {
            float[] curr = items.get(i);
            createItem(curr[0], curr[1], i, itemTexture.get(i));
        }

        // Create platforms
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            if (tile.isSliding()) {
                createSlidingTile(tile.getCorners(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), level.getType(), "tile" + i, 1f, tile.getLeft(), tile.getRight());
            } else {
                createTile(tile.getCorners(), tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), level.getType(), "tile" + i, 1f);
            }
        }


        if (level.getType().contains("forest")) {
            for (Tile spike : spikes) {
                TextureRegion forestSpikeTexture = (spike.getDirection().equals("up") || spike.getDirection().equals("down")) ? forestSpikeTile : forestSpikeVertTile;
                createSpike(spike.getCorners(), spike.getX(), spike.getY(), spike.getDirection(), "spike", 1f, forestSpikeTexture);
            }
        } else if (level.getType().contains("village")) {
            for (Tile spike : spikes) {
                TextureRegion villageSpikeTexture = (spike.getDirection().equals("up") || spike.getDirection().equals("down")) ? villageSpikeTile : villageSpikeVertTile;
                createSpike(spike.getCorners(), spike.getX(), spike.getY(), spike.getDirection(), "spike", 1f, villageSpikeTexture);
            }
        } else {
            for (Tile spike : spikes) {
                TextureRegion spikeTexture = (spike.getDirection().equals("up") || spike.getDirection().equals("down")) ? spikeTile : spikeVertTile;
                createSpike(spike.getCorners(), spike.getX(), spike.getY(), spike.getDirection(), "spike", 1f, spikeTexture);
            }
        }
    }

    public Stone createTile(float[] points, float x, float y, float width, float height, String type, String name, float sc) {
        Stone tile = new Stone(points, x, y, width, height, type, sc);
        tile.setBodyType(BodyDef.BodyType.StaticBody);
        tile.setDensity(BASIC_DENSITY);
        tile.setFriction(BASIC_FRICTION);
        tile.setRestitution(BASIC_RESTITUTION);
        tile.setDrawScale(scale);
        tile.setTexture(tileTexture);
        tile.setName(name);
        addObject(tile);
        return tile;
    }


    public void createGate(float[] points, float x, float y, FilmStrip texture) {
        Gate gate = new Gate(texture, points, x, y);
        door.setFrameDuration(0.035f);
        gate.setBodyType(BodyDef.BodyType.StaticBody);
        gate.setFriction(0f);
        gate.setRestitution(BASIC_RESTITUTION);
        gate.setDrawScale(scale);
        gate.setName("gate");
        addObject(gate);
    }

    public void createSpike(float[] points, float x, float y, String direction, String name, float sc, TextureRegion tex) {
        Spikes spike = new Spikes(points, x, y, direction, sc);
        spike.setBodyType(BodyDef.BodyType.StaticBody);
        spike.setFriction(2000f);
        spike.setRestitution(BASIC_RESTITUTION);
        spike.setDrawScale(scale);
        spike.setTexture(tex);
        spike.setName(name);
        addObject(spike);
    }

    public void createItem(float x, float y, int id, TextureRegion tex) {
//        int n = rand.nextInt(itemTexture.size());
//        TextureRegion randTex = itemTexture.get(n);
        Vector2 dimensions = getScaledDimensions(tex);
        Item item = new Item(x, y, dimensions.x, dimensions.y, id);
        item.setTexture(tex);
        item.setDrawScale(scale);
        addObject(item);
    }

    /**
     * Retrieve the size of a model scaled
     * to fit the world units
     *
     * @param texture the texture of the model
     * @return physical dimensions of the model in world units
     */
    private Vector2 getScaledDimensions(TextureRegion texture) {
        float dWidth = texture.getRegionWidth() / scale.x;
        float dHeight = texture.getRegionHeight() / scale.y;
        return new Vector2(dWidth, dHeight);
    }


    public void createCouple(NpcData curr, NpcData next, int id) {
        float x1 = curr.getPos()[0], y1 = curr.getPos()[1], x2 = next.getPos()[0], y2 = next.getPos()[1];
        float[] points = new float[]{0f, 0f, 0f, .5f, .5f, .5f, .5f, 0f};
        int n1 = rand.nextInt(npcTypes.length);
        int n2 = rand.nextInt(npcTypes.length);
        while (n2 == n1) n2 = rand.nextInt(npcTypes.length);
        String randType1 = npcTypes[n1];
        String randType2 = npcTypes[n2];
        TextureRegion randTex1 = npcs.get(randType1);
        TextureRegion randTex2 = npcs.get(randType2);
        Stone leftTile;
        Stone rightTile;
        if (curr.isSliding()) {
            leftTile = createSlidingTile(points, x1 + .3f, y1 - 0.5f, 0.5f, 0.5f, currentlevel.getType(), "tile", 1f, curr.getLeft(), curr.getRight());
        } else if (curr.isRotating()) {
            leftTile = createRotatingTile(points, x1 + .3f, y1 - 0.5f, 0.5f, 0.5f, currentlevel.getType(), "tile", 1f, curr.getRotatingCenter(), curr.getRotatingDegree());
        } else {
            leftTile = createTile(points, x1 + .3f, y1 - 0.5f, 0.5f, 0.5f, currentlevel.getType(), "tile", 1f);
        }
        if (next.isSliding()) {
            rightTile = createSlidingTile(points, x2 + .3f, y2 - 0.5f, 0.5f, 0.5f, currentlevel.getType(), "tile", 1f, next.getLeft(), next.getRight());
        } else {
            rightTile = createTile(points, x2 + .3f, y2 - 0.5f, 0.5f, 0.5f, currentlevel.getType(), "tile", 1f);
        }
        Couple couple = new Couple(x1, y1, x2, y2, randType1, randType2, randTex1, randTex2, scale, leftTile, rightTile, id);
        addObject(couple);
    }

    public Stone createRotatingTile(float[] points, float x, float y, float width, float height, String type, String name, float sc,
                                    float[] rotatingCenter, float rotatingDegree) {
        Stone tile = new Stone(points, x, y, width, height, type, sc, rotatingCenter, rotatingDegree);
        tile.setBodyType(BodyDef.BodyType.KinematicBody);
        tile.setDensity(BASIC_DENSITY);
        tile.setFriction(BASIC_FRICTION);
        tile.setRestitution(BASIC_RESTITUTION);
        tile.setDrawScale(scale);
        tile.setTexture(tileTexture);
        tile.setName(name);
        addObject(tile);
        return tile;
    }

    public Stone createSlidingTile(float[] points, float x, float y, float width, float height, String type,
                                   String name, float sc,
                                   float[] leftPos, float[] rightPos) {
        Stone tile = new Stone(points, x, y, width, height, type, sc, leftPos, rightPos);
        tile.setBodyType(BodyDef.BodyType.KinematicBody);
        tile.setDensity(BASIC_DENSITY);
        tile.setRestitution(BASIC_RESTITUTION);
        tile.setDrawScale(scale);
        tile.setTexture(tileTexture);
        tile.setName(name);
        addObject(tile);
        return tile;
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
            if (input.isCameraZoom()) {
                isZoomed = false;
                float x = canvas.getWidth() / 2, y = canvas.getHeight() / 2;
                if (player.getX() * scale.x > canvas.getWidth() * 0.7f) {
                    x = canvas.getWidth();
                }
                if (player.getY() * scale.y > canvas.getHeight() * 0.7f) {
                    y = canvas.getHeight();
                }
                direction = new Vector2(x, y);
                targetViewPort = new Vector2(canvas.getWidth(), canvas.getHeight());
            }
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
            } else if (!player.isAlive() || player.won()) {
                if (player.isAttached() && playerRope != null) {
                    destroyPlayerRope();
                }
                if (player.won() && !didPlayWin) {
                    winSound.play(GDXRoot.soundVol);
                    didPlayWin = true;
                }
                if (!player.isAlive() && !didPlayLose) {
                    loseSound.play(GDXRoot.soundVol);
                    didPlayLose = true;
                }
                timeSeconds += Gdx.graphics.getRawDeltaTime();
                if (player.won()) {
                    if (volume > 0.00f)
                        volume -= timeSeconds * 0.005f;
                    else
                        volume = 0.0f;
                    music.setVolume(Math.abs(volume));
                }
                if (timeSeconds > period) {
                    timeSeconds = 0;
                    if (player.won())
                        listener.exitScreen(this, LevelTransitionMode.INTO_TRANSITION);
                    else
                        reset();
                }
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

        if (!isFailure() && player.getY() < -1 && !didPlayLose) {
            loseSound.play(GDXRoot.soundVol);
            didPlayLose = true;
            setFailure(true);
            return false;
        }

        return true;
    }

    public void updatePaused(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            gameState = GameState.PLAYING;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            exitToSelector();
        }
    }


    public void updateZoom(float dt) {

        float xpos = player.getX() * scale.x > 350 ? player.getX() * scale.x : 350;
        float ypos = player.getY() * scale.y > 240 ? player.getY() * scale.y : 240;


        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            direction = new Vector2(xpos, ypos);
            targetViewPort = new Vector2(canvas.getWidth() * 0.6f, canvas.getHeight() * 0.6f);
            isZoomed = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !isZoomed && lastpos.x < canvas.getWidth() / 2 + 500) {
            direction = new Vector2(lastpos.x + 20, lastpos.y);
            targetViewPort = new Vector2(canvas.getWidth(), canvas.getHeight());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !isZoomed && lastpos.x > canvas.getWidth() / 2 - 300) {
            direction = new Vector2(lastpos.x - 20, lastpos.y);
            targetViewPort = new Vector2(canvas.getWidth(), canvas.getHeight());
        }
        //    System.out.println((stillBackgroundTextures.get(0).getRegionHeight()/ stillBackgroundTextures.get(0).getRegionWidth())
        //    * canvas.getWidth());
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !isZoomed && lastpos.y <
                (((float) stillBackgroundTextures.get(0).getRegionHeight() / (float) stillBackgroundTextures.get(0).getRegionWidth())
                        * canvas.getWidth() - canvas.getHeight() * 0.55f)) {
            direction = new Vector2(lastpos.x, lastpos.y + 20);
            targetViewPort = new Vector2(canvas.getWidth(), canvas.getHeight());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !isZoomed && lastpos.y > canvas.getHeight() / 2 - 200) {
            direction = new Vector2(lastpos.x, lastpos.y - 20);
            targetViewPort = new Vector2(canvas.getWidth(), canvas.getHeight());
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            reset();
        }

        if (direction != null && targetViewPort != null) {
            if (targetViewPort.x == lastviewport.x && targetViewPort.y == lastviewport.y
                    && direction.x == lastpos.x && lastpos.y == direction.y) {
                if (isZoomed) gameState = GameState.PLAYING;
            }
            float xz = lastviewport.x, yz = lastviewport.y;
            float xp = lastpos.x, yp = lastpos.y;
            if (xz >= targetViewPort.x - 10 && xz <= targetViewPort.x + 10
                    && yz >= targetViewPort.y - 10 && yz <= targetViewPort.y + 10) {
                xz = targetViewPort.x;
                yz = targetViewPort.y;

            } else {
                if (xz < targetViewPort.x) {
                    xz += 15;
                }
                if (xz > targetViewPort.x) {
                    xz -= 15;
                }
                if (yz < targetViewPort.y) {
                    yz += 10;
                }
                if (yz > targetViewPort.y) {
                    yz -= 10;
                }
            }
            boolean xr = false, yr = false;
            if (xp >= direction.x - 6 && xp <= direction.x + 6) {
                xp = direction.x;

            } else if (xp < direction.x) {
                xp += 8;
            } else {
                xp -= 8;
            }

            if (yp >= direction.y - 6 && yp <= direction.y + 6) {
                yp = direction.y;

            } else if (yp < direction.y) {
                yp += 8;
            } else {
                yp -= 8;
            }

            canvas.moveCamera(xp, yp);
            canvas.changeViewport(xz, yz);

            lastpos = new Vector2(xp, yp);
            lastviewport = new Vector2(xz, yz);

        }
    }


    private void destroyPlayerRope() {
        playerRope.markRemoved(true);
        player.setTarget(null);
        playerRope = null;
        world.destroyJoint(player.getSwingJoint1());
        player.setAttached(false);
        player.setSwingJoint(null);
        player.resetShootCooldown();
    }


    private void setJumpUpAnimationFrame(float dt) {
        if (playerJumpUpAnimation.isRefreshed()) {
            float vy = player.getVY();
            float yDecel = 9.8f;
            float risingTime = vy / yDecel;
            playerJumpUpAnimation.setFrameDuration(risingTime / (float) playerJumpUpAnimation.getSize());
            playerJumpUpAnimation.setRefreshed(false);
        }
        playerJumpUpAnimation.setElapsedTime(dt);
        if (playerJumpUpAnimation.getFrameDuration() * 8f < 0.25f) {
            playerJumpUpAnimation.setFrame(6);
        } else {
            playerJumpUpAnimation.updateFrame();
        }
    }

    private void setJumpDownAnimationFrame(float dt) {
        if (playerJumpDownAnimation.isRefreshed()) {
            float risingTime = 1f;//estimate 1s
            playerJumpDownAnimation.setFrameDuration(risingTime / (float) playerJumpUpAnimation.getSize());
            playerJumpDownAnimation.setRefreshed(false);
        }
        playerJumpDownAnimation.setElapsedTime(dt);
        playerJumpDownAnimation.updateFrame();
    }

    private void setSwingingAnimations(float dt) {
        float eps = 0f;
        // playerVX: left (-) right (+)
        float npcPosX = playerRope.getNPC();
        float vx = (player.isFacingRight() ? 1 : -1) * player.getVX();
        boolean forwardHalf = player.getX() < npcPosX;
        if (player.isFallingBack()) {
            playerSwingForwardAnimation.refresh();
        }
        if (vx > eps) {
            playerSwingForwardAnimation.setReversed(false);
            if (playerSwingForwardAnimation.isRefreshed()) {
                float risingTime = 1.5f;//estimate 1s
                playerSwingForwardAnimation.setFrameDuration(risingTime / (float) playerSwingForwardAnimation.getSize());
                playerSwingForwardAnimation.setRefreshed(false);
            }
            playerSwingForwardAnimation.setElapsedTime(dt);
            playerSwingForwardAnimation.updateFrame();
            player.setTexture(playerSwingForwardAnimation);
        } else if (vx < -eps) {
            player.setFallingBack(!playerSwingForwardAnimation.isReversed());
            playerSwingForwardAnimation.setReversed(true);
            playerSwingForwardAnimation.setElapsedTime(dt);
            playerSwingForwardAnimation.updateFrame();
            player.setTexture(playerSwingForwardAnimation);
        }

    }

    private void setShockNpc(NpcPerson n, String command) {
        if (command.equals("onNpc")) {
            if (n != null) {
                String type = n.getType();
                if (player.isOnNpc()) {
                    TextureRegion shockTex = npcShock.get(type);
                    n.setTexture(shockTex);
                } else if (n.getCouple() != null) {
                    TextureRegion normalTex = npcs.get(type);
                    n.setTexture(normalTex);
                }
            }
        } else if (command.equals("cutrope")) {
            String type = n.getType();
            TextureRegion shockTex = npcShock.get(type);
            n.setTexture(shockTex);
        }
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
//        System.out.println(dt);
        // Process actions in object model
        if ((Gdx.input.isTouched() && Gdx.input.getX() >= 800
                && Gdx.input.getX() <= 950 && Gdx.input.getY() >= 48 && Gdx.input.getY() <= 132)
                || (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            listener.exitScreen(this, PauseMode.INTO_PAUSE);
        }

        boolean isGodModeKeyPressed = Gdx.input.isKeyPressed(Input.Keys.G);

        if (player.isGodModeActivated() && isGodModeKeyPressed) {
            player.setGodMode(false);
            player.setBodyType(BodyDef.BodyType.DynamicBody);
        } else if (!player.isGodModeActivated() && isGodModeKeyPressed) {
            player.setGodMode(true);
            player.setBodyType(BodyDef.BodyType.KinematicBody);
        }


        Vector2 playerPosition = player.getPosition();
        // If player has collected all items, indicate so
        player.setCollectedAll(items.size() == player.getInventory().size());
        if (player.won()) {
            playerExitAnimation.setElapsedTime(dt);
            playerExitAnimation.updateFrame();
            player.setTexture(playerExitAnimation);
            door.setElapsedTime(dt);
            door.updateFrame();

        } else if (player.isAlive()) {
            player.setHorizontalMovement(InputController.getInstance().getHorizontal() * player.getForce());
            player.setVerticalMovement(InputController.getInstance().getVertical() * player.getForce());
            player.setJumping(InputController.getInstance().didPrimary());
            player.setShooting(InputController.getInstance().didTertiary());
            player.setCutting(InputController.getInstance().didSecondary());
            player.applyForce();

            if (!player.isAttached()) {
                playerSwingForwardAnimation.refresh();
            }

            if (!player.isGrounded() && !player.isAttached()) {//rising
                if (!didPlayJump) {
                    if (!player.isOnTrampoline()) {
                        jumpSound.play(0.5f * GDXRoot.soundVol);
                    } else {
                        trampolineJumpSound.play(0.5f * GDXRoot.soundVol);
                    }
                    didPlayJump = true;
                    didPlayLand = false;
                }
                if (player.getVY() > 0) {
                    setJumpUpAnimationFrame(dt);
                    player.setTexture(playerJumpUpAnimation);
                } else {
                    setJumpDownAnimationFrame(dt);
                    player.setTexture(playerJumpDownAnimation);
                }
            } else {
                playerJumpUpAnimation.refresh();
                playerJumpDownAnimation.refresh();
                if (player.isAttached()) {
                    if (!didPlaySwing) {
                        if (GDXRoot.soundVol != 0) {
                            swingSound.play(GDXRoot.soundVol * (Math.abs(player.getVX() / player.getMaxHorizontalSpeed())));
                            swingSound.loop();
                            didPlaySwing = true;
                        }
                    }
                    setSwingingAnimations(dt);
                } else if (player.isWalking()) {
                    playerWalkingAnimation.setElapsedTime(dt);
                    playerWalkingAnimation.updateFrame();
                    player.setTexture(playerWalkingAnimation);
                } else if (player.isGrounded()) {
                    if (!didPlayLand) {
                        if (!player.isOnTrampoline()) {
//                            landSound.play(0.5f*GDXRoot.soundVol);
                        } else {
//                            trampolineLandSound.play(0.5f*GDXRoot.soundVol);
                        }
                        didPlayLand = true;
                        didPlayJump = false;
                        player.setOnTrampoline(false);
                    }
                    playerIdleAnimation.setElapsedTime(dt);
                    playerIdleAnimation.updateFrame();
                    player.setTexture(playerIdleAnimation);
                }
            }

            NpcPerson onNpc = player.getOnNpc();
            setShockNpc(onNpc, "onNpc");

            if (player.isShooting() && !player.isAttached() && player.getTarget() == null && player.getCanSwingTo() != null) {
                player.setTarget(player.getCanSwingTo());
                player.setCanSwingTo(null);
            }

            if (player.isCutting()) {
                world.QueryAABB(cuttingCallback, playerPosition.x - player.getWidth() / 2, playerPosition.y - player.getHeight() / 2 - 0.1f, playerPosition.x + player.getWidth() / 2, playerPosition.y + player.getHeight() / 2 + 0.1f);
                int id = cuttingCallback.getClosestBlobID();
                if (id != -1) {
                    for (Obstacle obs : objects) {
                        if (obs.getName().equals("couples" + id)) {
                            NpcRope r = ((Couple) obs).getRope();
                            if (r != null) {
                                NpcRope[] ropes = r.cut(player.getPosition(), world, player.getHeight());
                                if (ropes != null) {
                                    snipSound.play(0.5f * GDXRoot.soundVol);
                                    ((Couple) obs).breakBond(ropes[0], ropes[1]);
                                    NpcPerson left = ((Couple) obs).getL();
                                    setShockNpc(left, "cutrope");
                                    NpcPerson right = ((Couple) obs).getR();
                                    setShockNpc(right, "cutrope");
                                }
                            }
                        }
                    }
                    cuttingCallback.reset();
                }
            }

            // Nearest NPC for exclamation
            if (!player.isAttached() && player.getTarget() == null) {
                world.QueryAABB(ropeQueryCallback, playerPosition.x - 2.8f, playerPosition.y - 2.8f, playerPosition.x + 2.8f, playerPosition.y + 2.8f);
                NpcPerson p = ropeQueryCallback.getClosestNpc();
                player.setCanSwingTo(p);
                ropeQueryCallback.reset();
            }

            if (!player.isAttached()) {
                world.QueryAABB(cuttingCallback, playerPosition.x - player.getWidth() / 2, playerPosition.y - player.getHeight() / 2, playerPosition.x + player.getWidth() / 2, playerPosition.y + player.getHeight() / 2);
                int id = cuttingCallback.getClosestBlobID();
                if (id != -1) {
                    for (Obstacle obs : objects) {
                        if (obs.getName().equals("couples" + id)) {
                            NpcRope r = ((Couple) obs).getRope();
                            if (r != null) {
                                player.setCanCut((Couple) obs);
                                player.setCanJumpIndicator(player.getY() >= r.getY());
                            } else {
                                player.setCanCut(null);
                                player.setCanJumpIndicator(false);
                            }
                        }
                    }
                } else {
                    player.setCanCut(null);
                    player.setCanJumpIndicator(false);
                }
                cuttingCallback.reset();
            }

            if (!player.isShooting() && player.isAttached() && playerRope != null) {
                destroyPlayerRope();
            }

            if (player.isDidCollect() && !didPlayCollect) {
                collectSound.play(0.5f * GDXRoot.soundVol);
                player.setDidCollect(false);
                didPlayCollect = true;
            }

            if (didPlayCollect) {
                player.setDidCollect(false);
                didPlayCollect = false;
            }

            // Swinging
            if (player.getTarget() != null && player.isShooting() && !player.isAttached()) {
                float ancX = player.isFacingRight() ? player.getWidth() / 2f - 0.21f : -player.getWidth() / 2f + 0.21f;
                Vector2 anchor = new Vector2(ancX, player.getWidth() / 2f + 0.1f);
                Vector2 playerPos = player.getPosition();
                Vector2 targetPos = player.getTarget().getPosition();
                playerRope = new PlayerRope(playerPos.x, playerPos.y, targetPos.x, targetPos.y, 4.5f);
                playerRope.setLinearVelocityAll(player.getLinearVelocity());
                Filter playerRopeFilter = new Filter();
                playerRopeFilter.categoryBits = CollisionFilterConstants.CATEGORY_PLAYER_ROPE.getID();
                playerRopeFilter.maskBits = CollisionFilterConstants.MASK_NO_COLLISION.getID();
                playerRope.setFilterDataAll(playerRopeFilter);
                playerRope.setName("player_rope");
                playerRope.setDrawScale(scale);
                addObject(playerRope);

                revoluteJointDef.bodyB = player.getBody();
                revoluteJointDef.bodyA = playerRope.getBody();
                revoluteJointDef.localAnchorB.set(anchor);
                revoluteJointDef.collideConnected = false;
                world.createJoint(revoluteJointDef);

                anchor.set(0, 0);
                revoluteJointDef.bodyB = playerRope.getLastLink();
                revoluteJointDef.bodyA = player.getTarget().getBody();
                revoluteJointDef.localAnchorA.set(anchor);
                revoluteJointDef.localAnchorB.set(anchor);
                revoluteJointDef.collideConnected = false;
                world.createJoint(revoluteJointDef);

                ropeJointDef.bodyA = player.getBody();
                ropeJointDef.bodyB = player.getTarget().getBody();
                ropeJointDef.maxLength = playerRope.getLength();
                ropeJointDef.collideConnected = true;
                Joint swingJoint = world.createJoint(ropeJointDef);
                player.setSwingJoint(swingJoint);
                player.setAttached(true);
            }

            /*
             * Continuously update the rope position to match the player
             * position
             */
            if (player.isAttached() && playerRope != null) {
                Vector2 playerPos = player.getPosition();
                Vector2 targetPos = player.getTarget().getPosition();
                playerRope.setStart(playerPos, false);
                playerRope.setEnd(targetPos, false);
            }

            if (!player.isAttached()) {
                swingSound.stop();
                didPlaySwing = false;
            }

            if (player.isWalking() && player.isGrounded() && !player.isAttached() && !player.isFalling() &&
                    !player.isJumping() && !player.isRising() && !player.isOnTrampoline()) {
                if (!didPlayWalk) {
                    walkingMusic.play();
                    walkingMusic.setVolume(0.5f * GDXRoot.soundVol);
                    walkingMusic.setLooping(true);
                    didPlayWalk = true;
                }
            } else {
                if (didPlayWalk) {
                    walkingMusic.stop();
                    didPlayWalk = false;
                }
            }
        } else if (!player.isAlive()) {
            if (player.isGrounded())
                playerDeathAnimation.setElapsedTime(dt);
            playerDeathAnimation.updateFrame();
            player.setTexture(playerDeathAnimation);
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }

    public void drawPaused(float dt) {
        canvas.begin();
        canvas.drawUITextPause("Press Esc to return back to the game", canvas.getWidth() / 2 - 300, canvas.getHeight() / 2 + 100);
        canvas.drawUITextPause("Press Q to Quit", canvas.getWidth() / 2 - 300, canvas.getHeight() / 2);
        canvas.end();
    }

    NpcPerson target;
    Couple c;
    //    NpcRope r;
    NpcPerson l;
    NpcPerson r;

    public void draw(float dt) {
        canvas.begin();
        float camera = player.getX() * scale.x;
        if (level.getType().equals("forest")) {
            for (TextureRegion t : stillBackgroundTextures) {
                canvas.drawMirrorred(t.getTexture(), 0f * camera, 0f, canvas.getWidth(),
                        t.getRegionHeight() * (canvas.getWidth() / t.getRegionWidth()), t.getRegionWidth(), t.getRegionHeight(), 1f);
            }
        } else {
            for (TextureRegion t : stillBackgroundTextures) {
                canvas.drawMirrorred(t.getTexture(), 0f * camera, 0f, canvas.getWidth() * 1.2f, canvas.getHeight() * 1.2f, t.getRegionWidth(), t.getRegionHeight(), 1.2f);
            }
            for (TextureRegion t : slightMoveBackgroundTextures) {
                canvas.drawMirrorred(t.getTexture(), -.1f * camera, 0f, canvas.getWidth() * 1.2f, canvas.getHeight() * 1.2f, t.getRegionWidth(), t.getRegionHeight(), 1.2f);
            }
            for (TextureRegion t : movingBackgroundTextures) {
                canvas.drawMirrorred(t.getTexture(), -.3f * camera, 0f, canvas.getWidth() * 1.2f, canvas.getHeight() * 1.2f, t.getRegionWidth(), t.getRegionHeight(), 1.2f);
            }
        }

        canvas.end();
        canvas.begin();
        for (Obstacle obj : objects) {
            if (obj.getName().equals("player_rope")) {
                if (player.getTarget() != null && player.isAlive()) {
                    obj.draw(canvas);
                }
            } else {
                obj.draw(canvas);
            }
        }

//        canvas.draw(exclamationTexture, Color.WHITE,player.getX()*scale.x,
//                player.getY()*scale.y, exclamationTexture.getRegionWidth()*0.1f, exclamationTexture.getRegionHeight()*0.1f);
//        ((FilmStrip) exclamationTexture).setNextFrame();
        target = player.getCanSwingTo();
        if (target != null) {
            canvas.draw(targetTexture, Color.WHITE, target.getX() * scale.x - 40,
                    target.getY() * scale.y - 34, targetTexture.getRegionWidth() * 10f / scale.x, targetTexture.getRegionHeight() * 10f / scale.y);
//            ((FilmStrip) exclamationTexture).setNextFrame();
        }

        c = player.canCut();
        if (c != null) {
            l = c.getL();
            r = c.getR();
            canvas.draw(cutIndicatorTexture, Color.WHITE, (l.getX() + r.getX()) / 2 * scale.x - 5, (r.getY() + l.getY()) / 2 * scale.y - 20, cutIndicatorTexture.getRegionWidth() * 15f / scale.x, cutIndicatorTexture.getRegionHeight() * 15f / scale.y);
        }

        canvas.drawUI(UI_restart, canvas.getWidth() - UI_restart.getRegionWidth(),
                canvas.getHeight() - UI_restart.getRegionHeight(), 1f);
        canvas.drawUI(UI_exit, canvas.getWidth() - UI_restart.getRegionWidth() - UI_exit.getRegionWidth(),
                canvas.getHeight() - UI_restart.getRegionHeight(), 1f);
        //       float UIX = 120;
        //       float UIY = canvas.getHeight() - UI_restart.getRegionHeight() - 20;
//        int itemCount = player.getInventory().size();
//        if (itemCount == 0) {
////            canvas.drawUI(basketEmptyTexture, UIX, UIY, 1f);
////        } else if (itemCount == 1) {
////            canvas.drawUI(basketOneTexture, UIX, UIY, 1f);
////        } else if (itemCount == 2) {
////            canvas.drawUI(basketTwoTexture, UIX, UIY, 1f);
////        } else {
////            canvas.drawUI(basketThreeTexture, UIX, UIY, 1f);
////        }
        if (billboards.size() >= level.getText().size()) {
            for (int i = 0; i < level.getText().size(); i++) {
                TextBox text = level.getText().get(i);
                TextureRegion tex = billboards.get(i);
                canvas.draw(tex,
                        text.getX() * this.scale.x - tex.getRegionWidth() / 2, text.getY() * this.scale.y - tex.getRegionHeight() / 2);
            }
        }

        float UIX = 70;
        float UIY = canvas.getHeight() - UI_restart.getRegionHeight();
        for (int i = 1; i <= items.size(); i++) {
            if (i <= player.getInventory().size()) {
                canvas.drawUI(yarnTexture, UIX, UIY, 0.25f);
            } else {
                canvas.drawUI(greyYarnTexture, UIX, UIY, 1f);
            }
            UIX += greyYarnTexture.getRegionWidth() + 10;
        }
        String text = player.getInventory().size() + " / " + items.size();
        canvas.drawItemCount(text, (int) UIX - 20, (int) UIY + 5);
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
    protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size, boolean loop) {
        if (manager.isLoaded(file)) {
            FilmStrip strip = new FilmStrip(manager.get(file, Texture.class), rows, cols, size, loop);
            strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return strip;
        }
        return null;
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

    public boolean levelComplete() {
        return player.won();
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
        if (music != null)
            music.dispose();
        if (walkingMusic != null)
            walkingMusic.dispose();
        objects = null;
        addQueue = null;
        bounds = null;
        scale = null;
        world = null;
        canvas = null;
        if (swingSound != null) {
            swingSound.dispose();
            landSound.dispose();
            winSound.dispose();
            loseSound.dispose();
            collectSound.dispose();
            jumpSound.dispose();
            trampolineLandSound.dispose();
            trampolineJumpSound.dispose();
        }
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


    private Vector2 direction = null;
    private Vector2 targetViewPort = null;

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
        physicsStepAccumulator += dt;
        while (physicsStepAccumulator > WORLD_STEP) {
            // Turn the physics engine crank.
            world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);
            physicsStepAccumulator -= WORLD_STEP;
        }


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

        float xpos = player.getX() * scale.x > 350 ? player.getX() * scale.x : 350;
        float ypos = player.getY() * scale.y > 240 ? player.getY() * scale.y : 240;


        if (isZoomed) {
            canvas.moveCamera(xpos, ypos);
            lastpos = new Vector2(xpos, ypos);
        } else {
            gameState = GameState.ZOOM;
        }

    }


    public void cleanUpZoom(float dt) {
        Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
        while (iterator.hasNext()) {
            PooledList<Obstacle>.Entry entry = iterator.next();
            Obstacle obj = entry.getValue();
            if (obj.isRemoved()) {
                obj.deactivatePhysics(world);
                entry.remove();
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

    @Override
    public void show() {

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
        switch (gameState) {
            case PLAYING:
                if (preUpdate(delta)) {
                    update(delta); // This is the one that must be defined.
                    postUpdate(delta);
                }
                draw(delta);
                break;
            case PAUSED:
                updatePaused(delta);
                drawPaused(delta);
                break;
            case ZOOM:
                updateZoom(delta);
                draw(delta);
                cleanUpZoom(delta);
        }
    }

    /**
     * Called when the Screen is paused.
     * <p>
     * This is usually when it's not active or visible on screen. An Application is
     * also paused before it is destroyed.
     */
    public void pause() {
        music.pause();
        walkingMusic.pause();
    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        music.play();
        music.setVolume(0.5f * GDXRoot.musicVol);
    }

    @Override
    public void hide() {

    }


    /**
     * Sets the ScreenListener for this mode
     * <p>
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void exitToSelector() {
        if (listener != null) {
            music.pause();
            walkingMusic.pause();
            listener.exitScreen(this, LevelSelectorMode.INTO_SELECTOR);
        }
    }

    public void exitToNext() {
        if (listener != null) {
            music.dispose();
            walkingMusic.dispose();
            listener.exitScreen(this, EXIT_NEXT);
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    private enum GameState {
        PLAYING, PAUSED, ZOOM
    }


}

