import java.util.ArrayList;
import java.util.List;

public class PhysicsPolygon {
    private List<Vector2> points;
    private Double mass;
    private Double density;
    private Vector2 centerOfMass;
    private Double inertia;

    public PhysicsPolygon(Vector2[] points, double mass) {
        List<Vector2> copyList = new ArrayList<>();
        for (Vector2 point : points) {
            copyList.add(new Vector2(point));
        }
        this.points.addAll(copyList); 
        this.mass = mass;

        updateCenterOfMass();
        updateInertia(new Vector2(0.0,0.0));
    }

    public PhysicsPolygon(List<Vector2> points, double mass) {
        this.points = points;
        this.mass = mass;
        updateCenterOfMass();
        updateInertia(new Vector2(0.0,0.0));
    }

    public PhysicsPolygon(PhysicsPolygon that) {
        this.points = new ArrayList<>();
        for (Vector2 point : that.points) {
            this.points.add(new Vector2(point));
        }
        this.mass = that.mass;
        this.density = that.density;
        this.centerOfMass = new Vector2(that.centerOfMass);
        this.inertia = that.inertia;
    }

    public List<Vector2> getPoints() {
        return points;
    }

    /*
     * https://en.wikipedia.org/wiki/Polygon#Area
     * 
     * https://en.wikipedia.org/wiki/Centroid#Of_a_polygon
     * https://math.stackexchange.com/questions/3177/why-doesnt-a-simple-mean-give-the-position-of-a-centroid-in-a-polygon
     * 
     * https://en.wikipedia.org/wiki/Second_moment_of_area#Any_polygon
     */
    public void updateInertia(Vector2 pivot) {
        this.inertia = 0.0;
        double area = 0.0;

        Vector2 curr;
        Vector2 next = points.get(0);
        for (int i = 0; i < points.size(); i++) {
            curr = next;
            next = points.get((i+1)%points.size());

            double trapizodArea = curr.cross(next);
            area += trapizodArea;
            centerOfMass = centerOfMass.add(curr.add(next).scale(trapizodArea));

            Vector2 dcurr = curr.minus(pivot);
            Vector2 dnext = next.minus(pivot);
            inertia += trapizodArea * (dcurr.x*dnext.y + 2*dcurr.x*dcurr.y + 2*dnext.x*dnext.y + dnext.x*dcurr.y);
        }
        area /= 2;
        this.density = mass/area;

        inertia *= density/12;
    }

    public Vector2 updateCenterOfMass() {
        double area = 0.0;
        this.centerOfMass = new Vector2();

        Vector2 curr;
        Vector2 next = points.get(0);
        for (int i = 0; i < points.size(); i++) {
            curr = next;
            next = points.get((i+1)%points.size());

            double trapizodArea = curr.cross(next);
            area += trapizodArea;
            centerOfMass = centerOfMass.add(curr.add(next).scale(trapizodArea));
        }
        area /= 2;
        centerOfMass = centerOfMass.scale(1/(6*area));
        
        return centerOfMass;
    }

    public Vector2 centerToCenterOfMass() {
        Vector2 oldCenterOfMass = updateCenterOfMass();

        for (int i = 0; i < points.size(); i++) {
            points.set(i, points.get(i).minus(oldCenterOfMass));
        }

        return oldCenterOfMass;
    }

    public Double getMass() {
        return mass;
    }

    public Double getInertia() {
        return inertia;
    }
}
