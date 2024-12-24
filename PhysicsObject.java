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

    Color color = new Color(0x000000);

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

        this.color = that.color;
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
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, points.size());

        color = new Color(0x000000);
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        return v.rotate(rotation).add(position);
    }

    public ArrayList<Vector2> getPointsWorld() {
        List<Vector2> pointsLocal = physicsShape.getPoints();
        ArrayList<Vector2> pointsWorld = new ArrayList<>();
        for (Vector2 point : pointsLocal) {
            pointsWorld.add(localToWorld(point));
        }
        return pointsWorld;
    }

    void combine(PhysicsObject that) {
        this.position = new Vector2(that.position);
        this.velocity = new Vector2(that.velocity);
        this.acceleration = new Vector2(that.acceleration);
        this.rotation = that.rotation;
        this.omega = that.omega;
        this.alpha = that.alpha;
        this.color = that.color;
    }

    // onSegment orientation and do intersect based on geeksforgeeks tutorial

    // Given three points p, q, r, the function checks if
    // point q lies on line segment 'pr'
    private static boolean onSegment(Vector2 p, Vector2 q, Vector2 r) {
        return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
            q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y));
    }
    
    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are collinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    private static int orientation(Vector2 p, Vector2 q, Vector2 r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        double val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);
        
        
        if (val == 0) return 0;  // collinear
    
        return (val > 0)? 1: 2; // clock or counterclock wise
    }
    
    // The main function that returns true if line segment 'p1q1'
    // and 'p2q2' intersect.
    private static boolean doIntersect(LineSegment s1, LineSegment s2)
    {
        Vector2 p1 = s1.first, q1 = s1.second, p2 = s2.first, q2 = s2.second;
    
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
    
        // General case
        if (o1 != o2 && o3 != o4)
            return true;
    
        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
    
        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
    
        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
    
        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;
    
        return false; // Doesn't fall in any of the above cases
    }

    public static ArrayList<ArrayList<Vector2>> collidingPoints(PhysicsObject physicsObject0, PhysicsObject physicsObject1) {
        

        List<LineSegment> segmentList0 = new ArrayList<>();
        List<Vector2> pointList0 = physicsObject0.getPointsWorld();
        for (int i = 0; i < pointList0.size(); i++) {
            segmentList0.add(new LineSegment(
                pointList0.get(i),
                pointList0.get((i+1)%pointList0.size())
                ));
        }

        boolean[] prefix0 = new boolean[pointList0.size()];

        List<LineSegment> segmentList1 = new ArrayList<>();
        List<Vector2> pointList1 = physicsObject1.getPointsWorld();
        for (int i = 0; i < pointList1.size(); i++) {
            segmentList1.add(new LineSegment(
                pointList1.get(i),
                pointList1.get((i+1)%pointList1.size())
                ));
        }

        boolean[] prefix1 = new boolean[pointList1.size()];

        for (int i = 0; i < segmentList0.size(); i++) {
            for (int j = 0; j < segmentList1.size(); j++) {
                if (doIntersect(segmentList0.get(i), segmentList1.get(i))) {
                    prefix0[i] ^= true;
                    prefix1[j] ^= true;
                    //return true;
                }
            }
        }

        ArrayList<ArrayList<Vector2>> collidingPoints = new ArrayList<>();
        collidingPoints.add(new ArrayList<>());
        collidingPoints.add(new ArrayList<>());
        boolean is0in = physicsObject1.inside(pointList0.get(0));
        boolean is1in = physicsObject0.inside(pointList1.get(1));

        for (int i = 0; i < pointList0.size(); i++) {
            if (is0in) {
                System.out.println(pointList0.get(i));
                collidingPoints.get(0).add(pointList0.get(i));
            }
            //already inside? swap? result
            //true false true
            //false false false
            //true true false
            //false true true
            is0in = is0in ^ prefix0[i];
        }

        for (int i = 0; i < pointList1.size()-1; i++) {
            if (is1in) {
                collidingPoints.get(1).add(pointList1.get(i));
            }
            is1in = is1in ^ prefix1[i];
        }

        return collidingPoints;
    }

    public double distanceToSegement(Vector2 point, LineSegment segment) {
        Vector2 slope = segment.second.minus(segment.first);
        Vector2 normal = slope.normal().normalize();
        Vector2 pointRelative = point.minus(segment.first);
        return Vector2.comp(normal, pointRelative);
    }

    public boolean inside(Vector2 point) {
        boolean result = true;
        double epsilon = 0.1;
        double leftmost = point.x;
        List<Vector2> pointList = getPointsWorld();
        for (int i = 0; i < pointList.size(); i++) {
            leftmost = Math.min(leftmost, pointList.get(i).x);

            LineSegment ray = new LineSegment(new Vector2(leftmost-epsilon, point.y), point);
            LineSegment segment = new LineSegment(pointList.get(i), pointList.get((i+1)%pointList.size()));

            if (doIntersect(ray, segment)) {
                result = !result;
            }
        }
        
        return result;
    }

    public static void resolveCollision(PhysicsObject physicsObject0, PhysicsObject physicsObject1) {
        // System.out.println(physicsObject0);
        // System.out.println(physicsObject1);
        physicsObject0.color = new Color(0x00ffff);
        physicsObject1.color = new Color(0x00ffff);
    }

    private static class LineSegment /*implements Comparable<LineSegment>*/{
        Vector2 first;
        Vector2 second;

        public LineSegment(Vector2 v, Vector2 u) {
            this.first = v;
            this.second = u;
            
            // if (v.x <= u.x) {
            //     left = v;
            //     right = u;
            // } else {
            //     left = u;
            //     right = v;
            // }
        }

        // public Vector2 getLeft() {
        //     if (first.x <= second.x) {
        //         return first;
        //     } else {
        //         return second;
        //     }
        // }

        // public Vector2 getRight() {
        //     if (first.x <= second.x) {
        //         return second;
        //     } else {
        //         return first;
        //     }
        // }
        
        // @Override
        // public int compareTo(LineSegment that) {
        //     return (int)Math.signum(this.left.x - that.left.x);
        // }
    }
    
}
