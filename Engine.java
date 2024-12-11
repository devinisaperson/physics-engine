
import java.awt.Graphics;
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
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.physicsUpdate(dt);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        for (GameObject gameObject : scene.gameObjects) {
            gameObject.render(g, camera);
        }
    }
}