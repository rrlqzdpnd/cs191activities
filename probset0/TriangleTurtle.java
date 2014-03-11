import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image an isoseles
 * triangle - the standard Logo turtle. 
 *
 * @author Guy Haas
 */
public class TriangleTurtle extends TurtlePixels
{
   //
   // constants
   //

   // height and width of the isosceles triangle that represents the
   // default turtle.  initTurtlePixels() assumes that the height is
   // greater than or equal to the width.  both the height and the
   // width should be odd numbers so that the image looks good when
   // rotated around its center bit
   private static final int TURTLE_HEIGHT = 21;
   private static final int TURTLE_WIDTH = 15;

   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;


   //
   // TriangleTurtle Constructor
   //
   public TriangleTurtle( Color color, float heading )
   { super( TURTLE_WIDTH, TURTLE_HEIGHT, color, heading ); }


   // Initialize the turtle's pixels, an isosceles triangle, into the
   // provided int[] turtlePixels.  TurtlePixels EXPECTS/REQUIRES the
   // image to be on its side, pointing EAST, aligned with the positive
   // X-axis, a heading of mathematical 0 degrees (not to be confused
   // with TurtleSpace's coordinate system where 0 degrees is NORTH,
   // the positive Y-axis).
   //
   // First, the base is drawn as a vertical line, from its top to
   // its bottom. then, the legs of the isosceles triangle are drawn.
   //
   // The code that does this is an adaptation of the "midpoint"
   // technique for scanline creation (see van Aken, J., and Novak,
   // Mark. Curve Drawing Algorithms for Raster Displays. ACM
   // Transactions on Graphics. Apr 1985.)  also available in the
   // classic book: Computer Graphics: Principles and Practice, by
   // Foley, van Dam, Feiner, Hughes
   //
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      int center, x, y;

      center = turtleSideSize / 2;
      x = center - TURTLE_HEIGHT / 2;
      y = center - TURTLE_WIDTH / 2;
      while ( y <= center + TURTLE_WIDTH/2 )
      {
         turtlePixels[y*turtleSideSize + x] = BLACK_OPAQUE_PIXEL;
         y++;
      }
      int deltaX = TURTLE_HEIGHT;
      int deltaY = TURTLE_WIDTH/2;
      int incrE = 2 * deltaY;
      int incrNE = 2 * (deltaY - deltaX);
      int which = (2 * deltaY) - deltaX;
      int lowerY = center + TURTLE_WIDTH/2;
      int upperY = center - TURTLE_WIDTH/2;
      while ( x <= center + TURTLE_HEIGHT/2 )
      {
         if ( which <= 0 )
         {
            which += incrE;
            x++;
         }
         else
         {
            which += incrNE;
            x++;
            lowerY--;
            upperY++;
         }
         turtlePixels[lowerY * turtleSideSize + x] = BLACK_OPAQUE_PIXEL;
         turtlePixels[upperY * turtleSideSize + x] = BLACK_OPAQUE_PIXEL;
      }

   } // end initTurtlePixels()


} // end class TriangleTurtle
