public class GravityForceActor implements ForceActor {

    @Override
    public Force getForce(PhysicsObject physicsObject) {
        return new Force(Vector2.ZERO, new Vector2(0,-9.8).scale(physicsObject.physicsShape.getMass()));
    }
    
}
