import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class FactoryObstacle extends Factory
{
    public static Entity createObstacle(Point position, List<PImage> images)
    {
        List<PImage> temp = new ArrayList<>();
        String id = "obstacle_" + position.x + "_" + position.y;
        if (position.y < 5)
        {
            temp.add(images.get(0));
            return new Obstacle(id, position, temp);
        }
        else if (position.y < 10)
        {
            temp.add(images.get(1));
            return new Obstacle(id, position, temp);
        }
        else if (position.y < 15)
        {
            temp.add(images.get(1));
            return new Obstacle(id, position, temp);
        }
        else if (position.y < 20)
        {
            temp.add(images.get(2));
            return new Obstacle(id, position, temp);
        }
        else
        {
            temp.add(images.get(3));
            return new Obstacle(id, position, temp);
        }
    }
}
