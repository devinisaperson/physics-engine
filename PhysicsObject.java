import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject implements GameObject {
    PhysicsPolygon physicsShape;
    Vector2 position = new Vector2(5,12);
    Vector2 velocity = new Vector2(0,0);
    Vector2 acceleration = new Vector2(0,0);
    double rotation = 0;
    double omega = 0.0;
    double alpha = 0.0;

    Vector2 springAttach = new Vector2(-0.5,-7.0/18.0);

    List<ForceActor> forceActors = new ArrayList<>();

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
        this.forceActors = that.forceActors;
    }

    public void addForceActor(ForceActor forceActor) {
        forceActors.add(forceActor);
    }

    public void updatePivot() {
        Vector2 nudge = physicsShape.centerToCenterOfMass();
        position = position.add(nudge);
    }

    private void applyContinuousForce(Force force) {
        acceleration = acceleration.add(force.vector.scale(1/physicsShape.getMass()));
        alpha += force.point.cross(force.vector.scale(1/physicsShape.getInertia()));
    }

    @Override
    public void physicsUpdate(double dt) {
        acceleration = Vector2.ZERO;
        alpha = 0;
        //System.out.println(forceActors.get(0).getForce(this));
        //System.out.println(this);
        for (ForceActor forceActor : forceActors) {
            //System.out.println(forceActor.getClass() + " " + forceActor.getForce(this).toString());
            applyContinuousForce(forceActor.getForce(this));
        }
    }

    PhysicsObject physicsStep(double dt) {
        PhysicsObject that = new PhysicsObject(this);
        that.physicsUpdateSelf(dt);
        //System.out.println(that.alpha);
        return that;
    }

    private void physicsUpdateSelf(double dt) {

        alpha += omega*-0.1/physicsShape.getInertia();
        //System.out.println()

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
        g.setColor(new Color(0x000000));
        g.fillPolygon(xPoints, yPoints, points.size());

        /*
        Vector2 springEnd = camera.worldToScreen(localToWorld(springAttach));
        Vector2 springStart = camera.worldToScreen(new Vector2(5,8));
        g.drawLine(
            (int)Math.floor(springEnd.x),
            (int)Math.floor(springEnd.y),
            (int)Math.floor(springStart.x),
            (int)Math.floor(springStart.y)
        );
        */
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        return v.rotate(rotation).add(position);
    }

    void combine(PhysicsObject that) {
        this.position = new Vector2(that.position);
        this.velocity = new Vector2(that.velocity);
        this.acceleration = new Vector2(that.acceleration);
        this.rotation = that.rotation;
        this.omega = that.omega;
        this.alpha = that.alpha;
    }
}
