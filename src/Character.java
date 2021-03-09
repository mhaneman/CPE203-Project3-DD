import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Character extends EntityDynamic
{
    private int direction;
    private boolean leftOrRight; // right is false
    private int waitTime = 1;
    private int waitTick = -1; // -1 means no tick

    private int livesTime = 2;
    private int livesTick = livesTime;

    public Character(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        if (livesTick < livesTime)
            livesTick++;
        if (livesTick <= 0)
            world.removeEntity(this);

        if (waitTick < 0) {
            switch (direction) {
                case 1: // move up
                    Point newPos = new Point(this.getPosition().x, this.getPosition().y - 1);
                    if (!world.isOccupiedNotDirt(newPos))
                    {
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    }
                    break;

                case 2: // move down
                    newPos = new Point(this.getPosition().x, this.getPosition().y + 1);
                    if (!world.isOccupiedNotDirt(newPos))
                    {
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    }
                    break;

                case 3: // move left
                    leftOrRight = true;
                    newPos = new Point(this.getPosition().x - 1, this.getPosition().y);
                    if (!world.isOccupiedNotDirt(newPos))
                    {
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    }
                    break;

                case 4: // move right
                    leftOrRight = false;
                    newPos = new Point(this.getPosition().x + 1, this.getPosition().y);
                    if (!world.isOccupiedNotDirt(newPos))
                    {
                        world.removeEntityAt(newPos);
                        world.moveEntity(this, newPos);
                    }
                    break;
            }
        } else
        {
            Point shootPoint = leftOrRight ?
                    new Point(this.getPosition().x - 1, this.getPosition().y) :
                    new Point(this.getPosition().x + 1, this.getPosition().y);
            if (this.waitTick == this.waitTime)
                if (shootPoint.y < 5)
                    world.setBackground(shootPoint,
                            new Background("sky", imageStore.getImageList("sky")));
                else
                    world.setBackground(shootPoint,
                            new Background("black", imageStore.getImageList("black")));

            if (this.waitTick == 0)
                if (leftOrRight)
                    world.setBackground(shootPoint,
                            new Background("arrowLeft", imageStore.getImageList("arrowLeft")));
                else
                    world.setBackground(shootPoint,
                            new Background("arrowRight", imageStore.getImageList("arrowRight")));

            if (this.waitTick < this.waitTime)
                waitTick++;
            else
                waitTick = -1;
        }

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
            Point shootPoint = new Point(getPosition().x + 1, getPosition().y);
            if (world.withinBounds(shootPoint) && world.getOccupancyCell(shootPoint) instanceof EntityHostile)
            {
                world.removeEntityAt(shootPoint);
            }
        } else
        {
            Point shootPoint = new Point(getPosition().x - 1, getPosition().y);
            if (world.withinBounds(shootPoint) && world.getOccupancyCell(shootPoint) instanceof EntityHostile)
            {
                world.removeEntityAt(shootPoint);
            }

        }
    }

    public void removeLifeTick()
    {
        this.livesTick -= 2;
    }

    public void moveUp()
    {
        direction = 1;
    }

    public void moveDown()
    {
        direction = 2;
    }

    public void moveLeft(ImageStore imageStore)
    {
        direction = 3;
        this.setImages(imageStore.getImageList("characterLeft"));
    }

    public void moveRight(ImageStore imageStore)
    {
        direction = 4;
        this.setImages(imageStore.getImageList("characterRight"));
    }

}
