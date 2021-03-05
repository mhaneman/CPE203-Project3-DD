import processing.core.PImage;

import java.util.List;

public class Blob extends EntityHostile
{
    public Blob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler) {

    }

    @Override
    protected boolean generatePath(Point pos, Point goal, WorldModel world) {
        return false;
    }
}
