import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject implements GameObject {
    PhysicsPolygon physicsShape;
    Vector2 position = new Vector2(0,0);
    Vector2 velocity = new Vector2(-5,5);
    Vector2 acceleration = new Vector2(0,0);
    double rotation = 0;
    double omega = 0.0;
    double alpha = 0.0;

    Vector2 springAttach = new Vector2(-0.5,-7.0/18.0);

    public PhysicsObject() {
        List<Vector2> points = new ArrayList<>();
        points.add(new Vector2(0,0));
        points.add(new Vector2(0,1));
        points.add(new Vector2(0.5,0.5));
        points.add(new Vector2(1,1));
        points.add(new Vector2(1,0));

        physicsShape = new PhysicsPolygon(points, 1.0);
        updatePivot();
    }

    public PhysicsObject(PhysicsObject that) {
        this.physicsShape = new PhysicsPolygon(that.physicsShape);
        this.position = new Vector2(that.position);
        this.velocity = new Vector2(that.velocity);
        this.acceleration = new Vector2(that.acceleration);
        this.rotation = that.rotation;
        this.omega = that.omega;
        this.alpha = that.alpha;
    }

    public void updatePivot() {
        Vector2 nudge = physicsShape.centerToCenterOfMass();
        position = position.add(nudge);
    }

    private void applyContinuousForce(Vector2 point, Vector2 force) {
        acceleration = acceleration.add(force.scale(1/physicsShape.getMass()));
        alpha += point.cross(force.scale(1/physicsShape.getInertia()));
    }

    @Override
    public PhysicsObject physicsUpdate(double dt) {
        PhysicsObject that = new PhysicsObject(this);
        that.physicsUpdateSelf(dt);
        return that;
    }

    private void physicsUpdateSelf(double dt) {
        acceleration = Vector2.ZERO;
        alpha = 0;

        applyContinuousForce(new Vector2(0.0,0.0), new Vector2(0,-9.8));

        Vector2 springPostion = new Vector2(5,8);
        applyContinuousForce(springAttach.rotate(rotation), (springPostion.minus(position)).scale(3));

        applyContinuousForce(new Vector2(0.0,0.0), velocity.scale(-0.1));
        alpha += omega*-0.1/physicsShape.getInertia();

        velocity = velocity.add(acceleration.scale(dt));
        position = position.add(velocity.scale(dt));

        omega += alpha * dt;
        rotation += omega * dt;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        List<Vector2> points = physicsShape.getPoints();
        int[] xPoints = new int[points.size()];
        int[] yPoints = new int[points.size()];

        int i = -1;
        for (Vector2 point : points) {
            i++;
            Vector2 screenPoint = camera.worldToScreen(localToWorld(point));
            xPoints[i] = (int)Math.floor(screenPoint.x);
            yPoints[i] = (int)Math.floor(screenPoint.y);
        }
        g.fillPolygon(xPoints, yPoints, points.size());

        Vector2 springEnd = camera.worldToScreen(localToWorld(springAttach));
        Vector2 springStart = camera.worldToScreen(new Vector2(5,8));
        g.drawLine(
            (int)Math.floor(springEnd.x),
            (int)Math.floor(springEnd.y),
            (int)Math.floor(springStart.x),
            (int)Math.floor(springStart.y)
        );
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        return v.rotate(rotation).add(position);
    }
}
