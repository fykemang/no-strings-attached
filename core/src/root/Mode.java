package root;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

public abstract class Mode {
    /**
     * Track all loaded assets (for unloading purposes)
     */
    Array<String> assets = new Array<>();
    GameCanvas canvas;
    AssetState assetState = AssetState.EMPTY;

    /**
     * Preloads the assets for this controller.
     * <p>
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public abstract void preloadContent(AssetManager manager);

    /**
     * Load the assets for this controller.
     * <p>
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public abstract void loadContent(AssetManager manager);

    public void loadAsset(String filepath, Class type, AssetManager manager) {
        manager.load(filepath, type);
        assets.add(filepath);
    }

    /**
     * Unloads the assets for this game.
     * <p>
     * This method erases the static variables.  It also deletes the associated textures
     * from the asset manager. If no assets are loaded, this method does nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void unloadContent(AssetManager manager) {
        for (String asset : assets) {
            if (manager.isLoaded(asset)) {
                manager.unload(asset);
            }
        }
    }

    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
    }

    enum AssetState {
        /**
         * No assets loaded
         */
        EMPTY,
        /**
         * Still loading assets
         */
        LOADING,
        /**
         * Assets are complete
         */
        COMPLETE
    }

}
