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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;

import java.util.ArrayList;


public class LevelTransition implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    private static final String BK_FILE = "platform/background.png";
    private static final String BUTTON = "shared/earthtile.png";
//    private static final String CITY_FILE = "shared/city.png";
//    private static final String SUBURB_FILE = "shared/suburbs.png";
//    private static final String FOREST_FILE = "shared/forest.png";
//    private static final String MOUNTAIN_FILE = "shared/mountains.png";
//    private static final String SELECT_FILE = "shared/selector.png";
//    private static final String MUSIC_FILE = "platform/themoreyouknow.mp3";

    Texture background;
    TextureRegion buttonTex;
    private AssetManager manager;
    private GameCanvas canvas;
    private Stage stage;

    public LevelTransition(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        this.levels = new ArrayList<>();
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        background = new Texture(BK_FILE);
        buttonTex = new TextureRegion(manager.get(BUTTON, Texture.class));
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(buttonTex);
        resize(canvas.getWidth(), canvas.getHeight());
        ImageButton button = new ImageButton(myTexRegionDrawable);
        //** Button text and style **//
        button.setHeight(Gdx.graphics.getHeight()/3); //** Button Height **//
        button.setWidth(Gdx.graphics.getWidth()/4); //** Button Width **//
        button.setPosition(Gdx.graphics.getWidth()/2-button.getWidth()/2, Gdx.graphics.getHeight());
        button.addListener(new InputListener() {
                               public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                   Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//
                                   return true;

                               }
                           });

        Gdx.input.setInputProcessor(this);
        stage.addActor(button);
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
            canvas.drawBackground(background);
            canvas.actStage(stage);
            canvas.end();
    }





    public void reset(GameCanvas canvas) {
        this.canvas = canvas;
    }

}
