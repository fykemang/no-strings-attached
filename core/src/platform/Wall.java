package platform;

public class Wall {
    float[] indices;
    float height;
    float width;

    public Wall(float height, float width, float[] indices) {
        this.indices = indices;
        this.height = height;
        this.width = width;

    }

    public void setIndices(float[] indices) {
        this.indices = indices;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float[] getIndices() {
        return indices;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
