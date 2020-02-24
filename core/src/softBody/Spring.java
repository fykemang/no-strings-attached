package softBody;

import com.badlogic.gdx.math.Vector2;

public class Spring implements ForceGenerator {
    private float constant;
    private float damping;
    private float restlength;
    private StringObject string;
    private NPCObject objA;
    private NPCObject objB;

    public float getRestlength(){
        return restlength;
    }

    public StringObject getString() {
        return string;
    }

    public void setString(StringObject string) {
        this.string = string;
    }

    public float getConstant() {
        return this.constant;
    }

    public void setConstant(float c) {
        this.constant = c;
    }

    public float getDamping() {
        return this.damping;
    }

    public void setDamping(float d) {
        this.damping = d;
    }

    public SimObject getObjA() {
        return this.objA;
    }

    public void setObjA(NPCObject objA) {
        this.objA = objA;
    }

    public SimObject getObjB() {
        return this.objB;
    }

    public void setObjB(NPCObject objB) {
        this.objB = objB;
    }

    public Spring(float constant, float damping, StringObject str, NPCObject simObjectA, NPCObject simObjectB) {
        this.constant = constant;
        this.damping = damping;
        this.string = str;
        this.objA = simObjectA;
        this.objB = simObjectB;
        double x = Math.pow(simObjectA.currPosition.x - simObjectB.currPosition.x, 2);
        double y = Math.pow(simObjectA.currPosition.y - simObjectB.currPosition.y, 2);
        this.restlength = (float) Math.pow(x+y, 0.5);
    }

    public Spring(float constant, float damping, StringObject str, NPCObject simObjectA, NPCObject simObjectB, float restlength) {
        this.constant = constant;
        this.damping = damping;
        this.string = str;
        this.objA = simObjectA;
        this.objB = simObjectB;
        this.restlength = restlength;
    }

    private Vector2 direction = new Vector2();
    private float currLength;
    @Override
    public void applyForce(SimObject s) {
        direction.x = objA.currPosition.x - objB.currPosition.x;
        direction.y = objA.currPosition.y - objB.currPosition.y;

        if (direction != Vector2.Zero) {
            currLength = direction.len();
        }

        direction.nor();

        float fx = -constant * direction.x * (currLength - restlength), fy = -constant * direction.y * (currLength - restlength);

        float dx = -damping * direction.x * (direction.x * (objA.currVelocity.x - objB.currVelocity.x));
        float dy = -damping * direction.y * (direction.y * (objA.currVelocity.y - objB.currVelocity.y));

        objA.setResultantForce(fx + dx, fy + dy);
    }
}
