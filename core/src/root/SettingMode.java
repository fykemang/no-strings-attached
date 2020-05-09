package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;


public class SettingMode extends Mode implements Screen {


    public static final int INTO_SETTING = 15;
    private static final String[] opening = {"cutscenes/opening-1.png", "cutscenes/opening-2.png",
            "cutscenes/opening-3.png", "cutscenes/opening-4.png", "cutscenes/opening-5.png"};
    private static final String RIGHT = "cutscenes/skipButton.png";
    private static final String BKG = "ui/grey.png";
    private static final String LOGO = "ui/game-logo.png";
    private static final String TITLE = "ui/setting.png";
    private static final String ON = "ui/on.png";
    private static final String OFF = "ui/off.png";
    private static final String SOUND = "ui/sound.png";
    private static final String CONTROLS = "ui/controls.png";
    private static final String SETTING_SELECT = "ui/setting-select.png";
    private static final String KEYBOARD_ARROW = "ui/keyboard-arrows.png";
    private static final String KEYBOARD_WASD = "ui/keyboardWasd.png";
    private static final String MUSIC = "ui/music.png";
    private static final String LEFT = "ui/setting-next.png";
    private static final String BACK = "ui/back.png";


    private TextureRegion logoTexture;
    private TextureRegion background;
    private TextureRegion titleTexture;
    private TextureRegion onTexture;
    private TextureRegion offTexture;
    private TextureRegion soundTexture;
    private TextureRegion selectTexture;
    private TextureRegion keyboardArrowTexture;
    private TextureRegion keyboardWasdtexture;
    private TextureRegion controlsTexture;
    private TextureRegion musicTexture;
    private TextureRegion leftTexture;
    private TextureRegion backTexture;

    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    private ScreenListener listener;
    private final boolean active;
    private ImageButton backButtom;
    private ImageButton nextButtom;
    private SettingMode setting;

    private boolean musicOn = true;
    private boolean soundOn = true;
    private boolean arrow = true;


    private AssetState selectorAssetState = AssetState.EMPTY;

    public SettingMode(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        this.stage = new Stage();
        setting = this;

        active = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void exit() {

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
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (active) {
            update(delta);
            draw();
        }
    }

    private void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            listener.exitScreen(setting, LoadingMode.INTO_STARTSCREEN);
        }

    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(background.getTexture());
        canvas.drawUI(logoTexture, canvas.getWidth() * 0.1f, canvas.getHeight() * 0.9f, 0.3f);
        canvas.drawUI(titleTexture, canvas.getWidth() / 2, canvas.getHeight() * 0.85f, 1.0f);
        canvas.drawUI(soundTexture, canvas.getWidth() * 0.25f, canvas.getHeight() * 0.65f, 1.0f);
        //  canvas.drawUI(onTexture, canvas.getWidth()*0.55f, canvas.getHeight()*0.65f, 1.0f);
        // canvas.drawUI(offTexture, canvas.getWidth()*0.65f, canvas.getHeight()*0.65f, 1.0f);

        canvas.drawUI(musicTexture, canvas.getWidth() * 0.25f, canvas.getHeight() * 0.5f, 1.0f);
        //  canvas.drawUI(onTexture, canvas.getWidth()*0.55f, canvas.getHeight()*0.45f, 1.0f);
        //   canvas.drawUI(offTexture, canvas.getWidth()*0.65f, canvas.getHeight()*0.45f, 1.0f);


        canvas.drawUI(controlsTexture, canvas.getWidth() * 0.25f, canvas.getHeight() * 0.35f, 1.0f);
        if (arrow) {
            canvas.drawUI(keyboardArrowTexture, canvas.getWidth() * 0.6f, canvas.getHeight() * 0.25f, 0.5f);
        }
        canvas.actStage(stage);
        canvas.end();
    }

    @Override
    public void preloadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.EMPTY) {
            return;
        }
        loadAsset(BKG, Texture.class, manager);
        loadAsset(LOGO, Texture.class, manager);
        loadAsset(TITLE, Texture.class, manager);
        loadAsset(ON, Texture.class, manager);
        loadAsset(OFF, Texture.class, manager);
        loadAsset(CONTROLS, Texture.class, manager);
        loadAsset(KEYBOARD_ARROW, Texture.class, manager);
        loadAsset(SOUND, Texture.class, manager);
        loadAsset(SETTING_SELECT, Texture.class, manager);
        loadAsset(BACK, Texture.class, manager);
        loadAsset(KEYBOARD_WASD, Texture.class, manager);
        loadAsset(LEFT, Texture.class, manager);
        loadAsset(MUSIC, Texture.class, manager);
        selectorAssetState = AssetState.LOADING;
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.LOADING) {
            return;
        }
        background = createTexture(manager, BKG, false);
        logoTexture = createTexture(manager, LOGO, false);
        titleTexture = createTexture(manager, TITLE, false);
        onTexture = createTexture(manager, ON, false);
        offTexture = createTexture(manager, OFF, false);
        selectTexture = createTexture(manager, SETTING_SELECT, false);
        controlsTexture = createTexture(manager, CONTROLS, false);
        soundTexture = createTexture(manager, SOUND, false);
        keyboardArrowTexture = createTexture(manager, KEYBOARD_ARROW, false);
        keyboardWasdtexture = createTexture(manager, KEYBOARD_WASD, false);
        musicTexture = createTexture(manager, MUSIC, false);
        backTexture = createTexture(manager, BACK, false);
        selectorAssetState = AssetState.COMPLETE;
    }


    private ImageButton createButton(TextureRegion texture) {
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(myTexRegionDrawable);
        return button;
    }


    private TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return region;
        }
        return null;
    }


    public void initUI() {
        // final ImageButton.ImageButtonStyle backbuttonStyle = new ImageButton.ImageButtonStyle();
        backButtom = createButton(backTexture);
        backButtom.setPosition(canvas.getWidth() * 0.05f, canvas.getHeight() * 0.05f);
        backButtom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(setting, LoadingMode.INTO_STARTSCREEN);
            }
        });
        stage.addActor(backButtom);

        Gdx.input.setInputProcessor(stage);


    }

    public void reset(GameCanvas canvas) {
        this.canvas = canvas;
    }

}
