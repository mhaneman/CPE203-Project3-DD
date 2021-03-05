import processing.core.PImage;

import java.util.List;

public abstract class EntityAnimates extends EntityAction
{
    private int animationPeriod;
    public EntityAnimates(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    protected Action createAnimationAction(int repeatCount)
    {
        return new Animation(this, repeatCount);
    }

    protected int getAnimationPeriod()
    {
        return animationPeriod;
    }
}
