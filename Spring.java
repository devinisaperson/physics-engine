import java.awt.Graphics;

public class Spring implements GameObject {
    Vector2 startPoint;
    Vector2 endPoint;
    PhysicsObject startObject;
    PhysicsObject endObject;
    
    public Spring(Vector2 startPoint, Vector2 endPoint, PhysicsObject startObject, PhysicsObject endObject) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startObject = startObject;
        this.endObject = endObject;
    }

    @Override
    public GameObject physicsUpdate(double dt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'physicsUpdate'");
    }

    @Override
    public void render(Graphics g, Camera camera) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }    
}
