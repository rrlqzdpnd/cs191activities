import java.awt.Color;

/**
 * TurtlePixels is an interface that a class implements when
 * it wants to provide the pixels that make up the image of
 * a turtle.
 *
 * @author Guy Haas
 */

public abstract class TurtlePixels
{
   //
   // constants
   //
   private static final int MAX_TURTLE_HEIGHT = 100;
   private static final int MAX_TURTLE_WIDTH =  100;
   private static final int MIN_TURTLE_HEIGHT =   5;
   private static final int MIN_TURTLE_WIDTH =    5;
   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;
   private static final int PIXEL_OPACITY_BITS = 0xff000000;
   private static final int WHITE_OPAQUE_PIXEL = 0xffffffff;


   // variables with class-wide scope
   //
   private boolean[] turtleFillMask;    // used to determine which pixels in
                                        // baseTurtlePixels[] change when the
                                        // pen color changes
   private Color turtleColor;		// inner pixels Color
   private float turtleHeading;         // radians in conventional/AWT manner
   private int pixRectSideSize;         // this needs to be computed such
                                        // that it is larger than either the
                                        // turtle's height or the turtle's
                                        // width so the corners of the image
                                        // of the turtle are not clipped when
                                        // drawn at 45 degree angles
   private int turtleHeight;            // the height of the turtle as given
                                        // in the constructor
   private int turtleWidth;             // the width of the turtle as given
                                        // in the constructor
   private int[] baseTurtlePixels;      // pixel array for the turtle in an
                                        // EASTern orientation, i.e. headed
                                        // along the positive X axis
   private int[] turtlePixRect;         // pixel array for current turtle's
                                        // image - if it's visible.  it is
                                        // oriented in the current heading
                                        // and its body is filled with the
                                        // current color


   //
   // TurtlePixels Constructor
   //
   public TurtlePixels( int width, int height, Color color, float heading )
   {
      if ( height > MAX_TURTLE_HEIGHT )
         height = MAX_TURTLE_HEIGHT;
      if ( height < MIN_TURTLE_HEIGHT )
         height = MIN_TURTLE_HEIGHT;
      if ( width > MAX_TURTLE_WIDTH )
         width = MAX_TURTLE_WIDTH;
      if ( width < MIN_TURTLE_WIDTH )
         width = MIN_TURTLE_WIDTH;
      turtleHeight = height;
      turtleWidth = width;
      if ( height >= width )
         pixRectSideSize = height + height/4;
      else
         pixRectSideSize = width + width/4;
      if ( pixRectSideSize % 2 != 0 )
         pixRectSideSize++;
      baseTurtlePixels = new int[pixRectSideSize * pixRectSideSize];
      for (int pixIdx=0; pixIdx < baseTurtlePixels.length; pixIdx++)
         baseTurtlePixels[pixIdx] = WHITE_OPAQUE_PIXEL;
      initTurtlePixels( baseTurtlePixels, pixRectSideSize );
      clearToTurtleEdge();
      buildTurtleFillMask();
      turtleColor = color;
      int pixel = turtleColor.getRGB();
      pixel |= PIXEL_OPACITY_BITS;
      for (int pixIdx=0; pixIdx < baseTurtlePixels.length; pixIdx++)
         if ( turtleFillMask[pixIdx] == true )
            baseTurtlePixels[pixIdx] = pixel;
      turtleHeading = heading;
      updateTurtlePixels( heading );

   } // end TurtlePixels()


   // Build a mask of pixels that are to be changed when
   // the foreground color is changed.  Starting with
   // the center pixel of the turtle image, a flood-fill
   // algorithm is used to identify all pixels with the
   // same color value.  These pixels are set to true in
   // a mask array. colorTurtle() uses this mask to change
   // the color of the turtle.
   //
   // Although baseTurtlePixels is manipulated as an array
   // with a single index, it is really a two dimensional
   // array, a series of rows of pixels.  Each row is
   // pixRectSideSize pixels in width.  This explains why
   // pixelIndex = rowNumber * pixRectSideSize + columnNumber
   // is used to calculate a pixel's index.
   //
   private void buildTurtleFillMask()
   {
      float center = ((float)pixRectSideSize ) / 2.0F;
      int row = Math.round(center) - 1;
      int column = row;
      int color = baseTurtlePixels[ row * pixRectSideSize + column ];
      turtleFillMask = new boolean[pixRectSideSize * pixRectSideSize];
      floodFill( row, column, color );

   } // end buildTurtleFillMask


