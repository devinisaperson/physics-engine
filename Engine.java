
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JComponent;

public class Engine extends JComponent {
    private Camera camera = new Camera();
    private Scene scene = Scene.getInitialScene();
    private ArrayList<GameObject> waitToAdd = new ArrayList<>();

    // private double accumulator = 1.0;

    public void update(double dt) {
        /*
        accumulator += dt;
        while (accumulator >= 1.0) {
            physicsObjects.add(new PhysicsObject());
            accumulator -= 1.0;
        }
        */
        List<PhysicsObject> physicsObjects = new ArrayList<>(); 
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.physicsUpdate(dt);
            if (gameObject instanceof PhysicsObject physicsObject) {
                physicsObjects.add(physicsObject);
            }
        }
        
        
        // List<Double> times = new ArrayList<Double>();
        // List<List<PhysicsObject>> objectsAtTime = new ArrayList<>();

        List<PhysicsObject> steppedPhysicsObjects = getSteppedPhysicsObjects(physicsObjects, dt);
        // times.add(dt);
        // objectsAtTime.add(steppedPhysicsObjects);
        // int simTimeIndex = 0;

        class Collision {
            double time;
            int lower;
            int higher;

            Collision(double time, int lower, int higher) {
                this.time = time;
                this.lower = lower;
                this.higher = higher;
            }
        }

        class sortByTime implements Comparator<Collision> {
            @Override
            public int compare(Collision a, Collision b)
            {
                return (int)Math.signum(a.time - b.time);
            }
        }

        // it's 4:22 am, I do not know what the correct data structures are to keep track of this stuff
        // it's probably best done with just arraylists or something
        // oh well
        PriorityQueue<Collision> collisionQueue = new PriorityQueue<>(5, new sortByTime());
        boolean[][] collisionDected = new boolean[steppedPhysicsObjects.size()-1][steppedPhysicsObjects.size()];

        do {
            if (collisionQueue.peek() != null) {
                Collision collision = collisionQueue.poll();
                PhysicsObject lowerAtCollision = physicsObjects.get(collision.lower).physicsStep(collision.time);
                PhysicsObject higherAtCollision = physicsObjects.get(collision.higher).physicsStep(collision.time);
                PhysicsObject.resolveCollision(lowerAtCollision, higherAtCollision);
                steppedPhysicsObjects.get(collision.lower).combine(lowerAtCollision.physicsStep(dt - collision.time));
                steppedPhysicsObjects.get(collision.higher).combine(higherAtCollision.physicsStep(dt - collision.time));
            }

            for (int i = 0; i < steppedPhysicsObjects.size()-1; i++) {
                for (int j = i+1; j < steppedPhysicsObjects.size(); j++) {

                    ArrayList<ArrayList<Vector2>> currentCollidingPoints = PhysicsObject.collidingPoints(steppedPhysicsObjects.get(i), steppedPhysicsObjects.get(j));
                    ArrayList<ArrayList<Vector2>> previousCollidingPoints = PhysicsObject.collidingPoints(physicsObjects.get(i), physicsObjects.get(j));

                    boolean currentlyColliding = currentCollidingPoints.get(0).size() > 0 || currentCollidingPoints.get(1).size() > 0;
                    boolean previouslyColliding = previousCollidingPoints.get(0).size() > 0 || previousCollidingPoints.get(1).size() > 0;

                    if (!collisionDected[i][j] && currentlyColliding && !previouslyColliding) {
                        for (Vector2 point : currentCollidingPoints.get(0)) {
                            waitToAdd.add(new Marker(point));
                        }
                        for (Vector2 point : currentCollidingPoints.get(1)) {
                            waitToAdd.add(new Marker(point));
                        }
                        double collisionTime = findCollisionTime(physicsObjects.get(i), physicsObjects.get(j), dt, 0.0001);
                        collisionQueue.add(new Collision(collisionTime, i, j));
                        collisionDected[i][j] = true;
                    }
                }
            }
        } while (!collisionQueue.isEmpty());
        
        for (int i = 0; i < physicsObjects.size(); i++) {
            physicsObjects.get(i).combine(steppedPhysicsObjects.get(i));
        }

        scene.gameObjects.addAll(waitToAdd);
        waitToAdd = new ArrayList<>();
    }

    private double findCollisionTime(PhysicsObject physicsObject0, PhysicsObject physicsObject1, double maxTime, double timeEpsilon) {
        // .5^x * maxTime <= timeEpsilon
        // .5^x <= timeEpsilon/maxTime
        // x <= log_1/2 (timeEpsilon/maxTime)
        // x <= -log_2 timeEpsilon/maxTime
        // x <= -(log(timeEpsilon/maxTime) / log(2))
        double low = 0;
        double high = maxTime;
        for (int i = 0; i <= -Math.log(timeEpsilon/maxTime) / Math.log(2); i++) {
            double middle = (low + high)/2;
            ArrayList<ArrayList<Vector2>> collidingPoints = PhysicsObject.collidingPoints(physicsObject0.physicsStep(middle), physicsObject1.physicsStep(middle));
            if (collidingPoints.get(0).size() > 0 || collidingPoints.get(1).size() > 0) {
                high = middle;
            } else {
                low = middle;
            }
        }
        return low;
    }
    
    private ArrayList<PhysicsObject> getSteppedPhysicsObjects(List<PhysicsObject> physicsObjects, double dt) {
        ArrayList<PhysicsObject> steppedPhysicsObjects = new ArrayList<>();
        for (int i = 0; i < physicsObjects.size(); i++) {
            steppedPhysicsObjects.add(physicsObjects.get(i).physicsStep(dt));
        }
        return steppedPhysicsObjects;
    }

    @Override
    public void paintComponent(Graphics g) {
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.render(g, camera);
        }
    }
}