package root;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import entities.LevelMetadata;


/**
 * Responsible for loading in JSON text files
 * which each represent a level
 */
public class LevelLoader extends AsynchronousAssetLoader<LevelMetadata, LevelLoader.LevelLoaderParameters> {
    private final Json json;
    private LevelMetadata level;

    public LevelLoader(FileHandleResolver resolver) {
        super(resolver);
        json = new Json();
    }

    public LevelMetadata getLoadedLevel() {
        return level;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, LevelLoaderParameters parameter) {
        level = json.fromJson(LevelMetadata.class, file);
    }

    @Override
    public LevelMetadata loadSync(AssetManager manager, String fileName, FileHandle file, LevelLoaderParameters parameter) {
        LevelMetadata level = this.level;
        this.level = null;
        return level;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LevelLoaderParameters parameter) {
        return null;
    }

    public static class LevelLoaderParameters extends AssetLoaderParameters<LevelMetadata> {

    }
}
