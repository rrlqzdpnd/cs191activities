import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Rectangle;
import java.awt.Toolkit;

/*
 * This class implements a TurtleGraphics FILL graphics operation.
 *
 * Complexity in performing a FILL has its roots in the AWT's
 * mechanism for manipulating the RGB values of pixels representing
 * a Component.  The Component in TGCanvas' case is the Canvas
 * that has been drawn on.  Access to the pixels comes via the
 * in-memory Image which is maintained by TGCanvas.
 *
 * Due to an early emphasis placed on supporting the WWW (java
 * applets), image manipulation is asynchronous.  Specifically,
 * Graphics.drawImage() schedules/requests that the task is to be
 * done. IT DOESN'T NECESSARILY DO IT.  The situation is the same
 * going in the other direction, but PixelGrabber.grabPixels()
 * waits for completion.
 *
 * Soapbox Opinion: Asynchronous manipulation of an Image makes
 * sense when its source is bits out on the net, it makes no
 * sense when the bits are in memory.  In fact, the M$ Windows
 * implementation appears to treat the in-memory case specially
 * and do it synchronously.
 *
 * Second Complexity... once the FILL has been done on the pixels,
 * the new Image needs to be generated.  This is done with the
 * createImage() method.  The problem is that this is a "peer"
 * method, i.e., is different for each system.  On Apple OS-X
 * and Sun systems, createImage() dithers the pixels in the
 * process.  Nasty!!!  This means that back-to-back FILLs do not
 * work!  Worse yet, the dithering effects all pixels in the
 * rectangle passed to createImage().  So, I've added complexity
 * to limit the rectangle of pixels given to createImage().
 *
 * I'm not even going to comment on how ludicrus the decision
 * to dither supplied pixels was, especially with no alternative.
 *
 * @author Guy Haas
 */

class TGFillOp implements ImageObserver, TGGraphicsOp
{

   // constants
   //
   private static final int EXPAND_SIZE = 10;


   // variables with class-wide scope
   //
   private boolean waitingForImage;
   private Color color;        // FILL Color
   private Image sourceImage;
   private int maxFloodX;
   private int maxFloodY;
   private int minFloodX;
   private int minFloodY;
   private int sourceHeight;
   private int sourceWidth;
   private int subPixHt;
   private int subPixLeftX;
   private int subPixTopY;
   private int subPixWd;
   private int xCenter;
   private int yCenter;
   private int[] pixels;       // holds the RGB pixel representation
                               // of an Image
   private TGPoint point;      // center of the FILL operation


   //
   // constructor
   //
   public TGFillOp( TGPoint point, Color color )
   {
      this.color = color;
      this.point = point;
      waitingForImage = false;
   }


   private void addLeftPixels()
   {
      int addedPixWidth = EXPAND_SIZE;
      if ( subPixLeftX < EXPAND_SIZE )
         addedPixWidth = subPixLeftX;
      subPixLeftX -= addedPixWidth;
      getPixels( subPixLeftX, subPixTopY, addedPixWidth, subPixHt );
      subPixWd += addedPixWidth;

   } // end addLeftPixels()


   private void addLowerPixels()
   {
      int topY = subPixTopY + subPixHt;
      int addedPixHeight = EXPAND_SIZE;
      if ( topY+EXPAND_SIZE > sourceHeight )
         addedPixHeight = sourceHeight - topY;
      getPixels( subPixLeftX, topY, subPixWd, addedPixHeight );
      subPixHt += addedPixHeight;

   } // end addLowerPixels()


   private void addRightPixels()
   {
      int leftX = subPixLeftX + subPixWd;
      int addedPixWidth = EXPAND_SIZE;
      if ( leftX+EXPAND_SIZE > sourceWidth )
         addedPixWidth = sourceWidth - leftX;
      getPixels( leftX, subPixTopY, addedPixWidth, subPixHt );
      subPixWd += addedPixWidth;

   } // end addRightPixels()


   private void addUpperPixels()
   {
      int addedPixHeight = EXPAND_SIZE;
      if ( subPixTopY < EXPAND_SIZE )
         addedPixHeight = subPixTopY;
      subPixTopY -= addedPixHeight;
      getPixels( subPixLeftX, subPixTopY, subPixWd, addedPixHeight );
      subPixHt += addedPixHeight;

   } // end addUpperPixels()


