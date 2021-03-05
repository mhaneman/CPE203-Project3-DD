import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OctoNotFull extends EntityOcto
{
    private int resourceCount;
    public OctoNotFull(String id, Point position,
                  List<PImage> images, int resourceLimit, int resourceCount,
                  int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;

    }

    protected Entity _transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (this.resourceCount >= this.getResourceLimit()) {
            return new OctoFull(getId(), getPosition(), getImages(),
                    getResourceLimit(), getActionPeriod(), getAnimationPeriod());
        }
        return null;
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(), Fish.class);

        if (!notFullTarget.isPresent() ||
                !moveTo(world, notFullTarget.get(), eventScheduler) ||
                !transform(world, eventScheduler, imageStore))
        {
            eventScheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
    }

    @Override
    protected void _moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        this.resourceCount += 1;
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
