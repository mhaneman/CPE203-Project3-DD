public class FactoryHostile
{
    private static int dragonAnimationPeriod = 100;
    private static int dragonActionPeriod = 1000;

    private static int blobAnimationPeriod = 4000;
    private static int blobActionPeriod = 1250;

    private static int moleAnimationPeriod = 1000;
    private static int moleActionPeriod = 600;

    public static EntityHostile createDragon(Point position, ImageStore imageStore)
    {
        String id = "dragon_" + position.x + "_" + position.y;
        return new Dragon(id, position, imageStore.getImageList("dragon"), dragonActionPeriod, dragonAnimationPeriod);
    }

    public static EntityHostile createBlob(Point position, ImageStore imageStore)
    {
        String id = "blob_" + position.x + "_" + position.y;
        return new Blob(id, position, imageStore.getImageList("blob"), blobActionPeriod, blobAnimationPeriod);
    }

    public static EntityHostile createMole(Point position, ImageStore imageStore)
    {
        String id = "mole_" + position.x + "_" + position.y;
        return new Mole(id, position, imageStore.getImageList("mole"), moleActionPeriod, moleAnimationPeriod);
    }
}
