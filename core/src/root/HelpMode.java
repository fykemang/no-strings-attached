package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;


public class HelpMode extends Mode implements Screen {
    public static final int INTO_PAUSE = 15;


    private static final String BACK_FILE = "ui/back.png";
    private static final String BKG = "ui/sky.png";


    private TextureRegion backTexture;
    private TextureRegion bkgTexture;


    ImageButton continueButton;
    ImageButton mainButton;

    @Override
    public void preloadContent(AssetManager manager) {
        if (assetState != AssetState.EMPTY) {
            return;
        }
        assetState = AssetState.LOADING;

        loadAsset(BKG, Texture.class, manager);

        loadAsset(BACK_FILE, Texture.class, manager);

    }

    @Override
    public void loadContent(AssetManager manager) {
        if (assetState != AssetState.LOADING) {
            return;
        }

        bkgTexture = createTexture(manager, BKG, false);
        backTexture = createTexture(manager, BACK_FILE, false);
    }


    private final Stage stage;
    private AssetManager manager;

    public HelpMode(AssetManager manager, GameCanvas canvas) {
        this.stage = new Stage();
        this.canvas = canvas;
        this.manager = manager;
        active = true;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    final HelpMode help = this;


    public void initialize() {

        mainButton = createButton(backTexture);
        mainButton.setPosition(canvas.getWidth() / 2 - mainButton.getWidth() / 2, canvas.getHeight() * 0.1f);
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(help, PauseMode.INTO_PAUSE);
            }

        });
        stage.addActor(mainButton);
        Gdx.input.setInputProcessor(stage);
    }

    public void exit() {

    }

    /**
     * Listener that will update the player mode when we are done
     */
    private ScreenListener listener;

    private boolean active;

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

    }


    private ImageButton createButton(TextureRegion texture) {
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(texture);
        return new ImageButton(myTexRegionDrawable);
    }


    public void reset() {
        Gdx.input.setInputProcessor(stage);
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


}

