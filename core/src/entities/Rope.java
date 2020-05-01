package entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import obstacle.ComplexObstacle;
import obstacle.WheelObstacle;

public abstract class Rope extends ComplexObstacle {
    protected final int MAX_DRAW_POINTS = 100;
    // Dimensions of the rope
    protected Vector2 dimension;
    protected CatmullRomSpline<Vector2> splineCurve;
    protected float density;
    protected Vector2[] contPoints;
    protected Vector2[] points;
    protected float length;
    protected float spacing;
    protected float lWidth;
    protected int id;
    protected Color tint = new Color(0.67f, 0f, 0f, 1f);

    public Rope() {
        points = new Vector2[MAX_DRAW_POINTS];
    }

    public Rope(float x0, float y0, float x1, float y1, String ropeName, float density, float length, float lWidth, int id) {
        super(x0, y0);
        this.dimension = new Vector2(x1 - x0, y1 - y0);
        this.density = density;
        this.points = new Vector2[MAX_DRAW_POINTS];
        this.length = length;
        this.lWidth = lWidth;
        this.id = id;
        setName(ropeName);
        initializeSegments();

    }

    abstract void initializeSegments();

    abstract void extractContPoints();

    abstract Body getLastLink();

    void setCurrentSplineCurve() {
        extractContPoints();
        if (splineCurve == null)
            splineCurve = new CatmullRomSpline<>(contPoints, false);
        else
            splineCurve.set(contPoints, false);
    }

    boolean isCloser(WheelObstacle a, WheelObstacle b, Vector2 pos, float h) {
        return a.getPosition().dst2(pos.x, pos.y - h / 2) <= b.getPosition().dst2(pos.x, pos.y - h / 2);
    }

    public void setStart(Vector2 start, boolean scaled) {
        if (!scaled) {
            contPoints[0].set(start.x * drawScale.x, start.y * drawScale.y);
            contPoints[1].set(start.x * drawScale.x, start.y * drawScale.y);
        } else {
            contPoints[0].set(start.x, start.y);
            contPoints[1].set(start.x, start.y);
        }
    }

    public void setEnd(Vector2 end, boolean scaled) {
        if (!scaled) {
            contPoints[contPoints.length - 1].set(end.x * drawScale.x, end.y * drawScale.y);
            contPoints[contPoints.length - 2].set(end.x * drawScale.x, end.y * drawScale.y);
        } else {
            contPoints[contPoints.length - 1].set(end.x, end.y);
            contPoints[contPoints.length - 2].set(end.x, end.y);
        }
    }

    public float getLength() {
        return length;
    }
}
