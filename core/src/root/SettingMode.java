package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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


public class SettingMode  extends Mode implements Screen{


    public static final int INTO_CUTSCENE= 8;
    private static final String[] opening = {"cutscenes/opening-1.png", "cutscenes/opening-2.png",
            "cutscenes/opening-3.png", "cutscenes/opening-4.png", "cutscenes/opening-5.png"};
    private static final String SKIP = "cutscenes/PressEnterSkip.png";
    private static final String RIGHT = "cutscenes/skipButton.png";
    private static final String START = "ui/start.png";
    private final ImageButton.ImageButtonStyle skipbuttonStyle = new ImageButton.ImageButtonStyle();
    private ArrayList<TextureRegion> textures = new ArrayList<>();
    private TextureRegion skiptexture;
    private TextureRegion nextTexture;
    private static final String[] city = {};

    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    private ScreenListener listener;
    private final boolean active;
    private ImageButton skipButtom;
    private ImageButton nextButtom;
    private SettingMode setting;

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

    }

    private void draw() {
        canvas.begin();

        canvas.actStage(stage);
        canvas.end();
    }

    @Override
    public void preloadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.EMPTY) {
            return;
        }
        for (String s : opening) loadAsset(s, Texture.class, manager);
        for (String s : city) loadAsset(s, Texture.class, manager);

        selectorAssetState = AssetState.LOADING;
    }

    @Override
    public void loadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.LOADING) {
            return;
        }
        skiptexture =  createTexture(manager,SKIP, false);
        nextTexture = createTexture(manager, RIGHT, false);

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

    public void reset(GameCanvas canvas) {
        this.canvas = canvas;
    }

}
