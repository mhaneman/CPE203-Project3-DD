import processing.core.PImage;

import java.util.List;

public class FactoryHostile
{
    private static int dragonAnimationPeriod = 100;
    private static int dragonActionPeriod = 813;

    private static int blobAnimationPeriod = 4000;
    private static int blobActionPeriod = 813;

    private static int moleAnimationPeriod = 4000;
    private static int moleActionPeriod = 813;

    public static EntityHostile createDragon(Point position, ImageStore imageStore)
    {
        String id = "dragon_" + position.x + "_" + position.y;
        return new Dragon(id, position, imageStore.getImageList("mole"), dragonActionPeriod, dragonAnimationPeriod);
    }

    public static EntityHostile createBlob(Point position, ImageStore imageStore)
    {
        String id = "blob_" + position.x + "_" + position.y;
        return new Blob(id, position, imageStore.getImageList("mole"), blobActionPeriod, blobAnimationPeriod);
    }

    public static EntityHostile createMole(Point position, ImageStore imageStore)
    {
        String id = "mole_" + position.x + "_" + position.y;
        return new Mole(id, position, imageStore.getImageList("mole"), moleActionPeriod, moleAnimationPeriod);
    }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}