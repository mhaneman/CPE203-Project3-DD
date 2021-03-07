import processing.core.PImage;

import java.util.List;

public class FactoryObstacle {
    public static Entity createObstacle(Point position, List<PImage> images)
    {
        String id = "obstacle_" + position.x + "_" + position.y;
        return new Obstacle(id, position, images);
    }
}
