package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class LevelMetadata implements Json.Serializable {
    private final Map<Integer, Level> levelMap;

    public LevelMetadata() {
        levelMap = new HashMap<>();
    }

    @Override
    public void write(Json json) {
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        JsonValue.JsonIterator levels = jsonData.get("levels").iterator();
        for (JsonValue jsonLevel : levels) {
            String levelPath = jsonLevel.getString("path");
            int levelID = jsonLevel.getInt("id");
            boolean levelUnlocked = jsonLevel.getBoolean("unlocked");
            Level level = json.fromJson(Level.class, Gdx.files.internal(levelPath));
            level.setUnlocked(levelUnlocked);
            levelMap.put(levelID, level);
        }
    }

    public Level getLevel(int index) {
        return levelMap.get(index);
    }

    public void unlockLevel(int index){
        Level l = getLevel(index);
        l.setUnlocked(true);
    }

    public int getLevelCount() {
        return levelMap.size();
    }

}
