package platform;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class NpcData implements Json.Serializable {
    private float[] pos = new float[2];
    private boolean isSliding;
    private float[] left;
    private float[] right;
    private boolean isRotating;
    private float[] rotatingCenter;
    private float rotatingDegree;
    private int pairIdx;

    public boolean isSliding() {
        return isSliding;
    }

    public float[] getRight() {
        return right;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public float[] getRotatingCenter() {
        return rotatingCenter;
    }

    public float getRotatingDegree() {
        return rotatingDegree;
    }

    public float[] getPos() {
        return pos;
    }

    public float[] getLeft() {
        return left;
    }

    public int getPairIdx() {
        return pairIdx;
    }

    @Override
    public void write(Json json) {
        // unimplemented
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        pos[0] = jsonData.getFloat("x");
        pos[1] = jsonData.getFloat("y");
        isSliding = jsonData.getBoolean("isSliding");
        if (isSliding){
            left = new float[2];
            left[0] = jsonData.get("leftPos").getFloat("x");
            left[1] = jsonData.get("leftPos").getFloat("y");
            right = new float[2];
            right[0] = jsonData.get("rightPos").getFloat("x");
            right[1] = jsonData.get("rightPos").getFloat("y");
        }
        isRotating = jsonData.getBoolean("isRotating");
        if (isRotating){
            rotatingCenter = new float[2];
            rotatingCenter[0] = jsonData.get("center").getFloat("x");
            rotatingCenter[1] = jsonData.get("center").getFloat("y");
            rotatingDegree = jsonData.getFloat("degree");
        }
        pairIdx = jsonData.getInt("pairIdx");
    }
}
