/*
 * PolygonObstacle.java
 *
 * Sometimes boxes and shapes do not cut it.  In that case, you have to bring
 * out the polygons.  This class is substantially more complex than the other
 * physics objects, but it will allow you to draw arbitrary shapes.
 *
 * Be careful with modifying this file.  Even if you have had computer
 * graphics, there are A LOT of subtleties in handling the physics of
 * polygons.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ShortArray;
import root.GameCanvas;


/**
 * Arbitrary polygonal-shaped model to support collisions.
 * <p>
 * The polygon coordinates are all in local space, relative to the object
 * center.  In addition the texture coordinates are computed automatically
 * from the texture size, using the same policy as PolygonSpriteBatch.
 */
public class PolygonObstacle extends SimpleObstacle {
    /**
     * An earclipping triangular to make sure we work with convex shapes
     */
    private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();

    /**
     * Shape information for this physics object
     */
    protected PolygonShape[] shapes;
    /**
     * Texture information for this object
     */
    protected PolygonRegion region;

    /**
     * The polygon vertices, scaled for drawing
     */
    private float[] scaled;
    /**
     * The triangle indices, used for drawing
     */
    private short[] tridx;

    /**
     * A cache value for the fixtures (for resizing)
     */
    private Fixture[] geoms;
    /**
     * The polygon bounding box (for resizing purposes)
     */
    private Vector2 dimension;
    /**
     * A cache value for when the user wants to access the dimensions
     */
    private Vector2 sizeCache;
    /**
     * Cache of the polygon vertices (for resizing)
     */
    private float[] vertices;

    /**
     * Returns the dimensions of this box
     * <p>
     * This method does NOT return a reference to the dimension vector. Changes to this
     * vector will not affect the shape.  However, it returns the same vector each time
     * its is called, and so cannot be used as an allocator.
     *
     * @return the dimensions of this box
     */
    public Vector2 getDimension() {
        return sizeCache.set(dimension);
    }

    /**
     * Sets the dimensions of this box
     * <p>
     * This method does not keep a reference to the parameter.
     *
     * @param value the dimensions of this box
     */
    public void setDimension(Vector2 value) {
        setDimension(value.x, value.y);
    }

    /**
     * Sets the dimensions of this box
     *
     * @param width  The width of this box
     * @param height The height of this box
     */
    public void setDimension(float width, float height) {
        resize(width, height);
        markDirty(true);
    }

    /**
     * Returns the box width
     *
     * @return the box width
     */
    public float getWidth() {
        return dimension.x;
    }

    /**
     * Sets the box width
     *
     * @param value the box width
     */
    public void setWidth(float value) {
        sizeCache.set(value, dimension.y);
        setDimension(sizeCache);
    }

    /**
     * Returns the box height
     *
     * @return the box height
     */
    public float getHeight() {
        return dimension.y;
    }

    /**
     * Sets the box height
     *
     * @param value the box height
     */
    public void setHeight(float value) {
        sizeCache.set(dimension.x, value);
        setDimension(sizeCache);
    }

    /**
     * Creates a (not necessarily convex) polygon at the origin.
     * <p>
     * The points given are relative to the polygon's origin.  They
     * are measured in physics units.  They tile the image according
     * to the drawScale (which must be set for drawing to work
     * properly).
     *
     * @param points The polygon vertices
     */
    public PolygonObstacle(float[] points) {
        this(points, 0, 0);
    }

    /**
     * Creates a (not necessarily convex) polygon
     * <p>
     * The points given are relative to the polygon's origin.  They
     * are measured in physics units.  They tile the image according
     * to the drawScale (which must be set for drawing to work
     * properly).
     *
     * @param points The polygon vertices
     * @param x      Initial x position of the polygon center
     * @param y      Initial y position of the polygon center
     */
    public PolygonObstacle(float[] points, float x, float y) {
        super(x, y);
        assert points.length % 2 == 0;

        // Compute the bounds.
        initShapes(points);
        initBounds();
    }

    /**
     * Initializes the bounding box (and drawing scale) for this polygon
     */
    private void initBounds() {
        float minx = vertices[0];
        float maxx = vertices[0];
        float miny = vertices[1];
        float maxy = vertices[1];

        for (int i = 2; i < vertices.length; i += 2) {
            if (vertices[i] < minx) {
                minx = vertices[i];
            } else if (vertices[i] > maxx) {
                maxx = vertices[i];
            }
            if (vertices[i + 1] < miny) {
                miny = vertices[i + 1];
            } else if (vertices[i] > maxy) {
                maxy = vertices[i + 1];
            }
        }
        dimension = new Vector2((maxx - minx), (maxy - miny));
        sizeCache = new Vector2(dimension);
    }

