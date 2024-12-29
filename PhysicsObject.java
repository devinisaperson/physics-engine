import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Segment;

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
        points.add(new Vector2(1,0));
        points.add(new Vector2(1,1));
        points.add(new Vector2(0.5,0.5));
        points.add(new Vector2(0,1));

        physicsShape = new PhysicsPolygon(points, 1.0);
        updatePivot();
    }

    public PhysicsObject(Vector2 position, Vector2 velocity, double rotation, double omega) {
        this();
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
        this.omega = omega;
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

        // System.out.println(this);
        // System.out.println(position);
        // System.out.println(rotation);
    }

    @Override
    public Vector2 localToWorld(Vector2 v) {
        return v.rotate(rotation).add(position);
    }

    public Vector2 getPointWorld(int idx) {
        return localToWorld(physicsShape.getPoints().get(idx));
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

    private static Vector2 findIntersectPoint(LineSegment s1, LineSegment s2) {
        if (doIntersect(s1, s2)) {
            double m1 = (s1.first.y - s1.second.y)/(s1.first.x - s1.second.x);
            double m2 = (s2.first.y - s2.second.y)/(s2.first.x - s2.second.x);
            // y = mx + b
            // y - mx = b
            double b1 = s1.first.y - m1*s1.first.x;
            double b2 = s1.first.y - m1*s1.first.x;

            if (s1.first.x - s1.second.x == 0) {
                return new Vector2(s1.first.x, m2 * s1.first.x + b2);
            }
            if (s2.first.x - s2.second.x == 0) {
                return new Vector2(s2.first.x, m1 * s2.first.x + b1);
            }

            // https://www.baeldung.com/java-intersection-of-two-lines
            double x = (b2 - b1) / (m1 - m2);
            double y = m1 * x + b1;
            return new Vector2(x,y);
        }
        return null;
    }

    public static ArrayList<ArrayList<Integer>> collidingPoints(PhysicsObject physicsObject0, PhysicsObject physicsObject1) {
        

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
                if (doIntersect(segmentList0.get(i), segmentList1.get(j))) {
                    prefix0[i] ^= true;
                    prefix1[j] ^= true;
                    //return true;
                }
            }
        }

        ArrayList<ArrayList<Integer>> collidingPoints = new ArrayList<>();
        collidingPoints.add(new ArrayList<>());
        collidingPoints.add(new ArrayList<>());
        boolean is0in = physicsObject1.inside(pointList0.get(0));
        boolean is1in = physicsObject0.inside(pointList1.get(0)); //wtf why is this 1?

        for (int i = 0; i < pointList0.size(); i++) {
            if (is0in) {
                //System.out.println(pointList0.get(i));
                collidingPoints.get(0).add(i);
            }
            //already inside? swap? result
            //true false true
            //false false false
            //true true false
            //false true true
            is0in = is0in ^ prefix0[i];
        }

        for (int i = 0; i < pointList1.size(); i++) {
            if (is1in) {
                //System.out.println(pointList1.get(i));
                collidingPoints.get(1).add(i);
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
        boolean result = false;
        double epsilon = 0.1;
        List<Vector2> pointList = getPointsWorld();
        double leftmost = Math.min(point.x, pointList.get(0).x);
        for (int i = 0; i < pointList.size(); i++) {
            leftmost = Math.min(leftmost, pointList.get((i+1)%pointList.size()).x);

            LineSegment ray = new LineSegment(new Vector2(leftmost-epsilon, point.y), point);
            LineSegment segment = new LineSegment(pointList.get(i), pointList.get((i+1)%pointList.size()));

            if (doIntersect(ray, segment)) {
                result = !result;
            }
        }
        
        return result;
    }

    private static Double findCollisionTime(Vector2 startPoint, Vector2 endPoint, LineSegment startSegment, LineSegment endSegment) {
        /*
         * https://www.desmos.com/calculator/jskox1ak4j
         * 1 = startSegment.first
         * 2 = startSegment.second
         * 3 = endSegment.first
         * 4 = endSegment.second
         * 5 = startPoint
         * 6 = endPoint
         */

        // these vectors probably mean something
        // I have no idea what they mean other than the algerba works out this way
        
        Vector2 A = startPoint.minus(startSegment.first);
        Vector2 B = endPoint.minus(startPoint).minus(endSegment.first).add(startSegment.first);
        Vector2 C = startSegment.first.minus(startSegment.second);
        Vector2 D = endSegment.first.minus(startSegment.first).minus(endSegment.second).add(startSegment.second);
        
        double c = A.cross(C);
        double b = A.cross(D) + B.cross(C);
        double a = B.cross(D);

        // bt+c=0
        // t=-c/b
        if (a == 0) {
            double linearRoot = -c/b;
            if (0 <= linearRoot && linearRoot <= 1) {
                return linearRoot;
            }
            return null;
        }

        //quadratic formula
        double determinant = b*b - 4*a*c;

        if (determinant < 0) {
            return null;
        }

        double vertex = -b/(2*a);
        double offset = Math.sqrt(determinant)/(2*a);

        double lowRoot = vertex - offset;
        double highRoot = vertex + offset;
        
        if (0 <= lowRoot && lowRoot <= 0 && lowRoot <= highRoot) {
            return lowRoot;
        }
        if (0 <= highRoot && highRoot <= 0) {
            return highRoot;
        }

        return null;
    }

    public static PhysicsObject[] resolveCollision(Collision collision) {
        PhysicsObject[] result = null;
        
        if (collision.points.get(0).size() == 1 && collision.points.get(1).size() == 0) {
            // Vector2 start = iAtCollision.getPointsWorld().get(collision.pointIndexes.get(0).get(0));
            // Vector2 end = collision.points.get(0).get(0);
            result = PhysicsObject.pointEdgeCollision(collision.pointIndexes.get(0).get(0), collision.iOutside, collision.iInside, collision.jOutside, collision.jInside);
        } else if (collision.points.get(1).size() == 1 && collision.points.get(0).size() == 0) {
            // Vector2 start = jAtCollision.getPointsWorld().get(collision.pointIndexes.get(1).get(0));
            // Vector2 end = collision.points.get(1).get(0);
            result = PhysicsObject.pointEdgeCollision(collision.pointIndexes.get(1).get(0), collision.jOutside, collision.jInside, collision.iOutside, collision.iInside);
        }

        for (PhysicsObject physicsObject : result) {
            physicsObject.color = new Color(0x00ffff);
        }

        return result;
    }

    public static PhysicsObject[] pointEdgeCollision(int pointIndex, PhysicsObject pointOutside, PhysicsObject pointInside, PhysicsObject edgeOutside, PhysicsObject edgeInside) {
        ArrayList<ArrayList<Integer>> debugList = collidingPoints(pointInside, edgeInside);

        PhysicsObject pointObject = new PhysicsObject(pointOutside);
        PhysicsObject edgeObject = new PhysicsObject(edgeOutside);

        Vector2 start = pointOutside.getPointWorld(pointIndex);
        Vector2 end = pointInside.getPointWorld(pointIndex);
        List<Vector2> edgeOutsidePoints = edgeOutside.getPointsWorld();
        List<Vector2> edgeInsidePoints = edgeInside.getPointsWorld();

        int segementIndex = -1;
        double minTime = 1.0;
        for (int i = 0; i < edgeOutsidePoints.size(); i++) {
            Double collisionTime = findCollisionTime(
                start, 
                end, 
                new LineSegment(edgeOutsidePoints.get(i), edgeOutsidePoints.get((i+1)%edgeOutsidePoints.size())),
                new LineSegment(edgeInsidePoints.get(i), edgeInsidePoints.get((i+1)%edgeInsidePoints.size()))
            );
            if (collisionTime != null && collisionTime <= minTime) {
                segementIndex = i;
                minTime = collisionTime;
            }
        }

        if (segementIndex == -1) {
            return new PhysicsObject[]{pointOutside, edgeOutside};
        }

        Vector2 collisionPoint = Vector2.lerp(start, end, minTime);
        LineSegment segment = new LineSegment(
            Vector2.lerp(edgeOutsidePoints.get(segementIndex), edgeInsidePoints.get(segementIndex), minTime),
            Vector2.lerp(edgeOutsidePoints.get((segementIndex+1)%edgeOutsidePoints.size()), edgeInsidePoints.get((segementIndex+1)%edgeInsidePoints.size()), minTime)
        );
        
        // https://www.myphysicslab.com/engine2D/collision-en.html
        double m_a = pointObject.physicsShape.getMass(); //mass of bodies A, B
        double m_b = edgeObject.physicsShape.getMass(); 
        double I_a = pointObject.physicsShape.getInertia();
        double I_b = pointObject.physicsShape.getInertia();
        Vector2 r_ap = collisionPoint.minus(pointObject.position); //distance vector from center of mass of body A to point P
        Vector2 r_bp = collisionPoint.minus(edgeObject.position); //distance vector from center of mass of body B to point P
        double w_a1 = pointObject.omega; //initial pre-collision angular velocity of bodies A, B
        double w_b1 = edgeObject.omega;
        Vector2 v_a1 = pointObject.velocity; //initial pre-collision velocities of center of mass bodies A, B
        Vector2 v_b1 = edgeObject.velocity;
        Vector2 v_ap1 = v_a1.add(r_ap.scale(w_a1)); //initial pre-collision velocity of impact point P on body A
        Vector2 v_bp1 = v_b1.add(r_bp.scale(w_b1)); //initial pre-collision velocity of impact point P on body B
        Vector2 vp1 = v_ap1.minus(v_bp1); //pre-collision relative velocity of impact points on body A, B
        Vector2 n = segment.first.minus(segment.second).normal().normalize(); //normal (perpendicular) vector to edge of body B
        double e = 1.0; //(0 = inelastic, 1 = perfectly elastic)
        double thinga = Math.pow(r_ap.cross(n), 2.0)/I_a;
        double thingb =  Math.pow(r_bp.cross(n), 2.0)/I_b;
        double num = (-(1.0 + e) * vp1.dot(n));
        double denom = (1/m_a + 1/m_b + thinga + thingb);
        double j = num/denom;

        pointObject.velocity = v_a1.add(n.scale(j/m_a));
        edgeObject.velocity = v_b1.minus(n.scale(j/m_b));
        
        pointObject.omega = w_a1 + (r_ap.cross(n.scale(j)))/I_a;
        edgeObject.omega = w_b1 - (r_bp.cross(n.scale(j)))/I_b;

        return new PhysicsObject[]{pointObject, edgeObject};
    }

    private static class LineSegment {
        Vector2 first;
        Vector2 second;

        public LineSegment(Vector2 v, Vector2 u) {
            this.first = v;
            this.second = u;
        }
    }
}
