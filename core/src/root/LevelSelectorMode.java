package root;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import entities.Level;
import entities.LevelMetadata;
import util.ScreenListener;

import java.util.ArrayList;
import java.util.Arrays;

public class LevelSelectorMode extends Mode implements Screen {
    public static final int INTO_SELECTOR = 4;
    private static final String BACKGROUND_FILE = "ui/select_bg.png";
    private static final String CITY_FILE = "ui/city.png";
    private static final String SUBURB_FILE = "ui/suburbs.png";
    private static final String FOREST_FILE = "ui/forest.png";
    private static final String MOUNTAIN_FILE = "ui/mountains.png";
    private static final String LOCKED_VILLAGE_FILE = "ui/locked_village.png";
    private static final String LOCKED_FOREST_FILE = "ui/locked_forest.png";
    private static final String LOCKED_MOUNTAIN_FILE = "ui/locked_mountains.png";
    private static final String SELECT_FILE = "ui/selector.png";
    private static final String MUSIC_FILE = "music/screen.mp3";
    private static final String LEVEL_METADATA = "levels/levels.json";
    private static final String SELECTOR_FONT = "ui/blackjack.otf";
    private static final String MENU_CLICK_FILE = "sounds/click.mp3";
    private static final String HOVER_FILE = "sounds/hover.mp3";
    private static final String BACK_FILE = "ui/backToMenu.png";
    private static final String CITY_CARD = "ui/city_card.png";
    private static final String FOREST_CARD = "ui/forest_card.png";
    private static final String MOUNTAIN_CARD = "ui/mountain_card.png";
    private static final String VILLAGE_CARD = "ui/village_card.png";
    private static final String LOCKED_CARD = "ui/locked.png";
    private static final String ENTER_START = "ui/press-space.png";
    private static final String MAP_LOCK = "ui/map-lock.png";
    private static final String ARROWDOWN = "ui/arrow-down.png";
    private static final String ARROWRIGHT = "ui/arrow-right.png";
    private static final String ARROWLEFT = "ui/arrow-left.png";
    private static final String PRESS_SPACE = "ui/pressSpace.png";
    public static int curLevel = 1;
    int lastLevel = 0;
    Table container;
    /**
     * Reference to game.GameCanvas created by the root
     */
    private Music levelSelectorMusic;
    private Sound clickSound;
    private Sound hoverSound;
    private Texture background;
    private Texture city;
    private Texture suburb;
    private Texture forest;
    private Texture mountain;
    private Texture selector;
    private Texture lockedVillage;
    private Texture lockedForest;
    private Texture lockedMountain;
    private TextureRegion citycard;
    private TextureRegion mountaincard;
    private TextureRegion arrowDone;
    private TextureRegion arrowLeft;
    private TextureRegion arrowRight;
    private TextureRegion spaceTexture;
    private TextureRegion villagecard;
    private TextureRegion forestcard;
    private TextureRegion backTexture;
    private TextureRegion MapLockTexture;
    private TextureRegion enterTexture;
    private TextureRegion lockedcard;
    private BitmapFont selectorFont;
    private final int city_level = 4;
    private final int suburb_level = 9;
    private boolean ready = false;
    Stage stage;
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

    private final int NONE = 0, CITY = 1, VILLAGE = 2, FOREST = 3, MOUNTAIN = 4;
    public static boolean[] themeUnlocked = new boolean[5];

