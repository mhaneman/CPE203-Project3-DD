import processing.core.PImage;

import java.util.List;

public class Quake extends EntityAnimates {
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Quake(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void executeActivity(WorldModel world,
                                     ImageStore imageStore, EventScheduler eventScheduler)
    {

        eventScheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this,
                createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
    }


}
