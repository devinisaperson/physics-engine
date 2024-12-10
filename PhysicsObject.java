import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject {
    PhysicsPolygon physicsShape;
    Vector2 position = new Vector2(0,0);
    Vector2 velocity = new Vector2(0,5);
    Vector2 acceleration = new Vector2(0,0);
    double rotation = 0;
    double omega = 0.0;
    double alpha = 0.0;

    public PhysicsObject() {
        List<Vector2> points = new ArrayList<>();
        points.add(new Vector2(0,0));
        points.add(new Vector2(0,1));
        //points.add(new Vector2(0.5,0.5));
        points.add(new Vector2(1,1));
        points.add(new Vector2(1,0));

        physicsShape = new PhysicsPolygon(points, 1.0);
        updatePivot();
    }

    public void updatePivot() {
        Vector2 nudge = physicsShape.centerToCenterOfMass();
        position = position.add(nudge);
    }

    public void applyContinuousForce(Vector2 point, Vector2 force) {
        acceleration = acceleration.add(force.scale(1/physicsShape.getMass()));
        alpha = point.cross(force.scale(1/physicsShape.getInertia()));
    }

    public void update(double dt) {
        acceleration = Vector2.ZERO;
        alpha = 0;

        applyContinuousForce(new Vector2(0.0,0.0), new Vector2(0,-9.8));

        Vector2 springPostion = new Vector2(5,5);
        applyContinuousForce(new Vector2(0.0,0.0), (springPostion.minus(position)).scale(3));

        velocity = velocity.add(acceleration.scale(dt));
        position = position.add(velocity.scale(dt));

        omega += alpha * dt;
        rotation += omega * dt;
    }

    public void render(Graphics g, Camera camera) {
        List<Vector2> points = physicsShape.getPoints();
        int[] xPoints = new int[points.size()];
        int[] yPoints = new int[points.size()];

        int i = -1;
        for (Vector2 point : points) {
            i++;
            Vector2 screenPoint = camera.worldToScreen(point.rotate(rotation).add(position));
            xPoints[i] = (int)Math.floor(screenPoint.x);
            yPoints[i] = (int)Math.floor(screenPoint.y);
        }
        g.fillPolygon(xPoints, yPoints, points.size());
    }
}
