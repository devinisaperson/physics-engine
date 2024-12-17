public class SpringForceActor implements ForceActor {
    Spring spring;
    double k;
    double restLength;

    public SpringForceActor(Spring spring, double k, double restLength) {
        this.spring = spring;
        this.k = k;
        this.restLength = restLength;
    }

    @Override
    public Force getForce(PhysicsObject physicsObject) {
        Vector2 onLocalPosition;
        Vector2 onPosition;
        Vector2 byPosition;
        if (physicsObject == spring.startObject) {
            onLocalPosition = spring.startPoint.rotate(physicsObject.rotation);
            onPosition = spring.getStartWorldPosition();
            byPosition = spring.getEndWorldPosition();
        } else if (physicsObject == spring.endObject) {
            onLocalPosition = spring.endPoint.rotate(physicsObject.rotation);
            onPosition = spring.getEndWorldPosition();
            byPosition = spring.getStartWorldPosition();
        } else {
            return null;
        }
        byPosition = byPosition.minus(byPosition.minus(onPosition).normalize().scale(restLength));
        return new Force(onLocalPosition, byPosition.minus(onPosition).scale(k));
    }
}
