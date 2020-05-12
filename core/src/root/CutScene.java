package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import util.ScreenListener;

import java.util.ArrayList;


public class CutScene extends Mode implements Screen {
    public static enum THEME {
        OPENING,
        CITY,
        VILLAGE,
        FOREST,
        END
    }

    private final String OPENING_CUTSCENE_FILE = "music/cutscene.mp3";
    private final String ENDING_CUTSCENE_FILE = "music/youshoulddosomereflecting.mp3";
    private final String TRANSITION_CUTSCENE_FILE = "music/goodnight.mp3";
    private THEME theme;
    public static final int INTO_CUTSCENE = 8;
    private static final String[] opening = {"cutscenes/opening-1.png", "cutscenes/opening-2.png",
            "cutscenes/opening-3.png"};
    private static final String city = "cutscenes/city_edit.png";
    private static final String village = "cutscenes/village_edit.png";
    private static final String forest = "cutscenes/forest_edit.png";
    private static final String[] ending = {"cutscenes/end_01.png", "cutscenes/end_02.png", "cutscenes/end_03.png",
            "cutscenes/end_04.png", "cutscenes/end_05.png", "cutscenes/end_06.png", "cutscenes/end_07.png",
            "cutscenes/end_08.png", "cutscenes/end_09.png", "cutscenes/end_10.png", "cutscenes/end_11.png"};
    private static final String SKIP = "cutscenes/PressEnterSkip.png";
    private static final String RIGHT = "cutscenes/skipButton.png";
    private static final String START = "ui/start.png";
    private final ImageButton.ImageButtonStyle skipbuttonStyle = new ImageButton.ImageButtonStyle();
    private ArrayList<TextureRegion> textures = new ArrayList<>();
    private TextureRegion skiptexture;
    private TextureRegion nextTexture;
//    private static final String[] city = {};
    private boolean slideMode;
    private int currentSlide = 0;

    private final AssetManager manager;
    private GameCanvas canvas;
    private final Stage stage;
    private ScreenListener listener;
    private final boolean active;
    private ImageButton skipButtom;
    private ImageButton nextButtom;
    private CutScene cutScene;
    private Music music;

    private AssetState selectorAssetState = AssetState.EMPTY;

    public CutScene(AssetManager manager, GameCanvas canvas) {
        this.manager = manager;
        this.canvas = canvas;
        this.stage = new Stage();
        cutScene = this;

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

    public void stopMusic() {
        music.dispose();
    }

    public void startMusic() {

    }

    private void update(float dt) {

    }

    private void draw() {
        canvas.begin();
        if (slideMode) {
            canvas.drawBackground(textures.get(currentSlide).getTexture(), canvas.getWidth() / 2, canvas.getHeight() / 2,
                    canvas.getWidth() / 2, canvas.getHeight() / 2, Color.WHITE);
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                listener.exitScreen(cutScene, LevelSelectorMode.INTO_SELECTOR);
            }
            if (currentSlide < textures.size() - 1) {
                canvas.drawUI(skiptexture, canvas.getWidth() * 0.9f - skiptexture.getRegionWidth() / 2, canvas.getHeight() * 0.9f, 1f);
            }
        }
        canvas.actStage(stage);
        canvas.end();
    }

    @Override
    public void preloadContent(AssetManager manager) {
        if (selectorAssetState != AssetState.EMPTY) {
            return;
        }
        for (String s : opening) loadAsset(s, Texture.class, manager);
//        for (String s : city) loadAsset(s, Texture.class, manager);
        for (String s : ending) loadAsset(s, Texture.class, manager);
        loadAsset(RIGHT, Texture.class, manager);
        loadAsset(SKIP, Texture.class, manager);
        loadAsset(START, Texture.class, manager);
        loadAsset(city, Texture.class, manager);
        loadAsset(village, Texture.class, manager);
        loadAsset(forest, Texture.class, manager);
        loadAsset(OPENING_CUTSCENE_FILE, Music.class, manager);
        loadAsset(ENDING_CUTSCENE_FILE, Music.class, manager);
        loadAsset(TRANSITION_CUTSCENE_FILE, Music.class, manager);
        selectorAssetState = AssetState.LOADING;
    }

    @Override
    public void loadContent(AssetManager manager) {
        stage.clear();
        textures = new ArrayList<>();
        skiptexture = createTexture(manager, SKIP, false);
        nextTexture = createTexture(manager, RIGHT, false);
        switch (theme) {
            case OPENING:
                for (String file : opening) {
                    textures.add(createTexture(manager, file, false));
                    music = manager.get(OPENING_CUTSCENE_FILE);
                }
                slideMode = true;
                break;
            case CITY:
                textures.add(createTexture(manager, city, false));
                music = manager.get(TRANSITION_CUTSCENE_FILE);
                break;
            case VILLAGE:
                textures.add(createTexture(manager, village, false));
                music = manager.get(TRANSITION_CUTSCENE_FILE);
                break;
            case FOREST:
                textures.add(createTexture(manager, forest, false));
                music = manager.get(TRANSITION_CUTSCENE_FILE);
                break;
            case END:
                for (String file : ending) {
                    textures.add(createTexture(manager, file, false));
                    music = manager.get(ENDING_CUTSCENE_FILE);
                }
                slideMode = true;
                break;
            default:
        }
        music.play();
        music.setVolume(0.25f * GDXRoot.musicVol);
        music.setLooping(true);
        if (slideMode) {
            nextButtom = createButton(nextTexture);
            nextButtom.setPosition(canvas.getWidth() * 0.9f - nextButtom.getWidth() / 2,
                    canvas.getHeight() * 0.5f);
            nextButtom.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (currentSlide < textures.size() - 2)
                        currentSlide++;
                    else if (currentSlide == textures.size() - 2) {
                        currentSlide++;
                        stage.clear();
                        addStart();
                    }
                }

            });
            if (textures.size() > 1) {
                stage.addActor(nextButtom);
            }
            else addStart();


            Gdx.input.setInputProcessor(stage);

        } else {
            //set up the buttoms
            // deal with animations


        }
        selectorAssetState = AssetState.COMPLETE;
    }

    private void addStart() {
        TextureRegion startTexture = createTexture(manager, START, false);
        ImageButton start = createButton(startTexture);
        start.setPosition(canvas.getWidth() * 0.7f - start.getWidth() / 2,
                canvas.getHeight() * 0.4f);
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(cutScene, LevelSelectorMode.INTO_SELECTOR);
            }

        });
        stage.addActor(start);
        Gdx.input.setInputProcessor(stage);
    }

    private ImageButton createButton(TextureRegion texture) {
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(myTexRegionDrawable);
        return button;
    }


    public void setTheme(THEME t) {
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
