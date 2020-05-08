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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import entities.LevelMetadata;
import util.ScreenListener;

import java.util.ArrayList;


public class CutScene  extends Mode implements Screen{
    public static enum THEME {
        OPENING,
        CITY,
        FOREST,
        MOUNTAIN,
        END
    };
    private THEME theme;
    public static final int INTO_CUTSCENE= 8;
    private static final String[] opening = {"cutscenes/opening-1.png", "cutscenes/opening-2.png"};
    private ArrayList<TextureRegion> textures = new ArrayList<>();
    private static final String[] city = {};
    private boolean slideMode;
    private int currentSlide =  0;

    TextureRegion pc;
    TextureRegion buttonNextDown;
    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    private ScreenListener listener;
    private final boolean active;

    private AssetState selectorAssetState = AssetState.EMPTY;

    public CutScene(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        active = true;
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

        canvas.drawBackground(textures.get(0).getTexture(), canvas.getWidth() / 2, canvas.getHeight() / 2,
                canvas.getWidth() / 2, canvas.getHeight() / 2, Color.GRAY);

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
        switch(theme){
            case OPENING:
                for (String file: opening) {
                    System.out.println("here");
                    textures.add(createTexture(manager, file, false));
                    slideMode = true;
                }
            default:
        }
        if (slideMode){
            //set up the buttoms


        }else {
            //set up the buttoms
            // deal with animations


        }
        selectorAssetState = AssetState.COMPLETE;
    }


    private ImageButton createButton(Texture texture) {
        TextureRegion buttonRegion = new TextureRegion(texture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(buttonRegion);
        ImageButton button = new ImageButton(myTexRegionDrawable);

        return button;
    }


    public void setTheme(THEME t){
           theme = t;
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
