

public class SceneSpring extends Scene {
    public SceneSpring() {
        PhysicsObject physicsObject = new PhysicsObject();
        Spring spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(5,8), physicsObject, null);
        physicsObject.addForceActor(new GravityForceActor());
        physicsObject.addForceActor(new DampingForceActor());
        physicsObject.addForceActor(spring.forceActor);
        gameObjects.add(physicsObject);
        gameObjects.add(spring);
    }
}
