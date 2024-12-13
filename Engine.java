
import java.awt.Graphics;
import javax.swing.JComponent;

public class Engine extends JComponent {
    private Camera camera = new Camera();
    private Scene previousScene = null;
    private Scene currentScene = Scene.getInitialScene();
    // private double accumulator = 1.0;

    public void update(double dt) {
        /*
        accumulator += dt;
        while (accumulator >= 1.0) {
            physicsObjects.add(new PhysicsObject());
            accumulator -= 1.0;
        }
        */
        Scene nextScene = new Scene();
        for (GameObject gameObject : currentScene.gameObjects) {
            nextScene.gameObjects.add(gameObject.physicsUpdate(dt));
        }

        currentScene = nextScene;
        previousScene = currentScene;
    }

    @Override
    public void paintComponent(Graphics g) {
        for (GameObject gameObject : currentScene.gameObjects) {
            gameObject.render(g, camera);
        }
    }
}