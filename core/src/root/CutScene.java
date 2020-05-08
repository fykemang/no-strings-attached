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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import entities.LevelMetadata;
import util.ScreenListener;

import java.util.ArrayList;


public class CutScene  extends Mode implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_TRANSITION = 5;
    private static final String[] OPENING = {"cutscenes/opening-1.png", "cutscenes/opening-2.png"};
    private static final String[] CITY = {};



    Texture background;
    Texture yarnie;
    TextureRegion pc;
    Texture winMessage;
    TextureRegion buttonNextDown;
    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    //private final ImageButton nextButton;;

    private AssetState selectorAssetState = AssetState.EMPTY;

    public CutScene(AssetManager manager, GameCanvas canvas, boolean win) {
        this.manager = manager;
        this.canvas = canvas;
        this.stage = new Stage();
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

    private final int level = -1;
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
        canvas.end();
    }

    @Override
    public void preloadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.EMPTY) {
            return;
        }
        selectorAssetState = AssetState.LOADING;
//        loadAsset(LEVEL_METADATA, LevelMetadata.class, manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.LOADING) {
            return;
        }
     //   background = manager.get(BACKGROUND_FILE, Texture.class);
//        city = manager.get(CITY_FILE, Texture.class);
//        suburb = manager.get(SUBURB_FILE, Texture.class);
//        forest = manager.get(FOREST_FILE, Texture.class);
//        mountain = manager.get(MOUNTAIN_FILE, Texture.class);
//        selector = manager.get(SELECT_FILE, Texture.class);
//        levelSelectorMusic = manager.get(MUSIC_FILE, Music.class);
//        levelMetadata = manager.get(LEVEL_METADATA, LevelMetadata.class);
//        clickSound = manager.get(MENU_CLICK_FILE, Sound.class);
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/blackjack.otf"));
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.size = 50;
//        selectorFont = generator.generateFont(parameter);
        selectorAssetState = AssetState.COMPLETE;
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
