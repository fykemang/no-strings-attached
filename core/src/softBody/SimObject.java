package softBody;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import obstacle.Obstacle;
import obstacle.SimpleObstacle;
import root.GameCanvas;

enum SimObjectType {PASSIVE, ACTIVE};

public abstract class SimObject {
    private float mass;
    private SimObjectType simObjectType;
    protected Vector2 currPosition;
    protected Vector2 prevPosition;
    protected Vector2 currVelocity;
    protected Vector2 resultantForce;
    protected Vector2 acceleration;
    protected TextureRegion texture;
    private Vector2 drawScale;
    private Vector2 origin;

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    public void setAcceleration(float x, float y) {
        this.acceleration.x = x;
        this.acceleration.y = y;
    }
    public void setVelocity(float x, float y) {
        this.currVelocity.x = x;
        this.currVelocity.y = y;
    }

    public Vector2 getResultantForce() {
        return this.resultantForce;
    }

    public void setResultantForce(Vector2 v) {
        this.resultantForce = v;
    }

    public float getMass() {
        return this.mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public SimObjectType getSimObjectType() {
        return this.simObjectType;
    }

    public void setSimObjectType(SimObjectType s) {
        this.simObjectType = s;
    }

    public Vector2 getCurrPosition() {
        return this.currPosition;
    }

    public void setCurrPosition(Vector2 pos) {
        this.currPosition = pos;
    }

    public void setResultantForce(float x, float y) {
        this.resultantForce.x = x;
        this.resultantForce.y = y;
    }
    public void setCurrPosition(float posX, float posY) {
        this.currPosition.x = posX;
        this.currPosition.y = posY;
    }

    public Vector2 getPrevPosition() {
        return this.prevPosition;
    }

    public void setPrevPosition(Vector2 pos) {
        this.prevPosition = pos;
    }

    public Vector2 getCurrVelocity() {
        return this.currVelocity;
    }

    public void setCurrVelocity(Vector2 v) {
        this.currVelocity = v;
    }

    public void resetForces() {
        this.resultantForce = Vector2.Zero;
    }

    public SimObject(float mass, SimObjectType s) {
        this.mass = mass;
        this.simObjectType = s;
        this.currPosition = new Vector2();
        this.prevPosition = currPosition;
        this.currVelocity = new Vector2();
        this.origin = new Vector2();
        this.resultantForce = new Vector2();
        this.acceleration = new Vector2();
    }

    public SimObject(float mass, SimObjectType s, Vector2 currPosition) {
        this.mass = mass;
        this.simObjectType = s;
        this.currPosition = currPosition;
        this.prevPosition = currPosition;
        this.currVelocity = new Vector2();
        this.origin = new Vector2();
        this.resultantForce = new Vector2();
        this.acceleration = new Vector2();
    }

    public abstract void update(float dt) ;

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
        origin.set(texture.getRegionWidth() / 2.0f, texture.getRegionHeight() / 2.0f);
    }

    public void setDrawScale(Vector2 scale) {
        drawScale = scale;
    }


    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        System.out.print("draw");
        if (texture != null) {
            canvas.draw(texture, Color.WHITE, origin.x, origin.y, currPosition.x * drawScale.x, currPosition.y * drawScale.x, 0f, 1, 1);
        }
    }
}
