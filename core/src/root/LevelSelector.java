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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import util.ScreenListener;

import java.util.ArrayList;

public class LevelSelector implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_SELECTOR = 4;
    private static final String BK_FILE = "shared/select_bg.png";
    private static final String CITY_FILE = "shared/city.png";
    private static final String SUBURB_FILE = "shared/suburbs.png";
    private static final String FOREST_FILE = "shared/forest.png";
    private static final String MOUNTAIN_FILE = "shared/mountains.png";
    private static final String SELECT_FILE = "shared/selector.png";
    private static final String MUSIC_FILE = "platform/themoreyouknow.mp3";
    private AssetManager manager;
    /**
     * Reference to game.GameCanvas created by the root
     */
    private Music music;
    private GameCanvas canvas;
    private Texture background;
    private Texture city;
    private Texture suburb;
    private Texture forest;
    private Texture mountain;
    private Texture selector;
    private themes theme = themes.none;
    private int city_level = 4;
    private int suburb_level = 9;
    private boolean ready = false;
    int city_l = 200;
    int city_r = 600;
    int city_d = 563;
    int city_u = 700;
    int sub_l = 660;
    int sub_r = 950;
    int sub_d = 550;
    int sub_u = 750;
    int for_l = 655;
    int for_r = 1200;
    int for_d = 350;
    int for_u = 550;
    int mon_l = 56;
    int mon_r = 590;
    int mon_d = 150;
    int mon_u = 296;
    private ArrayList<Vector2> buttonPos = new ArrayList<>();

    private enum themes {
        city, none, suburb, forest, mountain
    }

    public LevelSelector(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        this.levels = new ArrayList<>();
        resize(canvas.getWidth(), canvas.getHeight());
        background = new Texture(BK_FILE);
        city = new Texture(CITY_FILE);
        suburb = new Texture(SUBURB_FILE);
        forest = new Texture(FOREST_FILE);
        mountain = new Texture(MOUNTAIN_FILE);
        selector = new Texture(SELECT_FILE);
        this.music = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_FILE));
        music.play();
        music.setVolume(0.5f);
        music.setLooping(true);
        buttonPos.add(new Vector2(280, 610));
        buttonPos.add(new Vector2(350, 650));
        buttonPos.add(new Vector2(440, 630));
        buttonPos.add(new Vector2(520, 650));
        levels.add(new LevelMetaData(false, "levels/test_level.json", ""));
        levels.add(new LevelMetaData(false, "levels/level2.json", ""));
        levels.add(new LevelMetaData(false, "levels/level3.json", ""));
//=======
//        levels.add(new LevelMetaData(false, "levels/level_editor_level.json", ""));
//>>>>>>> 5564ad51ce26a414a0a57ebb553485afc0b2e419

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
//        for (int i = 0; i < buttonPos.size(); i++){
//            Vector2 screenP = new Vector2(screenX, canvas.getHeight() - screenY);
//            System.out.println(screenP.dst(buttonPos.get(0)));
//            if (screenP.dst(buttonPos.get(i)) < 50) {
//                level = i+1;
//            }
//        }
        if (level != -1 && level < levels.size() + 1) {
            ready = true;
            music.stop();
        }
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
        int screen = canvas.getHeight() - screenY;
        int start = 0;
        int end = 0;
        if (screenX > city_l && screenX < city_r && screen > city_d && screen < city_u) {
            theme = themes.city;
            start = 0;
            end = city_level;
        } else if (screenX > sub_l && screenX < sub_r && screen > sub_d && screen < sub_u) {
            theme = themes.suburb;
            start = city_level;
            end = suburb_level;
        } else if (screenX > for_l && screenX < for_r && screen > for_d && screen < for_u) {
            theme = themes.forest;
            start = suburb_level;
            end = 14;
        } else if (screenX > mon_l && screenX < mon_r && screen > mon_d && screen < mon_u) {
            start = 15;
            end = 21;
            theme = themes.mountain;
        } else {
            theme = themes.none;
        }

        boolean select = false;
        for (int i = 0; i < levels.size(); i++) {
            Vector2 screenP = new Vector2(screenX, canvas.getHeight() - screenY);
            if (screenP.dst(buttonPos.get(i)) < 50) {
                select = true;
                level = i + 1;
            }
        }

        if (!select) {
            level = -1;
        }
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
            if (ready && listener != null) {
                listener.exitScreen(this, GameMode.EXIT_INTO_GAME);
            }
        }
    }

    private void update(float dt) {

    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(background);
        switch (theme) {
            case city:
                canvas.drawBackground(mountain);
                canvas.drawBackground(suburb);
                canvas.drawBackground(forest);
                canvas.drawBackground(city, 426, 646, Color.WHITE, 1.3f);
                break;
            case suburb:
                canvas.drawBackground(city);
                canvas.drawBackground(mountain);
                canvas.drawBackground(suburb, 868, 669, Color.WHITE, 1.2f);
                canvas.drawBackground(forest);
                break;
            case forest:
                canvas.drawBackground(mountain);
                canvas.drawBackground(city);
                canvas.drawBackground(suburb);
                canvas.drawBackground(forest, 960, 450, Color.WHITE, 1.2f);
                break;
            case mountain:
                canvas.drawBackground(mountain, 330, 230, Color.WHITE, 1.2f);
                canvas.drawBackground(city);
                canvas.drawBackground(suburb);
                canvas.drawBackground(forest);
                break;
            case none:
                canvas.drawBackground(mountain);
                canvas.drawBackground(city);
                canvas.drawBackground(suburb);
                canvas.drawBackground(forest);
                break;
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("shared/blackjack.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        BitmapFont font = generator.generateFont(parameter);
        for (int i = 0; i < buttonPos.size(); i++) {
            Vector2 button = buttonPos.get(i);
            canvas.drawText(i + 1 + "", font, button.x, button.y);
        }

        if (level > 0 && level < levels.size() + 1) {
            canvas.draw(selector, buttonPos.get(level - 1).x - selector.getWidth() / 2 + 5,
                    buttonPos.get(level - 1).y - selector.getHeight() / 2 - 15);
        }

        canvas.end();
    }


    public LevelMetaData getMetaData() {
        if (level > levels.size() || level == -1) return null;
        return levels.get(level - 1);
    }


    public void reset(GameCanvas canvas) {
        this.canvas = canvas;
        level = -1;
        ready = false;
        this.music = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_FILE));
        music.play();
        music.setVolume(0.5f);
        music.setLooping(true);
    }

}