   private void floodFill(int x, int y, int wd, int ht, int curRGB, int newRGB)
   {
      int pixIdx;

      //System.out.println("floodFill: x="+x+", y="+y);
      int leftLimit = x, rightLimit = x;
      if ( y < minFloodY )
         minFloodY = y;
      if ( y > maxFloodY )
         maxFloodY = y;
      for ( int i = x; i >= 0; i-- )
      {
         if ( i < subPixLeftX )
            addLeftPixels();
         pixIdx = y * wd + i;
         if ( (pixels[pixIdx] & 0xFFFFFF) != curRGB )
            break;
         pixels[pixIdx] = (pixels[pixIdx] & 0xFF000000) | newRGB;
         leftLimit = i;
      }
      if ( leftLimit < minFloodX )
         minFloodX = leftLimit;
      for ( int i = x+1; i < wd; i++ )
      {
         if ( i == (subPixLeftX + subPixWd) )
            addRightPixels();
         pixIdx = y * wd + i;
         if ( (pixels[pixIdx] & 0xFFFFFF) != curRGB )
            break;
         pixels[pixIdx] = (pixels[pixIdx] & 0xFF000000) | newRGB;
         rightLimit = i;
      }
      if ( rightLimit > maxFloodX )
         maxFloodX = rightLimit;
      if ( y > 0 )
      {
         for ( int i = leftLimit; i <= rightLimit; i++ )
         {
            int newY = y - 1;
            if ( newY < subPixTopY )
               addUpperPixels();
            pixIdx = newY * wd + i;
            if ( (pixels[pixIdx] & 0xFFFFFF) == curRGB )
               floodFill( i, newY, wd, ht, curRGB, newRGB );
         }
      }
      if ( y < ht-1 )
      {
         for ( int i = leftLimit; i <= rightLimit; i++ )
         {
            int newY = y + 1;
            if ( newY == (subPixTopY + subPixHt) )
               addLowerPixels();
            pixIdx = newY * wd + i;
            if ( (pixels[pixIdx] & 0xFFFFFF) == curRGB )
               floodFill( i, newY, wd, ht, curRGB, newRGB );
         }
      }

   } // end floodFill


   // print pixels in TG-coordinate-based rectangle,
   // i.e., x and y origins are at center of the graphicsImage.
   // x=leftmostEdge, y=bottomEdge
   private void printPixels( int x, int y, int wd, int ht )
   {
      int lin = yCenter - (y+(ht-1));
      System.out.print( "printPixels: sourceHeight="+sourceHeight+", YCenter=");
      System.out.println( yCenter+", lines "+lin+" .. "+(yCenter-y) );
      while ( lin <= yCenter-y )
      {
         System.out.print( x + "," + (yCenter - lin) + ":" );
         int col = x + xCenter;
         while ( col < x+xCenter+wd )
         {
            System.out.print( " " );
            int pixel = pixels[lin*sourceWidth + col];
            pixel &= 0xFFFFFF;
            System.out.print( (pixel >>> 16) + ".");
            pixel &= 0xFFFF;
            System.out.print( (pixel >>> 8) + ".");
            pixel &= 0xFF;
            System.out.print( pixel );
            col++;
         }
         System.out.println("");
         lin++;
      }
   
   } // end printPixels()


   // get color under my point and flood fill it and all its
   // neighbors, and their neighbors, etc... that are the
   // same color, with this operation's color
   //
   // *NOTE* the point where the fill operation is to start
   //        may not be within current bounds of the graphics
   //        Image.  in this case, the operation can not be
   //        performed.
   //        
   public Rectangle doIt( Image graphicsImage )
   {
      sourceImage = graphicsImage;
      sourceWidth = graphicsImage.getWidth( this );
      if ( sourceWidth == -1 )
         return null;
      int imageX = point.imageX( sourceWidth );
      if ( imageX < 0 || imageX >= sourceWidth )
         return null;
      xCenter = sourceWidth / 2;
      sourceHeight = graphicsImage.getHeight( this );
      if ( sourceHeight == -1 )
         return null;
      int imageY = point.imageY( sourceHeight );
      if ( imageY < 0 || imageY >= sourceHeight )
         return null;
      pixels = new int[sourceWidth * sourceHeight];
      yCenter = sourceHeight / 2;
      subPixLeftX = imageX - EXPAND_SIZE;
      if ( subPixLeftX < 0 )
         subPixLeftX = 0;
      subPixTopY = imageY - EXPAND_SIZE;
      if ( subPixTopY < 0 )
         subPixTopY = 0;
      subPixHt = EXPAND_SIZE * 2;
      if ( subPixHt > sourceHeight )
         subPixHt = sourceHeight;
      subPixWd = EXPAND_SIZE * 2;
      if ( subPixWd > sourceWidth )
         subPixWd = sourceWidth;
      if ( ! getPixels(subPixLeftX, subPixTopY, subPixWd, subPixHt) )
         return null;
      int curRGB = pixels[ imageY * sourceWidth + imageX ];
      curRGB &= 0xFFFFFF;
      int newRGB = color.getRGB();
      newRGB &= 0xFFFFFF;
      if ( curRGB == newRGB )
         return null;
      maxFloodX = minFloodX = imageX;
      maxFloodY = minFloodY = imageY;
      floodFill( imageX, imageY, sourceWidth, sourceHeight, curRGB, newRGB );
      //printPixels( -2, -2, 10, 20 );
      // create an ImageProducer, based on the new pixel data
      int pixelOffset = (sourceWidth * minFloodY) + minFloodX;
      int floodWidth = (maxFloodX+1) - minFloodX;
      int floodHeight = (maxFloodY+1) - minFloodY;
      MemoryImageSource mis = new MemoryImageSource( floodWidth, floodHeight,
                                                     pixels, pixelOffset, sourceWidth
                                                   );
      Image newImage = Toolkit.getDefaultToolkit().createImage( mis );
      drawImage( newImage, minFloodX, minFloodY );
      return new Rectangle( minFloodX, minFloodY, floodWidth, floodHeight );

   } // end doIt()


