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
            JSONArray platArray = (JSONArray) (((JSONObject) rootobj).get("platforms"));
            platforms = parseArray(platArray);
            JSONArray characterPos = (JSONArray) (((JSONObject) rootobj).get("characterPos"));
            dudePos =  new Vector2((float)(double)characterPos.get(0),
                    (float)(double)characterPos.get(1));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public float[][] parseArray(JSONArray array){
        float[][] result = new float[array.size()][];
        for(int row = 0; row < array.size(); row++) {
            JSONArray cols = (JSONArray)((JSONArray) array.get(row));
            float[] temp = new float[cols.size()];
            for(int col=0; col < cols.size(); col++) {
                temp[col] = (float)(double) cols.get(col);
            }
            result[row] = temp;
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
