

public class SceneSpring extends Scene {
    public SceneSpring() {
        PhysicsObject physicsObject = new PhysicsObject();
        Spring spring = new Spring(Vector2.ZERO, new Vector2(5,8), physicsObject, null);
        physicsObject.addForceActor(spring.forceActor);
        gameObjects.add(physicsObject);
    }
}