   // circles have an eight-way symmetry to them
   // only the points on a 45 degree segment (one eighth) of a circle
   // need to be computed - the rest are reflections of this point
   // given a center-point and deltas to a point on the circle, fill
   // eight points on the circle with a supplied value
   // 
   private void circlePixels( int ctrX, int ctrY, int dX, int dY, int value )
   {
      fillPixel(  ctrX + dX,  ctrY + dY, value );
      fillPixel(  ctrX + dY,  ctrY + dX, value );
      fillPixel(  ctrX + dY,  ctrY - dX, value );
      fillPixel(  ctrX + dX,  ctrY - dY, value );
      fillPixel(  ctrX - dX,  ctrY - dY, value );
      fillPixel(  ctrX - dY,  ctrY - dX, value );
      fillPixel(  ctrX - dY,  ctrY + dX, value );
      fillPixel(  ctrX - dX,  ctrY + dY, value );

   } // end circlePixels


   // clearToTurtleEdge - set all white pixels outside the
   // edge of the turtle to be transparent
   //
   private void clearToTurtleEdge()
   {
      for (int y=0; y < pixRectSideSize; y++ )
         for (int x=0; x < pixRectSideSize; x++ )
         {
            int i = y * pixRectSideSize + x;
            if ( (baseTurtlePixels[i] & 0x00ffffff) == 0x00ffffff )
               baseTurtlePixels[i] = 0;
            else
               break;
         }
      for (int y=0; y < pixRectSideSize; y++ )
         for (int x=pixRectSideSize-1; x >= 0; x-- )
         {
            int i = y * pixRectSideSize + x;
            if ( (baseTurtlePixels[i] & 0x00ffffff) == 0x00ffffff )
               baseTurtlePixels[i] = 0;
            else
               break;
         }
      for (int x=0; x < pixRectSideSize; x++ )
         for (int y=0; y < pixRectSideSize; y++ )
         {
            int i = y * pixRectSideSize + x;
            if ( baseTurtlePixels[i]  == 0 )
               continue;
            if ( (baseTurtlePixels[i] & 0x00ffffff) == 0x00ffffff )
               baseTurtlePixels[i] = 0;
            else
               break;
         }
      for (int x=0; x < pixRectSideSize; x++ )
         for (int y=pixRectSideSize-1; y >= 0; y-- )
         {
            int i = y * pixRectSideSize + x;
            if ( baseTurtlePixels[i]  == 0 )
               continue;
            if ( (baseTurtlePixels[i] & 0x00ffffff) == 0x00ffffff )
               baseTurtlePixels[i] = 0;
            else
               break;
         }

   } // end clearToTurtleEdge()


   // NOTE: x1 MUST BE <= x2
   //
   private void fillHorizLine( int x1, int x2, int y, int value )
   {
      for (int x=x1; x <= x2; x++)
         fillPixel( x, y, value );

   } // end fillHorizLine()


   // NOTE: y1 MUST BE <= y2
   //
   private void fillVertLine( int x, int y1, int y2, int value )
   {
      for (int y=y1; y <= y2; y++)
         fillPixel( x, y, value );

   } // end fillVertLine()


   // Fill in pixels that make up a line where X is increasing in
   // integer units while Y values are changing by a specified delta.
   // NOTE: x0 MUST BE <= x1
   //
   private void fillXUnitLine(int x0, int y0, int x1, int y1, int value)
   {
      //System.out.print("TurtlePixels.fillXUnitLine: x0="+x0+", y0="+y0);
      //System.out.println(", x1="+x1+", y1="+y1);
      int dX = x1 - x0;
      boolean negSlope = false;
      int dY = y1 - y0;
      int y = y0;
      if  ( dY < 0 )
      {
         negSlope = true;
         dY = -dY;
      }
      int d = 2 * dY - dX;
      int incrE = 2 * dY;
      int incrNE = 2 * (dY - dX);
      fillPixel( x0, y0, value );
      while ( x0 < x1 )
      {
         x0++;
         if ( d <= 0 )
            d += incrE;
         else
         {
            d += incrNE;
            y++;
         }
         if ( negSlope )
            fillPixel( x0, y0-(y-y0), value );
         else
            fillPixel( x0, y, value );
      }

   } // end fillXUnitLine()


   // Fill in pixels that make up a line where Y is increasing in
   // integer units while X values are changing by a specified delta.
   // NOTE: y1 MUST BE <= y2
   //
   private void fillYUnitLine(int x0, int y0, int  x1, int  y1, int value)
   {
      //System.out.print("TurtlePixels.fillYUnitLine: x0="+x0+", y0="+y0);
      //System.out.println(", x1="+x1+", y1="+y1);
      boolean negSlope = false;
      int dX = x1 - x0;
      int x = x0;
      if  ( dX < 0 )
      {
         negSlope = true;
         dX = -dX;
      }
      int dY = y1 - y0;
      int d = 2 * dX - dY;
      int incrE = 2 * dX;
      int incrNE = 2 * (dX - dY);
      fillPixel( x0, y0, value );
      while ( y0 < y1 )
      {
         y0++;
         if ( d <= 0 )
            d += incrE;
         else
         {
            d += incrNE;
            x++;
         }
         if ( negSlope )
            fillPixel( x0-(x-x0), y0, value );
         else
            fillPixel( x, y0, value );
      }

   } // end fillYUnitLine()


