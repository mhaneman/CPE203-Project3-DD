import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Crab extends EntityMoves {
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;
    public static final String QUAKE_KEY = "quake";

    public Crab(String id, Point position,
                  List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> crabTarget = world.findNearest(getPosition(), Sgrass.class);
        long nextPeriod = getActionPeriod();

        if (crabTarget.isPresent())
        {
            Point tgtPos = crabTarget.get().getPosition();

            if (moveTo(world, crabTarget.get(), eventScheduler))
            {
                Entity quake = new Quake(QUAKE_ID, tgtPos,
                        imageStore.getImageList(QUAKE_KEY),
                        QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                ((EntityAction)quake).scheduleActions(world, imageStore, eventScheduler);
            }
        }

        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }

    protected void _moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
    }

    private PathingStrategy strategy = new SingleStepPathingStrategy();
    protected boolean generatePath(Point pos, Point goal, WorldModel world)
    {
        List<Point> points;
        points = strategy.computePath(pos, goal,
                p ->  (world.withinBounds(p) && !world.isOccupied(p)), EntityMoves::neighbors,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (points.size() == 0)
            return false;

        path.addAll(points);
        return true;
    }
}
