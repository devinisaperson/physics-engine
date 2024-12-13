public class SpringForceActor implements ForceActor {
    Spring spring;
    double k;

    public SpringForceActor(Spring spring, double k) {
        this.spring = spring;
        this.k = k;
    }

    @Override
    public Force getForce(PhysicsObject physicsObject) {
        Vector2 onPosition;
        Vector2 byPosition;
        if (physicsObject == spring.startObject) {
            onPosition = spring.getStartWorldPosition();
            byPosition = spring.getEndWorldPosition();
        } else if (physicsObject == spring.endObject) {
            onPosition = spring.getEndWorldPosition();
            byPosition = spring.getStartWorldPosition();
        } else {
            return null;
        }

        return new Force(onPosition, byPosition.minus(onPosition).scale(k));
    }
}