   private void floodFill( int row, int column, int color )
   {
      int pixIdx = row * pixRectSideSize + column;
      if ( baseTurtlePixels[pixIdx] == color && !turtleFillMask[pixIdx] )
      {
         turtleFillMask[pixIdx] = true;
         if ( row - 1 >= 0 )
            floodFill( row - 1, column, color );
         if ( row + 1 < pixRectSideSize )
            floodFill( row + 1, column, color );
         if ( column - 1 >= 0 )
            floodFill( row, column - 1, color );
         if ( column + 1 < pixRectSideSize )
            floodFill( row, column + 1, color );
      }

   } // end floodFill


   //private void printPixels(String what, int[] pixels, int width, int height)
   //{
   //   for ( int y=0; y < height; y++ )
   //   {
   //      for ( int x=0; x < width; x++ )
   //      {
   //         int i = y * width + x;
   //         int a = (pixels[i] >> 24) & 0xff;
   //         int r = (pixels[i] >> 16) & 0xff;
   //         int g = (pixels[i] >> 8) & 0xff;
   //         int b = pixels[i] & 0xff;
   //         System.out.println(what+"["+i+"] a="+a+", r="+r+", g="+g+", b="+b);
   //      }
   //      System.out.println( "----------" );
   //   }
   //
   //} // end printPixels()


   private float slope( int x1, int y1, int x2, int y2 )
   { return ((float) y2 - y1) / ((float) x2 - x1); }


   // fill turtlePixRect[] to reflect the specified heading (in radians)
   // which is the amount of rotation needed since baseTurtlePixels[] is
   // aligned to 0.0.  i use a 'Reverse-Rotation' algorithm, computing
   // which pixel in the original image maps to every pixel in the new
   // image (vs projecting forward) to avoid holes due to rounding errors.
   // since i'm rotating around the center of the image (not its origin),
   // three steps are needed:
   // (1) translate origin to center,
   // (2) reverse rotatation of pixel x,y, and
   // (3) translate back to initial origin.
   //
   private void updateTurtlePixels( float heading )
   {
      if ( turtlePixRect == null )
         turtlePixRect = new int[pixRectSideSize * pixRectSideSize];
      if ( heading == 0.0F )
         // no translation if heading aligned with orig image
         for (int i=0; i < pixRectSideSize * pixRectSideSize; i++)
            turtlePixRect[i] = baseTurtlePixels[i];
      else
      {
         // int center = Math.round(((float)pixRectSideSize ) / 2.0F) - 1;
         int center = pixRectSideSize / 2;
         double theta = (double) heading;
         double cosTheta = Math.cos( theta );
         double sinTheta = Math.sin( theta );

         // first zap destination, make all pixels transparent
         for (int y=0; y < pixRectSideSize; y++)
            for (int x=0; x < pixRectSideSize; x++)
               turtlePixRect[y * pixRectSideSize + x] = 0;
         for (int row=0; row < pixRectSideSize; row++)
         {
            int rowIdx = row * pixRectSideSize;
            int rowPrime = 2*(row - center) + 1;
            for (int col=0; col < pixRectSideSize; col++)
            {
               int colPrime = 2*(col - center) + 1;
               int srcX = (int) Math.rint(colPrime*cosTheta - rowPrime*sinTheta);
               srcX =  (srcX - 1) / 2 + center;
               int srcY = (int) Math.rint(colPrime*sinTheta + rowPrime*cosTheta);
               srcY =  (srcY - 1) / 2 + center;
               if ( srcX < 0 || srcX >= pixRectSideSize )
                  continue;
               if ( srcY < 0 || srcY >= pixRectSideSize )
                  continue;
               int pixelIndex = srcY * pixRectSideSize + srcX;
               int pixel = baseTurtlePixels[ pixelIndex ];
               turtlePixRect[rowIdx + col] = pixel;
            }
         }
      }

   } //end updateTurtlePixels()


   // ---------------------------------------------------------------
   // Methods available outside the this class, sorted alphabetically
   // ---------------------------------------------------------------

