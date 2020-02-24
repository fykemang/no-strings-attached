package softBody;

import com.badlogic.gdx.math.Vector2;

public class NPCObject extends SimModel {
    private StringObject stringObject;

    public void setStringObject(StringObject stringObject) {
        this.stringObject = stringObject;
    }

    public StringObject getStringObject() {
        return stringObject;
    }

    public NPCObject(float mass, SimObjectType s, Vector2 currPosition, StringObject stringObject) {
        super(mass, s, currPosition);
        this.stringObject = stringObject;
    }

}
