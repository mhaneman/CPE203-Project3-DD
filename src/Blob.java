import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Blob extends EntityHostile
{
    public Blob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, new SingleStepPathingStrategy());
    }
}
