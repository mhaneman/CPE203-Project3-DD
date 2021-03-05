import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Fish extends EntityAction {

    private static final String CRAB_KEY = "crab";
    private static final String CRAB_ID_SUFFIX = " -- crab";
    private static final int CRAB_PERIOD_SCALE = 4;
    private static final int CRAB_ANIMATION_MIN = 50;
    private static final int CRAB_ANIMATION_MAX = 150;
    private static final Random rand = new Random();

    public Fish(String id, Point position,
                  List<PImage> images,
                  int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        eventScheduler.unscheduleAllEvents(this);

        Entity crab = new Crab(getId() + CRAB_ID_SUFFIX, pos, imageStore.getImageList(CRAB_KEY),
                getActionPeriod() / CRAB_PERIOD_SCALE,
                CRAB_ANIMATION_MIN + rand.nextInt(CRAB_ANIMATION_MAX - CRAB_ANIMATION_MIN));

        world.addEntity(crab);
        ((EntityAction)crab).scheduleActions(world, imageStore, eventScheduler);
    }

    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());

    }
}
