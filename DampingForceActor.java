public class DampingForceActor implements ForceActor {

    @Override
    public Force getForce(PhysicsObject physicsObject) {
        return new Force(Vector2.ZERO, physicsObject.velocity.scale(-0.9));
    }
   
}
