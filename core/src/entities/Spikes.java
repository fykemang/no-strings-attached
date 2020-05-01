package entities;

import com.badlogic.gdx.graphics.Color;
import obstacle.PolygonObstacle;
import root.GameCanvas;

public class Spikes extends PolygonObstacle {

    float scale;
    String direction;

    public Spikes(float[] points, float x, float y, String direction, float scale) {
        super(points, x, y);
        this.scale = scale;
        this.direction = direction;
    }


    @Override
    public void draw(GameCanvas canvas) {
        int num;
        float dist;
        float sc;
        float scy;
        float scx;
        switch (direction) {
            case "up":

                scy = 1.2f * drawScale.y / texture.getRegionHeight();
                num = (int) (getWidth() * drawScale.x / (texture.getRegionWidth() * scy));
                dist = getWidth() * drawScale.x / num;
                scx = dist / (texture.getRegionWidth() * scy);
                num = num == 0 ? 1 : num;
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x + i * dist,
                            getY() * drawScale.y, getAngle(), scx * scy, scy);
                break;
            case "down":
                scy = 1.2f * drawScale.y / texture.getRegionHeight();
                num = (int) (getWidth() * drawScale.x / (texture.getRegionWidth() * scy));
                dist = getWidth() * drawScale.x / num;
                scx = dist / (texture.getRegionWidth() * scy);
                num = num == 0 ? 1 : num;
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x + i * dist,
                            (getY() + 1) * drawScale.y, getAngle(), scx * scy, -scy);
                break;
            case "right":
                scx = 1.2f * drawScale.x / texture.getRegionWidth();
                num = (int) (getHeight() * drawScale.y / (texture.getRegionHeight() * scx));
                dist = getHeight() * drawScale.y / num;
                scy = dist / (texture.getRegionHeight() * scx);
                num = num == 0 ? 1 : num;
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, (getX()) * drawScale.x,
                            getY() * drawScale.y + i * dist, getAngle(), scx, scy * scx);
                break;
            case "left":
                scx = 1.2f * drawScale.x / texture.getRegionWidth();
                num = (int) (getHeight() * drawScale.y / (texture.getRegionHeight() * scx));
                dist = getHeight() * drawScale.y / num;
                scy = dist / (texture.getRegionHeight() * scx);
                num = num == 0 ? 1 : num;
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, (getX() + 1) * drawScale.x,
                            getY() * drawScale.y + i * dist, getAngle(), -scx, scy * scx);
                break;
        }

    }
}

