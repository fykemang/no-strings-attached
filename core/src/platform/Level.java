package platform;

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
    private List<Tile> tiles;
    private Vector2 playerPos;
    private List<float[]> couples;
    private List<float[]> items;

    public Level() {
        tiles = new ArrayList<>();
        playerPos = new Vector2();
        couples = new ArrayList<>();
        items = new ArrayList<>();
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        // Game Tiles
        JsonValue tilesData = jsonData.get("tiles");
        for (JsonValue tileJsonValue : tilesData) {
            String tileJson = tileJsonValue.toJson(JsonWriter.OutputType.minimal);
            Tile tile = json.fromJson(Tile.class, tileJson);
            tiles.add(tile);
        }

        // Player Position
        JsonValue playerPosData = jsonData.get("playerPos");
        playerPos.set(playerPosData.getFloat("x"), playerPosData.getFloat("y"));

        // Couple Positions
        JsonValue couplesData = jsonData.get("couples");
        for (JsonValue coupleData : couplesData) {
            float[] coupleCoordinates = new float[4];
            coupleCoordinates[0] = coupleData.getFloat("left_x");
            coupleCoordinates[1] = coupleData.getFloat("left_y");
            coupleCoordinates[2] = coupleData.getFloat("right_x");
            coupleCoordinates[3] = coupleData.getFloat("right_y");
            couples.add(coupleCoordinates);
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

    /**
     * @return the player position
     */
    public Vector2 getPlayerPos() {
        return playerPos;
    }

    /**
     * @return the list of couple coordinates
     */
    public List<float[]> getCouples() {
        return couples;
    }

    public List<float[]> getItems() {
        return items;
    }


}
