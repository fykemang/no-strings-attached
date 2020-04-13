package entities;

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
    Vector2 rotDir;
    boolean back;
    boolean isRotating;
    Vector2 center;
    float rotatingRadians;

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
        slideDir = new Vector2(leftPos[0] - getX(), leftPos[1] - getY());
        slideDir.nor();
        setName("stone");
    }

    public Stone(float[] points, float x, float y, float sc, float[] rotatingCenter, float rotatingDegree) {
        this(points, x, y, sc);
        setFriction(100f);
        this.rotatingRadians = rotatingDegree * (float) Math.PI / 180f;
        this.center = new Vector2(rotatingCenter[0], rotatingCenter[1]);
        isRotating = true;
        rotDir = new Vector2();
    }

    @Override
    public void update(float dt){
        super.update(dt);
        if(isSliding){
            if(getPosition().epsilonEquals(leftSlideLim, 0.05f) ||
                    getPosition().epsilonEquals(rightSlideLim, 0.05f)){
                if (!back){
                    slideDir.set(rightSlideLim.x - leftSlideLim.x, rightSlideLim.y - leftSlideLim.y);
                    slideDir.nor();
                    back = true;
                }else
                    slideDir.scl(-1f);
            }
            setLinearVelocity(slideDir);
        }
//        if(isRotating){
//            float rotateBy = 10 * dt * (float)Math.PI/180f;
//            float rotatedX = (float) Math.cos(rotateBy) * (getX() - center.x) - (float) Math.sin(rotateBy) * (getY() - center.y) + center.x;
//            float rotatedY = (float) Math.sin(rotateBy) * (getX() - center.x) + (float)Math.cos(rotateBy) * (getY() - center.y) + center.y;
//            rotDir.set(rotatedX - getX(), rotatedY - getY());
//            rotDir.nor();
//            setLinearVelocity(rotDir);
//        }
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

