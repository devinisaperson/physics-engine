
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
        
        
        List<TimestampedPhysicsObject> midPhysicsObjects = new ArrayList<>();
        List<PhysicsObject> finalPhysicsObjects = new ArrayList<>();

        for (int i = 0; i < physicsObjects.size(); i++) {
            midPhysicsObjects.add(new TimestampedPhysicsObject(physicsObjects.get(i), 0.0));
            finalPhysicsObjects.add(physicsObjects.get(i).physicsStep(dt));
        }


        

        // it's 4:22 am, I do not know what the correct data structures are to keep track of this stuff
        // it's probably best done with just arraylists or something
        // oh well
        PriorityQueue<Collision> collisionQueue = new PriorityQueue<>(5, new sortByTime());
        boolean[][] collisionDected = new boolean[finalPhysicsObjects.size()-1][finalPhysicsObjects.size()];

        do {
            if (collisionQueue.peek() != null) {
                Collision collision = collisionQueue.poll();
                PhysicsObject iAtCollision = midPhysicsObjects.get(collision.i).getAtTime(collision.time);
                PhysicsObject jAtCollision = midPhysicsObjects.get(collision.j).getAtTime(collision.time);
                //PhysicsObject.resolveCollision(iAtCollision, jAtCollision, collision.point);

                if (collision.points.get(0).size() == 1 && collision.points.get(1).size() == 0) {
                    Vector2 start = iAtCollision.getPointsWorld().get(collision.pointIndexes.get(0).get(0));
                    Vector2 end = collision.points.get(0).get(0);
                    PhysicsObject.pointEdgeCollision(iAtCollision, start, end, jAtCollision);
                } else if (collision.points.get(1).size() == 1 && collision.points.get(0).size() == 0) {
                    Vector2 start = jAtCollision.getPointsWorld().get(collision.pointIndexes.get(1).get(0));
                    Vector2 end = collision.points.get(1).get(0);
                    PhysicsObject.pointEdgeCollision(jAtCollision, start, end, iAtCollision);
                }

                
                midPhysicsObjects.set(collision.i, new TimestampedPhysicsObject(iAtCollision, collision.time));
                midPhysicsObjects.set(collision.j, new TimestampedPhysicsObject(jAtCollision, collision.time));
                finalPhysicsObjects.set(collision.i, midPhysicsObjects.get(collision.i).getAtTime(dt));
                finalPhysicsObjects.set(collision.j, midPhysicsObjects.get(collision.j).getAtTime(dt));
            }

            for (int i = 0; i < physicsObjects.size()-1; i++) {
                for (int j = i+1; j < physicsObjects.size(); j++) {

                    ArrayList<ArrayList<Integer>> currentCollidingPoints = PhysicsObject.collidingPoints(finalPhysicsObjects.get(i), finalPhysicsObjects.get(j));
                    ArrayList<ArrayList<Integer>> previousCollidingPoints = PhysicsObject.collidingPoints(physicsObjects.get(i), physicsObjects.get(j));
                    

                    boolean currentlyColliding = currentCollidingPoints.get(0).size() > 0 || currentCollidingPoints.get(1).size() > 0;
                    boolean previouslyColliding = previousCollidingPoints.get(0).size() > 0 || previousCollidingPoints.get(1).size() > 0;

                    if (!collisionDected[i][j] && currentlyColliding && !previouslyColliding) {
                        collisionQueue.add(findEarlistCollision(midPhysicsObjects, i, j, dt, 0.0001));
                        collisionDected[i][j] = true;
                    }
                }
            }
        } while (!collisionQueue.isEmpty());
        
        for (int i = 0; i < physicsObjects.size(); i++) {
            physicsObjects.get(i).combine(finalPhysicsObjects.get(i));
        }

        scene.gameObjects.addAll(waitToAdd);
        waitToAdd = new ArrayList<>();
    }

    private class TimestampedPhysicsObject {
        PhysicsObject object;
        double time;

        TimestampedPhysicsObject(PhysicsObject object, double time) {
            this.object = object;
            this.time = time;
        }

        public PhysicsObject getAtTime(double end) {
            if (end - time < 0) {
                try {
                    throw new Exception("asking TimestampedPhysicsObject to go before its time");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return object.physicsStep(end - time);
        }
    }

    private class Collision {
        double time;
        int i;
        int j;
        ArrayList<ArrayList<Integer>> pointIndexes;
        ArrayList<ArrayList<Vector2>> points;

        Collision(double time, int i, int j, ArrayList<ArrayList<Integer>> pointIndexes, ArrayList<ArrayList<Vector2>> points) {
            this.time = time;
            this.i = i;
            this.j = j;
            this.pointIndexes = pointIndexes;
            this.points = points;
        }
    }

    private class sortByTime implements Comparator<Collision> {
        @Override
        public int compare(Collision a, Collision b)
        {
            return (int)Math.signum(a.time - b.time);
        }
    }

    private Collision findEarlistCollision(List<TimestampedPhysicsObject> midPhysicsObjects, int i, int j, double maxTime, double timeEpsilon) {
        double low = Math.max(midPhysicsObjects.get(i).time, midPhysicsObjects.get(j).time);
        double high = maxTime;
        PhysicsObject maxFirstObject = midPhysicsObjects.get(i).getAtTime(maxTime);
        PhysicsObject maxSecondObject = midPhysicsObjects.get(j).getAtTime(maxTime);
        ArrayList<ArrayList<Integer>> collidingPoints = PhysicsObject.collidingPoints(maxFirstObject, maxSecondObject);
        ArrayList<ArrayList<Integer>> lastCollidingPoints = collidingPoints;
        ArrayList<ArrayList<Vector2>> lastCollidingCoordinates = new ArrayList<>();
        lastCollidingCoordinates.add(new ArrayList<>());
        for (Integer idx : lastCollidingPoints.get(0)) {
            lastCollidingCoordinates.get(0).add(maxFirstObject.getPointsWorld().get(idx));
        }
        lastCollidingCoordinates.add(new ArrayList<>());
        for (Integer idx : lastCollidingPoints.get(1)) {
            lastCollidingCoordinates.get(1).add(maxFirstObject.getPointsWorld().get(idx));
        }

        // .5^x * maxTime <= timeEpsilon
        // .5^x <= timeEpsilon/maxTime
        // x <= log_1/2 (timeEpsilon/maxTime)
        // x <= -log_2 timeEpsilon/maxTime
        // x <= -(log(timeEpsilon/maxTime) / log(2))
        for (int run = 0; run <= -Math.log(timeEpsilon/maxTime) / Math.log(2); run++) {
            double middle = (low + high)/2;
            PhysicsObject midFirstObject = midPhysicsObjects.get(i).getAtTime(middle);
            PhysicsObject midSecondObject = midPhysicsObjects.get(j).getAtTime(middle);
            
            collidingPoints = PhysicsObject.collidingPoints(midFirstObject, midSecondObject);
            if (collidingPoints.get(0).size() > 0 || collidingPoints.get(1).size() > 0) {

                lastCollidingPoints = collidingPoints;

                lastCollidingCoordinates = new ArrayList<>();
                lastCollidingCoordinates.add(new ArrayList<>());
                for (Integer idx : lastCollidingPoints.get(0)) {
                    lastCollidingCoordinates.get(0).add(midFirstObject.getPointsWorld().get(idx));
                }
                lastCollidingCoordinates.add(new ArrayList<>());
                for (Integer idx : lastCollidingPoints.get(1)) {
                    lastCollidingCoordinates.get(1).add(midSecondObject.getPointsWorld().get(idx));
                }

                high = middle;
            } else {
                low = middle;
            }
        }
        
        for (Vector2 point : lastCollidingCoordinates.get(0)) {
            waitToAdd.add(new Marker(point));
        }
        for (Vector2 point : lastCollidingCoordinates.get(1)) {
            waitToAdd.add(new Marker(point));
        }

        return new Collision(low, i, j, lastCollidingPoints, lastCollidingCoordinates);
    }

    // private Vector2 returnOnlyPoint(ArrayList<ArrayList<Vector2>> twoLists) {
    //     if (twoLists.get(0).size() == 1 && twoLists.get(1).size() == 0) {
    //         return twoLists.get(0).get(0);
    //     } else if (twoLists.get(0).size() == 0 && twoLists.get(1).size() == 1) {
    //         return twoLists.get(1).get(0);
    //     }
    //     return null;
    // } 
    
    // private ArrayList<PhysicsObject> getSteppedPhysicsObjects(List<PhysicsObject> physicsObjects, double dt) {
    //     ArrayList<PhysicsObject> steppedPhysicsObjects = new ArrayList<>();
    //     for (int i = 0; i < physicsObjects.size(); i++) {
    //         steppedPhysicsObjects.add(physicsObjects.get(i).physicsStep(dt));
    //     }
    //     return steppedPhysicsObjects;
    // }

    @Override
    public void paintComponent(Graphics g) {
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.render(g, camera);
        }
    }
}