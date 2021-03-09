import processing.core.PImage;

import java.util.List;

public class Mole extends EntityHostile
{

    public Mole(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, new GreedyPathingStrategy());
    }
}
