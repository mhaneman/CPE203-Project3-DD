import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Character extends EntityDynamic
{
    public Character(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
    }

    @Override
    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                getAnimationPeriod());
    }

    public void moveUp(WorldModel world)
    {
        world.moveEntity(this, new Point(this.getPosition().x, this.getPosition().y - 1));
    }

    public void moveDown(WorldModel world)
    {
        world.moveEntity(this, new Point(this.getPosition().x, this.getPosition().y + 1));
    }

    public void moveLeft(WorldModel world)
    {
        world.moveEntity(this, new Point(this.getPosition().x - 1, this.getPosition().y));
    }

    public void moveRight(WorldModel world)
    {
        world.moveEntity(this, new Point(this.getPosition().x + 1, this.getPosition().y));
    }

}
