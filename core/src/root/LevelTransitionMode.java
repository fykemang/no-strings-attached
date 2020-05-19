package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Select;
import entities.Level;
import util.ScreenListener;


public class LevelTransitionMode extends Mode implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    private static final String BK_FILE = "ui/sky.png";
    private static final String BUTTON = "ui/next.png";
    private static final String PC = "player/player_idle.png";
    private static final String REPLAY = "ui/replay.png";
    private static final String MAIN_MENU = "ui/main-menu.png";
    private static final String WIN_TEXT = "ui/excellent.png";
    private static final String BUTTON_PRESSED = "ui/next-down.png";
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";
    private static final String VICTORY_FILE = "sounds/victorymarimba.mp3";
    private final String SELECTOR = "ui/next-select.png";
    private static final String BASKET_EMPTY = "ui/basket_empty.png";
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
    private TextureRegion basketTexture;
    private TextureRegion basketEmptyTexture;
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
        loadAsset(REPLAY, Texture.class, manager);
        loadAsset(MAIN_MENU, Texture.class, manager);
        loadAsset(WIN_TEXT, Texture.class, manager);
        loadAsset(VICTORY_FILE, Sound.class, manager);
        loadAsset(SELECTOR, Texture.class, manager);
        loadAsset(BUTTON_PRESSED, Texture.class, manager);
        manager.load(BASKET_EMPTY, Texture.class);
        assets.add(BASKET_EMPTY);
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
        winMessage = manager.get(WIN_TEXT, Texture.class);
        pc = new TextureRegion(manager.get(PC, Texture.class));
        TextureRegion nextButtonPressed = new TextureRegion(manager.get(BUTTON_PRESSED, Texture.class));
        replayButtonTexture = manager.get(REPLAY, Texture.class);
        mainMenuButtonTexture = manager.get(MAIN_MENU, Texture.class);
        selectTexture = new TextureRegion(manager.get(SELECTOR, Texture.class));
        nextButtonTexture = manager.get(BUTTON, Texture.class);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(nextButtonTexture));
        buttonStyle.over = new TextureRegionDrawable(nextButtonPressed);
        victorySound = manager.get(VICTORY_FILE, Sound.class);
        clickSound = manager.get(CLICK_FILE, Sound.class);
        hoverSound = manager.get(HOVER_FILE, Sound.class);
        basketEmptyTexture = new TextureRegion(manager.get(BASKET_EMPTY, Texture.class));
        basketCity1Texture = new TextureRegion(manager.get(BASKET_CITY1, Texture.class));
        basketCity2Texture = new TextureRegion(manager.get(BASKET_CITY2, Texture.class));
        basketCity3Texture = new TextureRegion(manager.get(BASKET_CITY3, Texture.class));
        basketVillage1Texture = new TextureRegion(manager.get(BASKET_VILLAGE1, Texture.class));
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
    private Sound victorySound;
    private Sound hoverSound;
    private Sound clickSound;

    public LevelTransitionMode() {
        this.stage = new Stage();
        active = true;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void initializeInterface() {
        ImageButton nextButton = new ImageButton(buttonStyle);
        nextButton.setPosition(canvas.getWidth() * 5 / 6 - nextButtonTexture.getWidth() / 2, canvas.getHeight() / 6);
        final LevelTransitionMode transition = this;
        Gdx.input.setInputProcessor(this);
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

    private Level level;

    public void setLastLevel(Level level) {
        this.level = level;
    }

    public void setBaskets() {
        String environment = level.getType();
        int numItems = level.getItems().size();
        if (GDXRoot.currentLevel == 1) {
            basketTexture = basketEmptyTexture;
        } else if (environment.contains("city")) {
            if (numItems == 1)
                basketTexture = basketCity1Texture;
            if (numItems == 2)
                basketTexture = basketCity2Texture;
            if (numItems == 3)
                basketTexture = basketCity3Texture;
        } else if (environment.contains("village")) {
            if (numItems == 1)
                basketTexture = basketVillage1Texture;
            if (numItems == 2)
                basketTexture = basketVillage2Texture;
            if (numItems == 3)
                basketTexture = basketVillage3Texture;
        } else if (environment.contains("forest")) {
            if (numItems == 1)
                basketTexture = basketForest1Texture;
            if (numItems == 2)
                basketTexture = basketForest2Texture;
            if (numItems == 3)
                basketTexture = basketForest3Texture;
        } else {
            if (numItems == 1)
                basketTexture = basketMt1Texture;
            if (numItems == 2)
                basketTexture = basketMt2Texture;
            if (numItems == 3)
                basketTexture = basketMt3Texture;
        }
    }

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private final boolean active;

    private int keyState = 0;

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            final LevelTransitionMode transition = this;
            if (currentSelection == SelectedButton.REPLAY) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, GameMode.EXIT_INTO_GAME);
            }
            else if (currentSelection == SelectedButton.EXIT) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, LevelSelectorMode.INTO_SELECTOR);
            }
            else if (currentSelection == SelectedButton.NEXT) {
                clickSound.play(0.5f * GDXRoot.soundVol);
                listener.exitScreen(transition, GameMode.EXIT_INTO_NEXT);
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (keyState < 3)
                keyState += 1;
            else
                keyState = 0;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (keyState > 0)
                keyState -= 1;
            else
                keyState = 3;
        }
        keyState %= 4;
        if (keyState == 0)
            currentSelection = null;
        if (keyState == 1)
            currentSelection = SelectedButton.REPLAY;
        if (keyState == 2)
            currentSelection = SelectedButton.EXIT;
        if (keyState == 3)
            currentSelection = SelectedButton.NEXT;
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
        setBaskets();
        stage.draw();
        canvas.begin();
        canvas.drawBackground(background, canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.draw(basketTexture, canvas.getWidth() * 2.2f / 5 - basketTexture.getRegionWidth() / 2,
                canvas.getHeight() / 2 - basketTexture.getRegionHeight() / 2 - pc.getRegionHeight() / 5 + 40);
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
        Gdx.input.setInputProcessor(stage);
    }

    public void setLevelComplete(boolean isLevelComplete) {
        this.isLevelComplete = isLevelComplete;
    }

}
