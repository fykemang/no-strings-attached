package platform;

import java.util.*;
import java.io.*;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.JSONValue;


/**
 * a loader for platforms
 */
public class PlatformLoader {
    private String filepath;
    private Object rootobj;
    private float[][] walls;
    private float[][] platforms;
    private Vector2 dudePos;

    public PlatformLoader(String filePath){
        this.filepath = filePath;
        try {
            rootobj = new JSONParser().parse(new FileReader(filePath));;
            JSONArray wallArray = (JSONArray) (((JSONObject) rootobj).get("walls"));
            walls = parseArray(wallArray);
            JSONArray characterPos = (JSONArray) (((JSONObject) rootobj).get("characterPos"));
            dudePos =  new Vector2((float)(double)characterPos.get(0),
                    (float)(double)characterPos.get(1));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses a walls array to Polygon obstacle input
     * @param array JSONarray for the walls
     * @return
     */
    public float[][] parseArray(JSONArray array){
        float[][] result = new float[array.size()][];
        for(int i = 0; i < array.size(); i++) {
            JSONObject wall = ((JSONObject) array.get(i));
            float[] temp = new float[8];
            Vector2 wallPos = new Vector2((float)(double)wall.get("x"), (float)(double)wall.get("y"));
            float width = (float)(double) wall.get("width");
            float height = (float)(double) wall.get("height");
            temp[0] = wallPos.x;
            temp[1] = wallPos.y;
            temp[2] = wallPos.x;
            temp[3] = wallPos.y + height;
            temp[4] = wallPos.x + width;
            temp[5] = wallPos.y + height;
            temp[6] = wallPos.x + width;
            temp[7] = wallPos.y;
            result[i] = temp;
        }
        return result;
    }
    public float[][] getWalls(){
        return walls;
    }
    public float[][] getPlatforms(){
        return platforms;
    }
    public Vector2 getCharacterPos(){
        return dudePos;
    }
}
