
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
        
        List<PhysicsObject> steppedPhysicsObjects = new ArrayList<>();
        for (int i = 0; i < physicsObjects.size(); i++) {
            steppedPhysicsObjects.add(physicsObjects.get(i).physicsStep(dt));
        }

        for (int i = 0; i < steppedPhysicsObjects.size(); i++) {
            for (int j = i+1; j < steppedPhysicsObjects.size(); j++) {
                if (PhysicsObject.colliding(steppedPhysicsObjects.get(i), steppedPhysicsObjects.get(j))) {
                    PhysicsObject.resolveCollision(steppedPhysicsObjects.get(i), steppedPhysicsObjects.get(j));
                }
            }
        }

        for (int i = 0; i < physicsObjects.size(); i++) {
            physicsObjects.get(i).combine(steppedPhysicsObjects.get(i));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.render(g, camera);
        }
    }
}