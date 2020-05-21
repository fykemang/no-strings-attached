package root;/*
 * game.LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do
 * anything until loading is complete. You know those loading screens with the inane tips
 * that want to be helpful?  That is asynchronous loading.
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the
 * start of the game.
 */

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import util.FilmStrip;
import util.ScreenListener;

/**
 * Class that provides a loading screen for the state of the game.
 * <p>
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 * <p>
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LoadingMode implements Screen, InputProcessor, ControllerListener {
    // Textures necessary to support the loading screen
    private static final String CHAR_ANIMATION_FILE = "ui/background_sc.png";
    private static final String PROGRESS_FILE = "ui/progressbar.png";
    private static final String SETTINGS_FILE = "ui/settings.png";
    private static final String LOGO_FILE = "ui/game-logo.png";
    private static final String QUIT_FILE = "ui/quit.png";
    private static final String START_FILE = "ui/new-game-normal.png";
    private static final String SELECT_FILE = "ui/select.png";
    private static final String MUSIC_FILE = "music/storybook.mp3";
    private static final String MENU_CLICK_FILE = "sounds/click.mp3";
    private static final String LOADING_FILE = "ui/loading.png";
    private static final String LOAD_GAME_ENABLED = "ui/load-game-text-enabled.png";
    private static final String NEVER_MIND = "ui/nevermind-text-deselected.png";
    private static final String CARD = "ui/new-game-warning-text.png";
    private static final String NEW_GAME_GREY = "ui/new-game-grey.png";
    private static final String LOAD_GAME_DISABLED = "ui/load-game-text-disabled.png";

    private TextureRegion loadingTexture;
    private final FilmStrip loadingStrip;

    /**
     * Background texture for start-up
     */
    private final Texture logo;
    private FilmStrip animatedBkg;

    private final Music music;
    private final Sound clickSound;

    /**
     * Play button to display when done
     */
    private Texture startGameButton;
    private Texture settingsButton;
    private Texture quitButton;
    private Texture loadGameButtonEnabled;
    private Texture loadGameButtonDisabled;
    private Texture select;

    private int frameCount;
    /**
     * Texture atlas to support a progress bar
     */
    private Texture statusBar;

    private final Texture cardTexture;

    private final Texture nvmTexture;

    private final Texture newGameGrey;

    // statusBar is a "texture atlas." Break it up into parts.
    /**
     * Left cap to the status background (grey region)
     */
    private TextureRegion statusBkgLeft;
    /**
     * Middle portion of the status background (grey region)
     */
    private TextureRegion statusBkgMiddle;
    /**
     * Right cap to the status background (grey region)
     */
    private TextureRegion statusBkgRight;
    /**
     * Left cap to the status forground (colored region)
     */
    private TextureRegion statusFrgLeft;
    /**
     * Middle portion of the status forground (colored region)
     */
    private TextureRegion statusFrgMiddle;
    /**
     * Right cap to the status forground (colored region)
     */
    private TextureRegion statusFrgRight;

    /**
     * Default budget for asset loader (do nothing but load 60 fps)
     */
    private static final int DEFAULT_BUDGET = 15;
    /**
     * Standard window size (for scaling)
     */
    private static final int STANDARD_WIDTH = 800;
    /**
     * Standard window height (for scaling)
     */
    private static final int STANDARD_HEIGHT = 700;

    public static final int INTO_STARTSCREEN = 18;
    /**
     * Ratio of the bar width to the screen
     */
    private static final float BAR_WIDTH_RATIO = 0.66f;
    /**
     * Ration of the bar height to the screen
     */
    private static final float BAR_HEIGHT_RATIO = 0.25f;
    /**
     * Height of the progress bar
     */
    private static final int PROGRESS_HEIGHT = 30;
    /**
     * Width of the rounded cap on left or right
     */
    private static final int PROGRESS_CAP = 15;
    /**
     * Width of the middle portion in texture atlas
     */
    private static final int PROGRESS_MIDDLE = 200;
    /**
     * Amount to scale the play button
     */
    private static final float BUTTON_SCALE = 0.9f;

    /**
     * Start button for XBox controller on Windows
     */
    private static final int WINDOWS_START = 7;
    /**
     * Start button for XBox controller on Mac OS X
     */
    private static final int MAC_OS_X_START = 4;

    /**
     * AssetManager to be loading in the background
     */
    private final AssetManager manager;
    /**
     * Reference to game.GameCanvas created by the root
     */
    private final GameCanvas canvas;
    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private float buttonX;
    private float buttonX1;
    private float buttonX2;
    private float buttonX3;
    private float buttonX4;
    private float buttonY1;
    private float buttonY2;
    private float buttonY3;
    private float buttonY4;
    private final float buttonNVMX;
    private final float buttonNVMY;
    private final float buttonSRTX;
    private float logoX;
    private float logoY;


    /**
     * The width of the progress bar
     */
    private int width;
    /**
     * The y-coordinate of the center of the progress bar
     */
    private int centerY;
    /**
     * The x-coordinate of the center of the progress bar
     */
    private int centerX;
    /**
     * The height of the canvas window (necessary since sprite origin != screen origin)
     */
    private int heightY;
    /**
     * Scaling factor for when the student changes the resolution.
     */
    private float scale;

    /**
     * Current progress (0 to 1) of the asset manager
     */
    private float progress;

    private boolean cardOpen = false;
    /**
     * The current state of the play button
     */
    private MouseState pressState;

    private MouseState selectState;
    /**
     * The amount of time to devote to loading assets (as opposed to on screen hints, etc.)
     */
    private int budget;
    /**
     * Support for the X-Box start button in place of play button
     */
    private final int startButton;
    /**
     * Whether or not this player mode is still active
     */
    private boolean active;

    private final Preferences levelState;

    private enum MouseState {
        NONE, QUIT, START, SETTINGS, OTHER, LOAD_GAME
    }

    private int keyState;

    /**
     * Returns the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @return the budget in milliseconds
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Sets the budget for the asset loader.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @param millis the budget in milliseconds
     */
    public void setBudget(int millis) {
        budget = millis;
    }

    /**
     * Returns true if all assets are loaded and the player is ready to go.
     *
     * @return true if the player is ready to go
     */
    public boolean isReady() {
        return pressState == MouseState.OTHER;
    }

    /**
     * Creates a game.LoadingMode with the default budget, size and position.
     *
     * @param manager The AssetManager to load in the background
     */
    public LoadingMode(GameCanvas canvas, AssetManager manager) {
        this(canvas, manager, DEFAULT_BUDGET);
    }

    /**
     * Creates a game.LoadingMode with the default size and position.
     * <p>
     * The budget is the number of milliseconds to spend loading assets each animation
     * frame.  This allows you to do something other than load assets.  An animation
     * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
     * do something else.  This is how game companies animate their loading screens.
     *
     * @param manager The AssetManager to load in the background
     * @param millis  The loading budget in milliseconds
     */
    public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
        this.manager = manager;
        this.canvas = canvas;
        budget = millis;

        manager.load(CHAR_ANIMATION_FILE, Texture.class);
        // assets.add(CHAR_ANIMATION_FILE);
        // Compute the dimensions from the canvas
        resize(canvas.getWidth(), canvas.getHeight());

        // Load the next two images immediately.
        startGameButton = null;
        settingsButton = null;
        quitButton = null;
        animatedBkg = null;
        select = new Texture(SELECT_FILE);
        statusBar = new Texture(PROGRESS_FILE);
        logo = new Texture(LOGO_FILE);
        nvmTexture = new Texture(NEVER_MIND);
        newGameGrey = new Texture(NEW_GAME_GREY);
        cardTexture = new Texture(CARD);
        // No progress so far.
        progress = 0;
        frameCount = 0;
        keyState = 0;
        buttonNVMX = canvas.getWidth() / 2 - 190;
        buttonNVMY = canvas.getHeight() / 2 - 120;
        buttonSRTX = canvas.getWidth() / 2 + 190;
        pressState = MouseState.NONE;
        selectState = MouseState.OTHER;
        active = false;

        loadingStrip = createFilmStrip(manager, LOADING_FILE, 71, 1, 71);
