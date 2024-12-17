public class Force {
    public Vector2 point;
    public Vector2 vector;

    public Force(Vector2 point, Vector2 vector) {
        this.point = point;
        this.vector = vector;
    }

    @Override
    public String toString() {
        return "point: " + point + " vector: " + vector;
    }
}
