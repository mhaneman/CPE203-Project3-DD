import processing.core.PImage;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public abstract class EntityMoves extends EntityAnimates
{
    protected Queue<Point> path = new ArrayDeque<>();
    public EntityMoves(String id, Point position,
                       List<PImage> images, int actionPeriod,
                       int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                getAnimationPeriod());
    }

    protected Point nextPosition(Point destPos, WorldModel world)
    {
        if (path.isEmpty())
            generatePath(this.getPosition(), destPos, world);
        if (path.isEmpty())
            return this.getPosition();
        return path.remove();
    }
    protected abstract boolean generatePath(Point pos, Point goal, WorldModel world);

    protected static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

    protected abstract void _moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            _moveTo(world, target, scheduler);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(target.getPosition(), world);

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

}
