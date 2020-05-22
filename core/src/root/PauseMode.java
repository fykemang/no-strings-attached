package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.ControllerListener;
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


public class PauseMode extends Mode implements Screen, InputProcessor {
    public static final int INTO_PAUSE = 15;

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            pressState = currentState;
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
            currentState = null;
        if (keyState == 1)
            currentState = SelectedButton.CONTINUE;
        if (keyState == 2)
            currentState = SelectedButton.RESTART;
        if (keyState == 3)
            currentState = SelectedButton.LEVEL_SELECT;
        if (keyState == 4)
            currentState = SelectedButton.SETTINGS;
        if (keyState == 4)
            currentState = SelectedButton.HELP;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    enum SelectedButton {
        CONTINUE,
        LEVEL_SELECT,
        RESTART,
        SETTINGS,
        HELP
    }

    private ImageButton currentSelection;
    private SelectedButton pressState;
    private SelectedButton currentState;
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
        final PauseMode pause = this;
        Gdx.input.setInputProcessor(this);
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
                keyState = 1;
                currentState = SelectedButton.CONTINUE;
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
                keyState = 2;
                currentState = SelectedButton.RESTART;
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
                keyState = 3;
                currentState = SelectedButton.LEVEL_SELECT;
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
                keyState = 4;
                currentState = SelectedButton.SETTINGS;
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
                keyState = 5;
                currentState = SelectedButton.HELP;
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
        }
    }

    private void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pressState = currentState;
            if (pressState == SelectedButton.CONTINUE) {
                clickSound.play(GDXRoot.soundVol);
                listener.exitScreen(pause, GameMode.EXIT_INTO_GAME);
            } else if (pressState == SelectedButton.LEVEL_SELECT) {
                clickSound.play(GDXRoot.soundVol);
                listener.exitScreen(pause, LevelSelectorMode.INTO_SELECTOR);
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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)
                || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (keyState < 5)
                keyState += 1;
            else
                keyState = 0;
            hoverSound.play(GDXRoot.soundVol);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)
                || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (keyState > 0)
                keyState -= 1;
            else
                keyState = 5;
            hoverSound.play(GDXRoot.soundVol);
        }
        keyState %= 6;
        if (keyState == 0) {
            currentState = null;
            currentSelection = null;
        }
        if (keyState == 1) {
            currentState = SelectedButton.CONTINUE;
            currentSelection = continueButton;
        }
        if (keyState == 2) {
            currentState = SelectedButton.RESTART;
            currentSelection = reStartButton;
        }
        if (keyState == 3) {
            currentState = SelectedButton.LEVEL_SELECT;
            currentSelection = levelSelectButton;
        }
        if (keyState == 4) {
            currentState = SelectedButton.SETTINGS;
            currentSelection = settingsButton;
        }
        if (keyState == 5) {
            currentState = SelectedButton.HELP;
            currentSelection = mainButton;
        }
    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(bkgTexture.getTexture(), canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.drawUI(logoTexture, canvas.getWidth() / 2, canvas.getHeight() * 0.75f, 1f);
        canvas.actStage(stage);

        if (currentSelection != null) {
            canvas.drawUI(selectorTexture, currentSelection.getX() + reStartButton.getWidth() / 4,
                    currentSelection.getY(), 1.1f);
//            switch (currentState) {
//                case RESTART:
//                    canvas.drawUI(selectorTexture, currentSelection.getX() + reStartButton.getWidth() / 4,
//                            currentSelection.getY(), 1.1f);
//                    break;
//                case HELP:
//                    canvas.drawUI(selectorTexture, currentSelection.getX() + continueButton.getWidth() / 4,
//                            currentSelection.getY(), 1.1f);
//                    break;
//                case CONTINUE:
//                    canvas.drawUI(selectorTexture, currentSelection.getX() + continueButton.getWidth() / 4,
//                            currentSelection.getY(), 1.1f);
//                    break;
//                case LEVEL_SELECT:
//                    canvas.drawUI(selectorTexture, currentSelection.getX() + selectorTexture.getRegionWidth() / 4,
//                            currentSelection.getY(), 1f);
//                    break;
//                case SETTINGS:
//                    canvas.drawUI(selectorTexture, currentSelection.getX() + settingsButton.getWidth() / 4,
//                            currentSelection.getY(), 1.1f);
//                    break;
//                default:
//                    break;
//            }
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

