import java.awt.Color;
import java.awt.Graphics;

public class Spring implements GameObject {
    Vector2 startPoint;
    Vector2 endPoint;
    PhysicsObject startObject;
    PhysicsObject endObject;
    SpringForceActor forceActor;
    
    public Spring(Vector2 startPoint, Vector2 endPoint, PhysicsObject startObject, PhysicsObject endObject) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startObject = startObject;
        this.endObject = endObject;
        forceActor = new SpringForceActor(this, 10, 0);
    }

    public Vector2 getStartWorldPosition() {
        return startObject == null ? startPoint : startObject.localToWorld(startPoint);
    }

    public Vector2 getEndWorldPosition() {
        return endObject == null ? endPoint : endObject.localToWorld(endPoint);
    }

    @Override
    public void physicsUpdate(double dt) {
        return;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        // System.out.print("hi");
        Vector2 startScreenPoint = camera.worldToScreen(getStartWorldPosition());
        Vector2 endScreenPoint = camera.worldToScreen(getEndWorldPosition());
        g.setColor(new Color(0x000000));
        g.drawLine((int)startScreenPoint.x, (int)startScreenPoint.y, (int)endScreenPoint.x, (int)endScreenPoint.y);
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'localToWorld'");
    }    
}