//        loadingTexture = new TextureRegion()

        // Break up the status bar texture into regions
        statusBkgLeft = new TextureRegion(statusBar, 0, 0, PROGRESS_CAP, PROGRESS_HEIGHT);
        statusBkgRight = new TextureRegion(statusBar, statusBar.getWidth() - PROGRESS_CAP, 0, PROGRESS_CAP, PROGRESS_HEIGHT);
        statusBkgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, 0, PROGRESS_MIDDLE, PROGRESS_HEIGHT);

        int offset = statusBar.getHeight() - PROGRESS_HEIGHT;
        statusFrgLeft = new TextureRegion(statusBar, 0, offset, PROGRESS_CAP, PROGRESS_HEIGHT);
        statusFrgRight = new TextureRegion(statusBar, statusBar.getWidth() - PROGRESS_CAP, offset, PROGRESS_CAP, PROGRESS_HEIGHT);
        statusFrgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, offset, PROGRESS_MIDDLE, PROGRESS_HEIGHT);

        startButton = (System.getProperty("os.name").equals("Mac OS X") ? MAC_OS_X_START : WINDOWS_START);
        Gdx.input.setInputProcessor(this);
        music = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_FILE));
        music.setVolume(0.5f * GDXRoot.musicVol);
        music.setLooping(true);
        clickSound = Gdx.audio.newSound(Gdx.files.internal(MENU_CLICK_FILE));
        active = true;
        levelState = Gdx.app.getPreferences("no-strings-attached.save");
    }


    /**
     * Called when this screen should release all resources.
     */
    public void dispose() {
        statusBkgLeft = null;
        statusBkgRight = null;
        statusBkgMiddle = null;

        statusFrgLeft = null;
        statusFrgRight = null;
        statusFrgMiddle = null;

        statusBar.dispose();
        music.dispose();
        statusBar = null;
        select = null;
        animatedBkg = null;
        if (quitButton != null) {
            quitButton.dispose();
            quitButton = null;
        }
        if (startGameButton != null) {
            startGameButton.dispose();
            startGameButton = null;
        }
        if (settingsButton != null) {
            settingsButton.dispose();
            settingsButton = null;
        }

    }

    /**
     * Update the status of this player mode.
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     *
     * @param dt Number of seconds since last animation frame
     */
    private void update(float dt) {

        if (cardOpen && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            cardOpen = false;
            listener.exitScreen(this, CutScene.INTO_CUTSCENE);
        }


        if (loadGameButtonEnabled == null) {
            manager.update(budget);
            this.progress = manager.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
                loadGameButtonEnabled = new Texture(LOAD_GAME_ENABLED);
                loadGameButtonEnabled.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                buttonX4 = buttonX + loadGameButtonEnabled.getWidth() / 2 * scale * BUTTON_SCALE - 80f;
            }
        }

        if (loadGameButtonDisabled == null) {
            manager.update(budget);
            this.progress = manager.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
                loadGameButtonDisabled = new Texture(LOAD_GAME_DISABLED);
                loadGameButtonDisabled.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                buttonX4 = buttonX + loadGameButtonDisabled.getWidth() / 2 * scale * BUTTON_SCALE - 80f;
            }
        }

        if (startGameButton == null) {
            manager.update(budget);
            this.progress = manager.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
                startGameButton = new Texture(START_FILE);
                startGameButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);

                buttonX1 = buttonX + startGameButton.getWidth() / 2 * scale * BUTTON_SCALE - 70f;
            }
        }
        if (settingsButton == null) {
            manager.update(budget);
            this.progress = manager.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
                settingsButton = new Texture(SETTINGS_FILE);
                settingsButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);

                buttonX2 = buttonX + settingsButton.getWidth() / 2 * BUTTON_SCALE * scale - 70f;
            }
        }
        if (quitButton == null) {
            manager.update(budget);
            this.progress = manager.getProgress();
            if (progress >= 1.0f) {
                this.progress = 1.0f;
                quitButton = new Texture(QUIT_FILE);
                quitButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);

                buttonX3 = buttonX + quitButton.getWidth() / 2 * BUTTON_SCALE * scale - 70f;
            }
        }
        if (animatedBkg == null) {
            animatedBkg = createFilmStrip(manager, CHAR_ANIMATION_FILE, 1, 4, 4);
        }


    }

    /**
     * Draw the status of this player mode.
     * <p>
     * We prefer to separate update and draw from one another as separate methods, instead
     * of using the single render() method that LibGDX does.  We will talk about why we
     * prefer this in lecture.
     */
    private void draw() {
        canvas.begin();


        if (animatedBkg != null) {
            frameCount++;
            if (frameCount % 8 == 0) {
                animatedBkg.setNextFrame();
                frameCount = 0;
            }
            Color tint = cardOpen ? Color.GRAY : Color.WHITE;
            canvas.drawAnimatedBkg(animatedBkg, tint);
        }
        if (startGameButton == null) {
//            canvas.draw(exclamationTexture, Color.WHITE,player.getX()*scale.x,
//                player.getY()*scale.y, exclamationTexture.getRegionWidth()*0.1f, exclamationTexture.getRegionHeight()*0.1f);
//        ((FilmStrip) exclamationTexture).setNextFrame();
//            canvas.drawUI();
            canvas.drawItemCount("LOADING..." + ((int) (progress * 100)) + "%", canvas.getWidth() * 3 / 5 - 30, (int) buttonY2 + 80);
        }

        if (levelState.getBoolean("saveExists")) {
            if (loadGameButtonEnabled != null) {
                Color tint = Color.WHITE;
                canvas.draw(loadGameButtonEnabled, tint, startGameButton.getWidth() / 2, startGameButton.getHeight() / 2,
                        buttonX4, buttonY4, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
            }
        } else {
            if (loadGameButtonDisabled != null) {
                Color tint = Color.WHITE;
                canvas.draw(loadGameButtonDisabled, tint, startGameButton.getWidth() / 2, startGameButton.getHeight() / 2,
                        buttonX4, buttonY4, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
            }
        }

        if (startGameButton != null) {
            Color tint = (pressState == MouseState.START ? Color.GRAY : Color.WHITE);
            canvas.draw(startGameButton, tint, startGameButton.getWidth() / 2, startGameButton.getHeight() / 2,
                    buttonX1, buttonY1, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
        }
        if (settingsButton != null) {
            Color tint = (pressState == MouseState.SETTINGS ? Color.GRAY : Color.WHITE);
            canvas.draw(settingsButton, tint, settingsButton.getWidth() / 2, settingsButton.getHeight() / 2,
                    buttonX2, buttonY2, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
        }
        if (quitButton != null) {
            Color tint = (pressState == MouseState.QUIT ? Color.GRAY : Color.WHITE);
            canvas.draw(quitButton, tint, quitButton.getWidth() / 2, quitButton.getHeight() / 2,
                    buttonX3, buttonY3, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
        }
        if (selectState != MouseState.NONE && selectState != MouseState.OTHER) {
            float y = selectState == MouseState.START ? buttonY1 : selectState == MouseState.SETTINGS ? buttonY2 : buttonY3;
            if (selectState == MouseState.LOAD_GAME) {
                y = buttonY4;
            }
            Color tint = Color.WHITE;
            canvas.draw(select, tint, select.getWidth() / 2, select.getHeight() / 2,
                    buttonX, y, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);
        }


        canvas.draw(logo, Color.WHITE, logo.getWidth() / 2, logo.getHeight() / 2,
                logoX, logoY, 0, 0.85f * scale, 0.85f * scale);


        if (cardOpen) {
            canvas.draw(cardTexture, Color.WHITE, cardTexture.getWidth() / 2, cardTexture.getHeight() / 2,
                    canvas.getWidth() / 2, canvas.getHeight() / 2, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);

            canvas.draw(nvmTexture, Color.WHITE, nvmTexture.getWidth() / 2, nvmTexture.getHeight() / 2,
                    buttonNVMX, buttonNVMY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);

            canvas.draw(newGameGrey, Color.WHITE, nvmTexture.getWidth() / 2, nvmTexture.getHeight() / 2,
                    buttonSRTX, buttonNVMY, 0, BUTTON_SCALE * scale, BUTTON_SCALE * scale);


        }


        canvas.end();
    }

    /**
     * Updates the progress bar according to loading progress
     * <p>
     * The progress bar is composed of parts: two rounded caps on the end,
     * and a rectangle in a middle.  We adjust the size of the rectangle in
     * the middle to represent the amount of progress.
     *
     * @param canvas The drawing context
     */
//    private void drawProgress(GameCanvas canvas) {
//        canvas.draw(statusBkgLeft, Color.WHITE, centerX - width / 2, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//        canvas.draw(statusBkgRight, Color.WHITE, centerX + width / 2 - scale * PROGRESS_CAP, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//        canvas.draw(statusBkgMiddle, Color.WHITE, centerX - width / 2 + scale * PROGRESS_CAP, centerY, width - 2 * scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//
//        canvas.draw(statusFrgLeft, Color.WHITE, centerX - width / 2, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//        if (progress > 0) {
//            float span = progress * (width - 2 * scale * PROGRESS_CAP) / 2.0f;
//            canvas.draw(statusFrgRight, Color.WHITE, centerX - width / 2 + scale * PROGRESS_CAP + span, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//            canvas.draw(statusFrgMiddle, Color.WHITE, centerX - width / 2 + scale * PROGRESS_CAP, centerY, span, scale * PROGRESS_HEIGHT);
//        } else {
//            canvas.draw(statusFrgRight, Color.WHITE, centerX - width / 2 + scale * PROGRESS_CAP, centerY, scale * PROGRESS_CAP, scale * PROGRESS_HEIGHT);
//        }
//    }

    // ADDITIONAL SCREEN METHODS

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
            update(delta);
            draw();

            // We are are ready, notify our listener
            if (listener != null && pressState == MouseState.QUIT) {
                System.exit(0);
            } else if (listener != null && pressState == MouseState.START) {
                pressState = MouseState.OTHER;
                cardOpen = true;
            } else if (listener != null && pressState == MouseState.SETTINGS) {
                pressState = MouseState.NONE;
                listener.exitScreen(this, SettingMode.INTO_SETTING);
            } else if (listener != null && pressState == MouseState.LOAD_GAME) {
                if (levelState.getBoolean("saveExists", false)) {
                    listener.exitScreen(this, LevelSelectorMode.INTO_SELECTOR);
                }
                pressState = MouseState.NONE;
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
        // Compute the drawing scale
        float sx = ((float) width) / STANDARD_WIDTH;
        float sy = ((float) height) / STANDARD_HEIGHT;
        scale = (Math.min(sx, sy));

        this.width = (int) (BAR_WIDTH_RATIO * width);
        centerY = (int) (BAR_HEIGHT_RATIO * height);
        centerX = width / 2;

        logoX = 6 * width / 10;
        logoY = 2 * height / 3;
        heightY = height;
        buttonX = (3 * width) / 4;
        buttonY4 = 4.5f * height / 10f;
        buttonY1 = buttonY4 - height / 9;
        buttonY2 = buttonY1 - height / 9;
        buttonY3 = buttonY2 - height / 9;
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
        music.setVolume(0.5f * GDXRoot.musicVol);
        music.play();
        music.setLooping(true);
        active = true;
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    public void hide() {
        // Useless if called in outside animation loop
        active = false;
        music.pause();
        pressState = MouseState.NONE;
    }

    public void reset() {
        music.setVolume(0.5f * GDXRoot.musicVol);
        music.play();
    }

    /**
     * Sets the ScreenListener for this mode
     * <p>
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    // PROCESSING PLAYER INPUT

    /**
     * Called when the screen was touched or a mouse button was pressed.
     * <p>
     * This method checks to see if the play button is available and if the click
     * is in the bounds of the play button.  If so, it signals the that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (settingsButton == null || quitButton == null || startGameButton == null
                || pressState == MouseState.OTHER) {
            return true;
        }
        screenY = heightY - screenY;
        if (cardOpen) {
            float b1 = BUTTON_SCALE * scale * nvmTexture.getWidth() / 2.0f;
            float b2 = BUTTON_SCALE * scale * nvmTexture.getHeight() / 2.0f;
            if (Math.abs(screenX - buttonNVMX) < b1 && Math.abs(screenY - buttonNVMY) < b2) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                cardOpen = false;
            }

            float s1 = BUTTON_SCALE * scale * newGameGrey.getWidth() / 2.0f;
            float s2 = BUTTON_SCALE * scale * newGameGrey.getHeight() / 2.0f;
            if (Math.abs(screenX - buttonSRTX) < s1 && Math.abs(screenY - buttonNVMY) < s2) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                cardOpen = false;
                listener.exitScreen(this, CutScene.INTO_CUTSCENE);
            }
            return false;
        }

        // Flip to match graphics coordinates


        float w1 = BUTTON_SCALE * scale * startGameButton.getWidth() / 2.0f;
        float h1 = BUTTON_SCALE * scale * startGameButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX) < w1 && Math.abs(screenY - buttonY1) < h1) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            pressState = MouseState.START;
        }

        float w2 = BUTTON_SCALE * scale * settingsButton.getWidth() / 2.0f;
        float h2 = BUTTON_SCALE * scale * settingsButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX) < w2 && Math.abs(screenY - buttonY2) < h2) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            pressState = MouseState.SETTINGS;
        }

        float w3 = BUTTON_SCALE * scale * quitButton.getWidth() / 2.0f;
        float h3 = BUTTON_SCALE * scale * quitButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX) < w3 && Math.abs(screenY - buttonY3) < h3) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            pressState = MouseState.QUIT;
        }

        float w4 = BUTTON_SCALE * scale * loadGameButtonDisabled.getWidth() / 2.0f;
        float h4 = BUTTON_SCALE * scale * loadGameButtonDisabled.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX) < w4 && Math.abs(screenY - buttonY4) < h4) {
            clickSound.play(0.5f * GDXRoot.soundVol);
            pressState = MouseState.LOAD_GAME;
        }


        return false;
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     * <p>
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pressState == MouseState.START) {
            pressState = MouseState.OTHER;
            return false;
        } else if (pressState == MouseState.QUIT) {
            listener.exitScreen(this, 0);
            return false;
        }
        pressState = MouseState.NONE;
        return true;
    }

    /**
     * Called when a button on the Controller was pressed.
     * <p>
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == startButton && pressState == MouseState.NONE) {
            pressState = MouseState.START;
            return false;
        }
        return true;
    }

    /**
     * Called when a button on the Controller was released.
     * <p>
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (pressState == MouseState.START && buttonCode == startButton) {
            pressState = MouseState.OTHER;
            return false;
        }
        return true;
    }


    // UNSUPPORTED METHODS FROM InputProcessor

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyDown(int keycode) {
        if (settingsButton == null || quitButton == null || startGameButton == null
                || pressState == MouseState.OTHER) {
            return true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            pressState = selectState;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (keyState < 4)
                keyState += 1;
            else
                keyState = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (keyState > 0)
                keyState -= 1;
            else
                keyState = 4;
        }
        keyState %= 5;
        if (keyState == 0)
            selectState = MouseState.NONE;
        if (keyState == 2)
            selectState = MouseState.START;
        if (keyState == 3)
            selectState = MouseState.SETTINGS;
        if (keyState == 4)
            selectState = MouseState.QUIT;
        if (keyState == 1)
            selectState = MouseState.LOAD_GAME;
        return false;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @param character the key typed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyTyped(char character) {
        return true;
    }

    /**
     * Called when a key is released.
     * <p>
     * We allow key commands to start the game this time.
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
            pressState = MouseState.OTHER;
            return false;
        }
        return true;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */
    public boolean mouseMoved(int screenX, int screenY) {
        if (settingsButton == null || quitButton == null || startGameButton == null
                || pressState == MouseState.OTHER) {
            return true;
        }
        if (cardOpen) {
            return false;
        }
        screenY = heightY - screenY;

        float w1 = BUTTON_SCALE * scale * startGameButton.getWidth() / 2.0f;
        float h1 = BUTTON_SCALE * scale * startGameButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX1) < w1 && Math.abs(screenY - buttonY1) < h1) {
            selectState = MouseState.START;
            return false;
        }

        float w2 = BUTTON_SCALE * scale * settingsButton.getWidth() / 2.0f;
        float h2 = BUTTON_SCALE * scale * settingsButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX2) < w2 && Math.abs(screenY - buttonY2) < h2) {
            selectState = MouseState.SETTINGS;
            return false;
        }

        float w3 = BUTTON_SCALE * scale * quitButton.getWidth() / 2.0f;
        float h3 = BUTTON_SCALE * scale * quitButton.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX3) < w3 && Math.abs(screenY - buttonY3) < h3) {
            selectState = MouseState.QUIT;
            return false;
        }

        float w4 = BUTTON_SCALE * scale * loadGameButtonDisabled.getWidth() / 2.0f;
        float h4 = BUTTON_SCALE * scale * loadGameButtonDisabled.getHeight() / 2.0f;
        if (Math.abs(screenX - buttonX4) < w4 && Math.abs(screenY - buttonY4) < h4) {
            selectState = MouseState.LOAD_GAME;
            return false;
        }

        selectState = MouseState.NONE;
        return true;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param amount the amount of scroll from the wheel
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(int amount) {
        return true;
    }

    /**
     * Called when the mouse or finger was dragged. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    // UNSUPPORTED METHODS FROM ControllerListener

    /**
     * Called when a controller is connected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void connected(Controller controller) {
    }

    /**
     * Called when a controller is disconnected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void disconnected(Controller controller) {
    }

    /**
     * Called when an axis on the Controller moved. (UNSUPPORTED)
     * <p>
     * The axisCode is controller specific. The axis value is in the range [-1, 1].
     *
     * @param controller The game controller
     * @param axisCode   The axis moved
     * @param value      The axis value, -1 to 1
     * @return whether to hand the event to other listeners.
     */
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return true;
    }

    /**
     * Called when a POV on the Controller moved. (UNSUPPORTED)
     * <p>
     * The povCode is controller specific. The value is a cardinal direction.
     *
     * @param controller The game controller
     * @param povCode    The POV controller moved
     * @param value      The direction of the POV
     * @return whether to hand the event to other listeners.
     */
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return true;
    }

    /**
     * Called when an x-slider on the Controller moved. (UNSUPPORTED)
     * <p>
     * The x-slider is controller specific.
     *
     * @param controller The game controller
     * @param sliderCode The slider controller moved
     * @param value      The direction of the slider
     * @return whether to hand the event to other listeners.
     */
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return true;
    }

    /**
     * Called when a y-slider on the Controller moved. (UNSUPPORTED)
     * <p>
     * The y-slider is controller specific.
     *
     * @param controller The game controller
     * @param sliderCode The slider controller moved
     * @param value      The direction of the slider
     * @return whether to hand the event to other listeners.
     */
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return true;
    }

    /**
     * Called when an accelerometer value on the Controller changed. (UNSUPPORTED)
     * <p>
     * The accelerometerCode is controller specific. The value is a Vector3 representing
     * the acceleration on a 3-axis accelerometer in m/s^2.
     *
     * @param controller        The game controller
     * @param accelerometerCode The accelerometer adjusted
     * @param value             A vector with the 3-axis acceleration
     * @return whether to hand the event to other listeners.
     */
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return true;
    }

    protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size) {
        if (manager.isLoaded(file)) {
            FilmStrip strip = new FilmStrip(manager.get(file, Texture.class), rows, cols, size, true);
            strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return strip;
        }
        return null;
    }

}