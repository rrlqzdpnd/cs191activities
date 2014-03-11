import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image is an arrow.
 *
 * @author Guy Haas
 */
public class ArrowTurtle extends TurtlePixels
{
   //
   // constants
   //
   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;

   // default height and width of a square, ArrowTurtle's image
   private static final int DEFAULT_ARROW_HEIGHT = 24;
   private static final int DEFAULT_ARROW_WIDTH = 12;


   //
   // ArrowTurtle Constructor
   //
   public ArrowTurtle( Color color, float heading )
   { this( DEFAULT_ARROW_WIDTH, DEFAULT_ARROW_HEIGHT, color, heading ); }

   public ArrowTurtle( int width, int height, Color color, float heading )
   { super( width, height, color, heading ); }


   // Initialize the turtle's pixels, a square.
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      float center = ((float)turtleSideSize) / 2.0F;
      int turtleHeight = super.getTurtleHeight();
      int turtleWidth = super.getTurtleWidth();
      float gap = turtleWidth / 4;
      int right = Math.round(center-(turtleWidth/2.0F));
      int left = right + turtleWidth;
      int bottom = Math.round(center-(turtleHeight/2.0F));
      int top = bottom + turtleHeight;
      int g = Math.round(gap);
      int x1 = Math.round(center+(turtleHeight/2.0F)-turtleWidth);
      int y1 = Math.round(center + (gap/2.0F));
      int y2 = Math.round(center - (gap/2.0F));
      fillLinePixels(x1, y1, x1, left, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, left, top, Math.round(center), BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, y2, x1, right, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, right, top, Math.round(center), BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, y1, bottom, y1, BLACK_OPAQUE_PIXEL);
      fillLinePixels(bottom, y1, bottom, y2, BLACK_OPAQUE_PIXEL);
      fillLinePixels(x1, y2, bottom, y2, BLACK_OPAQUE_PIXEL);

   } // end initTurtlePixels()


} // end class ArrowTurtle
