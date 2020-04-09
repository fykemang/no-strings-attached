package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import util.FilmStrip;
import util.ScreenListener;

import java.util.ArrayList;

public class LevelSelector  implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_SELECTOR = 0;
    private static final String BK_FILE = "shared/select_bg.png";
    private static final String CITY_FILE = "shared/city.png";
    private AssetManager manager;
    /**
     * Reference to game.GameCanvas created by the root
     */
    private GameCanvas canvas;
    private Texture background;
    private Texture city;
    private themes theme;
    int city_l = 200;
    int city_r = 600;
    int city_d = 563;
    int city_u = 700;

    private enum themes {
        city, sky, none
    }

    public LevelSelector(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        resize(canvas.getWidth(), canvas.getHeight());
        background =  new Texture(BK_FILE);
        city =  new Texture(CITY_FILE);
        Gdx.input.setInputProcessor(this);
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

    private ArrayList<GameMetaData> levels;


    public boolean isReady() {
        return level != -1;
    }

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
        if (theme == themes.city){
            level=1;
        }

//        System.out.println("canvasx " + canvas.getWidth() + "canvasy " + (canvas.getHeight()));
//        System.out.println("x " +screenX + "y" + (canvas.getHeight() - screenY));


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
        int screeny = canvas.getHeight() - screenY;
        if (screenX > city_l && screenX < city_r && screeny>city_d && screeny < city_u) {
            theme = themes.city;
            return true;
        }
        else{ theme = themes.none; }
        return false;
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
            if (isReady() && listener != null) {
                System.out.println("here");
                listener.exitScreen(this, 3);
            }
        }
    }

    private void update(float delta) {

    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(background);
       if (theme == themes.city) {
           canvas.drawBackground(city, 426, 646,Color.WHITE,1.5f);
       }else {
            canvas.drawBackground(city);
           //canvas.drawBackground(city ,426, 646, Color.WHITE,1.5f);
       }
       canvas.end();
    }


    public GameMetaData getMetaData(){
            if (level > levels.size() || level == -1 ) return null;
            return levels.get(level);
    }
}
