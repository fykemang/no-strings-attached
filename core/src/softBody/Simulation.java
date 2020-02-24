package softBody;

import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import softBody.Constants;

public class Simulation {
    protected ArrayList<NPCObject> npcObjects = new ArrayList<NPCObject>();
    protected ArrayList<StringObject> stringObjects = new ArrayList<StringObject>();
    protected ArrayList<ForceGenerator> globalForceGenerators = new ArrayList<ForceGenerator>();
    protected ArrayList<Spring> springs = new ArrayList<Spring>();

    public Simulation(){
        Gravity g = new Gravity();
        Drag d = new Drag();
        globalForceGenerators.add(g);
        globalForceGenerators.add(d);
    }
    public void setSprings(ArrayList<Spring> springs) {
        this.springs = springs;
    }

    public void setGlobalForceGenerators(ArrayList<ForceGenerator> globalForceGenerators) {
        this.globalForceGenerators = globalForceGenerators;
    }


    public ArrayList<ForceGenerator> getGlobalForceGenerators() {
        return globalForceGenerators;
    }

    public ArrayList<NPCObject> getNpcObjects() {
        return npcObjects;
    }

    public ArrayList<Spring> getSprings() {
        return springs;
    }

    public ArrayList<StringObject> getStringObjects() {
        return stringObjects;
    }

    public void setNpcObjects(ArrayList<NPCObject> npcObjects) {
        this.npcObjects = npcObjects;
    }

    public void setStringObjects(ArrayList<StringObject> stringObjects) {
        this.stringObjects = stringObjects;
    }

    public void populate(Vector2 person1, Vector2 person2) {
        Vector2 pos = new Vector2((person1.x + person2.x) /2, (person1.y + person2.y) / 2);
        StringObject o1 = new StringObject(1, SimObjectType.ACTIVE, pos);
        NPCObject o2 = new NPCObject(1000, SimObjectType.ACTIVE, person1, o1);
        NPCObject o3 = new NPCObject(1000, SimObjectType.ACTIVE, person2, o1);

        this.npcObjects.add(o2);
        this.npcObjects.add(o3);
        this.stringObjects.add(o1);

        addSpring(8f, 0.1f, o1, o2, o3);
    }

    public void addSpring(float constant, float damping, StringObject str, NPCObject objA, NPCObject objB) {
        Spring spr = new Spring(constant, damping, str, objA, objB);
        this.springs.add(spr);
        str.setSpringForce(spr);
    }

    public void addGlobalForceGenerator(ForceGenerator f) {
        globalForceGenerators.add(f);
    }

    private void integrate(SimObject s, float dt) {
        if (s.getSimObjectType() == SimObjectType.ACTIVE) {
            for (ForceGenerator f : globalForceGenerators) {
                f.applyForce(s);
            }
            Vector2 force = s.getResultantForce();
            float ax = force.x / s.getMass(), ay = force.y / s.getMass();
            s.setAcceleration(ax, ay);
            s.update(dt);
        }
    }

    private void reset(SimObject s) {
        if (s.getSimObjectType() == SimObjectType.ACTIVE) {
            s.resetForces();
        }
    }

    public void update(float dt) {
        for (Spring spr : getSprings()) {
            spr.applyForce(spr.getString());
        }

        for (SimObject s : getNpcObjects()) {
            integrate(s, dt);
        }
        for (SimObject s : getStringObjects()) {
            integrate(s, dt);
        }
        for (SimObject s : getNpcObjects()) {
            reset(s);
        }
        for (SimObject s : getStringObjects()) {
            reset(s);
        }
    }
}
