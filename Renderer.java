
import java.awt.Graphics;
import javax.swing.JComponent;

public class Renderer extends JComponent {
    
    private Camera camera = new Camera();
    private PhysicsObject physicsObject = new PhysicsObject();

    @Override
    public void paintComponent(Graphics g) {
        physicsObject.render(g, camera);
    }
}