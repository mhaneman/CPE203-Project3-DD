public class FactoryDynamic
{
    private static int characterActionPeriod = 700;
    private static int characterAnimationPeriod = 100;

    public static EntityDynamic createCharacter(Point position, ImageStore imageStore)
    {
        String id = "character_" + position.x + "_" + position.y;
        return new Character(id, position, imageStore.getImageList("characterLeft"),
                characterActionPeriod, characterAnimationPeriod);
    }
}
