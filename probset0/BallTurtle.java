import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image is a circle.
 *
 * @author Guy Haas
 */
public class BallTurtle extends TurtlePixels
{
   //
   // constants
   //
   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;
   private static final int DEFAULT_TURTLE_DIAMETER = 15;


   //
   // BallTurtle Constructors
   //
   public BallTurtle( Color color, float heading )
   { this( DEFAULT_TURTLE_DIAMETER, color, heading ); }

   public BallTurtle( int diameter, Color color, float heading )
   { super( diameter, diameter, color, heading ); }


   // Initialize the turtle's pixels, a square.
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      int centerX = turtleSideSize / 2;
      int centerY = turtleSideSize / 2;
      int radius = super.getTurtleHeight() / 2;
      fillCirclePixels( centerX, centerY, radius, BLACK_OPAQUE_PIXEL );

   } // end initTurtlePixels()


} // end class BallTurtle
