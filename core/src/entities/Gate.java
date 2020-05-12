package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import obstacle.PolygonObstacle;
import root.GameCanvas;
import util.FilmStrip;

public class Gate extends PolygonObstacle {
    private final FilmStrip texture;
    private float x;
    private float y;
    private PolygonShape sensorShape;
    private Fixture sensorFixture;

    public Gate(FilmStrip image, float[] points, float x, float y) {
        super(points, x, y);
        this.texture = image;
        this.setTexture(image);
    }

    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }
        Vector2 sensorCenter = new Vector2(getWidth() / 2, 1f);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 0f;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.3f, 0.1f, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;
        sensorDef.filter.maskBits = getFilterData().maskBits;
        sensorDef.filter.categoryBits = getFilterData().categoryBits;
        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData("gatesensor");
        return true;
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, 0, 0, getX() * drawScale.x - texture.getRegionWidth() * 0.7f / 2f,
                getY() * drawScale.y + texture.getRegionHeight() * 0.17f, getAngle(), 0.7f, 0.7f);
    }


    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }

}
