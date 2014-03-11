import java.awt.Color;

/**
 * This class provides the turtle's appearance, the array of pixels
 * that make up its image.  In this case, the image is a cross.
 *
 * @author Guy Haas
 */
public class CrossTurtle extends TurtlePixels
{
   //
   // constants
   //
   private static final int BLACK_OPAQUE_PIXEL = 0xff000000;

   // default height and width of a square, CrossTurtle's image
   private static final int DEFAULT_CROSS_HEIGHT = 17;
   private static final int DEFAULT_CROSS_WIDTH = 17;


   //
   // CrossTurtle Constructor
   //
   public CrossTurtle( Color color, float heading )
   { this( DEFAULT_CROSS_WIDTH, DEFAULT_CROSS_HEIGHT, color, heading ); }

   public CrossTurtle( int width, int height, Color color, float heading )
   { super( (width>9) ? width : 9, (height>9) ? height : 9, color, heading ); }


   // drawLine is a wrapper-like method
   // it does 2 things that would distract from envisioning cross algorithm
   // it converts real numbers (points) to integer pixel row/column numbers
   // it subtracts 1 from all points since pixel array indicies start at 0
   private void drawLine( float x1, float y1, float x2, float y2 )
   {
      int c1 = Math.round(x1) - 1;
      int r1 = Math.round(y1) - 1;
      int c2 = Math.round(x2) - 1;
      int r2 = Math.round(y2) - 1;
      fillLinePixels(c1, r1, c2, r2, BLACK_OPAQUE_PIXEL);

   } // end drawLine()


   // Initialize the turtle's pixels, cross-hairs.
   // *note* initial turtle image's heading is to the east
   public void initTurtlePixels( int[] turtlePixels, int turtleSideSize )
   {
      float center = ((float)turtleSideSize) / 2.0F;
      int turtleHeight = super.getTurtleHeight();
      float hafHt = turtleHeight / 2.0F;
      int turtleWidth = super.getTurtleWidth();
      float hafWd = turtleWidth / 2.0F;
      float gap = turtleHeight / 8;
      if ( turtleWidth < turtleHeight )
         gap = turtleWidth / 8;
      float hafGap = gap / 2.0F;
      // right side of cross
      drawLine( center-hafGap, center+hafGap, center-hafGap, center+hafWd );
      drawLine( center-hafGap, center+hafWd, center+hafGap, center+hafWd );
      drawLine( center+hafGap, center+hafGap, center+hafGap, center+hafWd );
      // top of cross
      drawLine( center+hafGap, center+hafGap, center+hafHt, center+hafGap );
      drawLine( center+hafHt, center-hafGap, center+hafHt, center+hafGap );
      drawLine( center+hafGap, center-hafGap, center+hafHt, center-hafGap );
      // left side of cross
      drawLine( center+hafGap, center-hafGap, center+hafGap, center-hafWd );
      drawLine( center-hafGap, center-hafWd, center+hafGap, center-hafWd );
      drawLine( center-hafGap, center-hafGap, center-hafGap, center-hafWd );
      // bottom of cross
      drawLine( center-hafGap, center-hafGap, center-hafHt, center-hafGap );
      drawLine( center-hafHt, center-hafGap, center-hafHt, center+hafGap );
      drawLine( center-hafHt, center+hafGap, center-hafGap, center+hafGap );

   } // end initTurtlePixels()


} // end class CrossTurtle
