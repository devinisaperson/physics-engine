import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject {
    PhysicsPolygon physicsShape;
    Vector2 position;
    double rotation;

    public PhysicsObject() {
        List<Vector2> points = new ArrayList<>();
        points.add(new Vector2(0,0));
        points.add(new Vector2(0,10));
        points.add(new Vector2(5,5));
        points.add(new Vector2(10,10));
        points.add(new Vector2(10,0));

        physicsShape = new PhysicsPolygon(points, 1.0);
    }

    public void render(Graphics g, Camera camera) {
        List<Vector2> points = physicsShape.getPoints();
        int[] xPoints = new int[points.size()];
        int[] yPoints = new int[points.size()];

        int i = -1;
        for (Vector2 point : points) {
            i++;
            Vector2 screenPoint = camera.worldToScreen(point);
            xPoints[i] = (int)Math.floor(screenPoint.x);
            yPoints[i] = (int)Math.floor(screenPoint.y);
        }
        g.drawPolygon(xPoints, yPoints, points.size());
    }
}
