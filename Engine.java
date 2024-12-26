
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

        

        // it's 4:22 am, I do not know what the correct data structures are to keep track of this stuff
        // it's probably best done with just arraylists or something
        // oh well
        PriorityQueue<Collision> collisionQueue = new PriorityQueue<>(5, new sortByTime());
        boolean[][] collisionDected = new boolean[steppedPhysicsObjects.size()-1][steppedPhysicsObjects.size()];

        do {
            if (collisionQueue.peek() != null) {
                Collision collision = collisionQueue.poll();
                PhysicsObject firstAtCollision = collision.first.physicsStep(collision.time);
                PhysicsObject secondAtCollision = collision.second.physicsStep(collision.time);
                PhysicsObject.resolveCollision(firstAtCollision, secondAtCollision);
                collision.first.combine(firstAtCollision.physicsStep(dt - collision.time));
                collision.second.combine(secondAtCollision.physicsStep(dt - collision.time));
            }

            for (int i = 0; i < steppedPhysicsObjects.size()-1; i++) {
                for (int j = i+1; j < steppedPhysicsObjects.size(); j++) {

                    ArrayList<ArrayList<Vector2>> currentCollidingPoints = PhysicsObject.collidingPoints(steppedPhysicsObjects.get(i), steppedPhysicsObjects.get(j));
                    ArrayList<ArrayList<Vector2>> previousCollidingPoints = PhysicsObject.collidingPoints(physicsObjects.get(i), physicsObjects.get(j));

                    boolean currentlyColliding = currentCollidingPoints.get(0).size() > 0 || currentCollidingPoints.get(1).size() > 0;
                    boolean previouslyColliding = previousCollidingPoints.get(0).size() > 0 || previousCollidingPoints.get(1).size() > 0;

                    if (!collisionDected[i][j] && currentlyColliding && !previouslyColliding) {
                        //System.out.println();
                        collisionQueue.add(findEarlistCollision(physicsObjects.get(i), physicsObjects.get(j), dt, 0.0001));
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

    private class Collision {
        double time;
        PhysicsObject first;
        PhysicsObject second;
        Vector2 point;

        Collision(double time, PhysicsObject first, PhysicsObject second, Vector2 point) {
            this.time = time;
            this.first = first;
            this.second = second;
            this.point = point;
        }
    }

    private class sortByTime implements Comparator<Collision> {
        @Override
        public int compare(Collision a, Collision b)
        {
            return (int)Math.signum(a.time - b.time);
        }
    }

    private Collision findEarlistCollision(PhysicsObject firstObject, PhysicsObject secondObject, double maxTime, double timeEpsilon) {
        // .5^x * maxTime <= timeEpsilon
        // .5^x <= timeEpsilon/maxTime
        // x <= log_1/2 (timeEpsilon/maxTime)
        // x <= -log_2 timeEpsilon/maxTime
        // x <= -(log(timeEpsilon/maxTime) / log(2))
        double low = 0;
        double high = maxTime;
        ArrayList<ArrayList<Vector2>> collidingPoints = null;
        Vector2 collisionPoint = null;
        for (int i = 0; i <= -Math.log(timeEpsilon/maxTime) / Math.log(2); i++) {
            double middle = (low + high)/2;
            collidingPoints = PhysicsObject.collidingPoints(firstObject.physicsStep(middle), secondObject.physicsStep(middle));
            if (collidingPoints.get(0).size() > 0 || collidingPoints.get(1).size() > 0) {
                if (collidingPoints.get(0).size() == 1 && collidingPoints.get(1).size() == 0) {
                    collisionPoint = collidingPoints.get(0).get(0);
                } else if (collidingPoints.get(0).size() == 0 && collidingPoints.get(1).size() == 1) {
                    collisionPoint = collidingPoints.get(1).get(0);
                }
                
                high = middle;
            } else {
                low = middle;
            }

            // if (collidingPoints != null) {
            //     for (Vector2 point : collidingPoints.get(0)) {
            //         // System.out.print(i);
            //         // System.out.print(" ");
            //         waitToAdd.add(new Marker(point));
            //     }
            //     //System.out.println();
            //     for (Vector2 point : collidingPoints.get(1)) {
            //         // System.out.print(j);
            //         // System.out.print(" ");
            //         waitToAdd.add(new Marker(point));
            //     }
            // }
        }

        waitToAdd.add(new Marker(collisionPoint));
        return new Collision(low, firstObject, secondObject, collisionPoint);
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