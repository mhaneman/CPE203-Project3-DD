import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Dragon extends EntityHostile {

    public Dragon(String id, Point position,
                  List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> dragonTarget = world.findNearest(getPosition(), Obstacle.class);
        long nextPeriod = getActionPeriod();

        world.moveEntity(this, new Point(this.getPosition().x, this.getPosition().y + 1));

        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }

    private PathingStrategy strategy = new SingleStepPathingStrategy();
    protected boolean generatePath(Point pos, Point goal, WorldModel world)
    {
        List<Point> points;
        points = strategy.computePath(pos, goal,
                p ->  (world.withinBounds(p) && !world.isOccupied(p)), EntityHostile::neighbors,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (points.size() == 0)
            return false;

        path.addAll(points);
        return true;
    }
}
