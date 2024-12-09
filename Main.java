import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame();

        frame.setSize(1280, 720);
        frame.setTitle("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Renderer renderer = new Renderer();
        frame.add(renderer);

        frame.setVisible(true);
        try {
            while (true) {
                frame.repaint();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}