package root;

public class LevelMetaData {

    private boolean isComplete;
    private String filePath;
    private final String background;

    public LevelMetaData(boolean isCompleted, String filePath, String background) {
        this.isComplete = isCompleted;
        this.filePath = filePath;
        this.background = background;

    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
