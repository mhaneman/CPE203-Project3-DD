public class FactoryDynamic
{
    private static int characterActionPeriod = 1000;
    private static int characterAnimationPeriod = 100;

    public static EntityDynamic createCharacter(Point position, ImageStore imageStore)
    {
        String id = "character_" + position.x + "_" + position.y;
        return new Character(id, position, imageStore.getImageList("character"),
                characterActionPeriod, characterAnimationPeriod);
    }
}
