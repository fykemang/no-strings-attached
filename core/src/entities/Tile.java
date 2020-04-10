package entities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Tile implements Json.Serializable {
    private float[] corners;
    private float height;
    private float width;

    public float[] getCorners() {
        return corners;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        corners = new float[8];
        width = jsonData.getFloat("width");
        height = jsonData.getFloat("height");
        float x = jsonData.getFloat("x");
        float y = jsonData.getFloat("x");
        corners[0] = x;
        corners[1] = y;
        corners[2] = x;
        corners[3] = y + height;
        corners[4] = x + width;
        corners[5] = y + height;
        corners[6] = x + width;
        corners[7] = y;
    }
}
