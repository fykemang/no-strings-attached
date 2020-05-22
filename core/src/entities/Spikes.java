package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import obstacle.PolygonObstacle;
import root.GDXRoot;
import root.GameCanvas;

public class Spikes extends PolygonObstacle {

    private float scale;
    private String direction;
    private PolygonShape sensorShape;
    private Fixture sensorFixture;
    private final String sensorName;
    private static final float SENSOR_HEIGHT = 0.05f;

    public String getSensorName() {
        return sensorName;
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }

    public Spikes(float[] points, float x, float y, String direction, float scale) {
        super(points, x, y);
        this.scale = scale;
        this.direction = direction;
        this.sensorName = "spike";
    }

    public boolean activatePhysics(World world) {
        if (!super.activatePhysics(world)) {
            return false;
        }
        Vector2 sensorCenter;
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        float f = 1.2f * drawScale.x / texture.getRegionWidth();
        int n = (int) (getHeight() * drawScale.y / (texture.getRegionHeight() * f));
        n = n == 0 ? 1 : n;
        float hx = getWidth()/2f;
        float addon = 0;
        if (GDXRoot.currentLevel >= 6 && GDXRoot.currentLevel <= 9) {
            hx += 0.2f;
            addon += 0.4f;
        }
        else if (GDXRoot.currentLevel >= 10 && GDXRoot.currentLevel <= 13) {
            hx -= 0.05f;
            addon -= 0.1f;
        }
        else if (GDXRoot.currentLevel >= 14) {
            hx += 0.05f;
            addon += 0.1f;
        }
        switch (direction) {
            case "up":
                sensorCenter = new Vector2(getWidth() / 2f, 0.8f);
                sensorShape.setAsBox(getWidth() / 2f, 0.25f, sensorCenter, 0.0f);
                break;
            case "left":
                sensorCenter = new Vector2(0.2f, n * (addon+getWidth()) / 2f);
                sensorShape.setAsBox(0.25f, n * hx, sensorCenter, 0.0f);
                break;
            case "down":
                sensorCenter = new Vector2(getWidth() / 2f, 0.2f);
                sensorShape.setAsBox(getWidth() / 2f, 0.25f, sensorCenter, 0.0f);
                break;
            case "right":
                sensorCenter = new Vector2(getWidth() - 0.2f, n * (addon+getWidth()) / 2f);
                sensorShape.setAsBox(0.25f, n * hx, sensorCenter, 0.0f);
                break;
        }
        sensorDef.shape = sensorShape;
        sensorDef.filter.maskBits = getFilterData().maskBits;
        sensorDef.filter.categoryBits = getFilterData().categoryBits;
        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());
        return true;
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

