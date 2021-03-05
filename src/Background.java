import processing.core.PImage;

import java.util.List;

final class Background
{
   public String id;
   public List<PImage> images;
   public int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }

   public PImage getCurrentImage()
   {
      if (this instanceof Background)
      {
         return ((Background)this).images
                 .get(((Background)this).imageIndex);
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         this));
      }
   }
}
