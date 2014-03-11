import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image is approximately
 * a turtle.
 *
 * @author Guy Haas
 */
public class TurtleTurtle extends TurtlePixels
{
   //
   // constants
   //

   // height and width of a square, TurtleTurtle's image
   private static final int TURTLE_HEIGHT = 30;
   private static final int TURTLE_WIDTH = 25;

   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;


   //
   // TurtleTurtle Constructor
   //
   public TurtleTurtle( Color color, float heading )
   {
      super( TURTLE_WIDTH, TURTLE_HEIGHT, color, heading );

   } // end TurtleTurtle()


   // Initialize the turtle's pixels, a square.
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      int xb = (turtleSideSize - TURTLE_HEIGHT) / 2;
      int yb = (turtleSideSize - TURTLE_WIDTH) / 2;
      // head
      fillLinePixels( xb+29, yb+12, xb+26, yb+15, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+29, yb+12, xb+26, yb+9, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+26, yb+15, xb+22, yb+14, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+26, yb+9, xb+22, yb+10, BLACK_OPAQUE_PIXEL);
      // body
      fillLinePixels( xb+22, yb+13, xb+19, yb+18, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+16, yb+21, xb+10, yb+20, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+5, yb+18, xb+4, yb+13, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+22, yb+11, xb+19, yb+6, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+16, yb+3, xb+10, yb+4, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+5, yb+6, xb+4, yb+11, BLACK_OPAQUE_PIXEL);
      // tail
      fillLinePixels( xb+0, yb+12, xb+4, yb+13, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+0, yb+12, xb+4, yb+11, BLACK_OPAQUE_PIXEL);
      // left front leg
      fillLinePixels( xb+20, yb+24, xb+19, yb+19, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+19, yb+24, xb+17, yb+22, BLACK_OPAQUE_PIXEL);
      // right front leg
      fillLinePixels( xb+20, yb+0, xb+19, yb+5, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+19, yb+0, xb+17, yb+2, BLACK_OPAQUE_PIXEL);
      // left rear leg
      fillLinePixels( xb+5, yb+22, xb+9, yb+20, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+4, yb+22, xb+6, yb+19, BLACK_OPAQUE_PIXEL);
      // right rear leg
      fillLinePixels( xb+5, yb+2, xb+9, yb+4, BLACK_OPAQUE_PIXEL);
      fillLinePixels( xb+4, yb+2, xb+6, yb+5, BLACK_OPAQUE_PIXEL);

   } // end initTurtlePixels()


} // end class TurtleTurtle
