package platform;

import com.badlogic.gdx.math.Vector2;

public class NpcPerson extends Person{

    private Vector2 leftAttachPt;
    private Vector2 rightAttachPt;
    private NpcPerson couple;
    /**
     * Creates a new dude avatar at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x          Initial x position of the avatar center
     * @param y          Initial y position of the avatar center
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param name
     * @param sensorName
     */
    public NpcPerson(float x, float y, float width, float height, String name, String sensorName) {
        super(x, y, width, height, name, sensorName);
        this.leftAttachPt = new Vector2(getX() + getWidth() / 1.5f + 0.1f, getY() + 0.1f);
        this.rightAttachPt = new Vector2(getX() - getWidth() / 1.5f - 0.1f, getY() + 0.1f);

    }

    public void setCouple(NpcPerson couple){
        this.couple = couple;
    }

    @Override
    public void update(float dt){
        super.update(dt);
        leftAttachPt.set(getX() + getWidth() / 1.5f + 0.1f, getY() + 0.1f);
        rightAttachPt.set(getX() - getWidth() / 1.5f - 0.1f, getY() + 0.1f);
    }

    public Vector2 getCloserAttachPoint(){
        Vector2 cpPos = couple.getPosition();
        if(leftAttachPt.dst2(cpPos) < rightAttachPt.dst2(cpPos)){
            return leftAttachPt;
        }else {
            return rightAttachPt;
        }

    }
}
