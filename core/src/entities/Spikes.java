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
        switch (direction) {
            case "up":
                num = (int) (getWidth() * drawScale.x / texture.getRegionWidth());
                dist = getWidth() * drawScale.x / num;
                sc = dist / texture.getRegionWidth();
                num = num == 0 ? 1 : num;
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x + i * dist,
                            getY() * drawScale.y, getAngle(), sc, .6f);
                break;
            case "down":
                num = (int) (getWidth() * drawScale.x / texture.getRegionWidth());
                num = num == 0 ? 1 : num;
                dist = getWidth() * drawScale.x / num;
                sc = dist / texture.getRegionWidth();
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x + i * dist,
                            getY() * drawScale.y, getAngle(), -sc, -.6f);
                break;
            case "right":
                num = (int) (getHeight() * drawScale.y / texture.getRegionHeight());
                num = num == 0 ? 1 : num;
                dist = getHeight() * drawScale.y / num;
                sc = dist / texture.getRegionHeight();
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x,
                            getY() * drawScale.y + i * dist, getAngle(), .6f, sc);
                break;
            case "left":
                num = (int) (getHeight() * drawScale.y / texture.getRegionHeight());
                num = num == 0 ? 1 : num;
                dist = getHeight() * drawScale.y / num;
                sc = dist / texture.getRegionHeight();
                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x,
                            getY() * drawScale.y + i * dist, getAngle(), -.6f, -sc);
                break;
        }

    }
}

