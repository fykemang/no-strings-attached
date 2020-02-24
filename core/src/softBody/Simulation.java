package softBody;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;

public class Simulation {
    protected ArrayList<SimObject> simObjects = new ArrayList<SimObject>();
    protected ArrayList<ForceGenerator> globalForceGenerators = new ArrayList<ForceGenerator>();
    protected ArrayList<Spring> springs = new ArrayList<Spring>();

    public ArrayList<ForceGenerator> getGlobalForceGenerators() {
        return globalForceGenerators;
    }

    public ArrayList<Spring> getSprings() {
        return springs;
    }

    public ArrayList<SimObject> getSimObjects() {
        return simObjects;
    }

    public void setGlobalForceGenerators(ArrayList<ForceGenerator> globalForceGenerators) {
        this.globalForceGenerators = globalForceGenerators;
    }

    public void setSimObjects(ArrayList<SimObject> simObjects) {
        this.simObjects = simObjects;
    }

    public void setSprings(ArrayList<Spring> springs) {
        this.springs = springs;
    }

    public void populate(Vector2 person1, Vector2 person2) {
        Vector2 pos = new Vector2((person1.x + person2.x) /2, (person1.y + person2.y) / 2);
        SimObject o1 = new SimModel(1, SimObjectType.ACTIVE, person1);
        SimObject o2 = new SimModel(1, SimObjectType.ACTIVE, person2);

        this.simObjects.add(o1);
        this.simObjects.add(o2);

        addSpring((float) 0.5, (float) 0.5, o1, o2);
    }

    public void addSpring(float constant, float damping, SimObject objA, SimObject objB) {
        Spring spr = new Spring(constant, damping, objA, objB);
        this.springs.add(spr);
    }

    public void addGlobalForceGenerator(ForceGenerator f) {
        globalForceGenerators.add(f);
    }

    public void update() {
        for (Spring spr : getSprings()) {
            spr.applyForce(null);
        }

        for (SimObject s : getSimObjects()) {
            if (s.getSimObjectType() == SimObjectType.ACTIVE) {
                for (ForceGenerator f : globalForceGenerators) {
                    f.applyForce(s);
                }
            }
        }
    }
}
