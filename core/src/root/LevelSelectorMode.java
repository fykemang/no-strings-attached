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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import entities.Level;
import entities.LevelMetadata;
import util.ScreenListener;

import java.util.ArrayList;

public class LevelSelectorMode extends Mode implements Screen, InputProcessor, ControllerListener {
    public static final int INTO_SELECTOR = 4;
    private static final String BACKGROUND_FILE = "ui/select_bg.png";
    private static final String CITY_FILE = "ui/city.png";
    private static final String SUBURB_FILE = "ui/suburbs.png";
    private static final String FOREST_FILE = "ui/forest.png";
    private static final String MOUNTAIN_FILE = "ui/mountains.png";
    private static final String SELECT_FILE = "ui/selector.png";
    private static final String MUSIC_FILE = "music/themoreyouknow.mp3";
    private static final String LEVEL_METADATA = "levels/levels.json";
    private static final String SELECTOR_FONT = "ui/blackjack.otf";
    private static final String MENU_CLICK_FILE = "sounds/click.mp3";
    /**
     * Reference to game.GameCanvas created by the root
     */
    private Music levelSelectorMusic;
    private Sound clickSound;
    private GameCanvas canvas;
    private Texture background;
    private Texture city;
    private Texture suburb;
    private Texture forest;
    private Texture mountain;
    private Texture selector;
    private BitmapFont selectorFont;
    private themes theme = themes.none;
    private final int city_level = 4;
    private final int suburb_level = 9;
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
    private final ArrayList<Vector2> buttonPos = new ArrayList<>();
    private AssetState selectorAssetState = AssetState.EMPTY;
    private LevelMetadata levelMetadata;

    private enum themes {
        city, none, suburb, forest, mountain
    }


    @Override
    public void preloadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.EMPTY) {
            return;
        }
        selectorAssetState = AssetState.LOADING;
        loadAsset(LEVEL_METADATA, LevelMetadata.class, manager);
        loadAsset(BACKGROUND_FILE, Texture.class, manager);
        loadAsset(SUBURB_FILE, Texture.class, manager);
        loadAsset(FOREST_FILE, Texture.class, manager);
        loadAsset(MOUNTAIN_FILE, Texture.class, manager);
        loadAsset(SELECT_FILE, Texture.class, manager);
        loadAsset(CITY_FILE, Texture.class, manager);
        loadAsset(MUSIC_FILE, Music.class, manager);
        loadAsset(MENU_CLICK_FILE, Sound.class, manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.LOADING) {
            return;
        }
        background = manager.get(BACKGROUND_FILE, Texture.class);
        city = manager.get(CITY_FILE, Texture.class);
        suburb = manager.get(SUBURB_FILE, Texture.class);
        forest = manager.get(FOREST_FILE, Texture.class);
        mountain = manager.get(MOUNTAIN_FILE, Texture.class);
        selector = manager.get(SELECT_FILE, Texture.class);
        levelSelectorMusic = manager.get(MUSIC_FILE, Music.class);
        levelMetadata = manager.get(LEVEL_METADATA, LevelMetadata.class);
        clickSound = manager.get(MENU_CLICK_FILE, Sound.class);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/blackjack.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        selectorFont = generator.generateFont(parameter);
        selectorAssetState = AssetState.COMPLETE;
    }


    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
    }


    public LevelSelectorMode() {
        this.assets = new Array<>();
        buttonPos.add(new Vector2(280, 610));
        buttonPos.add(new Vector2(350, 650));
        buttonPos.add(new Vector2(440, 630));
        buttonPos.add(new Vector2(520, 650));
        buttonPos.add(new Vector2(600, 680));
        buttonPos.add(new Vector2(680, 680));
        buttonPos.add(new Vector2(760, 670));
//        buttonPos.add(new Vector2(780, 650));

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
//        for (int i = 0; i < buttonPos.size(); i++){
//            Vector2 screenP = new Vector2(screenX, canvas.getHeight() - screenY);
//            System.out.println(screenP.dst(buttonPos.get(0)));
//            if (screenP.dst(buttonPos.get(i)) < 50) {
//                level = i+1;
//            }
//        }
        if (level != -1 && level < levelMetadata.getLevelCount() + 1) {
            clickSound.play(0.5f);
            ready = true;
            levelSelectorMusic.dispose();
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

    int screen;
    int start;
    int end;
    boolean select;

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

        select = false;
        for (int i = 0; i < levelMetadata.getLevelCount(); i++) {
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
        if (levelSelectorMusic != null)
            levelSelectorMusic.dispose();
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
                canvas.drawBackground(city);
                canvas.drawBackground(mountain);
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


        for (int i = 0; i < buttonPos.size(); i++) {
            Vector2 button = buttonPos.get(i);
            canvas.drawText(i + 1 + "", selectorFont, button.x, button.y);
        }

        if (level > 0 && level < levelMetadata.getLevelCount() + 1) {
            canvas.draw(selector, buttonPos.get(level - 1).x - selector.getWidth() / 2 + 5,
                    buttonPos.get(level - 1).y - selector.getHeight() / 2 - 15);
        }

        canvas.end();
    }

    public int getLevelIndex() {
        return level;
    }

    public Level getLevel(int level) {
        if (level > levelMetadata.getLevelCount() + 1 || level == -1) return null;
        return levelMetadata.getLevel(level);
    }

    public Level getCurrentLevel() {
        if (level > levelMetadata.getLevelCount() + 1 || level == -1) return null;
        return levelMetadata.getLevel(level);
    }

    public Level getNextLevel() {
        if (level + 1 > levelMetadata.getLevelCount() + 1 || level == -1) return null;
        return levelMetadata.getLevel(level + 1);
    }

    public void reset() {
        level = -1;
        ready = false;
        levelSelectorMusic.play();
        levelSelectorMusic.setVolume(0.5f);
        levelSelectorMusic.setLooping(true);
    }
}