   /**
    * fill pixels along the circumference of a circle
    *
    * This is an implementation of the simplest form of the midpoint
    * algorithm as described in Computer Graphics, Foley and van Dam 
    */
   public void fillCirclePixels( int ctrX, int ctrY, int radius, int value )
   {
      int deltaX = 0;
      int deltaY = radius;
      float midptVal = 5.0F / 4.0F - (float) radius;
      circlePixels( ctrX, ctrY, deltaX, deltaY, value );
      while ( deltaY > deltaX )
      {
         if ( midptVal < 0 )
         {
            midptVal += 2.0F * (float)deltaX + 3.0F;
            deltaX++;
         }
         else
         {
            midptVal += 2.0F * (float)(deltaX-deltaY) + 5.0F;
            deltaX++;
            deltaY--;
         }
         circlePixels( ctrX, ctrY, deltaX, deltaY, value );
      }

   } // end fillCirclePixels()


   public void fillLinePixels( int x0, int y0, int x1, int y1, int value )
   {
      //System.out.print("TurtlePixels.fillLinePixels: x0="+x0+", y0="+y0);
      //System.out.println(", x1="+x1+", y1="+y1);
      if ( x0 == x1 )
         if ( y0 <= y1 )
            fillVertLine( x0, y0, y1, value );
         else
            fillVertLine( x0, y1, y0, value );
      else if ( y0 == y1 )
         if ( x0 <= x1 )
            fillHorizLine( x0, x1, y0, value );
         else
            fillHorizLine( x1, x0, y0, value );
      else if ( Math.abs(slope(x0,y0,x1,y1)) > 1.0F )
         if ( y0 < y1 )
            fillYUnitLine(x0, y0, x1, y1, value);
         else
            fillYUnitLine(x1, y1, x0, y0, value);
      else if ( x0 < x1 )
         fillXUnitLine(x0, y0, x1, y1, value);
      else
         fillXUnitLine(x1, y1, x0, y0, value);

   } // end fillLinePixels()


   /**
    * Fill the specified (x,y) pixel in the turtle's pixels with
    * value.  Used in child's initTurtlePixels() to paint the
    * turtle's image.
    *
    */
   public void fillPixel( int x, int y, int value )
   {
      String me = "TurtlePixels.fillPixel: ";
      String outOfBounds = " out of bounds";

      //System.out.println( me + "x=" + x  + ", y=" + y );
      if ( x < 0 || x >= pixRectSideSize )
         System.err.println( me + "x=" + x + outOfBounds );
      else if ( y < 0 || y >= pixRectSideSize )
         System.err.println( me + "y=" + y + outOfBounds );
      else
         baseTurtlePixels[ x + (y * pixRectSideSize) ] = value;

   } // end fillPixel()


   /**
    * Return a pointer to the turtle's pixels
    */
   public int[] getPixels()
   { return turtlePixRect; }


   /**
    * Return the size of a side of the turtle's pixRect
    */
   public int getSideSize()
   { return pixRectSideSize; }
   

   /**
    *  Return the Color of the turtle's inner pixels
    */
   public Color getTurtleColor()
   { return turtleColor; }


   /**
    * Return the turtle's current heading (radians)
    */
   public float getTurtleHeading()
   { return turtleHeading; }


   /**
    * Return the height of the turtle's image
    */
   public int getTurtleHeight()
   { return turtleHeight; }


   /**
    * Return the height of the turtle's image
    */
   public int getTurtleWidth()
   { return turtleWidth; }


   /**
    * Initializes the pixels that make up the turtle's image.
    *
    */
   public abstract void initTurtlePixels( int[] turtlePixRect,
                                          int pixRectSideSize );
   

   /**
    * Fill inner pixels of the turtle with the specified color.
    *
    */
   public boolean setTurtleColor( Color newColor )
   {
      if ( newColor != turtleColor )
      {
         int rgbVals = newColor.getRGB();
         for (int pixIdx=0; pixIdx < baseTurtlePixels.length; pixIdx++)
            if ( turtleFillMask[pixIdx] == true )
            {
               int pixel = baseTurtlePixels[pixIdx] & PIXEL_OPACITY_BITS;
               pixel += rgbVals;
               baseTurtlePixels[pixIdx] = pixel;
            }
         updateTurtlePixels( turtleHeading );
	 turtleColor = newColor;
         return true;
      }
      return false;

   } // end setTurtleColor()


   /**
    * Rotate the turtle to a specified heading (radians).
    */
   public boolean setTurtleHeading( float newHeading )
   {
      if ( this instanceof BallTurtle )
         return false;
      if ( Math.abs(turtleHeading - newHeading) > 0.001 )
      {
         turtleHeading = newHeading;
         updateTurtlePixels( newHeading );
         return true;
      }
      return false;

   } // end setTurtleHeading()


} // end class TurtlePixels
