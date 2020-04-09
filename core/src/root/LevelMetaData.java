package root;

public class LevelMetaData {
    public LevelMetaData(boolean iscompleted, String filePath, String background) {
        this.iscompleted = iscompleted;
        this.filePath = filePath;
        this.background = background;

    }

    public boolean isIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    boolean iscompleted;
    String filePath;
    String background;
}
