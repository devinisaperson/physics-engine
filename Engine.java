
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class Engine extends JComponent {
    private Camera camera = new Camera();
    private Scene scene = Scene.getInitialScene();
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
        
        
        List<Double> times = new ArrayList<Double>();
        List<List<PhysicsObject>> objectsAtTime = new ArrayList<>();

        List<PhysicsObject> steppedPhysicsObjects = getSteppedPhysicsObjects(physicsObjects, dt);
        times.add(dt);
        objectsAtTime.add(steppedPhysicsObjects);
        int simTimeIndex = 0;

        do {
            for (int i = 0; i < objectsAtTime.get(simTimeIndex).size(); i++) {
                for (int j = i+1; j < objectsAtTime.get(simTimeIndex).size(); j++) {
                    if (PhysicsObject.colliding(objectsAtTime.get(simTimeIndex).get(i), objectsAtTime.get(simTimeIndex).get(j))) {
                        if (!PhysicsObject.colliding(physicsObjects.get(i), physicsObjects.get(j))) {
                            System.out.println(findCollisionTime(physicsObjects.get(i), physicsObjects.get(j), dt, 0.0001));
                        }
                        PhysicsObject.resolveCollision(objectsAtTime.get(simTimeIndex).get(i), objectsAtTime.get(simTimeIndex).get(j));
                    }
                }
            }
        } while (simTimeIndex < times.size()-1);
        
        for (int i = 0; i < physicsObjects.size(); i++) {
            physicsObjects.get(i).combine(steppedPhysicsObjects.get(i));
        }
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
            if (PhysicsObject.colliding(physicsObject0.physicsStep(middle), physicsObject1.physicsStep(middle))) {
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