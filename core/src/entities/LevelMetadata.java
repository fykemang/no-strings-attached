package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class LevelMetadata implements Json.Serializable {
    private final Map<Integer, Level> levelMap;
    private final Preferences levelState;
    private final boolean UNLOCK_ALL_LEVELS = true;

    public LevelMetadata() {
        levelMap = new HashMap<>();
        levelState = Gdx.app.getPreferences("no-strings-attached.save");
    }

    @Override
    public void write(Json json) {
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        boolean saveExists = levelState.getBoolean("saveExists", false);
        JsonValue.JsonIterator levels = jsonData.get("levels").iterator();
        for (JsonValue jsonLevel : levels) {
            String levelPath = jsonLevel.getString("path");
            int levelID = jsonLevel.getInt("id");
            Level level = json.fromJson(Level.class, Gdx.files.internal(levelPath));
            levelMap.put(levelID, level);
            if (!saveExists) {
                levelState.putBoolean(String.valueOf(levelID), levelID == 1);
            }
        }
    }

    public Level getLevel(int index) {
        return levelMap.get(index);
    }

    public boolean isLevelUnlocked(int index) {
        return levelState.getBoolean(String.valueOf(index)) || UNLOCK_ALL_LEVELS;
    }

    public void unlockLevel(int index) {
        if (UNLOCK_ALL_LEVELS) return;
        levelState.putBoolean(String.valueOf(index), true);
    }

    public int getLevelCount() {
        return levelMap.size();
    }

    public void saveGame() {
        levelState.flush();
    }

    public void resetSave() {
        levelState.putBoolean(String.valueOf(1), true);
        for (int i = 2; i <= levelMap.size(); i++) {
            levelState.putBoolean(String.valueOf(i), false);
        }
        levelState.putBoolean("saveExists", true);
        saveGame();
    }

}
