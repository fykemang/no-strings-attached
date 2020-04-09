package platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import obstacle.CapsuleObstacle;
import root.GameCanvas;

public class Item extends CapsuleObstacle {
    // Amount to shrink item by for fixture
    private static final float ITEM_VSHRINK = 0.01f;
    private static final float ITEM_HSHRINK = 0.01f;
    private String sensorName;
    private PolygonShape sensorShape;
    private FixtureDef sensorDef;
    private Fixture sensorFixture;
    private int id;

    public enum ItemState {COLLECTED, NOTCOLLECTED}

    private Item.ItemState state;

    public Item(float x, float y, float width, float height, int id) {
        super(x, y, width * ITEM_HSHRINK, height * ITEM_VSHRINK);
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.setPosition(x + this.getWidth() / 2 + 0.15f, y + this.getHeight() / 2);
        this.sensorName = "item_sensor";
        this.id = id;
        setName("item" + id);
    }

    public int getId() {
        return id;
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

    private String getSensorName() {
        return sensorName;
    }

    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }
}
