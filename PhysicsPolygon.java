import java.util.ArrayList;
import java.util.List;

public class PhysicsPolygon {
    private List<Vector2> points;
    private Double mass;
    private Double density;
    private Double area;
    private Vector2 centerOfMass;
    private Double inertia;

    public PhysicsPolygon() {
        this.points = new ArrayList<>();
        this.mass = 1.0;
    }

    public PhysicsPolygon(Vector2[] points, double mass) {
        this();
        List<Vector2> copyList = new ArrayList<>();
        for (Vector2 point : points) {
            copyList.add(new Vector2(point));
        }
        this.points.addAll(copyList); 
        this.mass = mass;
    }

    public PhysicsPolygon(List<Vector2> points, double mass) {
        this.points = points;
        this.mass = mass;
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
    public void updatePhysics() {
        this.area = 0.0;
        this.inertia = 0.0;
        this.centerOfMass = new Vector2();

        Vector2 curr;
        Vector2 next = points.get(0);
        for (int i = 0; i < points.size(); i++) {
            curr = next;
            next = points.get((i+1)%points.size());

            double trapizodArea = curr.cross(next);
            area += trapizodArea;
            centerOfMass = centerOfMass.add(curr.add(next).scale(trapizodArea));
            inertia += trapizodArea * (curr.x*next.y + 2*curr.x*curr.y + 2*next.x*next.y + next.x*curr.y);
        }
        this.density = mass/area;

        centerOfMass = centerOfMass.scale(1/(6*area));
        inertia *= density/12;
    }
}
