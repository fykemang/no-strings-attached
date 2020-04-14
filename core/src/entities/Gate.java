package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import obstacle.ComplexObstacle;
import obstacle.PolygonObstacle;
import root.GameCanvas;

public class Gate extends PolygonObstacle {
    private TextureRegion texture;
    private float x;
    private float y;
    private PolygonShape sensorShape;
    private Fixture sensorFixture;


    public Gate(TextureRegion image, float[] points, float x, float y){
        super(points, x, y);
        this.texture = image;
        this.setTexture(image);
    }

    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }
        Vector2 sensorCenter = new Vector2(0, 0);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 0f;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(getWidth()/2, getHeight()/2, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;
        sensorDef.filter.maskBits = getFilterData().maskBits;
        sensorDef.filter.categoryBits = getFilterData().categoryBits;
        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData("gatesensor");
        return true;
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, 0, 0, getX()*drawScale.x ,
                getY()*drawScale.y, getAngle(), 1, 1);
    }


    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
    }


}
