public class FourSpringScene extends Scene {
    public FourSpringScene() {
        PhysicsObject[] physicsObjects = {
            new PhysicsObject(new Vector2(16.730256,10.900747), new Vector2(3.507351,-6.043363), 0.401628, 1.580071),
            new PhysicsObject(new Vector2(18.431981,10.991109), new Vector2(4.116339,-4.043462), 0.616034, 0.440411),
            new PhysicsObject(new Vector2(19.328572,11.909756), new Vector2(-1.040115,-1.770449), 1.510700, 0.565224),
            new PhysicsObject(new Vector2(19.115590,12.988994), new Vector2(-1.642348,-2.682704), 1.970524, -0.273964)
        };

        for (int i = 0; i < physicsObjects.length; i++) {
            Spring spring;
            physicsObjects[i].addForceActor(new GravityForceActor());
            physicsObjects[i].addForceActor(new DampingForceActor());

            gameObjects.add(physicsObjects[i]);
            
            if (i > 0) {
                spring = new Spring(new Vector2(-0.5,-7.0/18.0), new Vector2(0.5,-7.0/18.0), physicsObjects[i], physicsObjects[i-1]);
                physicsObjects[i-1].addForceActor(spring.forceActor);
                physicsObjects[i].addForceActor(spring.forceActor);
                gameObjects.add(spring);
            }

            if (i == physicsObjects.length - 1) {
                spring = new Spring(new Vector2(0.5,-7.0/18.0), new Vector2(20,13.5), physicsObjects[i], null);
                physicsObjects[i].addForceActor(spring.forceActor);
                gameObjects.add(spring);
            }
        }

        /*
        from frame 0 on frame 53

        id
        x
        y
        omega
        rotation

        18
        16.730256
        10.900747
        1.580071
        0.401628

        20
        18.431981
        10.991109
        0.440411
        0.616034

        22
        19.328572
        11.909756
        0.565224
        1.510700

        24
        19.115590
        12.988994
        -0.273964
        1.970524
        */
    }
}
