import processing.core.PImage;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class Character extends EntityDynamic
{
    private int direction;
    private boolean leftOrRight; // right is false
    private int waitTime = 0;
    private int waitTick = -1; // -1 means no tick

    public Character(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        if (waitTick < 0)
        {
            switch (direction)
            {
                case 1: // move up
                    Point newPos = new Point(this.getPosition().x, this.getPosition().y - 1);
                    if (!world.isOccupiedNotDirt(newPos))
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    break;

                case 2: // move down
                    newPos = new Point(this.getPosition().x, this.getPosition().y + 1);
                    if (!world.isOccupiedNotDirt(newPos))
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    break;

                case 3: // move left
                    leftOrRight = true;
                    newPos = new Point(this.getPosition().x - 1, this.getPosition().y);
                    if (!world.isOccupiedNotDirt(newPos))
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    break;

                case 4: // move right
                    leftOrRight = false;
                    newPos = new Point(this.getPosition().x + 1, this.getPosition().y);
                    if (!world.isOccupiedNotDirt(newPos))
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    break;
            }
        } else if (waitTick < waitTime)
            waitTick++;
        else
            waitTick = -1;
        direction = 0;
        long nextPeriod = getActionPeriod();
        eventScheduler.scheduleEvent(this, createActivityAction(world, imageStore), nextPeriod);
    }

    @Override
    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this, createActivityAction(world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0), getAnimationPeriod());
    }

    public void shoot(WorldModel world, ImageStore imageStore)
    {
        waitTick = 0;
        if (!leftOrRight)
        {
            world.removeEntityAt(new Point(getPosition().x + 1, getPosition().y));
            if (world.getOccupancyCell(new Point(getPosition().x + 2, getPosition().y)) instanceof EntityHostile)
                world.removeEntityAt(new Point(getPosition().x + 2, getPosition().y));

        } else
        {
            world.removeEntityAt(new Point(getPosition().x - 1, getPosition().y));
            if (world.getOccupancyCell(new Point(getPosition().x - 2, getPosition().y)) instanceof EntityHostile)
                world.removeEntityAt(new Point(getPosition().x - 2, getPosition().y));
        }
    }

    public void moveUp()
    {
        direction = 1;
    }

    public void moveDown()
    {
        direction = 2;
    }

    public void moveLeft(WorldModel world)
    {
        direction = 3;
    }

    public void moveRight(WorldModel world)
    {
        direction = 4;
    }

}
