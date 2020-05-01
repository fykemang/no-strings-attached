/*
 * FilmStrip.java
 *
 * A filmstrip is a single image with multiple copies of the game object
 * as different animation frames.  The frames are arranged in rows and
 * columns, starting at the top-left.  If there are any blank frames,
 * they are at the end of the filmstrip (e.g. the bottom right).
 *
 * The frames must all be equally sized. The size of each frame is the image
 * width divided by the number of columns, and the image height divided
 * by the number of rows.  If the frames are not equally sized, this class
 * will not animate properly.
 *
 * Author: Walker M. White
 * Based on original GameX Ship Demo by Rama C. Hoetzlein, 2002
 * LibGDX version, 1/16/2015
 */
package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Texture class providing flipbook animation.
 * <p>
 * The class breaks up the image into regions, according the number of
 * rows and columns in the image.  It then indexes each region by frame,
 * starting from the top-left and processing one row at a time.
 * <p>
 * This is a subclass of TextureRegion, and so it keeps a rectangle region
 * that tracks the active part of the image to use for drawing.  See the
 * API for that class to understand how a TextureRegion.  The primary
 * advantage of this class is that it can quickly compute the new region
 * from the frame number.
 */
public class FilmStrip extends TextureRegion {
    /**
     * The number of columns in this filmstrip
     */
    private int cols;

    /**
     * The width of a single frame; computed from column count
     */
    private int rWidth;

    /**
     * The height of a single frame; computed from row count
     */
    private int rheight;

    /**
     * The number of frames in this filmstrip
     */
    private int size;

    /**
     * The active animation frame
     */
    private int frame;

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
//        setRefreshed(!reversed);
    }

    private boolean reversed;


    public float getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(float fd) {
        this.frameDuration = fd;
    }

    public float getElapsedTime() {
        return elapsedTime; // time since last frame
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime += elapsedTime;
    }

    private float frameDuration; //Time to wait between a frame and the next one.
    private float elapsedTime;

    private boolean loop;
    private boolean refreshed;
//runningAnimation = new Animation(FRAME_DURATION, runningFrames, PlayMode.LOOP);
//        ...
//    }
    /**
     * Whether or whether not to freeze the animation
     * at the current frame
     */
    private boolean freeze;

    /**
     * Creates a new filmstrip from the given texture.
     *
     * @param texture The texture image to use
     * @param rows    The number of rows in the filmstrip
     * @param cols    The number of columns in the filmstrip
     */
    public FilmStrip(Texture texture, int rows, int cols, boolean t) {
        this(texture, rows, cols, rows * cols, t);
    }

    /**
     * Creates a new filmstrip from the given texture.
     * <p>
     * The parameter size is to indicate that there are unused frames in
     * the filmstrip.  The value size must be less than or equal to
     * rows*cols, or this constructor will raise an error.
     *
     * @param texture The texture image to use
     * @param rows    The number of rows in the filmstrip
     * @param cols    The number of columns in the filmstrip
     * @param size    The number of frames in the filmstrip
     */
    public FilmStrip(Texture texture, int rows, int cols, int size, boolean l) {
        super(texture);
        if (size > rows * cols) {
            Gdx.app.error("FilmStrip", "Invalid strip size", new IllegalArgumentException());
            return;
        }
        this.cols = cols;
        this.size = size;
        rWidth = texture.getWidth() / cols;
        rheight = texture.getHeight() / rows;
        setFrame(0);
        frameDuration = 0f;
        loop = l;
        elapsedTime = 0f;
        reversed = false;
    }

    public void refresh() {
        refreshed = true;
        setFrame(reversed ? frame : 0);
        elapsedTime = 0f;
    }

    /**
     * Returns the number of frames in this filmstrip.
     *
     * @return the number of frames in this filmstrip.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the current active frame.
     *
     * @return the current active frame.
     */
    public int getFrame() {
        return frame;
    }

    /**
     * Sets the active frame as the given index.
     * <p>
     * If the frame index is invalid, an error is raised.
     *
     * @param frame the index to make the active frame
     */
    public void setFrame(int frame) {
        if (frame < 0 || frame >= size) {
            Gdx.app.error("FilmStrip", "Invalid animation frame", new IllegalArgumentException());
            return;
        }
        this.frame = frame;
        int x = (frame % cols) * rWidth;
        int y = (frame / cols) * rheight;
        setRegion(x, y, rWidth, rheight);
    }

    public void setNextFrame() {

        if (!reversed) {
            if (frame + 1 >= size && !loop)
                return;
            frame = frame + 1 >= size ? 0 : frame + 1;
        } else {
            if (frame == 0 && !loop) {
                return;
            }
            frame = frame == 0 ? size - 1 : frame - 1;
        }
        int x = (frame % cols) * rWidth;
        int y = (frame / cols) * rheight;
        setRegion(x, y, rWidth, rheight);
    }

    public void setShouldFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public boolean getShouldFreeze() {
        return freeze;
    }

    public void updateFrame() {
        if (elapsedTime >= frameDuration) {
            setNextFrame();
            elapsedTime = 0f;
        }
    }

    public boolean isRefreshed() {
        return refreshed;
    }

    public void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }
}