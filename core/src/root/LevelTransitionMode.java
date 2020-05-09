package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
    private static final String WIN = "ui/basket_3.png";
    private static final String REPLAY = "ui/replay.png";
    private static final String MAIN_MENU = "ui/main-menu.png";
    private static final String WIN_TEXT = "ui/excellent.png";
    private static final String BUTTON_PRESSED = "ui/next-down.png";
    private final String TRANSITION_MUSIC_FILE = "music/goodnight.mp3";
    private final String SELECTOR = "ui/next-select.png";

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
        loadAsset(WIN, Texture.class, manager);
        loadAsset(REPLAY, Texture.class, manager);
        loadAsset(MAIN_MENU, Texture.class, manager);
        loadAsset(WIN_TEXT, Texture.class, manager);
        loadAsset(TRANSITION_MUSIC_FILE, Music.class, manager);
        loadAsset(SELECTOR, Texture.class, manager);
        loadAsset(BUTTON_PRESSED, Texture.class, manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }
        background = manager.get(BK_FILE, Texture.class);
        yarnie = manager.get(WIN, Texture.class);
        winMessage = manager.get(WIN_TEXT, Texture.class);
        pc = new TextureRegion(manager.get(PC, Texture.class));
        TextureRegion nextButtonPressed = new TextureRegion(manager.get(BUTTON_PRESSED, Texture.class));
        replayButtonTexture = manager.get(REPLAY, Texture.class);
        mainMenuButtonTexture = manager.get(MAIN_MENU, Texture.class);
        selectTexture = new TextureRegion(manager.get(SELECTOR, Texture.class));
        nextButtonTexture = manager.get(BUTTON, Texture.class);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(nextButtonTexture));
        buttonStyle.over = new TextureRegionDrawable(nextButtonPressed);
        music = manager.get(TRANSITION_MUSIC_FILE, Music.class);
        assetState = AssetState.COMPLETE;
    }

    enum SelectedButton {
        REPLAY,
        EXIT,
        NEXT
    }

    private SelectedButton currentSelection;


    private final Stage stage;
    private ImageButton replayButton;
    private ImageButton mainMenuButton;
    private boolean isLevelComplete;
    private Music music;

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
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        music.play();
        music.setLooping(true);

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
        music.play();
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
        music.pause();
    }

    @Override
    public void dispose() {

//        music.dispose();
//        for (Actor a : stage.getActors()) {
//            a.remove();
//        }
        // stage.dispose();


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

    private void draw() {
        stage.draw();
        canvas.begin();
        canvas.drawBackground(background, canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.draw(yarnie, canvas.getWidth() * 2.2f / 5 - yarnie.getWidth() / 2,
                canvas.getHeight() / 2 - yarnie.getHeight() / 2 - pc.getRegionHeight() / 5);
        canvas.drawUI(pc, canvas.getWidth() * 3 / 5,
                canvas.getHeight() / 2, 0.5f);
        if (isLevelComplete) {
            canvas.draw(winMessage, canvas.getWidth() / 2 - winMessage.getWidth() / 2,
                    canvas.getHeight() * 4 / 5 - winMessage.getHeight() / 2);
        }

        if (currentSelection != null)
            switch (currentSelection) {
                case REPLAY:
                    canvas.drawUI(selectTexture, canvas.getWidth() / 6 - replayButton.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case EXIT:
                    canvas.drawUI(selectTexture, canvas.getWidth() / 2 - mainMenuButton.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case NEXT:
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
        music.play();
        Gdx.input.setInputProcessor(stage);
    }

    public void setLevelComplete(boolean isLevelComplete) {
        this.isLevelComplete = isLevelComplete;
    }

}
