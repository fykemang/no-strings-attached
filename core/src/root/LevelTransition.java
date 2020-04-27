package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;

import java.util.ArrayList;


public class LevelTransition implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    private static final String BK_FILE = "ui/sky.png";
    private static final String BUTTON = "ui/next.png";
    private static final String FAIL = "ui/yarnie-fail.png";
    private static final String WIN = "ui/soul-mate.png";
    private static final String REPLAY = "ui/replay.png";
    private static final String MEAN_MENU = "ui/main-menu.png";
    private static final String WIN_TEXT = "ui/excellent.png";


    Texture background;
    Texture yarnie;
    Texture winMessage;
    TextureRegion buttonNextTex;
    private AssetManager manager;
    private GameCanvas canvas;
    private Stage stage;
    private ImageButton nextButton;
    private ImageButton replaybutton;
    private ImageButton mainMenu;
    private boolean levelComplete;

    public LevelTransition(AssetManager manager, GameCanvas canvas, boolean win) {
        this.levelComplete = win;
        this.manager = manager;
        this.canvas = canvas;
        this.levels = new ArrayList<>();
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        background = new Texture(BK_FILE);
        yarnie = win ? new Texture(WIN) : new Texture(FAIL);
        winMessage = new Texture(WIN_TEXT);
        Texture buttonNext = new Texture(BUTTON);
        nextButton = createButton(buttonNext);
        nextButton.setPosition(canvas.getWidth() * 5 / 6 - nextButton.getWidth() / 2, canvas.getHeight() / 6);
        final LevelTransition transition = this;
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(transition, GameMode.EXIT_INTO_NEXT);
            }

            ;
        });
        stage.addActor(nextButton);

        Texture buttonReplay = new Texture(REPLAY);
        replaybutton = createButton(buttonReplay);
        replaybutton.setPosition(canvas.getWidth() / 6 - replaybutton.getWidth() / 2, canvas.getHeight() / 6);
        replaybutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(transition, GameMode.EXIT_INTO_GAME);
            }

            ;
        });
        stage.addActor(replaybutton);

        Texture buttonMain = new Texture(MEAN_MENU);
        mainMenu = createButton(buttonMain);
        mainMenu.setPosition(canvas.getWidth() / 2 - mainMenu.getWidth() / 2, canvas.getHeight() / 6);
        mainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(transition, LevelSelectorMode.INTO_SELECTOR);
            }

            ;
        });
        stage.addActor(mainMenu);

        Gdx.input.setInputProcessor(stage);

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

    private int level = -1;


    private boolean active;

    private ArrayList<LevelMetaData> levels;


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

    private void draw() {
        stage.draw();
        canvas.begin();
//           System.out.println(background.getWidth());

        canvas.drawBackground(background, canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);
        canvas.draw(yarnie, canvas.getWidth() / 2 - yarnie.getWidth() / 2,
                canvas.getHeight() / 2 - yarnie.getHeight() / 2);
        if (levelComplete) {
            canvas.draw(winMessage, canvas.getWidth() / 2 - winMessage.getWidth() / 2,
                    canvas.getHeight() * 4 / 5 - winMessage.getHeight() / 2);
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
