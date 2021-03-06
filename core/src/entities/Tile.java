package entities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Tile implements Json.Serializable {
    private float[] corners;
    private float height;
    private float width;
    private float x;
    private float y;
    private String direction;
    private String type;

    private boolean isSliding;
    private float[] left;
    private float[] right;

    public boolean isSliding() {
        return isSliding;
    }

    public float[] getRight() {
        return right;
    }

    public float[] getLeft() {
        return left;
    }

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

    public String getDirection() {
        return direction;
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
        direction = jsonData.getString("direction");
        corners[0] = 0;
        corners[1] = 0;
        corners[2] = 0;
        corners[3] = height;
        corners[4] = width;
        corners[5] = height;
        corners[6] = width;
        corners[7] = 0;

        isSliding = jsonData.getBoolean("isSliding");
        if (isSliding) {
            left = new float[2];
            left[0] = jsonData.get("leftPos").getFloat("x");
            left[1] = jsonData.get("leftPos").getFloat("y");
            right = new float[2];
            right[0] = jsonData.get("rightPos").getFloat("x");
            right[1] = jsonData.get("rightPos").getFloat("y");
        }
    }
}
