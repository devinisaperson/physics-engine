
import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;

public class Engine extends JComponent {
    private Camera camera = new Camera();
    private List<PhysicsObject> physicsObjects = new CopyOnWriteArrayList<>();
    private double accumulator = 1.0;

    public void update(double dt) {
        accumulator += dt;
        while (accumulator > 1.0) {
            physicsObjects.add(new PhysicsObject());
            accumulator -= 1.0;
        }
        for (PhysicsObject physicsObject : physicsObjects) {
            physicsObject.update(dt);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        for (PhysicsObject physicsObject : physicsObjects) {
            physicsObject.render(g, camera);
        }
    }
}