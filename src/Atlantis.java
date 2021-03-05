import processing.core.PImage;

import java.util.List;

public class Atlantis extends EntityAnimates {
    private static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;

    public Atlantis(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
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
                createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
    }
}
