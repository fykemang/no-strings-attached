package entities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Tile implements Json.Serializable {
    private float[] corners;
    private float height;
    private float width;
    private float x;
    private float y;
    private String type;

    public float[] getCorners() {
        return corners;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        corners = new float[8];
        width = jsonData.getFloat("width");
        height = jsonData.getFloat("height");
        x = jsonData.getFloat("x");
        y = jsonData.getFloat("y");
        type = jsonData.getString("type");
        corners[0] = 0;
        corners[1] = 0;
        corners[2] = 0;
        corners[3] = height;
        corners[4] = width;
        corners[5] = height;
        corners[6] = width;
        corners[7] = 0;
    }
}
