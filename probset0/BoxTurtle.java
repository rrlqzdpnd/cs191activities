import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image is a square.
 *
 * @author Guy Haas
 */
public class BoxTurtle extends TurtlePixels
{
   //
   // constants
   //
   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;

   // default height and width of a square, BoxTurtle's image
   private static final int DEFAULT_BOX_HEIGHT = 15;
   private static final int DEFAULT_BOX_WIDTH = 15;


   //
   // BoxTurtle Constructor
   //
   public BoxTurtle( Color color, float heading )
   { this( DEFAULT_BOX_WIDTH, DEFAULT_BOX_HEIGHT, color, heading ); }

   public BoxTurtle( int width, int height, Color color, float heading )
   { super( width, height, color, heading ); }


   // Initialize the turtle's pixels, a square.
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      int turtleHeight = super.getTurtleHeight();
      int turtleWidth = super.getTurtleWidth();
      float center = ((float)turtleSideSize) / 2.0F;
      float leftX = center - ((float) turtleHeight)/2.0F;
      float bottomY = center - ((float) turtleWidth)/2.0F;
      int x1 = Math.round(leftX);
      int y1 = Math.round(bottomY);
      int x2 = x1 + turtleHeight-1;
      int y2 = y1 + turtleWidth-1;
      fillLinePixels(x1, y1, x1, y2, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, y1, x2, y1, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x2, y1, x2, y2, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, y2, x2, y2, BLACK_OPAQUE_PIXEL);

   } // end initTurtlePixels()


} // end class BoxTurtle
