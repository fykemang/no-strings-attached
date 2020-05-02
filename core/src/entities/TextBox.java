package entities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TextBox implements Json.Serializable {
    private String text;
    private float x;
    private float y;

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        text = jsonData.getString("text");
        x = jsonData.getFloat("x");
        y = jsonData.getFloat("y");
    }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
