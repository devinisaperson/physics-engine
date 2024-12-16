
import java.awt.Graphics;

public interface GameObject {


    public void physicsUpdate(double dt);

    public void render(Graphics g, Camera camera);

    public Vector2 localToWorld(Vector2 v);
}