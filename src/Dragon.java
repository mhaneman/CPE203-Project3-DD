import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Dragon extends EntityHostile {

    public Dragon(String id, Point position,
                  List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, new AStarPathingStrategy());
    }
}
