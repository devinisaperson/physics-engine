public class SimpleScene extends Scene {
    public SimpleScene() {
        PhysicsObject physicsObject0 = new PhysicsObject();
        physicsObject0.position = new Vector2(5,5);
        physicsObject0.rotation = Math.TAU/8.0;
        physicsObject0.velocity = new Vector2(5,0);

        PhysicsObject physicsObject1 = new PhysicsObject();
        physicsObject1.position = new Vector2(6.5,5);

        super.gameObjects.add(physicsObject0);
        super.gameObjects.add(physicsObject1);
    }
}
