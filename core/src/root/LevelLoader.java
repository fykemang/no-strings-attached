package root;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;


/**
 * Responsible for loading in JSON text files
 * which each represent a level
 */
public class LevelLoader extends AsynchronousAssetLoader<Level, LevelLoader.LevelLoaderParameters> {
    private Json json;
    private Level level;

    public LevelLoader(FileHandleResolver resolver) {
        super(resolver);
        json = new Json();
    }

    public Level getLoadedLevel() {
        return level;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, LevelLoaderParameters parameter) {
        level = json.fromJson(Level.class, file);
    }

    @Override
    public Level loadSync(AssetManager manager, String fileName, FileHandle file, LevelLoaderParameters parameter) {
        Level level = this.level;
        this.level = null;
        return level;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LevelLoaderParameters parameter) {
        return null;
    }

    public static class LevelLoaderParameters extends AssetLoaderParameters<Level> {

    }
}
