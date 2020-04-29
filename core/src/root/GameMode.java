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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
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

    public static final int EXIT_INTO_GAME = 3;

    public static final int EXIT_INTO_NEXT = 6;
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
    // time after game ends
    private float timeSeconds = 0f;
    // wait time
    private final float period = 2f;


    /**
     * Player animations
     */
    private static final String PLAYER_IDLE_ANIMATION = "player/player_idle_animation.png";
    private static final String PLAYER_WALKING_ANIMATION_FILE = "player/player_walk_animation.png";
    private static final String PLAYER_SWING_ANIMATION = "player/player_swing_animation.png";
    private static final String PLAYER_JUMP_ANIMATION = "player/player_jump_animation.png";
    private static final String PLAYER_FALL = "player/player_fall.png";
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

    /**
     * Texture file for the exit door
     */
    private static final String CITYGATE = "entities/citydoor1.png";
    /**
     * Texture files for items
     */
    private static final String NEEDLE = "entities/needles.png";
    private static final String YARN = "entities/yarn.png";
    private static final String BUTTON = "entities/buttons.png";
    private static final String FABRIC_1 = "entities/fabric1.png";
    private static final String FABRIC_2 = "entities/fabric2.png";
    private static final String SPOOL = "entities/spool.png";
    private static final String STUFFING = "entities/stuffing.png";

    /**
     * Texture files for baskets (progress bar)
     */
    private static final String BASKET_EMPTY = "ui/basket_0.png";
    private static final String BASKET_ONE = "ui/basket_1.png";
    private static final String BASKET_TWO = "ui/basket_3.png";
    private static final String BASKET_THREE = "ui/basket_2.png";
    /**
     * The texture file for the spinning barrier
     */
    private static final String BARRIER_FILE = "entities/barrier.png";
    /**
     * The texture file for the bullet
     */
    private static final String BULLET_FILE = "entities/bullet.png";
    /**
     * The sound effects
     */
    private static final String JUMP_FILE = "sounds/jump.mp3";
    private static final String PEW_FILE = "sounds/pew.mp3";
    private static final String POP_FILE = "sounds/plop.mp3";
    private static final String COLLECT_FILE = "sounds/itemcollect.mp3";
    private static final String WIN_FILE = "sounds/win.mp3";
    private static final String LOSE_FILE = "sounds/lose.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";
    /**
     * The folder with all levels
     */
    private static final String TEST_LEVEL = "levels/level1.json";
    private static final String CROSSHAIR_FILE = "ui/crosshair.png";
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
    private static final String FONT_FILE = "ui/RetroGame.ttf";
    private static final String ROPE_SEGMENT = "entities/rope_segment.png";
    private static final int FONT_SIZE = 64;

    /**
     * The textures for walls and platforms
     */
    protected TextureRegion spikeTile;
    protected TextureRegion spikeVertTile;
    protected TextureRegion UI_restart;
    protected TextureRegion UI_exit;
    protected TextureRegion citydoor;
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
     * Texture assets for character avatar
     */
    private TextureRegion playerFallTexture;
    /**
     * Texture assets for NPCs
     */
    private TextureRegion npcCozyTexture;
    private TextureRegion npcCheeseTexture;
    private TextureRegion npcNervyTexture;
    private TextureRegion npcHeyoTexture;
    private TextureRegion npcSpikyTexture;
    private TextureRegion npcWelcomeTexture;
    private TextureRegion npcCozyShockTexture;
    private TextureRegion npcCheeseShockTexture;
    private TextureRegion npcNervyShockTexture;
    private TextureRegion npcHeyoShockTexture;
    private TextureRegion npcSpikyShockTexture;
    private TextureRegion npcWelcomeShockTexture;
    /**
     * Texture assets for items
     */
    private TextureRegion buttonTexture;
    private TextureRegion needleTexture;
    private TextureRegion yarnTexture;
    private TextureRegion fabric1Texture;
    private TextureRegion fabric2Texture;
    private TextureRegion spoolTexture;
    private TextureRegion stuffingTexture;
    private TextureRegion basketEmptyTexture;
    private TextureRegion basketOneTexture;
    private TextureRegion basketTwoTexture;
    private TextureRegion basketThreeTexture;
    /**
     * List of all unique NPC textures
     */
    private final String[] npcTypes = new String[]{"cheese", "cozy", "heyo", "nervy", "spiky", "welcome"};
    private final Map<String, TextureRegion> npcs;
    private final Map<String, TextureRegion> npcShock;
    /**
     * List of all unique item textures
     */
    private ArrayList<TextureRegion> itemTexture = new ArrayList<>();
    /**
     * List of item objects
     */
    private ArrayList<float[]> items;
    /**
     * FilmStrip objects to show player animations
     */
    private FilmStrip playerIdleAnimation;
    private FilmStrip playerSwingAnimation;
    private FilmStrip playerWalkingAnimation;
    private FilmStrip playerEnterAnimation;
    private FilmStrip playerExitAnimation;
    private FilmStrip playerJumpAnimation;
    private FilmStrip playerDeathAnimation;
    /**
     * FilmStrip objects to show NPC animations
     */
    private FilmStrip npcCozyIdleAnimation;
    private FilmStrip npcCozyShockAnimation;
    private FilmStrip npcCheeseIdleAnimation;
    private FilmStrip npcCheeseShockAnimation;
    private FilmStrip npcHeyoIdleAnimation;
    private FilmStrip npcHeyoShockAnimation;
    private FilmStrip npcNervyIdleAnimation;
    private FilmStrip npcNervyShockAnimation;
    private FilmStrip npcSpikyIdleAnimation;
    private FilmStrip npcSpikyShockAnimation;
    private FilmStrip npcWelcomeIdleAnimation;
    private FilmStrip npcWelcomeShockAnimation;
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
    private final String CITY_MUSIC_FILE = "music/flight.mp3";
    private final String VILLAGE_MUSIC_FILE = "music/warmsand.mp3";
    private final String FOREST_MUSIC_FILE = "music/youare.mp3";
    private final String MOUNTAIN_MUSIC_FILE = "music/happylittleclouds.mp3";
    private final String OPENING_CUTSCENE_FILE = "music/ineedasweater.mp3";
    private final String ENDING_CUTSCENE_FILE = "music/youshoulddosomereflecting.mp3";
    private final String TRANSITION_CUTSCENE_FILE = "music/goodnight.mp3";
    /**
     * Music object played in the game
     */
    private Music music;
    /**
     * Sound objects played in the game
     */
    private Sound jumpSound;
    private Sound collectSound;
    private Sound loseSound;
    private Sound clickSound;
    private Sound winSound;

    /**
     * Files for region tiles
     */
    private static final String CITY_TILE_FILE = "entities/city-brick.png";
    private static final String VILLAGE_TILE_FILE = "entities/village-tile.png";
    private static final String FOREST_TILE_FILE = "entities/mossyrocks.png";
    private static final String MOUNTAIN_TILE_FILE = "entities/earthtile.png";

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
    private final String[] VILLAGE_BKG_FILES_LAYER_A = new String[]{"background/village1.png", "background/village2.png", "background/village3.png"};
    private final String[] VILLAGE_BKG_FILES_LAYER_B = new String[]{"background/village4.png", "background/village5.png", "background/village6.png"};
    private final String[] VILLAGE_BKG_FILES_LAYER_C = new String[]{"background/village7.png"};
    private final String[] FOREST_BKG_FILES_LAYER_A = new String[]{"background/forest-1.png", "background/forest-2.png", "background/forest-3.png"};
    private final String[] FOREST_BKG_FILES_LAYER_B = new String[]{"background/forest-5.png", "background/forest-6.png", "background/forest-7.png"};
    private final String[] FOREST_BKG_FILES_LAYER_C = new String[]{"background/forest-4.png"};
    //    private final String[] FOREST_BKG_FILES_LAYER_A = new String[]{"background/forest-layer1.png"};
