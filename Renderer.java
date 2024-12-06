
import java.awt.Graphics;
import javax.swing.JComponent;

public class Renderer extends JComponent {
    public int x = 0;
    @Override
    public void paintComponent(Graphics g) {
        x+=1;
        x=x%1000;
        g.fillOval(x,0,50,50);
    }
}