import processing.core.PImage;

import java.util.List;

public abstract class EntityAction extends Entity
{
    private int actionPeriod;
    public EntityAction(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images);

        this.actionPeriod = actionPeriod;
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
    protected abstract void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);

    protected Action createActivityAction(WorldModel world,
                                       ImageStore imageStore)
    {
        return new Activity(this, world, imageStore);
    }

    protected int getActionPeriod() {
        return actionPeriod;
    }
}
