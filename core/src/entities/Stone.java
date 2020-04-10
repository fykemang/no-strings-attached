package entities;

import com.badlogic.gdx.graphics.Color;
import obstacle.PolygonObstacle;
import root.GameCanvas;

public class Stone extends PolygonObstacle {

    float scale;


    public Stone(float[] points) {
        super(points);
    }

    public Stone(float[] points, float x, float y, float scale) {
        super(points, x, y);
        this.scale = scale;
    }

    @Override
    public void draw(GameCanvas canvas) {
        float xoffset = getWidth() * drawScale.x / 20;
        float yoffset = getHeight() * drawScale.y / 20;
        float firstx = getX() * drawScale.x - xoffset;
        float lastx = getX() * drawScale.x + getWidth() * drawScale.x - texture.getRegionWidth() + xoffset;
        float firsty = getY() * drawScale.y - yoffset;
        float lasty = getY() * drawScale.y + getHeight() * drawScale.y - texture.getRegionHeight() + yoffset;
        float cloudsp = 40f;
        if (region != null) {
//            System.out.println("fst: " + firsty + "last " + lasty);
            if (getWidth() * drawScale.x < texture.getRegionWidth() * 1.5) {
                canvas.draw(texture, Color.WHITE, 0, 0, firstx, firsty, getAngle(), 1, 1);
                canvas.draw(texture, Color.WHITE, 0, 0,
                        getX() * drawScale.x + getWidth() * drawScale.x - getWidth() * drawScale.x / 2 + xoffset,
                        getY() * drawScale.y + getHeight() * drawScale.y - getHeight() * drawScale.y / 2 + yoffset
                        , getAngle(), 1, 1);
                return;
            }
            for (float x = firstx; x < lastx + cloudsp; x += cloudsp) {
                for (float y = firsty; y < lasty + cloudsp; y += cloudsp) {
                    canvas.draw(texture, Color.WHITE, 0, 0, x, y, getAngle(), 1, 1);
                }
            }
        }

    }
}

