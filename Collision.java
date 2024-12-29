import java.util.ArrayList;

public class Collision {
    int i;
    int j;
    double time;
    PhysicsObject iOutside;
    PhysicsObject jOutside;
    PhysicsObject iInside;
    PhysicsObject jInside;
    ArrayList<ArrayList<Integer>> pointIndexes;
    ArrayList<ArrayList<Vector2>> points;

    Collision(
        int i, 
        int j,
        double time, 
        PhysicsObject iOutside, 
        PhysicsObject jOutside, 
        PhysicsObject iInside, 
        PhysicsObject jInside, 
        ArrayList<ArrayList<Integer>> pointIndexes, 
        ArrayList<ArrayList<Vector2>> points) 
        {
        this.i = i;
        this.j = j;
        this.time = time;
        this.iOutside = iOutside;
        this.jOutside = jOutside;
        this.iInside = iInside;
        this.jInside = jInside;
        this.pointIndexes = pointIndexes;
        this.points = points;
    }
}