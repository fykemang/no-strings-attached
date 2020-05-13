package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    float width;
    float height;
    float firstx;
    float firsty;
    float sc;
    float start;
    float end;
    float ylower;
    float yupper;
    float approxDist = 0.6f;
    int num;
    float dist;
    float x;
    float y;
    String type;
    Vector2 rotBeginLim;
    Vector2 rotEndLim;
    float degree;
    float rotX, rotY;

    public void setIsSliding(boolean b) {
        isSliding = b;
    }

    public void setSlideLim(float[] left, float[] right) {
//        setFriction(10f);
        isSliding = true;
        back = false;
        leftSlideLim = new Vector2(left[0], left[1]);
        rightSlideLim = new Vector2(right[0], right[1]);
        slideDir = new Vector2(left[0] - getX(), left[1] - getY());
        slideDir.nor();
    }

    public Stone(float[] points) {
        super(points);
    }


    public Stone(float[] points, float x, float y, float width, float height, String type, float scale) {
        super(points, x, y);
        this.scale = scale;
        this.type = type;
        switch (type) {
            case "mountain":
                this.height = height + 0.6f;
                this.width = width + 0.6f;
                this.x = getX() - 0.3f;
                this.y = getY() - 0.3f;
                break;
            default:
                this.height = height;
                this.width = width;
                this.x = getX();
                this.y = getY();
        }

    }

    @Override
    public void setTexture(TextureRegion value) {
        super.setTexture(value);
        firstx = x * drawScale.x;
        firsty = y * drawScale.y;
        sc = height * drawScale.y / texture.getRegionHeight() * 1.1f;
        start = x * drawScale.x + texture.getRegionWidth() * sc / 2;
        end = (x + width) * drawScale.x - texture.getRegionWidth() * sc / 2;
        ylower = y * drawScale.y + texture.getRegionWidth() * sc / 3;
        yupper = y * drawScale.y + height - texture.getRegionWidth() * sc / 3;
        num = (int) Math.ceil((end - start) / (texture.getRegionWidth() * sc * approxDist));
        dist = (end - start) / num;
    }

    public Stone(float[] points, float x, float y, float width, float height, String type, float sc, float[] leftPos, float[] rightPos) {
        this(points, x, y, width, height, type, sc);
        setFriction(10f);
        isSliding = true;
        setSlideLim(leftPos, rightPos);
    }

    public Stone(float[] points, float x, float y, float width, float height, String type, float sc, float[] rotatingCenter, float rotatingDegree) {
        this(points, x, y, width, height, type, sc);
        setFriction(10f);
        this.rotatingRadians = rotatingDegree * (float) Math.PI / 180f;
        this.center = new Vector2(rotatingCenter[0], rotatingCenter[1]);
        isRotating = true;
        rotDir = new Vector2();
        degree = rotatingDegree * (float) Math.PI / 180f;
    }

    public void setRotationData(Vector2 center, Vector2 pos) {
        this.center = center;
        rotX = pos.x;
        rotY = pos.y;
        float rotatedX = (float) Math.cos(degree) * (pos.x - center.x) - (float) Math.sin(degree) * (pos.y - center.y) + center.x;
        float rotatedY = (float) Math.sin(degree) * (pos.x - center.x) + (float) Math.cos(degree) * (pos.y - center.y) + center.y;

        rotBeginLim = new Vector2(rotX, rotY);
        rotEndLim = new Vector2(rotatedX, rotatedY);
    }

    public void setPosData(Vector2 pos) {
        rotX = pos.x;
        rotY = pos.y;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isSliding) {

            if (getPosition().epsilonEquals(leftSlideLim, 0.05f) ||
                    getPosition().epsilonEquals(rightSlideLim, 0.05f)) {

                if (!back) {
                    slideDir.set(rightSlideLim.x - leftSlideLim.x, rightSlideLim.y - leftSlideLim.y);
                    slideDir.nor();
                    back = true;
                } else
                    slideDir.scl(-1f);
            }

            setLinearVelocity(slideDir);
        }
        if (isRotating) {
            float rotateBy = 10 * dt * (float) Math.PI / 180f;
            float rotatedX = 0f, rotatedY = 0f;
            if (!back) {//ccw
                rotatedX = (float) Math.cos(rotateBy) * (rotX - center.x) - (float) Math.sin(rotateBy) * (rotY - center.y) + center.x;
                rotatedY = (float) Math.sin(rotateBy) * (rotX - center.x) + (float) Math.cos(rotateBy) * (rotY - center.y) + center.y;
            } else {
                rotatedX = (float) Math.sin(rotateBy) * (rotY - center.y) + (float) Math.cos(rotateBy) * (rotX - center.x) + center.x;
                rotatedY = (float) Math.cos(rotateBy) * (rotY - center.y) - (float) Math.sin(rotateBy) * (rotX - center.x) + center.y;
            }
            rotDir.set(rotatedX - rotX, rotatedY - rotY);
            rotDir.nor();
            setLinearVelocity(rotDir);
            if (rotEndLim.epsilonEquals(rotX, rotY, 0.3f)) {
                back = true;
            } else if (rotBeginLim.epsilonEquals(rotX, rotY, 0.3f)) {
                back = false;

            }
        }
    }

    @Override
    public void draw(GameCanvas canvas) {

        if (type.equals("mountain") || type.equals("village")) {

            if (height <= 2 && width <= 2 && type.equals("village")) {
                float firstx = getX() * drawScale.x;
                float firsty = getY() * drawScale.y;
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        firstx + width * drawScale.x / 2, firsty + height * drawScale.y / 2, getAngle(),
                        width * drawScale.x / texture.getRegionWidth(), height * drawScale.y / texture.getRegionHeight());
            } else if (height <= 2 && width <= 2) {
                firstx = getX() * drawScale.x;
                firsty = getY() * drawScale.y;
                this.x = getX() - 0.3f;
                this.y = getY() - 0.3f;
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        firstx + width * drawScale.x / 2, firsty + height * drawScale.y / 2, getAngle(),
                        width * drawScale.x / texture.getRegionWidth(), height * drawScale.y / texture.getRegionHeight());

            } else if (height <= 2) {
                this.x = getX() - 0.3f;
                this.y = getY() - 0.3f;
                firstx = getX() * drawScale.x;
                firsty = getY() * drawScale.y;
                // scale by y
                approxDist = 0.5f;
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        start, firsty + height * drawScale.y / 2, getAngle(),
                        sc, sc);
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        end, firsty + height * drawScale.y / 2, getAngle(),
                        sc, sc);
                for (int i = 0; i < num; i++) {
                    //  float y = ylower + rand.nextFloat()*(yupper- ylower);
                    canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                            start + (i + 1) * dist, firsty + height * drawScale.y / 2, getAngle(),
                            sc, sc);
                }
            } else if (height > 2 * width) {
                this.x = getX();
                this.y = getY();
                float scx = width * drawScale.x / texture.getRegionWidth();
                float num = (int) (height * drawScale.y / (texture.getRegionHeight() * scx));
                float dist = height * drawScale.y / num;
                float scy = dist / (texture.getRegionHeight() * scx);
                num = num == 0 ? 1 : num;

                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, x * drawScale.x,
                            y * drawScale.y + i * dist, getAngle(), scx, scx * scy);

            } else {

                this.x = getX() - 0.3f;
                this.y = getY() - 0.3f;
                float sca = 1.1f;
                approxDist = 0.6f;
                float startX = getX() * drawScale.x + texture.getRegionWidth() / 2 * sca;
                float startY = getY() * drawScale.y + texture.getRegionHeight() / 2 * sca;
                float endX = (getX() + width) * drawScale.x - texture.getRegionWidth() * sca / 2;
                float endY = (getY() + height) * drawScale.y - texture.getRegionHeight() * sca / 2;
                float numX = (int) Math.ceil((endX - startX) / (texture.getRegionWidth() * sca * approxDist));
                float numY = (int) Math.ceil((endY - startY) / (texture.getRegionHeight() * sca * approxDist));
                float distX = (endX - startX) / numX;
                float distY = (endY - startY) / numY;
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        startX, startY, getAngle(),
                        sca, sca);

                for (int i = 0; i < numX + 1; i++) {
                    for (int j = 0; j < numY + 1; j++) {
                        canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                                startX + (i) * distX, startY + (j) * distY, getAngle(),
                                sca, sca);
                    }
                }
            }
        } else {
            if (height <= 2 && width <= 2) {
                float firstx = getX() * drawScale.x;
                float firsty = getY() * drawScale.y;
                canvas.draw(texture, Color.WHITE, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2,
                        firstx + width * drawScale.x / 2, firsty + height * drawScale.y / 2, getAngle(),
                        width * drawScale.x / texture.getRegionWidth(), height * drawScale.y / texture.getRegionHeight());
            } else if (height > 2 * width) {
                this.x = getX();
                this.y = getY();
                float scx = width * drawScale.x / texture.getRegionWidth();
                float num = (int) (height * drawScale.y / (texture.getRegionHeight() * scx));
                float dist = height * drawScale.y / num;
                float scy = dist / (texture.getRegionHeight() * scx);
                num = num == 0 ? 1 : num;

                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, x * drawScale.x,
                            y * drawScale.y + i * dist, getAngle(), scx, scx * scy);

            } else {
                this.x = getX();
                this.y = getY();


                float scy = height * drawScale.y / texture.getRegionHeight();
                float num = (int) (width * drawScale.x / (texture.getRegionWidth() * scy - 5));
                float dist = width * drawScale.x / num;
                float scx = dist / (texture.getRegionWidth() * scy);
                num = num == 0 ? 1 : num;

                for (int i = 0; i < num; i++)
                    canvas.draw(texture, Color.WHITE, 0, 0, x * drawScale.x + i * dist,
                            y * drawScale.y, getAngle(), scx * scy, scy);
            }

        }
    }
}