import processing.core.PImage;

import java.util.List;

public abstract class EntityOcto extends EntityMoves
{
    private int resourceLimit;
    public EntityOcto(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected abstract Entity _transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    protected boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (_transform(world, scheduler, imageStore) != null)
        {
            Entity octo = _transform(world, scheduler, imageStore);
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(octo);
            ((EntityAction)octo).scheduleActions(world, imageStore, scheduler);

            return true;
        }

        return false;
    }

    protected int getResourceLimit() {
        return resourceLimit;
    }
}
