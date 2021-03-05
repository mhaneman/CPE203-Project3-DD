public class Animation implements Action{
    private Entity entity;
    private int repeatCount;

    public Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        executeAnimationAction(scheduler);
    }

    private void executeAnimationAction(EventScheduler scheduler)
    {
        this.entity.nextImage();

        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(this.entity,
                    ((EntityDynamic)this.entity).createAnimationAction(
                            Math.max(this.repeatCount - 1, 0)),
                    ((EntityDynamic)this.entity).getAnimationPeriod());
        }
    }
}
