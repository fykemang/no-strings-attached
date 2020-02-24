package platform;

import java.util.*;
import java.io.*;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


/**
 * a loader to load a level from json
 */
public class PlatformLoader {
    private String filepath;
    private Object rootobj;
    private Wall[] walls;
    private float[][] couples;
    private Vector2 dudePos;

    /**
     * construst a platform given the file path to json
     * @param filePath file path to json
     */
    public PlatformLoader(String filePath){
        this.filepath = filePath;
        try {
            rootobj = new JSONParser().parse(new FileReader(filePath));;
            JSONArray wallArray = (JSONArray) (((JSONObject) rootobj).get("walls"));
            walls = parseArray(wallArray);
            JSONArray coupleArray = (JSONArray) (((JSONObject) rootobj).get("couples"));
            couples= parseCouples(coupleArray);
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
    public Wall[] parseArray(JSONArray array){
        Wall[] result = new Wall[array.size()];
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
            Wall w = new Wall(height, width, temp);
            result[i] = w;
        }
        return result;
    }

    /**
     * parses a Json Couple Array
     * @param coupleArray
     * @return
     */
    public float[][] parseCouples(JSONArray coupleArray){
        float[][] result = new float[coupleArray.size()][];
        for(int i = 0; i < coupleArray.size(); i++) {
            JSONObject couple = ((JSONObject) coupleArray.get(i));
            float[] temp = new float[4];
            temp[0] = (float)(double) couple.get("left_x");
            temp[1] = (float)(double) couple.get("left_y");
            temp[2] = (float)(double) couple.get("right_x");
            temp[3] = (float)(double) couple.get("right_y");
            result[i] = temp;
        }
        System.out.println(Arrays.deepToString(result));
        return result;
    }

    /**
     *getter for walls
     * @return the walls array to initialize the level
     */
    public Wall[] getWalls(){
        return walls;
    }

    /**
     *getter for character position
     * @return the character position to initialize the level
     */
    public Vector2 getCharacterPos(){
        return dudePos;
    }

    /**
     * getter for couples
     * @return the couple array to initialize the level
     */
    public float[][] getCouples(){
        return couples;
    }
}
