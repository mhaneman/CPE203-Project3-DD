import processing.core.PImage;

import java.util.*;

public abstract class EntityHostile extends EntityDynamic
{
    protected Stack<Point> path = new Stack<>();
    protected PathingStrategy strategy;
    public EntityHostile(String id, Point position, List<PImage> images,
                         int actionPeriod, int animationPeriod, PathingStrategy strategy)
    {
        super(id, position, images, actionPeriod, animationPeriod);
        this.strategy = strategy;
    }


    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                getAnimationPeriod());
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> hostileTarget = world.findNearest(getPosition(), Character.class);
        long nextPeriod = getActionPeriod();

        if (hostileTarget.isPresent())
        {
            Point tgtPos = hostileTarget.get().getPosition();
            world.moveEntity(this, nextPosition(tgtPos, world));
        }
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }

    protected Point nextPosition(Point destPos, WorldModel world)
    {
        if (path.isEmpty())
            generatePath(this.getPosition(), destPos, world);
        if (path.isEmpty())
            return this.getPosition();
        return path.pop();
    }
    protected boolean generatePath(Point pos, Point goal, WorldModel world)
    {
        List<Point> points;
        points = strategy.computePath(pos, goal, p -> !world.isOccupied(p),
                EntityHostile::neighbors, PathingStrategy.CARDINAL_NEIGHBORS);


        if (points.size() == 0)
        {
            System.out.println("No path found");
            return false;
        }
        path.addAll(points);
        return true;
    }

    protected static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }
}
