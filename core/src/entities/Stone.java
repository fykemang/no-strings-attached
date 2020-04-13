package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import obstacle.PolygonObstacle;
import root.GameCanvas;

import java.util.ArrayList;
import java.util.Random;

public class Stone extends PolygonObstacle {

    float scale;
    float width;
    float height;
    float firstx;
    float firsty;
    float sc;
    float start;
    float end;
    float ylower;
    float yupper;
    float approxDist = 0.6f;
    int num;
    float dist;
    float x;
    float y;


    public Stone(float[] points) {
        super(points);
    }


    public Stone(float[] points, float x, float y, float width, float height, float scale) {
        super(points, x, y);
        this.scale = scale;
        this.height = height+0.6f;
        this.width = width+0.6f;
        this.x = getX()-0.3f;
        this.y = getY()-0.3f;
    }

    @Override
    public void setTexture(TextureRegion value) {
        super.setTexture(value);
        firstx = x * drawScale.x;
        firsty = y * drawScale.y;
        sc = height * drawScale.y / texture.getRegionHeight() * 1.1f;
        start = x * drawScale.x + texture.getRegionWidth() * sc / 2;
        end = (x + width) * drawScale.x - texture.getRegionWidth() * sc / 2;
        ylower = y * drawScale.y + texture.getRegionWidth() * sc / 3;
        yupper = y * drawScale.y + height - texture.getRegionWidth() * sc / 3;
        num = (int) Math.ceil((end - start) / (texture.getRegionWidth() * sc * approxDist));
        dist = (end - start) / num;
    }

    @Override
    public void draw(GameCanvas canvas) {
        Random rand = new Random();
        if (height <=2&& width <= 2) {
            canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                    firstx + width * drawScale.x / 2, firsty + height * drawScale.y / 2, getAngle(),
                    width * drawScale.x / texture.getRegionWidth(), height * drawScale.y / texture.getRegionHeight());
        } else if (height <= 2) {
            // scale by y
            approxDist = 0.5f;
            canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                    start, firsty + height * drawScale.y / 2, getAngle(),
                    sc, sc);
            canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                    end, firsty + height * drawScale.y / 2, getAngle(),
                    sc, sc);
            for (int i = 0; i < num; i++) {
                //  float y = ylower + rand.nextFloat()*(yupper- ylower);
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        start + (i + 1) * dist, firsty + height * drawScale.y / 2, getAngle(),
                        sc, sc);
            }
        } else {
            float sca = 1.1f;
            approxDist = 0.6f;
            float startX = x * drawScale.x + texture.getRegionWidth() / 2 *sca;
            float startY = y * drawScale.y + texture.getRegionHeight() / 2*sca;
            float endX = (x + width) * drawScale.x - texture.getRegionWidth()*sca / 2;
            float endY = (y + height) * drawScale.y - texture.getRegionHeight() *sca/ 2;
            float numX = (int) Math.ceil((endX - startX) / (texture.getRegionWidth()*sca * approxDist));
            float numY = (int) Math.ceil((endY - startY) / (texture.getRegionHeight()*sca * approxDist));
            float distX = (endX - startX) / numX;
            float distY = (endY - startY) / numY;
            canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                    startX, startY, getAngle(),
                    sca, sca);

            for (int i = 0; i < numX+1; i++) {
                for (int j = 0; j < numY+1; j++) {
                    canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                            startX + (i) * distX, startY + (j) * distY, getAngle(),
                            sca, sca);
                }
        }
       }
    }
//
//    }
}

