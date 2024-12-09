
import java.awt.Graphics;
import javax.swing.JComponent;

public class Renderer extends JComponent {
    
    private Camera camera = new Camera();
    private PhysicsObject physicsObject = new PhysicsObject();

    public void update(double dt) {
        physicsObject.update(dt);
    }

    @Override
    public void paintComponent(Graphics g) {
        physicsObject.render(g, camera);
    }
}