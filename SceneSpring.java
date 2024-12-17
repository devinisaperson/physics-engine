

public class SceneSpring extends Scene {
    public SceneSpring() {
        PhysicsObject[] physicsObjects = {
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject(),
            new PhysicsObject()
        };

        for (int i = 0; i < physicsObjects.length; i++) {
            Spring spring;
            if (i == 0) {
                spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(5,8), physicsObjects[i], null);
            } else {
                spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(0.5,-7.0/18.0), physicsObjects[i], physicsObjects[i-1]);
            }
            physicsObjects[i].addForceActor(new GravityForceActor());
            physicsObjects[i].addForceActor(new DampingForceActor());
            physicsObjects[i].addForceActor(spring.forceActor);
            gameObjects.add(physicsObjects[i]);
            gameObjects.add(spring);
        }
        
    }
}
