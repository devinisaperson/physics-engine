

public class SceneSpring extends Scene {
    public SceneSpring() {
        PhysicsObject[] physicsObjects = {
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject()
        };

        for (int i = 0; i < physicsObjects.length; i++) {
            Spring spring;
            physicsObjects[i].position = physicsObjects[i].position.add(new Vector2(i,0));
            if (i == 0) {
                spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(5,13.5), physicsObjects[i], null);
            } else {
                spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(0.5,-7.0/18.0), physicsObjects[i], physicsObjects[i-1]);
                physicsObjects[i-1].addForceActor(spring.forceActor);
            }
            physicsObjects[i].addForceActor(new GravityForceActor());
            physicsObjects[i].addForceActor(new DampingForceActor());
            physicsObjects[i].addForceActor(spring.forceActor);
            gameObjects.add(physicsObjects[i]);
            gameObjects.add(spring);

            if (i == physicsObjects.length - 1) {
                spring = new Spring(new Vector2(0.5,-7.0/18.0), new Vector2(20,13.5), physicsObjects[i], null);
                physicsObjects[i].addForceActor(spring.forceActor);
                gameObjects.add(spring);
            }
        }
        
    }
}