   private synchronized void drawImage( Image newImg, int x, int y )
   {
      //System.out.println("TGFillOp.drawImage: got here!" );
      Graphics g = sourceImage.getGraphics();
      waitingForImage = true;
      boolean retVal = g.drawImage( newImg, x, y, this );
      //System.out.println("TGFillOp.drawImage: retVal=" + retVal );
      if ( retVal )
         while ( waitingForImage )
         {
            try  { wait(); }
            catch (InterruptedException ie ) { }
            //System.out.println("TGFillOp.drawImage: wait() returned" );
         }
      waitingForImage = false;
      pixels = null;
      g.dispose();

   } // end drawImage()


   public Color getColor()
   { return color; }


   private boolean getPixels( int x, int y, int width, int height )
   {
      //System.out.print("getPixels: x="+x+", y="+y);
      //System.out.println(", wd="+width+", ht="+height);
      String me = "TGFillOp.getPixels: ";
      int pixelOffset = (sourceWidth * y) + x;
      PixelGrabber pg = new PixelGrabber( sourceImage, x, y, width, height,
                                          pixels, pixelOffset, sourceWidth
                                        );
      try { pg.grabPixels(); }
      catch (InterruptedException e)
      {
         System.err.println( me + "grabPixels interrupted" );
         return false;
      }
      if ( (pg.getStatus() & ImageObserver.ABORT) != 0 )
      {
         System.err.println( me + "grabPixels ImageObserver.ABORT" );
         return false;
      }
      return true;

   } // end getPixels()


   /*
    * This method is called when information about an image which was
    * previously requested using an asynchronous interface becomes available.
    *
    * This method should return true if further updates are needed or false
    * if the required information has been acquired. The image which was being
    * tracked is passed in using the img argument. Various constants are
    * combined to form the infoflags argument which indicates what information
    * about the image is now available. The interpretation of the x, y, width,
    * and height arguments depends on the contents of the infoflags argument. 
    */
   public synchronized boolean imageUpdate(Image img, int flags, int x, int y, int wd, int ht)
   {
      //System.out.print("TGFillOp.imageUpdate: flags=" );
      //if ( (flags & ImageObserver.ABORT) != 0 )
      //   System.out.print(" ABORT" );
      //if ( (flags & ImageObserver.ALLBITS) != 0 )
      //   System.out.print(" ALLBITS" );
      //if ( (flags & ImageObserver.ERROR) != 0 )
      //   System.out.print(" ERROR" );
      //if ( (flags & ImageObserver.FRAMEBITS) != 0 )
      //   System.out.print(" FRAMEBITS" );
      //if ( (flags & ImageObserver.HEIGHT) != 0 )
      //   System.out.print(" HEIGHT" );
      //if ( (flags & ImageObserver.PROPERTIES) != 0 )
      //   System.out.print(" PROPERTIES" );
      //if ( (flags & ImageObserver.SOMEBITS) != 0 )
      //   System.out.print(" SOMEBITS" );
      //if ( (flags & ImageObserver.WIDTH) != 0 )
      //   System.out.print(" WIDTH" );
      //System.out.print("\n                      x=" + x + ", y=" + y );
      //System.out.println( ", width=" + wd + ", height=" + ht );
      if ( ! waitingForImage )
         return false;
      if ( (flags & (ImageObserver.ABORT | ImageObserver.ALLBITS | ImageObserver.ERROR)) != 0 )
      {
         waitingForImage = false;
         notifyAll();
         return false;
      }
      return true;


   } // end imageUpdate()


} // end class TGFillOp
