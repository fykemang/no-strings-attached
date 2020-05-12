package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;


public class LevelTransitionMode extends Mode implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    private static final String BK_FILE = "ui/sky.png";
    private static final String BUTTON = "ui/next.png";
    private static final String PC = "player/player_idle.png";
//    private static final String WIN = "ui/basket_3.png";
    private static final String REPLAY = "ui/replay.png";
    private static final String MAIN_MENU = "ui/main-menu.png";
    private static final String WIN_TEXT = "ui/excellent.png";
    private static final String BUTTON_PRESSED = "ui/next-down.png";
    //    private final String TRANSITION_MUSIC_FILE = "music/goodnight.mp3";
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";
    private static final String VICTORY_FILE = "sounds/victorymarimba.mp3";
    private final String SELECTOR = "ui/next-select.png";
    private static final String BASKET_CITY1 = "ui/city_basket1.png";
    private static final String BASKET_CITY2 = "ui/city_basket2.png";
    private static final String BASKET_CITY3 = "ui/city_basket3.png";
    private static final String BASKET_VILLAGE1 = "ui/village_basket1.png";
    private static final String BASKET_VILLAGE2 = "ui/village_basket2.png";
    private static final String BASKET_VILLAGE3 = "ui/village_basket3.png";
    private static final String BASKET_FOREST1 = "ui/forest_basket1.png";
    private static final String BASKET_FOREST2 = "ui/forest_basket2.png";
    private static final String BASKET_FOREST3 = "ui/forest_basket3.png";
    private static final String BASKET_MT1 = "ui/mt_basket1.png";
    private static final String BASKET_MT2 = "ui/mt_basket2.png";
    private static final String BASKET_MT3 = "ui/mt_basket3.png";
    private TextureRegion basketEmptyTexture;
    private TextureRegion basketOneTexture;
    private TextureRegion basketTwoTexture;
    private TextureRegion basketThreeTexture;
    private TextureRegion basketCity1Texture;
    private TextureRegion basketCity2Texture;
    private TextureRegion basketCity3Texture;
    private TextureRegion basketVillage1Texture;
    private TextureRegion basketVillage2Texture;
    private TextureRegion basketVillage3Texture;
    private TextureRegion basketForest1Texture;
    private TextureRegion basketForest2Texture;
    private TextureRegion basketForest3Texture;
    private TextureRegion basketMt1Texture;
    private TextureRegion basketMt2Texture;
    private TextureRegion basketMt3Texture;
    private Texture background;
    private Texture yarnie;
    private TextureRegion pc;
    private Texture winMessage;
    private Texture replayButtonTexture;
    private final ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
    private Texture mainMenuButtonTexture;
    private Texture nextButtonTexture;
    private TextureRegion selectTexture;

    @Override
    public void preloadContent(AssetManager manager) {
        if (assetState != AssetState.EMPTY) {
            return;
        }
        assetState = AssetState.LOADING;
        loadAsset(BK_FILE, Texture.class, manager);
        loadAsset(BUTTON, Texture.class, manager);
        loadAsset(PC, Texture.class, manager);
//        loadAsset(WIN, Texture.class, manager);
        loadAsset(REPLAY, Texture.class, manager);
        loadAsset(MAIN_MENU, Texture.class, manager);
        loadAsset(WIN_TEXT, Texture.class, manager);
//        loadAsset(TRANSITION_MUSIC_FILE, Music.class, manager);
        loadAsset(VICTORY_FILE, Sound.class, manager);
        loadAsset(SELECTOR, Texture.class, manager);
        loadAsset(BUTTON_PRESSED, Texture.class, manager);
        manager.load(BASKET_CITY1, Texture.class);
        assets.add(BASKET_CITY1);
        manager.load(BASKET_CITY2, Texture.class);
        assets.add(BASKET_CITY2);
        manager.load(BASKET_CITY3, Texture.class);
        assets.add(BASKET_CITY3);
        manager.load(BASKET_VILLAGE1, Texture.class);
        assets.add(BASKET_VILLAGE1);
        manager.load(BASKET_VILLAGE2, Texture.class);
        assets.add(BASKET_VILLAGE2);
        manager.load(BASKET_VILLAGE3, Texture.class);
        assets.add(BASKET_VILLAGE3);
        manager.load(BASKET_FOREST1, Texture.class);
        assets.add(BASKET_FOREST1);
        manager.load(BASKET_FOREST2, Texture.class);
        assets.add(BASKET_FOREST2);
        manager.load(BASKET_FOREST3, Texture.class);
        assets.add(BASKET_FOREST3);
        manager.load(BASKET_MT1, Texture.class);
        assets.add(BASKET_MT1);
        manager.load(BASKET_MT2, Texture.class);
        assets.add(BASKET_MT2);
        manager.load(BASKET_MT3, Texture.class);
        assets.add(BASKET_MT3);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }
        background = manager.get(BK_FILE, Texture.class);
