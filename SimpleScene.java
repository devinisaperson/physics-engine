public class SimpleScene extends Scene {
    public SimpleScene() {
        PhysicsObject physicsObject0 = new PhysicsObject();
        // physicsObject0.position = new Vector2(0.9499999999999993,5.909999999999997);
        // physicsObject0.rotation = 8.341661758165259;
        physicsObject0.position = new Vector2(2,6);
        physicsObject0.rotation = Math.TAU/8;
        physicsObject0.omega = -Math.TAU;
        physicsObject0.velocity = new Vector2(5,-3);

        PhysicsObject physicsObject1 = new PhysicsObject();
        physicsObject1.position = new Vector2(4,5);

        super.gameObjects.add(physicsObject1);
        super.gameObjects.add(physicsObject0);
    }
}
