package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import platform.NpcData;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Representation Class that represents
 * a single level
 */
public class Level implements Json.Serializable {
    private List<Tile> tiles;
    private List<Tile> spikes;
    private Vector2 gatePos;
    private Vector2 playerPos;
    private String type;
    private TextureRegion tileTexture;

    private List<NpcData> npcData;
    private List<float[]> couples;
    private List<float[]> items;

    public void setTileTexture(TextureRegion tileTexture) {
        this.tileTexture = tileTexture;
    }

    public TextureRegion getTileTexture() {
        return tileTexture;
    }

    public Level() {
        tiles = new ArrayList<>();
        playerPos = new Vector2();
        gatePos = new Vector2();
        couples = new ArrayList<>();
        items = new ArrayList<>();
        npcData = new ArrayList<>();
        spikes = new ArrayList<>();
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
        JsonValue typeData = jsonData.get("type");
        this.type = typeData.toString();

        //Game Tiles
        JsonValue tilesData = jsonData.get("tiles");
        for (JsonValue tileJsonValue : tilesData) {
            String tileJson = tileJsonValue.toJson(JsonWriter.OutputType.minimal);
            Tile tile = json.fromJson(Tile.class, tileJson);
            tiles.add(tile);
        }

        JsonValue spikeData = jsonData.get("spikes");
        for (JsonValue spikeJsonValue : spikeData) {
            String spikeJson = spikeJsonValue.toJson(JsonWriter.OutputType.minimal);
            Tile tile = json.fromJson(Tile.class, spikeJson);
            spikes.add(tile);
        }

        // Player Position
        JsonValue playerPosData = jsonData.get("playerPos");
        playerPos.set(playerPosData.getFloat("x"), playerPosData.getFloat("y"));

        //Gate Position
        JsonValue gatePosData = jsonData.get("gate");
        gatePos.set(gatePosData.getFloat("x"), gatePosData.getFloat("y"));

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
            System.out.print(itemData);
            float[] coordinate = new float[2];
            coordinate[0] = itemData.getFloat("x");
            coordinate[1] = itemData.getFloat("y");
            items.add(coordinate);
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

    public Vector2 getGatePos() {
        return gatePos;
    }

    /**
     * @return the list of couple coordinates
     */
    public List<float[]> getCouples() {
        return couples;
    }

    public List<NpcData> getNpcData() {
        return npcData;
    }

    public List<float[]> getItems() {
        return items;
    }



}
