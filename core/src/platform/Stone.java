package platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import obstacle.PolygonObstacle;
import root.GameCanvas;

public class Stone extends PolygonObstacle {

    float scale;
    boolean isSliding;
    Vector2 leftSlideLim;
    Vector2 rightSlideLim;
    Vector2 slideDir;
    boolean back;


    public Stone(float[] points) {
        super(points);
    }

    public Stone(float[] points, float x, float y, float scale) {
        super(points, x, y);
        this.scale = scale;
    }

    public Stone(float[] points, float x, float y, float sc, float[] leftPos, float[] rightPos) {
        this(points, x, y, sc);
        setFriction(100f);
        isSliding = true;
        back = false;
        leftSlideLim = new Vector2(leftPos[0], leftPos[1]);
        rightSlideLim = new Vector2(rightPos[0], rightPos[1]);
        slideDir = new Vector2(leftPos[0]*drawScale.x - getX(), leftPos[1]*drawScale.y - getY());
        slideDir.nor();
        System.out.println(slideDir);
        setName("stone");
    }

    @Override
    public void update(float dt){
        super.update(dt);
        if(isSliding){

            if(getPosition().epsilonEquals(leftSlideLim, 0.01f) ||
                    getPosition().epsilonEquals(rightSlideLim, 0.01f)){
                if (!back){
                    slideDir.set(rightSlideLim.x - leftSlideLim.x, rightSlideLim.y - leftSlideLim.y);
                    slideDir.nor();

                    back = true;
                }else
                    slideDir.scl(-1f);
            }
            setLinearVelocity(slideDir);
        }
    }

    @Override
    public void draw(GameCanvas canvas) {
        float xoffset = getWidth() * drawScale.x / 20;
        float yoffset = getHeight() * drawScale.y / 20;
        float firstx = getX() * drawScale.x - xoffset;
        float lastx = getX() * drawScale.x + getWidth() * drawScale.x - texture.getRegionWidth() + xoffset;
        float firsty = getY() * drawScale.y - yoffset;
        float lasty = getY() * drawScale.y + getHeight() * drawScale.y - texture.getRegionHeight() + yoffset;
        float cloudsp = 40f;
        if (region != null) {
//            System.out.println("fst: " + firsty + "last " + lasty);
            if (getWidth() * drawScale.x < texture.getRegionWidth() * 1.5) {
                canvas.draw(texture, Color.WHITE, 0, 0, firstx, firsty, getAngle(), 1, 1);
                canvas.draw(texture, Color.WHITE, 0, 0,
                        getX() * drawScale.x + getWidth() * drawScale.x - getWidth() * drawScale.x / 2 + xoffset,
                        getY() * drawScale.y + getHeight() * drawScale.y - getHeight() * drawScale.y / 2 + yoffset
                        , getAngle(), 1, 1);
                return;
            }
            for (float x = firstx; x < lastx + cloudsp; x += cloudsp) {
                for (float y = firsty; y < lasty + cloudsp; y += cloudsp) {
                    canvas.draw(texture, Color.WHITE, 0, 0, x, y, getAngle(), 1, 1);
                }
            }
        }

    }
}

