package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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

import java.util.ArrayList;


public class LevelTransition implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    public static float MUSIC_VOLUME = 0.5f;
    public static float SFX_VOLUME = 0.5f;
    private static final String BK_FILE = "ui/sky.png";
    private static final String BUTTON = "ui/next.png";
    private static final String PC = "player/player_idle.png";
    private static final String WIN = "ui/basket_3.png";
    private static final String REPLAY = "ui/replay.png";
    private static final String MEAN_MENU = "ui/main-menu.png";
    private static final String WIN_TEXT = "ui/excellent.png";
    private final String TRANSITION_MUSIC_FILE = "music/goodnight.mp3";
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String CLICK_FILE = "sounds/click.mp3";
    private final String SELECTOR = "ui/next-select.png";

    enum SELECTEDBUTTON {
        Replay,
        Exit,
        Next
    }

    private SELECTEDBUTTON currentSelection;

    Texture background;
    Texture yarnie;
    TextureRegion pc;
    Texture winMessage;
    TextureRegion buttonNextDown;
    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    TextureRegion selected;
    private final ImageButton nextButton;
    private final ImageButton replaybutton;
    private final ImageButton mainMenu;
    private final boolean levelComplete;
    private final Music music;
    private final Sound hoverSound;
    private final Sound clickSound;

    public LevelTransition(AssetManager manager, GameCanvas canvas, boolean win) {
        this.levelComplete = win;
        this.manager = manager;
        this.canvas = canvas;
        this.levels = new ArrayList<>();
        this.stage = new Stage();
        this.music = manager.get(TRANSITION_MUSIC_FILE);
        this.hoverSound = manager.get(HOVER_FILE);
        this.clickSound = manager.get(CLICK_FILE);

        Gdx.input.setInputProcessor(stage);

        Texture select = new Texture(SELECTOR);
        selected = new TextureRegion(select);
        buttonNextDown = new TextureRegion(new Texture("ui/next-down.png"));
        pc = new TextureRegion(new Texture(PC));
        background = new Texture(BK_FILE);
        yarnie = new Texture(WIN);
        winMessage = new Texture(WIN_TEXT);
        Texture buttonNext = new Texture(BUTTON);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(buttonNext));
        style.over = new TextureRegionDrawable(buttonNextDown);

        nextButton = new ImageButton(style);
        nextButton.setPosition(canvas.getWidth() * 5 / 6 - nextButton.getWidth() / 2, canvas.getHeight() / 6);
        final LevelTransition transition = this;
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(SFX_VOLUME);
                listener.exitScreen(transition, GameMode.EXIT_INTO_NEXT);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SELECTEDBUTTON.Next;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(nextButton);


        Texture buttonReplay = new Texture(REPLAY);
        replaybutton = createButton(buttonReplay);
        replaybutton.setPosition(canvas.getWidth() / 6 - replaybutton.getWidth() / 2, canvas.getHeight() / 6);
        replaybutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(SFX_VOLUME);
                listener.exitScreen(transition, GameMode.EXIT_INTO_GAME);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SELECTEDBUTTON.Replay;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(replaybutton);

        Texture buttonMain = new Texture(MEAN_MENU);
        mainMenu = createButton(buttonMain);
        mainMenu.setPosition(canvas.getWidth() / 2 - mainMenu.getWidth() / 2, canvas.getHeight() / 6);
        mainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(SFX_VOLUME);
                listener.exitScreen(transition, LevelSelectorMode.INTO_SELECTOR);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = SELECTEDBUTTON.Exit;

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                currentSelection = null;
            }

        });
        stage.addActor(mainMenu);

        Gdx.input.setInputProcessor(stage);

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
        active = true;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void exit() {

    }

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private final int level = -1;


    private final boolean active;

    private final ArrayList<LevelMetaData> levels;


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
        music.dispose();
        nextButton.setDisabled(true);
        replaybutton.setDisabled(true);
        nextButton.setDisabled(true);
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
        canvas.draw(yarnie, canvas.getWidth() * 2.2f / 5 - yarnie.getWidth() / 2,
                canvas.getHeight() / 2 - yarnie.getHeight() / 2 - pc.getRegionHeight() / 5);
        canvas.drawUI(pc, canvas.getWidth() * 3 / 5,
                canvas.getHeight() / 2, 0.5f);
        if (levelComplete) {
            canvas.draw(winMessage, canvas.getWidth() / 2 - winMessage.getWidth() / 2,
                    canvas.getHeight() * 4 / 5 - winMessage.getHeight() / 2);
        }

        if (currentSelection != null)
            switch (currentSelection) {
                case Replay:
                    if (lastState != 1) {
                        hoverSound.play(6 * SFX_VOLUME);
                        lastState = 1;
                    }
                    canvas.drawUI(selected, canvas.getWidth() / 6 - replaybutton.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case Exit:
                    if (lastState != 2) {
                        hoverSound.play(6 * SFX_VOLUME);
                        lastState = 2;
                    }
                    canvas.drawUI(selected, canvas.getWidth() / 2 - mainMenu.getWidth() / 4,
                            canvas.getHeight() / 6, 1.1f);
                    break;
                case Next:
                    if (lastState != 3) {
                        hoverSound.play(6 * SFX_VOLUME);
                        lastState = 3;
                    }
                    canvas.drawUI(selected, canvas.getWidth() * 5 / 6 - nextButton.getWidth() / 4,
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
        ImageButton button = new ImageButton(myTexRegionDrawable);

        return button;
    }


    public void reset(GameCanvas canvas) {
        this.canvas = canvas;
    }

}
