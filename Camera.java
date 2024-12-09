public class Camera {
    private Vector2 position = new Vector2(0,0);
    private double pixelsPerMeter = 50;
    private Vector2 size = new Vector2(1280, 720-37);

    public Vector2 worldToScreen(Vector2 v) {
        Vector2 u = v.scale(pixelsPerMeter);
        u.y = -u.y + size.y;
        u = u.add(position);
        return u;
    }
}