    /**
     * Initializes the Box2d shapes for this polygon
     * <p>
     * If the texture is not null, this method also allocates the PolygonRegion
     * for drawing.  However, the points in the polygon region may be rescaled
     * later.
     *
     * @param points The polygon vertices
     */
    private void initShapes(float[] points) {
        // Triangulate
        ShortArray array = TRIANGULATOR.computeTriangles(points);
        trimCollinear(points, array);

        tridx = new short[array.items.length];
        System.arraycopy(array.items, 0, tridx, 0, tridx.length);

        // Allocate space for physics triangles.
        int tris = array.items.length / 3;
        vertices = new float[tris * 6];
        shapes = new PolygonShape[tris];
        geoms = new Fixture[tris];
        for (int i = 0; i < tris; i++) {
            for (int j = 0; j < 3; j++) {
                vertices[6 * i + 2 * j] = points[2 * array.items[3 * i + j]];
                vertices[6 * i + 2 * j + 1] = points[2 * array.items[3 * i + j] + 1];
            }
            shapes[i] = new PolygonShape();
            shapes[i].set(vertices, 6 * i, 6);
        }

        // Draw the shape with the appropriate scaling factor
        scaled = new float[points.length];
        for (int i = 0; i < points.length; i += 2) {
            scaled[i] = points[i] * drawScale.x;
            scaled[i + 1] = points[i + 1] * drawScale.y;
        }
        if (texture != null) {
            // WARNING: PolygonRegion constructor by REFERENCE
            region = new PolygonRegion(texture, scaled, tridx);
        }

    }

    /**
     * Removes collinear vertices from the given triangulation.
     * <p>
     * For some reason, the LibGDX triangulator will occasionally return collinear
     * vertices.
     *
     * @param points  The polygon vertices
     * @param indices The triangulation indices
     */
    private void trimCollinear(float[] points, ShortArray indices) {
        int collinear = 0;
        for (int i = 0; i < indices.size / 3 - collinear; i++) {
            float t1 = points[2 * indices.items[3 * i]] * (points[2 * indices.items[3 * i + 1] + 1] - points[2 * indices.items[3 * i + 2] + 1]);
            float t2 = points[2 * indices.items[3 * i + 1]] * (points[2 * indices.items[3 * i + 2] + 1] - points[2 * indices.items[3 * i] + 1]);
            float t3 = points[2 * indices.items[3 * i + 2]] * (points[2 * indices.items[3 * i] + 1] - points[2 * indices.items[3 * i + 1] + 1]);
            if (Math.abs(t1 + t2 + t3) < 0.0000001f) {
                indices.swap(3 * i, indices.size - 3 * collinear - 3);
                indices.swap(3 * i + 1, indices.size - 3 * collinear - 2);
                indices.swap(3 * i + 2, indices.size - 3 * collinear - 1);
                collinear++;
            }
        }
        indices.size -= 3 * collinear;
        indices.shrink();
    }

    /**
     * Resize this polygon (stretching uniformly out from origin)
     *
     * @param width  The new width
     * @param height The new height
     */
    private void resize(float width, float height) {
        float scalex = width / dimension.x;
        float scaley = height / dimension.y;

        for (int i = 0; i < shapes.length; i++) {
            for (int j = 0; j < 3; j++) {
                vertices[6 * i + 2 * j] *= scalex;
                vertices[6 * i + 2 * j + 1] *= scaley;
            }
            shapes[i].set(vertices, 6 * i, 6);
        }

        // Reset the drawing shape as well
        for (int i = 0; i < scaled.length; i += 2) {
            scaled[i] *= scalex;
            scaled[i + 1] *= scaley;
        }

        dimension.set(width, height);
    }

    /**
     * Create new fixtures for this body, defining the shape
     * <p>
     * This is the primary method to override for custom physics objects
     */
    protected void createFixtures() {
        if (body == null) {
            return;
        }

        releaseFixtures();

        // Create the fixtures
        for (int i = 0; i < shapes.length; i++) {
            fixture.shape = shapes[i];
            geoms[i] = body.createFixture(fixture);
        }
        markDirty(false);
    }

    /**
     * Release the fixtures for this body, reseting the shape
     * <p>
     * This is the primary method to override for custom physics objects
     */
    protected void releaseFixtures() {
        if (geoms[0] != null) {
            for (Fixture fix : geoms) {
                body.destroyFixture(fix);
            }
        }
    }

    /**
     * Sets the object texture for drawing purposes.
     * <p>
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @param value the object texture for drawing purposes.
     */
    public void setTexture(TextureRegion value) {
        texture = value;
        region = new PolygonRegion(texture, scaled, tridx);
    }

    /**
     * Sets the drawing scale for this physics object
     * <p>
     * The drawing scale is the number of pixels to draw before Box2D unit. Because
     * mass is a function of area in Box2D, we typically want the physics objects
     * to be small.  So we decouple that scale from the physics object.  However,
     * we must track the scale difference to communicate with the scene graph.
     * <p>
     * We allow for the scaling factor to be non-uniform.
     *
     * @param x the x-axis scale for this physics object
     * @param y the y-axis scale for this physics object
     */
    public void setDrawScale(float x, float y) {
        assert x != 0 && y != 0 : "Scale cannot be 0";
        float dx = x / drawScale.x;
        float dy = y / drawScale.y;
        // Reset the drawing shape as well
        for (int i = 0; i < scaled.length; i += 2) {
            scaled[i] *= dx;
            scaled[i + 1] *= dy;
        }
        if (texture != null) {
            region = new PolygonRegion(texture, scaled, tridx);
        }
        drawScale.set(x, y);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (region != null) {
            canvas.draw(region, Color.WHITE, 0, 0, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
        }
    }

    /**
     * Draws the outline of the physics body.
     * <p>
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        for (PolygonShape tri : shapes) {
            canvas.drawPhysics(tri, Color.YELLOW, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
        }
    }

}