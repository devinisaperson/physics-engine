import java.awt.Color;
import java.awt.Graphics;

public class Marker implements GameObject{
    Vector2 position;
    
    public Marker(Vector2 position) {
        this.position = position;
    }

    @Override
    public void physicsUpdate(double dt) {
        return;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.setColor(new Color(0xff0000));
        
        g.drawOval((int)Math.floor(camera.worldToScreen(position).x-5), (int)Math.floor(camera.worldToScreen(position).y-5), 10, 10);
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localToWorld'");
    }
    
}
