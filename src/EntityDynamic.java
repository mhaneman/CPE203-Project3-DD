import processing.core.PImage;

import java.util.List;

public abstract class EntityDynamic extends Entity
{
    private int actionPeriod;
    private int animationPeriod;
    public EntityDynamic(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
        this.actionPeriod = actionPeriod;
    }
    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
    protected abstract void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);

    protected Action createAnimationAction(int repeatCount)
    {
        return new Animation(this, repeatCount);
    }
    protected int getAnimationPeriod()
    {
        return animationPeriod;
    }

    protected Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore);
    }
    protected int getActionPeriod()
    {
        return actionPeriod;
    }
}
