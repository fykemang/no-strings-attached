package softBody;

public class Drag implements ForceGenerator {
    private float drag;

    public float getDrag() {
        return this.drag;
    }

    public void setDrag(float d) {
        this.drag = d;
    }

    public Drag(float d) {
        this.drag = d;
    }

    @Override
    public void applyForce(SimObject s) {
        s.getResultantForce().mulAdd(s.getCurrVelocity(), -this.drag);
    }
}
