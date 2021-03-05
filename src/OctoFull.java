import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OctoFull extends EntityOcto
{
    public OctoFull( String id, Point position, List<PImage> images,
                     int resourceLimit, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), Atlantis.class);

        if (fullTarget.isPresent() &&
                moveTo(world, fullTarget.get(), eventScheduler))
        {
            //at atlantis trigger animation
            ((EntityAction)fullTarget.get()).scheduleActions(world, imageStore, eventScheduler);

            //transform to unfull
            transform(world, eventScheduler, imageStore);
        }
        else
        {
            eventScheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
    }

    @Override
    protected Entity _transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return new OctoNotFull(this.getId(), this.getPosition(), this.getImages(),
                this.getResourceLimit(), 0, this.getActionPeriod(), this.getAnimationPeriod());
    }

    @Override
    protected void _moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
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