    private int theme = NONE;
    private ScrollPane levelView;
    private float currentScroll = 0;

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
        loadAsset(LOCKED_FOREST_FILE, Texture.class, manager);
        loadAsset(LOCKED_MOUNTAIN_FILE, Texture.class, manager);
        loadAsset(LOCKED_VILLAGE_FILE, Texture.class, manager);
        loadAsset(SELECT_FILE, Texture.class, manager);
        loadAsset(CITY_FILE, Texture.class, manager);
        loadAsset(MUSIC_FILE, Music.class, manager);
        loadAsset(MENU_CLICK_FILE, Sound.class, manager);
        loadAsset(HOVER_FILE, Sound.class, manager);
        loadAsset(CITY_CARD, Texture.class, manager);
        loadAsset(BACK_FILE, Texture.class, manager);
        loadAsset(MOUNTAIN_CARD, Texture.class, manager);
        loadAsset(FOREST_CARD, Texture.class, manager);
        loadAsset(VILLAGE_CARD, Texture.class, manager);
        loadAsset(MAP_LOCK, Texture.class, manager);
        loadAsset(LOCKED_CARD, Texture.class, manager);
        loadAsset(VILLAGE_CARD, Texture.class, manager);
        loadAsset(ENTER_START, Texture.class, manager);
        loadAsset(ARROWLEFT, Texture.class, manager);
        loadAsset(ARROWRIGHT, Texture.class, manager);
        loadAsset(ARROWDOWN, Texture.class, manager);
        loadAsset(PRESS_SPACE, Texture.class, manager);
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
        lockedForest = manager.get(LOCKED_FOREST_FILE, Texture.class);
        lockedMountain = manager.get(LOCKED_MOUNTAIN_FILE, Texture.class);
        lockedVillage = manager.get(LOCKED_VILLAGE_FILE, Texture.class);
        levelSelectorMusic = manager.get(MUSIC_FILE, Music.class);
        levelMetadata = manager.get(LEVEL_METADATA, LevelMetadata.class);
        clickSound = manager.get(MENU_CLICK_FILE, Sound.class);
        hoverSound = manager.get(HOVER_FILE, Sound.class);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/BalooThambi.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        selectorFont = generator.generateFont(parameter);
        selectorAssetState = AssetState.COMPLETE;
        citycard = createTexture(manager, CITY_CARD, false);
        villagecard = createTexture(manager, VILLAGE_CARD, false);
        forestcard = createTexture(manager, FOREST_CARD, false);
        mountaincard = createTexture(manager, MOUNTAIN_CARD, false);
        backTexture = createTexture(manager, BACK_FILE, false);
        lockedcard = createTexture(manager, LOCKED_CARD, false);
        MapLockTexture = createTexture(manager, MAP_LOCK, false);
        enterTexture = createTexture(manager, ENTER_START, false);
        arrowDone = createTexture(manager, ARROWDOWN, false);
        arrowRight = createTexture(manager, ARROWRIGHT, false);
        arrowLeft = createTexture(manager, ARROWLEFT, false);

    }

    private int themeFromType(String type) {
        switch (type) {
            case "city":
                return CITY;
            case "mountain":
                return MOUNTAIN;
            case "village":
                return VILLAGE;
            case "forest":
                return FOREST;
            default:
                return NONE;
        }
    }

    public void unlockLevel(int index) {
        if (getLevel(index) != null) {
            levelMetadata.unlockLevel(index);
            String type = levelMetadata.getLevel(index).getType();
            themeUnlocked[themeFromType(type)] = true;
        }
    }

    public LevelSelectorMode() {
        stage = new Stage();
        this.assets = new Array<>();
        Arrays.fill(themeUnlocked, true);
        buttonPos.add(new Vector2(280, 610));
        buttonPos.add(new Vector2(350, 650));
        buttonPos.add(new Vector2(440, 630));
        buttonPos.add(new Vector2(520, 650));
        buttonPos.add(new Vector2(600, 680));
        buttonPos.add(new Vector2(690, 710));
        buttonPos.add(new Vector2(780, 760));
        buttonPos.add(new Vector2(840, 660));
        buttonPos.add(new Vector2(860, 540));
        buttonPos.add(new Vector2(920, 460));
        buttonPos.add(new Vector2(820, 380));
        buttonPos.add(new Vector2(720, 340));
        buttonPos.add(new Vector2(780, 300));
        buttonPos.add(new Vector2(860, 250));
        buttonPos.add(new Vector2(800, 200));
        buttonPos.add(new Vector2(740, 200));
        buttonPos.add(new Vector2(680, 290));
        buttonPos.add(new Vector2(500, 270));
        themeUnlocked[CITY] = true;
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
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        levelSelectorMusic.stop();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        levelSelectorMusic.stop();
    }

    @Override
    public void dispose() {
        if (levelSelectorMusic != null)
            levelSelectorMusic.dispose();
//        hoverSound.dispose();
//        clickSound.dispose();
//        buttonPos.clear();
//        background.dispose();
//        city.dispose();
//        suburb.dispose();
//        forest.dispose();
//        mountain.dispose();
//        selector.dispose();
//        lockedVillage.dispose();
//        lockedForest.dispose();
//        lockedMountain.dispose();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            listener.exitScreen(this, GameMode.EXIT_INTO_GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (curLevel < levelMetadata.getLevelCount()) {
                curLevel++;
                levelView.layout();
                levelView.setScrollX(levelView.getScrollX() + 350);
            }
            if (curLevel == 11) {
                next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.8f);
                last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.8f);
                container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.85f);
                isDown = false;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (curLevel > 1) {
                curLevel--;
                levelView.layout();
                levelView.setScrollX(levelView.getScrollX() - 350);

            }
            if (curLevel == 9) {
                next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.2f);
                last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.2f);
                container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.25f);
                isDown = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            listener.exitScreen(this, LoadingMode.INTO_STARTSCREEN);
        }

    }

    private Texture getTextureFromTheme(int t) {
        switch (t) {
            case CITY:
                return city;
            case VILLAGE:
                return themeUnlocked[VILLAGE] ? suburb : lockedVillage;
            case MOUNTAIN:
                return themeUnlocked[MOUNTAIN] ? mountain : lockedMountain;
            case FOREST:
                return themeUnlocked[FOREST] ? forest : lockedForest;
            default:
                return null;
        }
    }

    private void draw() {
        canvas.begin();
        canvas.drawBackground(background);

        theme = themeUnlocked[theme] ? theme : NONE;
        Texture cityBkg = getTextureFromTheme(CITY), villageBkg = getTextureFromTheme(VILLAGE),
                mountainBkg = getTextureFromTheme(MOUNTAIN), forestBkg = getTextureFromTheme(FOREST);

        canvas.drawBackground(mountainBkg);
        canvas.drawBackground(villageBkg);
        canvas.drawBackground(forestBkg);
        canvas.drawBackground(cityBkg);

        for (int i = 0; i < 5; i++) {
            Vector2 button = buttonPos.get(i);
            if (levelMetadata.getLevelCount() >= (i + 1) && levelMetadata.isLevelUnlocked(i + 1)) {
                selectorFont.setColor(Color.WHITE);
            } else {
                selectorFont.setColor(Color.GRAY);
            }
            canvas.drawText(i + 1 + "", selectorFont, button.x, button.y);

        }
        if (isDown) {
            canvas.drawUI(arrowDone, canvas.getWidth() / 2, canvas.getHeight() / 2 - 80, 1f);
        } else {
            canvas.drawUI(arrowDone, canvas.getWidth() / 2, canvas.getHeight() / 2 + 140, -1f);
        }
        if (!themeUnlocked[VILLAGE]) {
            canvas.draw(MapLockTexture,
                    868 - MapLockTexture.getRegionWidth() / 2, 669 - MapLockTexture.getRegionHeight() / 2);

        } else {
            for (int i = 5; i < 9; i++) {
                Vector2 button = buttonPos.get(i);
                if (levelMetadata.getLevelCount() >= (i + 1) && (levelMetadata.isLevelUnlocked(i + 1))) {
                    selectorFont.setColor(Color.WHITE);
                } else {
                    selectorFont.setColor(Color.GRAY);
                }
                canvas.drawText(i + 1 + "", selectorFont, button.x, button.y);

            }
        }


        if (!themeUnlocked[FOREST]) {
            canvas.draw(MapLockTexture,
                    960 - MapLockTexture.getRegionWidth() / 2, 450 - MapLockTexture.getRegionHeight() / 2);
        } else {
            for (int i = 9; i < 13; i++) {
                Vector2 button = buttonPos.get(i);
                Level l = (levelMetadata.getLevel(i + 1));
                if (levelMetadata.getLevelCount() >= (i + 1) && levelMetadata.isLevelUnlocked(i + 1)) {
                    selectorFont.setColor(Color.WHITE);
                } else {
                    selectorFont.setColor(Color.GRAY);
                }
                canvas.drawText(i + 1 + "", selectorFont, button.x, button.y);

            }
        }

        if (!themeUnlocked[MOUNTAIN]) {
            canvas.draw(MapLockTexture,
                    330 - MapLockTexture.getRegionWidth() / 2, 230 - MapLockTexture.getRegionHeight() / 2);
        } else {

            for (int i = 13; i < 17; i++) {
                Vector2 button = buttonPos.get(i);
                Level l = (levelMetadata.getLevel(i + 1));
                if (levelMetadata.getLevelCount() >= (i + 1) && levelMetadata.isLevelUnlocked(i + 1)) {
                    selectorFont.setColor(Color.WHITE);
                } else {
                    selectorFont.setColor(Color.GRAY);
                }
                canvas.drawText(i + 1 + "", selectorFont, button.x, button.y);
            }


        }

        if (curLevel > 0 && curLevel < levelMetadata.getLevelCount() + 1) {


            canvas.draw(selector, buttonPos.get(curLevel - 1).x - selector.getWidth() / 2 + 5,
                    buttonPos.get(curLevel - 1).y - selector.getHeight() / 2 - 15);
        }
        canvas.drawUI(enterTexture, canvas.getWidth() / 2, canvas.getHeight() * 0.05f, 0.7f);
        canvas.actStage(stage);
        canvas.end();
    }

    public int getLevelIndex() {
        return curLevel;
    }

    public Level getLevel(int level) {

        if (level > levelMetadata.getLevelCount() + 1 || level == -1) return null;
        Level l = levelMetadata.getLevel(level);
        l.setLevel(level);
        return l;
    }

    public Level getCurrentLevel() {

        if (curLevel > levelMetadata.getLevelCount() + 1 || curLevel == -1) return null;
        Level l = levelMetadata.getLevel(curLevel);
        l.setLevel(curLevel);
        return l;
    }


    public void reset() {
        levelSelectorMusic.play();
        if (curLevel > 10) {
            next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.8f);
            last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.8f);
            container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.85f);
        } else {
            next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.2f);
            last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.2f);
            container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.25f);

        }
        levelView.layout();
        levelView.setScrollX((curLevel - 1) * 350f);
        level = -1;
        ready = false;
        levelSelectorMusic.play();
        levelSelectorMusic.setVolume(0.5f * GDXRoot.musicVol);
        levelSelectorMusic.setLooping(true);
        Gdx.input.setInputProcessor(stage);
    }


    private boolean isDown = true;
    ImageButton next;
    ImageButton last;

    public void initUI() {
        stage.clear();
        container = new Table();
        Table levelTable = new Table();

        levelView = new ScrollPane(levelTable) {
        };
        final LevelSelectorMode select = this;
        levelTable.pad(600 - 175);
        for (int i = 0; i < levelMetadata.getLevelCount(); i++) {
            ImageTextButton Button = null;
            Level l = levelMetadata.getLevel(i + 1);
            ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
            style.font = selectorFont;
            if (levelMetadata.isLevelUnlocked(i + 1)) {
                switch (l.getType()) {
                    case "city":
                        style.up = new TextureRegionDrawable(citycard);
                        Button = new ImageTextButton("The City: \nLEVEL " + (i + 1), style);
                        break;
                    case "village":
                        style.up = new TextureRegionDrawable(villagecard);
                        Button = new ImageTextButton("The Village: \nLEVEL " + (i + 1), style);
                        break;
                    case "mountain":
                        style.up = new TextureRegionDrawable(mountaincard);
                        Button = new ImageTextButton("The Mountain: \nLEVEL " + (i + 1), style);
                        break;
                    case "forest":
                        style.up = new TextureRegionDrawable(forestcard);
                        Button = new ImageTextButton("The Forest: \nLEVEL " + (i + 1), style);
                        break;
                }
            } else {
                style.up = new TextureRegionDrawable(lockedcard);
                Button = new ImageTextButton("", style);

            }

            final int finalI = i;
            Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (levelMetadata.isLevelUnlocked(finalI + 1)) {
                        curLevel = finalI + 1;
                        currentScroll = levelView.getScrollX();
                        clickSound.play();
                        listener.exitScreen(select, GameMode.EXIT_INTO_GAME);
                    }
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    hoverSound.play();
                }
            });
            levelTable.add(Button).pad(10);
        }

        levelView.setScrollingDisabled(false, true);
        levelView.setOverscroll(true, true);
        container.add(levelView).width(canvas.getWidth()).height(300);
        container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.25f);
        levelView.layout();
        levelView.setScrollX(currentScroll);
        levelView.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                levelView.cancel();
            }
        });


        ImageButton BackButton = createButton(backTexture);
        BackButton.setPosition(canvas.getWidth() * 0.03f, canvas.getHeight() * 0.03f);
        BackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(select, LoadingMode.INTO_STARTSCREEN);
            }

        });
        stage.addActor(BackButton);
        stage.addActor(container);

        last = createButton(arrowLeft);
        next = createButton(arrowRight);
        next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.2f);
        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelView.layout();
                levelView.setScrollX(levelView.getScrollX() + 350);
                if (curLevel < levelMetadata.getLevelCount()) {
                    curLevel++;
                    clickSound.play();
                }

                if (curLevel > 10) {
                    isDown = false;
                    next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.8f);
                    last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.8f);
                    container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.85f);
                }
            }

        });
        stage.addActor(next);
        last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.2f);
        last.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                levelView.layout();
                levelView.setScrollX(levelView.getScrollX() - 350);
                 if (curLevel > 1){ curLevel--;  clickSound.play();}
                if (curLevel < 10) {
                    isDown = true;
                    next.setPosition(canvas.getWidth() * 0.9f, canvas.getHeight() * 0.2f);
                    last.setPosition(canvas.getWidth() * 0.1f, canvas.getHeight() * 0.2f);
                    container.setPosition(canvas.getWidth() / 2, canvas.getHeight() * 0.25f);
                }
            }

        });
        stage.addActor(last);


        Gdx.input.setInputProcessor(stage);
    }


    private ImageButton createButton(TextureRegion texture) {
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(texture);
        return new ImageButton(myTexRegionDrawable);
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

    public void unlock(int theme) {
        themeUnlocked[theme] = true;
    }

    public static void lock() {
        Arrays.fill(themeUnlocked, false);
        themeUnlocked[0] =true;
    }


    public void saveGame() {
        this.levelMetadata.saveGame();
    }

    public void startNewGame() {
        this.levelMetadata.resetSave();
    }


}

