import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   private static final String DRAGON_KEY = "dragon";
   private static final int DRAGON_NUM_PROPERTIES = 3;
   private static final int DRAGON_COL = 1;
   private static final int DRAGON_ROW = 2;

   private static final String BLOB_KEY = "blob";
   private static final int BLOB_NUM_PROPERTIES = 3;
   private static final int BLOB_COL = 1;
   private static final int BLOB_ROW = 2;

   private static final String MOLE_KEY = "mole";
   private static final int MOLE_NUM_PROPERTIES = 3;
   private static final int MOLE_COL = 1;
   private static final int MOLE_ROW = 2;

   private static final String CHARACTER_KEY = "character";
   private static final int CHARACTER_NUM_PROPERTIES = 3;
   private static final int CHARACTER_COL = 1;
   private static final int CHARACTER_ROW = 2;

   private static final String OBSTACLE_KEY = "obstacle";
   private static final int OBSTACLE_NUM_PROPERTIES = 3;
   private static final int OBSTACLE_COL = 1;
   private static final int OBSTACLE_ROW = 2;

   private static final int PROPERTY_KEY = 0;


   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   public Set<Entity> getEntities() {
      return entities;
   }

   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public boolean processLine(String line, ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case "background":
               return parseBackground(imageStore);
            case "dirt":
               return parseDirt(imageStore);
            case OBSTACLE_KEY:
               return parseObstacle(properties, imageStore);
            case DRAGON_KEY:
               return parseDragon(properties, imageStore);
            case BLOB_KEY:
               return parseBlob(properties, imageStore);
            case MOLE_KEY:
               return parseMole(properties, imageStore);
            case CHARACTER_KEY:
               return parseCharacter(properties, imageStore);
         }
      }
      return false;
   }

   private boolean parseObstacle(String[] properties, ImageStore imageStore)
   {
      if (properties.length == OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
            Integer.parseInt(properties[OBSTACLE_COL]),
            Integer.parseInt(properties[OBSTACLE_ROW]));

         Entity entity = FactoryObstacle.createObstacle(pt, imageStore.getImageList(OBSTACLE_KEY));
         tryAddEntity(entity);
      }
      return properties.length == OBSTACLE_NUM_PROPERTIES;
   }

   private boolean parseDragon(String[] properties, ImageStore imageStore)
   {
      if (properties.length == DRAGON_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[DRAGON_COL]),
                 Integer.parseInt(properties[DRAGON_ROW]));

         Entity entity = FactoryHostile.createDragon(pt, imageStore);
         tryAddEntity(entity);
      }
      return properties.length == DRAGON_NUM_PROPERTIES;
   }

   private boolean parseBlob(String[] properties, ImageStore imageStore)
   {
      if (properties.length == BLOB_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BLOB_COL]),
                 Integer.parseInt(properties[BLOB_ROW]));

         Entity entity = FactoryHostile.createBlob(pt, imageStore);
         tryAddEntity(entity);
      }
      return properties.length == BLOB_NUM_PROPERTIES;
   }

   private boolean parseMole(String[] properties, ImageStore imageStore)
   {
      if (properties.length == MOLE_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[MOLE_COL]),
                 Integer.parseInt(properties[MOLE_ROW]));

         Entity entity = FactoryHostile.createMole(pt, imageStore);
         tryAddEntity(entity);
      }
      return properties.length == MOLE_NUM_PROPERTIES;
   }

   private boolean parseCharacter(String[] properties, ImageStore imageStore)
   {
      if (properties.length == CHARACTER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[CHARACTER_COL]),
                 Integer.parseInt(properties[CHARACTER_ROW]));

         Entity entity = FactoryDynamic.createCharacter(pt, imageStore);
         tryAddEntity(entity);
      }
      return properties.length == CHARACTER_NUM_PROPERTIES;
   }

   private boolean parseBackground(ImageStore imageStore)
   {
      String id1 = "sky";
      String id2 = "black";
     for (int i = 0; i < numCols; i ++)
     {
        for (int j = 0; j < numRows; j ++)
        {
           if (j < 5)
              setBackground(new Point (i, j), new Background(id1, imageStore.getImageList(id1)));
           else
              setBackground(new Point (i, j), new Background(id2, imageStore.getImageList(id2)));
        }
     }
     return true;
   }

   private boolean parseDirt(ImageStore imageStore)
   {
      for (int i = 0; i < numCols; i ++)
      {
         for (int j = 5; j < numRows; j ++)
         {
            Point newPos = new Point(i, j);
            if (!isOccupied(newPos))
            {
               Entity entity = FactoryObstacle.createObstacle(newPos, imageStore.getImageList("dirt"));
               tryAddEntity(entity);
            }

         }

      }
      return true;
   }

   public Character getCharacter()
   {
      for (Entity e : this.entities)
      {
         if(e instanceof Character)
            return (Character) e;
      }
      return null;
   }

   public void setEntityTexture(Entity entity, ImageStore imageStore, String imageKey)
   {
      entity.setImages(imageStore.getImageList(imageKey));
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
         pos.x >= 0 && pos.x < this.numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return this.withinBounds(pos) && getOccupancyCell(pos) != null;
   }

   public boolean isOccupiedNotDirt(Point pos)
   {
      return this.withinBounds(pos)
              && !(getOccupancyCell(pos) == null || getOccupancyCell(pos).getClass().equals(Obstacle.class));
   }

   public Optional<Entity> findNearest(Point pos, Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : this.entities)
      {
         if (kind.isInstance(entity))
         {
            ofType.add(entity);
         }
      }

      return WorldModel.nearestEntity(ofType, pos);
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (this.withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(Point pos,
                             Background background)
   {
      if (this.withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (this.isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }

   private void setOccupancyCell(Point pos, Entity entity)
   {
      this.occupancy[pos.y][pos.x] = entity;
   }

   private Background getBackgroundCell(Point pos)
   {
      return this.background[pos.y][pos.x];
   }

   private void setBackgroundCell(Point pos,
                                 Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   private void tryAddEntity(Entity entity)
   {
      if (this.isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public void removeEntityAt(Point pos)
   {
      if (this.withinBounds(pos)
         && this.getOccupancyCell(pos) != null)
      {
         Entity entity = this.getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for debugging purposes */
         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         this.setOccupancyCell(pos, null);
      }
   }

   public void removeEntity(Entity entity)
   {
      this.removeEntityAt(entity.getPosition());
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (this.withinBounds(pos) && !pos.equals(oldPos))
      {
         this.setOccupancyCell(oldPos, null);
         this.removeEntityAt(pos);
         this.setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   /*
          Assumes that there is no entity currently occupying the
          intended destination cell.
       */
    public void addEntity(Entity entity)
    {
       if (this.withinBounds(entity.getPosition()))
       {
          this.setOccupancyCell(entity.getPosition(), entity);
          this.entities.add(entity);
       }
    }

   private static Optional<Entity> nearestEntity(List<Entity> entities,
                                                Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = nearest.getPosition().distanceSquared(pos);

         for (Entity other : entities)
         {
            int otherDistance = other.getPosition().distanceSquared(pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }
}
