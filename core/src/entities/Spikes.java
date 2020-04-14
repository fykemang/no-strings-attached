package entities;

import com.badlogic.gdx.graphics.Color;
import obstacle.PolygonObstacle;
import root.GameCanvas;

public class Spikes extends PolygonObstacle {

    float scale;

    public Spikes(float[] points, float x, float y, float scale) {
        super(points, x, y);
        this.scale = scale;
    }


    @Override
    public void draw(GameCanvas canvas) {
        int num = (int) (getWidth() * drawScale.x / texture.getRegionWidth());
        num = num == 0 ? 1 : num;
        for (int i = 0; i < num; i++)
            canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x + i * texture.getRegionWidth(),
                    getY() * drawScale.y - texture.getRegionHeight() / 2, getAngle(), 1, 1);
    }
}

