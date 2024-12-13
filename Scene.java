
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene {
    public static Scene getInitialScene() {
        return new SceneSpring();
    }

    List<GameObject> gameObjects = new CopyOnWriteArrayList<>();
}
