package entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Representation Class that represents
 * a single level
 */
public class Level implements Json.Serializable {
    private final List<Tile> tiles;
    private final List<Tile> spikes;
    private final Vector2 exitPos;
    private final Vector2 playerPos;
    private String type;

    private final List<NpcData> npcData;
    private final List<TextBox> text;
    private final List<float[]> items;
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Level() {
        tiles = new ArrayList<>();
        playerPos = new Vector2();
        exitPos = new Vector2();
        items = new ArrayList<>();
        npcData = new ArrayList<>();
        spikes = new ArrayList<>();
        text = new ArrayList<>();
    }

    public String getType() {
        return this.type;
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        //environment information
        this.type = jsonData.getString("type");

        //Game Tiles
        JsonValue tilesData = jsonData.get("tiles");
        for (JsonValue tileJsonValue : tilesData) {
            String tileJson = tileJsonValue.toJson(JsonWriter.OutputType.minimal);
            Tile tile = json.fromJson(Tile.class, tileJson);
            if (tile.getType().equals("spikes")) {
                spikes.add(tile);
            } else if (tile.getType().equals("normal")) {
                tiles.add(tile);
            }
        }

        // Player Position
        JsonValue playerPosData = jsonData.get("player");
        playerPos.set(playerPosData.getFloat("x"), playerPosData.getFloat("y"));

        //Exit Position
        JsonValue exitPosData = jsonData.get("exit");
        exitPos.set(exitPosData.getFloat("x"), exitPosData.getFloat("y"));

        // Couple Positions
        JsonValue npcsData = jsonData.get("npc");
        for (JsonValue npcData : npcsData) {
            String npcJson = npcData.toJson(JsonWriter.OutputType.minimal);
            NpcData npc = json.fromJson(NpcData.class, npcJson);
            this.npcData.add(npc);
        }

        JsonValue itemsData = jsonData.get("items");
        for (JsonValue itemData : itemsData) {
//            items.add([playerPos.set(playerPosData.getFloat("x"), playerPosData.getFloat("y"))]);
            float[] coordinate = new float[2];
            coordinate[0] = itemData.getFloat("x");
            coordinate[1] = itemData.getFloat("y");
            items.add(coordinate);
        }


        JsonValue textBoxData = jsonData.get("text");
        for (JsonValue textData : textBoxData) {
            String textJson = textData.toJson(JsonWriter.OutputType.minimal);
            TextBox text = json.fromJson(TextBox.class, textJson);
            this.text.add(text);
        }
    }

    /**
     * @return the tiles for the level
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Tile> getSpikes() {
        return spikes;
    }

    /**
     * @return the player position
     */
    public Vector2 getPlayerPos() {
        return playerPos;
    }

    public Vector2 getExitPos() {
        return exitPos;
    }

    public List<NpcData> getNpcData() {
        return npcData;
    }

    public List<float[]> getItems() {
        return items;
    }

    public List<TextBox> getText() {
        return text;
    }


}
