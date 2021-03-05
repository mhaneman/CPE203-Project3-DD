import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   private static final String DRAGON_KEY = "dragon";
   private static final int DRAGON_NUM_PROPERTIES = 6;
   private static final int DRAGON_ID = 1;
   private static final int DRAGON_COL = 2;
   private static final int DRAGON_ROW = 3;
   private static final int DRAGON_ACTION_PERIOD = 4;
   private static final int DRAGON_ANIMATION_PERIOD = 5;

   private static final String OBSTACLE_KEY = "obstacle";
   private static final int OBSTACLE_NUM_PROPERTIES = 4;
   private static final int OBSTACLE_ID = 1;
   private static final int OBSTACLE_COL = 2;
   private static final int OBSTACLE_ROW = 3;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;

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

   public boolean processLine(String line,
                              ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, imageStore);
            case OBSTACLE_KEY:
               return parseObstacle(properties, imageStore);
            case DRAGON_KEY:
               return parseDragon(properties, imageStore);
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
         Entity entity = new Obstacle(properties[OBSTACLE_ID],
            pt, imageStore.getImageList(OBSTACLE_KEY));
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

         Entity entity = new Dragon(properties[DRAGON_ID], pt, imageStore.getImageList(DRAGON_KEY),
                 Integer.parseInt(properties[DRAGON_ACTION_PERIOD]),
                 Integer.parseInt(properties[DRAGON_ANIMATION_PERIOD]));
         tryAddEntity(entity);
      }
      return properties.length == DRAGON_NUM_PROPERTIES;
   }

   private boolean parseBackground(String[] properties, ImageStore imageStore)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
            Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         setBackground(pt,
            new Background(id, imageStore.getImageList(id)));
      }
      return properties.length == BGND_NUM_PROPERTIES;
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < this.numRows &&
         pos.x >= 0 && pos.x < this.numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return this.withinBounds(pos) &&
         getOccupancyCell(pos) != null;
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

   private void setBackground(Point pos,
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

   private Entity getOccupancyCell(Point pos)
   {
      return this.occupancy[pos.y][pos.x];
   }

   private void setOccupancyCell(Point pos,
                                Entity entity)
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

   private void removeEntityAt(Point pos)
   {
      if (this.withinBounds(pos)
         && this.getOccupancyCell(pos) != null)
      {
         Entity entity = this.getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
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
