package platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import obstacle.CapsuleObstacle;
import root.GameCanvas;

public class Item extends CapsuleObstacle {
    private TextureRegion texture;
    private String sensorName;
    private PolygonShape sensorShape;
    private FixtureDef sensorDef;
    private Fixture sensorFixture;


    public Item(float x, float y, TextureRegion t, Vector2 drawScale, int id) {
//        super(x, y, t.getRegionWidth() * 0.2f, t.getRegionHeight() * 0.2f);
        super(x,y);
        this.setWidth(0.2f*this.getWidth());
        this.setHeight(0.2f*this.getHeight());
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.setPosition(x + this.getWidth() / 2 + 0.15f, y + this.getHeight() / 2);
        this.setDrawScale(drawScale);
        this.setTexture(t);
        this.texture = t;
        this.sensorName = sensorName;
        setName("couples" + id);
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x,
                getY() * drawScale.y, getAngle(), 0.2f, 0.2f);
    }

    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }
        
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        sensorDef = new FixtureDef();
        sensorDef.density = this.getDensity();
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.2f * getWidth() / 2.0f, 0.0f, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;
    }

    private Object getSensorName() {
        return sensorName;
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }
}
