package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;


public class PauseMode extends Mode implements Screen {
    public static final int INTO_PAUSE = 15;

    enum SelectedButton {
        CONTINUE,
        LEVEL_SELECT,
        MENU,
        RESTART,
        SETTINGS,
        HELP
    }

    private ImageButton currentSelection;
    private SelectedButton pressState;
    private int keyState;
    private Sound hoverSound;
    private Sound clickSound;
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";


    private static final String CONTINUE = "ui/continue.png";
    private static final String LEVEL_SELECT = "ui/level-select.png";
    private static final String MAIN_MENU = "ui/main-menu.png";
    private static final String RESTART = "ui/restart-level.png";
    private static final String TITLE = "ui/pause-logo.png";
    private static final String SETTINGS = "ui/settings.png";
    private static final String BKG = "ui/sky.png";
    private static final String SELECT = "ui/pause-selected.png";


    private TextureRegion continueTexture;
    private TextureRegion levelselectTexture;
    private TextureRegion mainTexture;
    private TextureRegion restartTexture;
    private TextureRegion logoTexture;
    private TextureRegion settingsTexture;
    private TextureRegion selectorTexture;
    private TextureRegion bkgTexture;


    ImageButton continueButton;
    ImageButton reStartButton;
    ImageButton settingsButton;
    ImageButton levelSelectButton;
    ImageButton mainButton;

    @Override
    public void preloadContent(AssetManager manager) {
        if (assetState != AssetState.EMPTY) {
            return;
        }
        assetState = AssetState.LOADING;
        loadAsset(CONTINUE, Texture.class, manager);
        loadAsset(LEVEL_SELECT, Texture.class, manager);
        loadAsset(RESTART, Texture.class, manager);
        loadAsset(BKG, Texture.class, manager);
        loadAsset(TITLE, Texture.class, manager);
        loadAsset(SELECT, Texture.class, manager);
        loadAsset(MAIN_MENU, Texture.class, manager);
        loadAsset(SETTINGS, Texture.class, manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }
        continueTexture = createTexture(manager, CONTINUE, false);
        restartTexture = createTexture(manager, RESTART, false);
        logoTexture = createTexture(manager, TITLE, false);
        mainTexture = createTexture(manager, MAIN_MENU, false);
        settingsTexture = createTexture(manager, SETTINGS, false);
        selectorTexture = createTexture(manager, SELECT, false);
        bkgTexture = createTexture(manager, BKG, false);
        levelselectTexture = createTexture(manager, LEVEL_SELECT, false);
        clickSound = manager.get(CLICK_FILE, Sound.class);
        hoverSound = manager.get(HOVER_FILE, Sound.class);
    }


    private final Stage stage;
    private AssetManager manager;

    public PauseMode(AssetManager manager, GameCanvas canvas) {
        this.stage = new Stage();
        this.canvas = canvas;
        this.manager = manager;
        active = true;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    final PauseMode pause = this;

    public void initialize() {
        pressState = null;
        continueButton = createButton(continueTexture);
        continueButton.setPosition(canvas.getWidth() / 2 - continueButton.getWidth() / 2, canvas.getHeight() * 0.5f);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(pause, GameMode.EXIT_INTO_GAME);
                clickSound.play(GDXRoot.soundVol);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = continueButton;
                hoverSound.play(GDXRoot.soundVol);
            }
        });
        stage.addActor(continueButton);
        reStartButton = createButton(restartTexture);
        reStartButton.setPosition(canvas.getWidth() / 2 - continueButton.getWidth() / 2, canvas.getHeight() * 0.4f);
        reStartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(pause, GameMode.EXIT_RESET);
                clickSound.play(GDXRoot.soundVol);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = reStartButton;
                hoverSound.play(GDXRoot.soundVol);
            }

        });
        stage.addActor(reStartButton);

        levelSelectButton = createButton(levelselectTexture);
        levelSelectButton.setPosition(canvas.getWidth() / 2 - continueButton.getWidth() / 2, canvas.getHeight() * 0.3f);
        levelSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(pause, LevelSelectorMode.INTO_SELECTOR);
                clickSound.play(GDXRoot.soundVol);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = levelSelectButton;
                hoverSound.play(GDXRoot.soundVol);
            }

        });
        stage.addActor(levelSelectButton);


        settingsButton = createButton(settingsTexture);
        settingsButton.setPosition(canvas.getWidth() / 2 - continueButton.getWidth() / 2, canvas.getHeight() * 0.2f);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(pause, SettingMode.INTO_SETTING);
                clickSound.play(GDXRoot.soundVol);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = settingsButton;
                hoverSound.play(GDXRoot.soundVol);
            }

        });
        stage.addActor(settingsButton);


        mainButton = createButton(mainTexture);
        mainButton.setPosition(canvas.getWidth() / 2 - continueButton.getWidth() / 2, canvas.getHeight() * 0.1f);
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(pause, HelpMode.INTO_HELP);
                clickSound.play(GDXRoot.soundVol);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = mainButton;
                hoverSound.play(GDXRoot.soundVol);
            }

        });

        stage.addActor(mainButton);


        Gdx.input.setInputProcessor(stage);
    }

    public void exit() {

    }

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private boolean active;

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        hoverSound.dispose();
        clickSound.dispose();
    }


    @Override
    public void render(float delta) {
        if (active) {
            update(delta);
            draw();

            // We are are ready, notify our listener
            if (listener != null) {
                if (pressState == SelectedButton.CONTINUE) {
                    clickSound.play(GDXRoot.soundVol);
                    listener.exitScreen(pause, GameMode.EXIT_INTO_GAME);
                } else if (pressState == SelectedButton.LEVEL_SELECT) {
                    clickSound.play(GDXRoot.soundVol);
                    listener.exitScreen(pause, LevelSelectorMode.INTO_SELECTOR);
                } else if (pressState == SelectedButton.MENU) {
                    clickSound.play(GDXRoot.soundVol);
                    currentSelection = mainButton;
                } else if (pressState == SelectedButton.RESTART) {
                    clickSound.play(GDXRoot.soundVol);
                    listener.exitScreen(pause, GameMode.EXIT_RESET);
                } else if (pressState == SelectedButton.SETTINGS) {
                    clickSound.play(GDXRoot.soundVol);
                    listener.exitScreen(pause, SettingMode.INTO_SETTING);
                } else if (pressState == SelectedButton.HELP) {
                    clickSound.play(GDXRoot.soundVol);
                    listener.exitScreen(pause, HelpMode.INTO_HELP);
                }
            }
        }
    }

    private void update(float dt) {

    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(bkgTexture.getTexture(), canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.drawUI(logoTexture, canvas.getWidth() / 2, canvas.getHeight() * 0.75f, 1f);
        canvas.actStage(stage);

        if (currentSelection != null) {
            canvas.drawUI(selectorTexture, currentSelection.getX() + selectorTexture.getRegionWidth() / 4, currentSelection.getY(), 1f);
        }
        canvas.end();
    }


    private ImageButton createButton(TextureRegion texture) {
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(texture);
        return new ImageButton(myTexRegionDrawable);
    }


    public void reset() {
        Gdx.input.setInputProcessor(stage);
    }

    private TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
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


}