//    private final String[] FOREST_BKG_FILES_LAYER_B = new String[]{"background/forest-layer2.png"};
//    private final String[] FOREST_BKG_FILES_LAYER_C = new String[]{"background/forest-layer3.png"};
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
        manager.load(PLAYER_FALL, Texture.class);
        assets.add(PLAYER_FALL);
        manager.load(BARRIER_FILE, Texture.class);
        assets.add(BARRIER_FILE);
        manager.load(UI_GreyYarn, Texture.class);
        assets.add(UI_GreyYarn);
        manager.load(UI_RedYarn, Texture.class);
        assets.add(UI_RedYarn);
        manager.load(CITYGATE, Texture.class);
        assets.add(CITYGATE);
        manager.load(NEEDLE, Texture.class);
        assets.add(NEEDLE);
        manager.load(BUTTON, Texture.class);
        assets.add(BUTTON);
        manager.load(YARN, Texture.class);
        assets.add(YARN);
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
        manager.load(BASKET_ONE, Texture.class);
        assets.add(BASKET_ONE);
        manager.load(BASKET_TWO, Texture.class);
        assets.add(BASKET_TWO);
        manager.load(BASKET_THREE, Texture.class);
        assets.add(BASKET_THREE);
        manager.load(BULLET_FILE, Texture.class);
        assets.add(BULLET_FILE);
        manager.load(CROSSHAIR_FILE, Texture.class);
        assets.add(CROSSHAIR_FILE);
        manager.load(CITY_TILE_FILE, Texture.class);
        assets.add(CITY_TILE_FILE);
        manager.load(VILLAGE_TILE_FILE, Texture.class);
        assets.add(VILLAGE_TILE_FILE);
        manager.load(FOREST_TILE_FILE, Texture.class);
        assets.add(FOREST_TILE_FILE);
        manager.load(MOUNTAIN_TILE_FILE, Texture.class);
        assets.add(MOUNTAIN_TILE_FILE);
        manager.load(SPIKE_FILE, Texture.class);
        assets.add(SPIKE_FILE);
        manager.load(SPIKE_VERT, Texture.class);
        assets.add(SPIKE_VERT);
        manager.load(RESTART_FILE, Texture.class);
        assets.add(RESTART_FILE);
        manager.load(ESC_FILE, Texture.class);
        assets.add(ESC_FILE);
        manager.load(ROPE_SEGMENT, Texture.class);
        assets.add(ROPE_SEGMENT);
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

        // Load Player Animations
        manager.load(PLAYER_IDLE_ANIMATION, Texture.class);
        assets.add(PLAYER_IDLE_ANIMATION);
        manager.load(PLAYER_WALKING_ANIMATION_FILE, Texture.class);
        assets.add(PLAYER_WALKING_ANIMATION_FILE);
        manager.load(PLAYER_ENTER, Texture.class);
        assets.add(PLAYER_ENTER);
        manager.load(PLAYER_EXIT, Texture.class);
        assets.add(PLAYER_EXIT);
        manager.load(PLAYER_JUMP_ANIMATION, Texture.class);
        assets.add(PLAYER_JUMP_ANIMATION);
        manager.load(PLAYER_FALL, Texture.class);
        assets.add(PLAYER_FALL);
        manager.load(PLAYER_SWING_ANIMATION, Texture.class);
        assets.add(PLAYER_SWING_ANIMATION);
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


        // Load Sound Assets
        loadAsset(JUMP_FILE, Sound.class, manager);
        loadAsset(PEW_FILE, Sound.class, manager);
        loadAsset(POP_FILE, Sound.class, manager);
        loadAsset(COLLECT_FILE, Sound.class, manager);
        loadAsset(WIN_FILE, Sound.class, manager);
        loadAsset(LOSE_FILE, Sound.class, manager);
        loadAsset(CLICK_FILE, Sound.class, manager);

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

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);
    }

    public void initializeContent(AssetManager manager) {
        String type = level.getType();
        stillBackgroundTextures.clear();
        slightMoveBackgroundTextures.clear();
        movingBackgroundTextures.clear();

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
        }

        music.play();
        music.setLooping(true);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }
        itemTexture = new ArrayList<>();
        playerSwingAnimation = createFilmStrip(manager, PLAYER_SWING_ANIMATION, 1, 20, 20);
        playerIdleAnimation = createFilmStrip(manager, PLAYER_IDLE_ANIMATION, 1, 24, 24);
        playerEnterAnimation = createFilmStrip(manager, PLAYER_ENTER, 1, 21, 21);
        playerExitAnimation = createFilmStrip(manager, PLAYER_EXIT, 1, 15, 15);
        playerJumpAnimation = createFilmStrip(manager, PLAYER_JUMP_ANIMATION, 1, 22, 22);
        playerDeathAnimation = createFilmStrip(manager, PLAYER_DEATH, 1, 24, 24);
        playerFallTexture = createTexture(manager, PLAYER_FALL, false);
        bulletTexture = createTexture(manager, BULLET_FILE, false);
        crosshairTexture = createTexture(manager, CROSSHAIR_FILE, false);
        playerWalkingAnimation = createFilmStrip(manager, PLAYER_WALKING_ANIMATION_FILE, 1, 17, 17);
        npcCheeseTexture = createFilmStrip(manager, NPC_CHEESE, 1, 49, 49);
        npcCozyTexture = createFilmStrip(manager, NPC_COZY, 1, 33, 33);
        npcNervyTexture = createFilmStrip(manager, NPC_NERVY, 1, 33, 33);
        npcHeyoTexture = createFilmStrip(manager, NPC_HEYO, 1, 4, 4);
        npcSpikyTexture = createFilmStrip(manager, NPC_SPIKY, 1, 16, 16);
        npcWelcomeTexture = createFilmStrip(manager, NPC_WELCOME, 1, 7, 7);
        npcHeyoShockTexture = createFilmStrip(manager, NPC_HEYO_SHOCK, 1, 9, 9);
        npcCheeseShockTexture = createFilmStrip(manager, NPC_CHEESE_SHOCK, 1, 9, 9);
        npcCozyShockTexture = createFilmStrip(manager, NPC_COZY_SHOCK, 1, 12, 12);
        npcNervyShockTexture = createFilmStrip(manager, NPC_NERVY_SHOCK, 1, 21, 21);
        npcSpikyShockTexture = createFilmStrip(manager, NPC_SPIKY_SHOCK, 1, 17, 17);
        npcWelcomeShockTexture = createFilmStrip(manager, NPC_WELCOME_SHOCK, 1, 13, 13);
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
        buttonTexture = createTexture(manager, BUTTON, false);
        needleTexture = createTexture(manager, NEEDLE, false);
        yarnTexture = createTexture(manager, YARN, false);
        fabric1Texture = createTexture(manager, FABRIC_1, false);
        fabric2Texture = createTexture(manager, FABRIC_2, false);
        spoolTexture = createTexture(manager, SPOOL, false);
        stuffingTexture = createTexture(manager, STUFFING, false);
        itemTexture.add(buttonTexture);
        itemTexture.add(needleTexture);
        itemTexture.add(yarnTexture);
        itemTexture.add(fabric1Texture);
        itemTexture.add(fabric2Texture);
        itemTexture.add(spoolTexture);
        itemTexture.add(stuffingTexture);
        basketEmptyTexture = createTexture(manager, BASKET_EMPTY, false);
        basketOneTexture = createTexture(manager, BASKET_ONE, false);
        basketTwoTexture = createTexture(manager, BASKET_TWO, false);
        basketThreeTexture = createTexture(manager, BASKET_THREE, false);
        citydoor = createTexture(manager, CITYGATE, false);
        bridgeTexture = createTexture(manager, ROPE_SEGMENT, false);

        SoundController sounds = SoundController.getInstance();
        sounds.allocate(manager, JUMP_FILE);
        sounds.allocate(manager, PEW_FILE);
        sounds.allocate(manager, POP_FILE);
        sounds.allocate(manager, COLLECT_FILE);
        sounds.allocate(manager, WIN_FILE);
        sounds.allocate(manager, LOSE_FILE);
        sounds.allocate(manager, CLICK_FILE);
        jumpSound = manager.get(JUMP_FILE);
        collectSound = manager.get(COLLECT_FILE);
        winSound = manager.get(WIN_FILE);
        loseSound = manager.get(LOSE_FILE);
        clickSound = manager.get(CLICK_FILE);
        spikeTile = createTexture(manager, SPIKE_FILE, false);
        spikeVertTile = createTexture(manager, SPIKE_VERT, false);
        UI_restart = createTexture(manager, RESTART_FILE, false);
        UI_exit = createTexture(manager, ESC_FILE, false);
        if (manager.isLoaded(FONT_FILE)) {
            displayFont = manager.get(FONT_FILE, BitmapFont.class);
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
    /**
     * The maximum y-axis offset from which a bullet
     * can appear from the person's body
     */
    private static final float MAX_BULLET_OFFSET_Y = 0.8f;

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
        float[] points = new float[]{0f, 0f, 0f, citydoor.getRegionHeight() / 3 / scale.y, citydoor.getRegionWidth() / scale.x,
                citydoor.getRegionHeight() / 3 / scale.y, citydoor.getRegionWidth() / scale.x,
                0f};


        // Create exit door
        createGate(points, level.getExitPos().x, level.getExitPos().y, citydoor);
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
            createItem(curr[0], curr[1], i);
        }

        // Create platforms
        for (int i = 0; i < tiles.size(); i++) {
            createTile(tiles.get(i).getCorners(), tiles.get(i).getX(), tiles.get(i).getY(), tiles.get(i).getWidth(), tiles.get(i).getHeight(), level.getType(), "tile" + i, 1f);
        }

        for (Tile spike : spikes) {
            TextureRegion spikeTexture = (spike.getDirection().equals("up") || spike.getDirection().equals("down")) ? spikeTile : spikeVertTile;
            createSpike(spike.getCorners(), spike.getX(), spike.getY(), spike.getDirection(), "spike", 1f, spikeTexture);
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


    public void createGate(float[] points, float x, float y, TextureRegion texture) {
        Gate gate = new Gate(texture, points, x, y);
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

    public void createItem(float x, float y, int id) {
        int n = rand.nextInt(itemTexture.size());
        TextureRegion randTex = itemTexture.get(n);
        Vector2 dimensions = getScaledDimensions(randTex);
        Item item = new Item(x, y, dimensions.x, dimensions.y, id);
        item.setTexture(randTex);
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
        Couple couple = new Couple(x1, y1, x2, y2, randType1, randType2, randTex1, randTex2, bridgeTexture, scale, leftTile, rightTile, id);
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

    public Stone createSlidingTile(float[] points, float x, float y, float width, float height, String type, String name, float sc,
                                   float[] leftPos, float[] rightPos) {
        Stone tile = new Stone(points, x, y, width, height, type, sc, leftPos, rightPos);
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
            } else if (!player.isAlive() || player.won()) {
                if (player.won() && player.isAttached() && playerRope != null) {
                    destroyPlayerRope();
                }
                timeSeconds += Gdx.graphics.getRawDeltaTime();
                if (timeSeconds > period) {
                    timeSeconds = 0;
                    listener.exitScreen(this, LevelTransition.INTO_TRANSITION);
                }
            } else if (countdown > 0) {
                countdown--;
            } else if (countdown == 0) {
                if (failed) {
                    listener.exitScreen(this, LevelTransition.INTO_TRANSITION);
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

    public void updatePaused(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameState = GameState.PLAYING;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            exitToSelector();
        }
    }

    private void destroyPlayerRope() {
        playerRope.markRemoved(true);
        player.setTarget(null);
        ropeQueryCallback.reset();
        playerRope = null;
        world.destroyJoint(player.getSwingJoint());
        player.setAttached(false);
        player.setSwingJoint(null);
        player.resetShootCooldown();
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
        if ((Gdx.input.isTouched() && Gdx.input.getX() >= 800
                && Gdx.input.getX() <= 950 && Gdx.input.getY() >= 48 && Gdx.input.getY() <= 132)
                || (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))) {
            gameState = GameState.PAUSED;
//            exitToSelector();
        }

        Vector2 playerPosition = player.getPosition();
        // If player has collected all items, indicate so
        player.setCollectedAll(items.size() == player.getInventory().size());
        if (player.won()) {
//            winSound.play();
            player.setTexture(playerExitAnimation);
        } else if (player.isAlive()) {
            player.setMovement(InputController.getInstance().getHorizontal() * player.getForce());
            player.setJumping(InputController.getInstance().didPrimary());
            player.setShooting(InputController.getInstance().didTertiary());
            player.setCutting(InputController.getInstance().didSecondary());
            player.applyForce();


            if (!player.isGrounded() && !player.isAttached()) {
                player.setTexture(playerJumpAnimation);
            } else if (player.isAttached()) {
                player.setTexture(playerSwingAnimation);
            } else if (player.isFalling()) {
                player.setTexture(playerFallTexture);
            } else if (player.isWalking()) {
                player.setTexture(playerWalkingAnimation);
            } else if (player.isGrounded()) {
                player.setTexture(playerIdleAnimation);
            }

            NpcPerson onNpc = player.getOnNpc();
            if (onNpc != null) {
                String type = onNpc.getType();
                if (player.isOnNpc()) {
                    TextureRegion shockTex = npcShock.get(type);
                    onNpc.setTexture(shockTex);
                } else {
                    TextureRegion normalTex = npcs.get(type);
                    onNpc.setTexture(normalTex);
                }
            }

            if (player.isShooting() && !player.isAttached() && player.getTarget() == null) {
                world.QueryAABB(ropeQueryCallback, playerPosition.x - 2.8f, playerPosition.y - 2.8f, playerPosition.x + 2.8f, playerPosition.y + 2.8f);
                player.setTarget(ropeQueryCallback.getClosestNpc());
            }

            if (player.isCutting()) {
                world.QueryAABB(cuttingCallback, playerPosition.x - player.getWidth() / 2, playerPosition.y - player.getHeight() / 2, playerPosition.x + player.getWidth() / 2, playerPosition.y + player.getHeight() / 2);
                int id = cuttingCallback.getClosestBlobID();
                if (id != -1) {
                    for (Obstacle obs : objects) {
                        if (obs.getName().equals("couples" + id)) {
                            NpcRope[] ropes = ((Couple) obs).getRope().cut(player.getPosition(), world);
                            if (ropes != null) {
                                ((Couple) obs).breakBond(ropes[0], ropes[1]);
//                                for (NpcRope r : ropes) {
//                                    r.markRemoved(true);
//                                }
                            }
                        }
                    }
                    cuttingCallback.reset();
                }
            }

            if (!player.isShooting() && player.isAttached() && playerRope != null) {
                destroyPlayerRope();
            }

            // Swinging
            if (player.getTarget() != null && player.isShooting() && !player.isAttached()) {
                Vector2 anchor = new Vector2(player.getWidth() / 2f - 0.2f, player.getWidth() / 2f + 0.1f);
                Vector2 playerPos = player.getPosition();
                Vector2 targetPos = player.getTarget().getPosition();
                playerRope = new PlayerRope(playerPos.x, playerPos.y, targetPos.x, targetPos.y, 4.5f);
                playerRope.setLinearVelocityAll(player.getLinearVelocity());
                Filter playerRopeFilter = new Filter();
                playerRopeFilter.categoryBits = CollisionFilterConstants.CATEGORY_PLAYER_ROPE.getID();
                playerRopeFilter.maskBits = CollisionFilterConstants.MASK_PLAYER_ROPE.getID();
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
        } else {
            player.setTexture(playerDeathAnimation);
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }

    private Vector2 screenToWorldCoordinates(Vector2 screenCoordinate) {
        screenCoordinate.scl(this.scale);
        Vector2 worldCoordinates = canvas.getMouseCoordinates(screenCoordinate.x, screenCoordinate.y);
        worldCoordinates.scl(1 / scale.x, 1 / scale.y);
        return worldCoordinates;
    }

    public void drawPaused(float dt) {
        canvas.begin();
        canvas.drawUIText("Press Esc to return back to the game", canvas.getWidth() / 2 - 300, canvas.getHeight() / 2 + 100);
        canvas.drawUIText("Press Q to Quit", canvas.getWidth() / 2 - 300, canvas.getHeight() / 2);
        canvas.end();
    }


    public void draw(float dt) {
        canvas.begin();
        float camera = player.getX() * scale.x;
        for (TextureRegion t : stillBackgroundTextures) {
            canvas.drawMirrorred(t.getTexture(), 0f * camera, 0f, canvas.getWidth()*1.2f, canvas.getHeight()*1.2f,t.getRegionWidth(), t.getRegionHeight() );
        }
        for (TextureRegion t : slightMoveBackgroundTextures) {
            canvas.drawMirrorred(t.getTexture(), -.1f * camera, 0f, canvas.getWidth()*1.2f, canvas.getHeight()*1.2f, t.getRegionWidth(), t.getRegionHeight());
        }
//        for (TextureRegion t : movingBackgroundTextures) {
////            canvas.drawWrapped(t, -.3f * camera, 0f, t.getRegionWidth() / 2, t.getRegionHeight() / 2);
////        }
        for (TextureRegion t : movingBackgroundTextures) {
            canvas.drawMirrorred(t.getTexture(), -.3f * camera, 0f, canvas.getWidth()*1.2f, canvas.getHeight()*1.2f,t.getRegionWidth(), t.getRegionHeight());
        }
//        canvas.drawWrapped(skyTexture, 0f * camera, 0f, skyTexture.getRegionWidth() / 2, skyTexture.getRegionHeight() / 2);
//        canvas.drawWrapped(sunTexture, 0f * camera, 0f, sunTexture.getRegionWidth() / 2, sunTexture.getRegionHeight() / 2);
//        canvas.drawWrapped(cityTexture, -0.1f * camera, 0f, cityTexture.getRegionWidth() / 2, cityTexture.getRegionHeight() / 2);
//        canvas.drawWrapped(cloudTexture, -0.5f * camera, 0f, cloudTexture.getRegionWidth() / 2, cloudTexture.getRegionHeight() / 2);

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


        canvas.drawUI(UI_restart, canvas.getWidth() - UI_restart.getRegionWidth(),
                canvas.getHeight() - UI_restart.getRegionHeight(), 1f);
        canvas.drawUI(UI_exit, canvas.getWidth() - UI_restart.getRegionWidth() - UI_exit.getRegionWidth(),
                canvas.getHeight() - UI_restart.getRegionHeight(), 1f);
        float UIX = 120;
        float UIY = canvas.getHeight() - UI_restart.getRegionHeight() - 20;
        int itemCount = player.getInventory().size();
        if (itemCount == 0) {
            canvas.drawUI(basketEmptyTexture, UIX, UIY, 1f);
        } else if (itemCount == 1) {
            canvas.drawUI(basketOneTexture, UIX, UIY, 1f);
        } else if (itemCount == 2) {
            canvas.drawUI(basketTwoTexture, UIX, UIY, 1f);
        } else {
            canvas.drawUI(basketThreeTexture, UIX, UIY, 1f);
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
        music.dispose();
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
            float xpos = player.getX() * scale.x > 350 ? player.getX() * scale.x : 350;
            float ypos = player.getY() * scale.y > 240 ? player.getY() * scale.y : 240;
            canvas.moveCamera(xpos, ypos);

        }
//        if (player.won() || !player.isAlive()){
//            listener.exitScreen(this, LevelTransition.INTO_TRANSITION);
//        }
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
    }

    /**
     * Called when the Screen is resumed from a paused state.
     * <p>
     * This is usually when it regains focus.
     */
    public void resume() {
        music.play();
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
            listener.exitScreen(this, LevelSelectorMode.INTO_SELECTOR);
        }
    }

    public void exitToNext() {
        if (listener != null) {
            music.dispose();
            listener.exitScreen(this, EXIT_NEXT);
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    private enum GameState {
        PLAYING, PAUSED
    }
}