//        yarnie = manager.get(WIN, Texture.class);
//        yarnie = basketThreeTexture;
        winMessage = manager.get(WIN_TEXT, Texture.class);
        pc = new TextureRegion(manager.get(PC, Texture.class));
        TextureRegion nextButtonPressed = new TextureRegion(manager.get(BUTTON_PRESSED, Texture.class));
        replayButtonTexture = manager.get(REPLAY, Texture.class);
        mainMenuButtonTexture = manager.get(MAIN_MENU, Texture.class);
        selectTexture = new TextureRegion(manager.get(SELECTOR, Texture.class));
        nextButtonTexture = manager.get(BUTTON, Texture.class);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(nextButtonTexture));
        buttonStyle.over = new TextureRegionDrawable(nextButtonPressed);
//        music = manager.get(TRANSITION_MUSIC_FILE, Music.class);
        victorySound = manager.get(VICTORY_FILE, Sound.class);
        clickSound = manager.get(CLICK_FILE, Sound.class);
        hoverSound = manager.get(HOVER_FILE, Sound.class);
//        basketEmptyTexture = createTexture(manager, BASKET_EMPTY, false);
        basketCity1Texture = new TextureRegion(manager.get(BASKET_CITY1, Texture.class));
        basketCity2Texture =  new TextureRegion(manager.get(BASKET_CITY2, Texture.class));
        basketCity3Texture =  new TextureRegion(manager.get(BASKET_CITY3, Texture.class));
        basketVillage1Texture =  new TextureRegion(manager.get(BASKET_VILLAGE1, Texture.class));
        basketVillage2Texture = new TextureRegion(manager.get(BASKET_VILLAGE2, Texture.class));
        basketVillage3Texture = new TextureRegion(manager.get(BASKET_VILLAGE3, Texture.class));
        basketForest1Texture = new TextureRegion(manager.get(BASKET_FOREST1, Texture.class));
        basketForest2Texture = new TextureRegion(manager.get(BASKET_FOREST2, Texture.class));
        basketForest3Texture = new TextureRegion(manager.get(BASKET_FOREST3, Texture.class));
        basketMt1Texture = new TextureRegion(manager.get(BASKET_MT1, Texture.class));
        basketMt2Texture = new TextureRegion(manager.get(BASKET_MT2, Texture.class));
        basketMt3Texture = new TextureRegion(manager.get(BASKET_MT3, Texture.class));
        assetState = AssetState.COMPLETE;
    }

    enum SelectedButton {
        REPLAY,
        EXIT,
        NEXT
    }

    enum basketType {
        C1, C2, C3, V1, V2, V3, F1, F2, F3, M1, M2, M3
    }

    private SelectedButton currentSelection;
    private final Stage stage;
    private ImageButton replayButton;
    private ImageButton mainMenuButton;
    private boolean isLevelComplete;
    //    private Music music;
    private Sound victorySound;
    private Sound hoverSound;
    private Sound clickSound;
    private int level;

    public LevelTransitionMode() {
        this.stage = new Stage();
        active = true;
        this.level = GDXRoot.currentLevel;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void initializeInterface() {
        ImageButton nextButton = new ImageButton(buttonStyle);
        nextButton.setPosition(canvas.getWidth() * 5 / 6 - nextButtonTexture.getWidth() / 2, canvas.getHeight() / 6);
        final LevelTransitionMode transition = this;
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, GameMode.EXIT_INTO_NEXT);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SelectedButton.NEXT;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(nextButton);


        replayButton = createButton(replayButtonTexture);
        replayButton.setPosition(canvas.getWidth() / 6 - replayButton.getWidth() / 2, canvas.getHeight() / 6);
        replayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, GameMode.EXIT_INTO_GAME);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SelectedButton.REPLAY;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(replayButton);

        mainMenuButton = createButton(mainMenuButtonTexture);
        mainMenuButton.setPosition(canvas.getWidth() / 2 - mainMenuButton.getWidth() / 2, canvas.getHeight() / 6);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, LevelSelectorMode.INTO_SELECTOR);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SelectedButton.EXIT;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(mainMenuButton);

        victorySound.play(0.2f * GDXRoot.soundVol);
