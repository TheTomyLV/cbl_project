package GameObjects;

import Engine.GameObject;
import Engine.Vector2;

public class Grass extends GameObject {
    
    @Override
    protected void setup() {
        setSprite("grass");
        scale = new Vector2(0.4f, 0.4f);
    }
}
