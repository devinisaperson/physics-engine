
import static java.lang.System.currentTimeMillis;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int frameCount = 0;
        JFrame frame = new JFrame();

        frame.setSize(1280, 1280);
        frame.setTitle("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(true);
        Engine engine = new Engine();
        frame.add(engine);

        double physicsStepLength = 0.01;
        
        double currentTime = millisToSeconds(currentTimeMillis());
        double accumulator = 0.0;

        // note, the program *will* take up 20% of your CPU if you don't have this at a reasonable value
        double maxFPS = 120.0;

        frame.setVisible(true);
        try {
            for (int i = 0; i < 0; i++) {
                engine.update(physicsStepLength);
            }
            while (true) {
                double newTime = millisToSeconds(currentTimeMillis());
                double frameTime = newTime - currentTime;
                if ( frameTime > 0.25 )
                    frameTime = 0.25;
                currentTime = newTime;

                accumulator += frameTime;

                while (accumulator >= physicsStepLength) {
                    engine.update(physicsStepLength);
                    accumulator -= physicsStepLength;
                    //accumulator = 0; // one physics step per frame good for debugging
                }
                frame.repaint();
                frameCount++;
                Thread.sleep((long)(1000.0/maxFPS - frameTime));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static double millisToSeconds(long time) {
        return time/5000.0;
    }
}