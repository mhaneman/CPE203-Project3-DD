import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   private static final int VIEW_WIDTH = 672;
   private static final int VIEW_HEIGHT = 880;

   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int WORLD_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

//   private static final int WORLD_WIDTH_SCALE = 2;
//   private static final int WORLD_HEIGHT_SCALE = 2;
//   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
//   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private long next_time;

   private int level = 1;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, "level" + level + ".sav", imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         this.scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }
      view.drawViewport();
      text("LEVEL " + level, 10, 20);
      drawGameOver();
      nextLevel();
   }

   private void drawGameOver()
   {
      if (world.getCharacter() == null)
      {
         level = 1;
         text("GAME OVER", (VIEW_WIDTH / 2) - 40, VIEW_HEIGHT / 2);
         text("PRESS R TO RESTART", (VIEW_WIDTH / 2) - 50, (VIEW_HEIGHT / 2) + 20);
      }
   }

   private void nextLevel()
   {
      if (world.getEntities().stream().filter(i -> i instanceof EntityHostile).count() <= 0)
      {
         level++;
         setup();
      }
   }

   public void keyPressed()
   {
      Character character = world.getCharacter();
      if (character != null)
      {
         switch (keyCode)
         {
            case UP:
               character.moveUp();
               break;

            case DOWN:
               character.moveDown();
               break;

            case LEFT:
               character.moveLeft(imageStore);
               break;

            case RIGHT:
               character.moveRight(imageStore);
               break;
            case KeyEvent.VK_SPACE:
               character.shoot(world, imageStore);
         }
      } else
      {
         switch (keyCode) {
            case KeyEvent.VK_R:
               System.out.println("restart");
               setup();
               break;
         }
      }
   }

   private static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.load(in, world);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities())
      {
         //Only start actions for entities that include action (not those with just animations)
         if (entity instanceof EntityDynamic)
            if (((EntityDynamic)entity).getActionPeriod() > 0)
               ((EntityDynamic)entity).scheduleActions(world, imageStore, scheduler);
      }
   }

   private static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
