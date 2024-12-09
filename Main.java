
import static java.lang.System.currentTimeMillis;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame();

        frame.setSize(1280, 720);
        frame.setTitle("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Renderer renderer = new Renderer();
        frame.add(renderer);

        double physicsStepLength = 0.01;
        
        double currentTime = currentTimeMillis()/1000.0;
        double accumulator = 0.0;

        frame.setVisible(true);
        try {
            while (true) {
                double newTime = currentTimeMillis()/1000.0;
                double frameTime = newTime - currentTime;
                currentTime = newTime;

                accumulator += frameTime;

                while (accumulator >= physicsStepLength) {
                    renderer.update(physicsStepLength);
                    accumulator -= physicsStepLength;
                }
                frame.repaint();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}