//        music.play();
//        music.setVolume(0.5f * GDXRoot.musicVol);
//        music.setLooping(true);

        if (level < 5) {
            basketOneTexture = basketCity1Texture;
            basketTwoTexture = basketCity2Texture;
            basketThreeTexture = basketCity3Texture;
        }
        else if (level < 9) {
            basketOneTexture = basketVillage1Texture;
            basketTwoTexture = basketVillage2Texture;
            basketThreeTexture = basketVillage3Texture;
        }
        else if (level < 13) {
            basketOneTexture = basketForest1Texture;
            basketTwoTexture = basketForest2Texture;
            basketThreeTexture = basketForest3Texture;
        }
        else {
            basketOneTexture = basketMt1Texture;
            basketTwoTexture = basketMt2Texture;
            basketThreeTexture = basketMt3Texture;
        }

        try {
            // Let ANY connected controller start the game.
            for (Controller controller : Controllers.getControllers()) {
                controller.addListener(this);
            }
        } catch (Exception e) {
            System.out.println("Error: Game Controllers could not be initialized");
        }
        Gdx.input.setInputProcessor(stage);
    }

    public void exit() {

    }

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private final boolean active;

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
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
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {
//        music.play();
        victorySound.play(0.2f * GDXRoot.soundVol);
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
        victorySound.pause();
//        music.pause();
    }

    @Override
    public void dispose() {
        victorySound.dispose();
//        music.dispose();
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
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

    }

    private int lastState = 0;

    private void draw() {
        stage.draw();
        canvas.begin();
        canvas.drawBackground(background, canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.draw(basketThreeTexture, canvas.getWidth() * 2.2f / 5 - basketThreeTexture.getRegionWidth() / 2,
                canvas.getHeight() / 2 - basketThreeTexture.getRegionHeight() / 2 - pc.getRegionHeight() / 5);
        canvas.drawUI(pc, canvas.getWidth() * 3 / 5,
                canvas.getHeight() / 2, 0.5f);
        if (isLevelComplete) {
            canvas.draw(winMessage, canvas.getWidth() / 2 - winMessage.getWidth() / 2,
                    canvas.getHeight() * 4 / 5 - winMessage.getHeight() / 2);
        }

        if (currentSelection != null)
            switch (currentSelection) {
                case REPLAY:
                    if (lastState != 1) {
                        hoverSound.play(3 * GDXRoot.soundVol);
                    }
                    lastState = 1;
                    canvas.drawUI(selectTexture, canvas.getWidth() / 6 - replayButton.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case EXIT:
                    if (lastState != 2) {
                        hoverSound.play(3 * GDXRoot.soundVol);
                    }
                    lastState = 2;
                    canvas.drawUI(selectTexture, canvas.getWidth() / 2 - mainMenuButton.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case NEXT:
                    if (lastState != 3) {
                        hoverSound.play(3 * GDXRoot.soundVol);
                    }
                    lastState = 3;
                    canvas.drawUI(selectTexture, canvas.getWidth() * 5 / 6 - nextButtonTexture.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                default:
                    break;
            }
        canvas.actStage(stage);
        canvas.end();
    }


    private ImageButton createButton(Texture texture) {
        TextureRegion buttonRegion = new TextureRegion(texture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(buttonRegion);
        return new ImageButton(myTexRegionDrawable);
    }


    public void reset() {
        victorySound.play(0.2f * GDXRoot.soundVol);
//        music.play();
//        music.setVolume(0.5f * GDXRoot.musicVol);
        Gdx.input.setInputProcessor(stage);
    }

    public void setLevelComplete(boolean isLevelComplete) {
        this.isLevelComplete = isLevelComplete;
    }

}
