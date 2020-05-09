package root;/*
 * game.GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter.
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import entities.LevelMetadata;
import util.ScreenListener;

/**
 * Root class for a LibGDX.
 * <p>
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However,
 * those classes are unique to each platform, while this class is the same across all
 * plaforms. In addition, this functions as the root class all intents and purposes,
 * and you would draw it as a root class in an architecture specification.
 */
public class GDXRoot extends Game implements ScreenListener {
    /**
     * AssetManager to load game assets (textures, sounds, etc.)
     */
    private final AssetManager manager;
    /**
     * Drawing context to display graphics (VIEW CLASS)
     */
    private GameCanvas canvas;
    /**
     * Player mode for the asset loading screen (CONTROLLER CLASS)
     */
    private LoadingMode loadingMode;

    private LevelSelectorMode levelSelector;

    /**
     * The controller for the game mode
     */
    private GameMode gameMode;

    private LevelTransitionMode transitionMode;

    private GameCanvas UIcanvas;

    private CutScene cutScene;

    private SettingMode settings;

    public static float musicVol = 1f;

    public static float soundVol = 1f;

    public static boolean useArrow = true;

    private int currentLevel;

    private boolean TransitionLoaded = false;


    /**
     * Creates a new game from the configuration settings.
     * <p>
     * This method configures the asset manager, but does not load any assets
     * or assign any screen.
     */
    public GDXRoot() {
        // Start loading with the asset manager
        manager = new AssetManager();

        // Add font support to the asset manager
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        manager.setLoader(LevelMetadata.class, ".json", new LevelLoader(resolver));
    }

    /**
     * Called when the Application is first created.
     * <p>
     * This is method immediately loads assets for the loading screen, and prepares
     * the asynchronous loader for all other assets.
     */
    public void create() {
        canvas = new GameCanvas(false);
        UIcanvas = new GameCanvas(true);

        loadingMode = new LoadingMode(UIcanvas, manager, 1);

        cutScene = new CutScene(manager, UIcanvas);
        cutScene.preloadContent(manager);

        settings = new SettingMode(manager, UIcanvas);
        settings.preloadContent(manager);

        gameMode = new GameMode();
        gameMode.preloadContent(manager);

        levelSelector = new LevelSelectorMode();
        levelSelector.preloadContent(manager);

        transitionMode = new LevelTransitionMode();
        transitionMode.preloadContent(manager);

        loadingMode.setScreenListener(this);
        Gdx.input.setInputProcessor(loadingMode);
        setScreen(loadingMode);
    }

    /**
     * Called when the Application is destroyed.
     * <p>
     * This is preceded by a call to pause().
     */
    public void dispose() {
        // Call dispose on our children
        setScreen(null);
        gameMode.unloadContent(manager);
        gameMode.dispose();

        levelSelector.unloadContent(manager);
        levelSelector.dispose();

        canvas.dispose();
        canvas = null;

        // Unload all of the resources
        manager.clear();
        manager.dispose();
        super.dispose();
    }

    /**
     * Called when the Application is resized.
     * <p>
     * This can happen at any point during a non-paused state but will never happen
     * before a call to create().
     *
     * @param width  The new width in pixels
     * @param height The new height in pixels
     */
    public void resize(int width, int height) {
        Vector2 size = Scaling.fit.apply(1024, 576, width, height);
        canvas.resize(width, height);
    }

    /**
     * The given screen has made a request to exit its player mode.
     * <p>
     * The value exitCode can be used to implement menu options.
     *
     * @param screen   The screen requesting to exit
     * @param exitCode The state of the screen upon exit
     */
    public void exitScreen(Screen screen, int exitCode) {
        // If start is selected from the loading screen
        if (screen == loadingMode) {
            Gdx.input.setInputProcessor(null);
            switch (exitCode) {
                case CutScene.INTO_CUTSCENE:
                    cutScene.setTheme(CutScene.THEME.OPENING);
                    cutScene.loadContent(manager);
                    cutScene.setScreenListener(this);
                    setScreen(cutScene);
                    loadingMode.hide();
                    break;
                case SettingMode.INTO_SETTING:
                    settings.setScreenListener(this);
                    settings.loadContent(manager);
                    settings.initUI();
                    setScreen(settings);
                    loadingMode.hide();
            }

            // If level is selected from level selector screen
        } else if (screen == settings) {
            useArrow = settings.isArrow();
            soundVol = settings.getSoundVol();
            musicVol = settings.getMusicVol();
            if (exitCode == LoadingMode.INTO_STARTSCREEN) {
                Gdx.input.setInputProcessor(loadingMode);
                loadingMode.reset();
                settings.hide();
                setScreen(loadingMode);
            }
        } else if (screen == cutScene && exitCode == LevelSelectorMode.INTO_SELECTOR) {
            levelSelector.loadContent(manager);
            levelSelector.setScreenListener(this);
            levelSelector.setCanvas(UIcanvas);
            Gdx.input.setInputProcessor(levelSelector);
            levelSelector.reset();
            setScreen(levelSelector);

        } else if (screen == levelSelector && exitCode == GameMode.EXIT_INTO_GAME) {
            Gdx.input.setInputProcessor(null);
            currentLevel = levelSelector.getLevelIndex();
            gameMode.setLevel(levelSelector.getCurrentLevel());
            gameMode.loadContent(manager);
            gameMode.initializeContent(manager);
            gameMode.setScreenListener(this);
            gameMode.setCanvas(canvas);
            gameMode.reset();
            setScreen(gameMode);
            levelSelector.pause();
            // If level select is selected from in game
        } else if (screen == gameMode) {
            switch (exitCode) {
                case LevelSelectorMode.INTO_SELECTOR:
                    levelSelector.setCanvas(UIcanvas);
                    levelSelector.reset();
                    levelSelector.setScreenListener(this);
                    Gdx.input.setInputProcessor(levelSelector);
                    setScreen(levelSelector);
                    gameMode.pause();
                    break;
                case LevelTransitionMode.INTO_TRANSITION:
                    transitionMode.setCanvas(UIcanvas);
                    if (!TransitionLoaded) {
                        transitionMode.loadContent(manager);
                        transitionMode.initializeInterface();
                        TransitionLoaded = true;
                    }
                    transitionMode.reset();
                    transitionMode.setLevelComplete(gameMode.levelComplete());
                    transitionMode.setScreenListener(this);
                    setScreen(transitionMode);
                    gameMode.pause();
            }
        } else if (screen == transitionMode) {
            Gdx.input.setInputProcessor(null);
            transitionMode.hide();
            switch (exitCode) {
                case (LevelSelectorMode.INTO_SELECTOR):
                    levelSelector.reset();
                    Gdx.input.setInputProcessor(levelSelector);
                    setScreen(levelSelector);
                    break;
                case (GameMode.EXIT_INTO_GAME):
                    gameMode.reset();
                    gameMode.resume();
                    setScreen(gameMode);
                    break;
                case (GameMode.EXIT_INTO_NEXT):
                    currentLevel++;
                    if (levelSelector.getLevel(currentLevel) == null) {
                        levelSelector.reset();
                        Gdx.input.setInputProcessor(levelSelector);
                        setScreen(levelSelector);
                        return;
                    }
                    gameMode.setLevel(levelSelector.getLevel(currentLevel));
                    gameMode.loadContent(manager);
                    gameMode.initializeContent(manager);
                    gameMode.reset();
                    setScreen(gameMode);
            }
        } else if (exitCode == GameMode.EXIT_QUIT) {
            Gdx.app.exit();
        }
    }

}

