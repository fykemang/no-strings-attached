package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import obstacle.CapsuleObstacle;
import root.GameCanvas;

public class Item extends CapsuleObstacle {
    // Amount to shrink item by for fixture
    private static final float ITEM_VSHRINK = 0.01f;
    private static final float ITEM_HSHRINK = 0.01f;
    private static final float ITEM_SENSOR_SCALE = 0.09f;
    private String sensorName;
    private CircleShape sensorShape;
    private FixtureDef sensorDef;
    private Fixture sensorFixture;
    private int id;

    public enum ItemState {COLLECTED, NOT_COLLECTED}

    private Item.ItemState state;

    public Item(float x, float y, float width, float height, int id) {
        super(x, y, width * ITEM_HSHRINK, height * ITEM_VSHRINK);
        this.setBodyType(BodyDef.BodyType.StaticBody);
        this.setPosition(x + this.getWidth() / 2 + 0.15f, y + this.getHeight() / 2);
        this.sensorName = "item_sensor";
        this.id = id;
        this.state = ItemState.NOT_COLLECTED;
        setName("item" + id);
    }

    public void setState(boolean isCollected) {
        if (isCollected) {
            this.state = ItemState.COLLECTED;
        }
    }

    public boolean getState() {
        return this.state == ItemState.COLLECTED;
    }

    public void draw(GameCanvas canvas) {
        if (this.state != ItemState.COLLECTED) {
            canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x,
                    getY() * drawScale.y, getAngle(), 0.2f, 0.2f);
        }
        else {
            canvas.draw(texture, Color.WHITE, origin.x, origin.y, origin.x,
                    origin.y, getAngle(), 0.2f, 0.2f);
        }
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
        sensorShape = new CircleShape();
        sensorShape.setPosition(sensorCenter);
        sensorShape.setRadius(ITEM_SENSOR_SCALE * getTexture().getRegionWidth() / drawScale.x);
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
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), drawScale.x, drawScale.y);
    }
}
