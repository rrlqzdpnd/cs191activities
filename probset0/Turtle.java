import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

/**
 * This class provides support for the *Turtle* (the object displayed which
 * is the source of all graphics activity).  It provides the actual image of
 * the turtle as well as support for all TurtleTalk commands and operators
 * which manipulate/query the turtle's state.
 *
 * @author Guy Haas
 */
public class Turtle extends Component
{
   //
   // constants
   //
   public static final int NORTH =   0;
   public static final int EAST =   90;
   public static final int SOUTH = 180;
   public static final int WEST =  270;

   public static final int BLACK =   0;
   public static final int BLUE =    1;
   public static final int GREEN =   2;
   public static final int CYAN =    3;
   public static final int RED =     4;
   public static final int MAGENTA = 5;
   public static final int YELLOW =  6;
   public static final int WHITE =   7;
   public static final int BROWN =   8;
   public static final int TAN =     9;
   public static final int FOREST = 10;
   public static final int AQUA =   11;
   public static final int SALMON = 12;
   public static final int VIOLET = 13;
   public static final int ORANGE = 14;
   public static final int GRAY =   15;

   public static final int TURTLE =    0;
   public static final int ARROW =     1;
   public static final int BALL =      2;
   public static final int BOX =       3;
   public static final int CROSS =     4;
   public static final int TRIANGLE =  5;

   private static final Color COLORS[] = new Color[16];

   static
   {
      COLORS[ BLACK ] = Color.black;
      COLORS[ BLUE ] = Color.blue;
      COLORS[ GREEN ] = Color.green;
      COLORS[ CYAN ] = Color.cyan;
      COLORS[ RED ] = Color.red;
      COLORS[ MAGENTA ] = Color.magenta;
      COLORS[ YELLOW ] = Color.yellow;
      COLORS[ WHITE ] = Color.white;
      COLORS[ BROWN ] = new Color(155,  96,  59);
      COLORS[ TAN ] = new Color(197, 136,  18);
      COLORS[ FOREST ] = new Color(100, 162,  64);
      COLORS[ AQUA ] = new Color(120, 187, 187);
      COLORS[ SALMON ] = new Color(255, 149, 119);
      COLORS[ VIOLET ] = new Color(144, 113, 208);
      COLORS[ ORANGE ] = Color.orange;
      COLORS[ GRAY ] = Color.lightGray;
   };

   private static final int INITIAL_PEN_SIZE = 2;
   private static final int INITIAL_FONT_SIZE = 14;
   private static final int INITIAL_FONT_STYLE = Font.PLAIN;
   private static final String INITIAL_FONT_NAME = "Courier";
   private static final Font INITIAL_FONT;
   static
   { 
      INITIAL_FONT = new Font( INITIAL_FONT_NAME,
                               INITIAL_FONT_STYLE,
                               INITIAL_FONT_SIZE
                             );
   } 
   private static final Color INITIAL_FOREGROUND = Color.black;


   // variables with class-wide scope
   //
   private boolean penDown;
   private boolean showTurtle;
   private Color curColor;
   private float curHeading;      // radians in conventional/AWT manner
   private Font curFont = INITIAL_FONT;
   private Image turtleImage;
   private int curPenSize;
   private MemoryImageSource turtleImageProducer;
   private TGCanvas tgc;          // where this turtle draws
   private TGPoint curPoint;      // current X,Y location of the turtle
   private TurtlePixels curTurtlePixels; // current turtle's image


   //
   // Turtle Constructors
   //
   public Turtle( TGCanvas tgc )
   {
      this.tgc = tgc;
      curFont = INITIAL_FONT;
      curColor = INITIAL_FOREGROUND;
      curPenSize = INITIAL_PEN_SIZE;
      curPoint = new TGPoint( 0, 0 );
      curHeading = (float) (Math.PI / 2.0);
      curTurtlePixels = new TurtleTurtle( curColor, curHeading );
      penDown = true;
      tgc.addTurtle( this );
      showTurtle = true;

   } // end Turtle()


   protected void finalize() throws Throwable
   {
      super.finalize();
      if ( showTurtle )
         tgc.removeTurtle( this );
      showTurtle = false;

   } // end finalize()


   private double getRadiansTwds(TGPoint frmPt, TGPoint toPt)
   {
      double retVal = 0.0;
      double frmX = (double) frmPt.xFloatValue();
      double frmY = (double) frmPt.yFloatValue();
      double toX = (double) toPt.xFloatValue();
      double toY = (double) toPt.yFloatValue();
      if ( frmX != toX || frmY != toY )
      {
         if ( frmX == toX )
            retVal = (frmY < toY) ? Math.PI/2.0 : Math.PI + Math.PI/2.0;
         else
         {
            double slope = (toY - frmY) / (toX - frmX);
            retVal = Math.atan(slope);
            if ( retVal == -0.0 )
               retVal = 0.0;
            if ( frmX > toX )
               retVal = retVal + Math.PI;
            if ( retVal < 0.0 )
               retVal += Math.PI*2;
         }
      }
      return retVal;

   } // end getRadiansTwds()


   private int rgbToPencolor( int rgbValue )
   {
      rgbValue &= 0xFFFFFF;
      for (int i=0; i < COLORS.length; i++)
         if ( (COLORS[i].getRGB() & 0xFFFFFF) == rgbValue  )
            return( i );
      return rgbValue;

   } // end rgbToPencolor()


   // -----------------------------------------------------------------
   // Methods available outside the Turtle class, sorted alphabetically
   // -----------------------------------------------------------------

   /**
    * Move the turtle backwards along its current heading.  If the
    * pen is currently in the DOWN position, a line is drawn.
    *
    * Long name for bk().  Both spellings need to provided for
    * compatibility.
    *
    * @param steps Number of pixels (in this implementation) to take.
    * @see #bk
    */
   public void back( double steps ) { bk( steps ); }
   public void back( float steps ) { bk( (double) steps ); }
   public void back( int steps ) { bk( (double) steps ); }

   /**
    * Move the turtle backwards along its current heading.  If the
    * pen is currently in the DOWN position, a line is drawn.
    *
    * Abbreviation for back().  Both spellings need to
    * provided for compatibility.
    *
    * @param steps Number of pixels (in this implementation) to take.
    * @see #back
    */
   public void bk( float steps ) { bk( (double) steps ); }
   public void bk( int steps ) { bk( (double) steps ); }
   public void bk( double steps )
   {
      if ( penDown )
         curPoint = tgc.drawLine( curPoint,
                                  -steps,
                                  (double) curHeading,
                                  curPenSize,
                                  curColor);
      else
         curPoint = curPoint.otherEndPoint((double) curHeading, -steps);
      if ( penDown  || showTurtle )
         tgc.repaint();

   } // end bk()


   /**
    * Return the color the Turtle is sitting on
    *
    * @see #pencolor
    * @see #setpc
    * @see #setpencolor
    */
   public int colorunder()
   {
      return rgbToPencolor( tgc.colorunder(curPoint) );

   } // end colorunder()


   /**
    * Fill a bounded area in the graphics image.
    *
    * The current pixel, and any of its neighbors that are the
    * same color as it (and any of their neighbors that are the
    * same color as it, etc...) are changed to the current color.
    */
    public void fill()
    {
       tgc.fill( curPoint, curColor );
       tgc.repaint();

    } // end fill()


   /**
    * Move the turtle forward along its current heading.  If the
    * pen is currently in the DOWN position, a line is drawn.
    *
    * Abbreviation for forward().  Both spellings need to
    * provided for compatibility.
    *
    * @param steps Distance in TurtleSpace to move.
    * @see #forward
    */
   public void fd( float steps ) { fd( (double) steps ); }
   public void fd( int steps ) { fd( (double) steps ); }
   public void fd( double steps )
   {
      if ( penDown )
         curPoint = tgc.drawLine( curPoint,
                                  steps,
                                  (double) curHeading,
                                  curPenSize,
                                  curColor );
      else
         curPoint = curPoint.otherEndPoint((double) curHeading, steps);
      if ( penDown  || showTurtle )
         tgc.repaint();

   } // end fd()

   /**
    * Move the turtle forward along its current heading.  If the
    * pen is currently in the DOWN position, a line is drawn.
    *
    * Long name for fd().  Both spellings need to provided for
    * compatibility.
    *
    * @param steps Number of pixels (in this implementation) to take.
    * @see #fd
    */
   public void forward( double steps ) { fd( steps ); }
   public void forward( float steps ) { fd( (double) steps ); }
   public void forward( int steps ) { fd( (double) steps ); }


   /**
    * Return the turtle's Image
    */
   public Image getImage()
   {
      while ( turtleImage == null )
      {
         int[] turtlePixels = curTurtlePixels.getPixels();
         int turtleSideSize = curTurtlePixels.getSideSize();
         // create turtleImage to match the array of pixels
         // AWT Graphics only supports painting of Image objects, no kind
         // of BitBlt for arrays of pixel values (?who know's why?)
         if ( turtleImageProducer == null )
            turtleImageProducer = new MemoryImageSource( turtleSideSize,
                                                         turtleSideSize,
                                                         turtlePixels,
                                                         0,
                                                         turtleSideSize
                                                       );
         turtleImage = createImage( turtleImageProducer );
      }
      return turtleImage;

   } // end getImage()


   /**
    * Return the size of a side of the turtle's Image
    *
    * @see #getImage
    */
   public int getImageSideSize()
   { return curTurtlePixels.getSideSize(); }


   /**
    * Return the width of the pen the turtle is currently
    * writing with
    *
    * @see #setpensize
    */
   public int pensize()
   { return( curPenSize ); }


   /**
    * Return the Turtle's heading
    *
    * @see #seth
    * @see #setheading
    */
   // Logo's TurtleSpace doesn't match the mathematical
   // convention of measuring angles counter-clockwise from
   // the positive X axis.  Logo defines NORTH (+Y axis) as
   // 0 degrees and degrees increase in the clockwise
   // direction, so East is 90 degrees, South is 180 degrees
   // and West is 270 degrees.  The module-wide variable
   // curHeading contains the mathematically-correct
   // heading in radians, not Logo's point of view.  So, we
   // must convert curHeading to TurtleSpace's scheme.
   //
   public int heading()
   {
      int degrees = (int) Math.rint( curHeading / (Math.PI/180.0) );
      int turtleSpaceDegrees = 360 - degrees;
      turtleSpaceDegrees += 90;
      turtleSpaceDegrees %= 360;
      return( turtleSpaceDegrees );
   }


   /**
    * Move the turtle to the center of the display.  If the
    * pen is currently in the DOWN position, a line is drawn.
    *
    * Home is equivilent to setxy( 0, 0 )
    *
    * @see #setxy
    */
   public void home()
   {
      setxy( 0, 0 );
      //seth( 0 );

   } // end home()


   /**
    * Hide the turtle; make it invisible.
    *
    * Abbreviation for hideturtle().  Both spellings need to
    * provided for compatibility.
    *
    * @see #hideturtle
    * @see #showturtle
    * @see #st
    */
   public void ht()
   {
      if ( showTurtle )
      {
         tgc.removeTurtle( this );
         tgc.repaint();
         showTurtle = false;
      }

   } // end ht()


   /**
    * Hide the turtle; make it invisible.
    *
    * Long name for ht().  Both spellings need to provided for
    * compatibility.
    *
    * @see #ht
    * @see #showturtle
    * @see #st
    */
   public void hideturtle() { ht(); }


   /**
    * Return the current status of the pen.
    *
    * Return true if the turtle's pen is down or
    * false if it in the up position.
    *
    * @see #pendown
    * @see #penup
    * @see #pd
    * @see #pu
    */
   public boolean ispendown() { return penDown; }


   /**
    * Paints a String of characters on the display.
    * The text is painted in the current pen's color,
    * starting at the current position of the turtle.
    *
    * The text is always painted in the standard
    * horizontal manner, i.e., the heading of the
    * turtle is ignored.
    *
    * @param text Characters to be painted on the display.
    */
   public void label( String text )
   {
      if ( text != null )
      {
         tgc.label( text, curPoint, curFont, curColor );
         tgc.repaint();
      }

   } // end label()


   /**
    * Rotate the turtle counterclockwise by the specified
    * angle, measured in degrees.
    *
    * @param degrees Angle to change turtle's heading by.
    * @see #lt
    */
   public void left( float degrees ) { lt( degrees ); }
   public void left( int degrees ) { lt( (float) degrees ); }

   /**
    * Rotate the turtle counterclockwise by the specified
    * angle, measured in degrees.
    *
    * Abbreviation for left().  Both spellings need
    * to provided for compatibility.
    *
    * @param degrees Angle to change turtle's heading by.
    * @see #left
    */
   // Logo degrees increase in the clockwise direction.
   // Since this does not match the mathematical convention
   // of measuring angles counterclockwise, we must convert
   // the parameter.
   public void lt( float degrees ) { lt( (double) degrees ); }
   public void lt( int degrees ) { lt( (double) degrees ); }
   public void lt( double degrees )
   {
      float radians = (float) (degrees * (Math.PI/180.0));
      curHeading += radians;
      if ( curHeading > Math.PI * 2.0 )
         curHeading -= Math.PI * 2.0;
      if ( showTurtle )
         if ( curTurtlePixels.setTurtleHeading(curHeading) )
         {
            turtleImage = null;
            tgc.repaint();
         }

   } // end lt()


   /**
    * Put the turtle's pen in the down position.
    *
    * When the turtle moves, it will leave a trace from its
    * current position to its destination (its new position).
    * @see #ispendown
    * @see #pendown
    * @see #pu
    * @see #penup
    */
   public void pd()
   {
      penDown = true;
   }


   /**
    * Return the color the pen is currently writing in
    *
    * @see #setpc
    * @see #setpencolor
    */
   public int pencolor()
   {
      int rgbVal = curColor.getRGB();
      return rgbToPencolor( rgbVal );

   } // end pencolor()


   /**
    * Put the turtle's pen in the down position.
    *
    * When the turtle moves, it will leave a trace from its
    * current position to its destination (its new position).
    * @see #ispendown
    * @see #pd
    * @see #pu
    * @see #penup
    */
   public void pendown() { pd(); }


   /**
    * Put the turtle's pen in the up position.
    *
    * When the turtle moves, it will leave no trace.
    * @see #ispendown
    * @see #pd
    * @see #pendown
    * @see #penup
    */
   public void pu()
   {
     penDown = false;
   }


   /**
    * Put the turtle's pen in the up position.
    *
    * When the turtle moves, it will leave no trace.
    * @see #ispendown
    * @see #pd
    * @see #pendown
    * @see #pu
    */
   public void penup() { pu(); }


   public static Color rgbToColor( int rgbValue )
   {
      rgbValue &= 0xFFFFFF;
      if ( rgbValue < COLORS.length )
         return( COLORS[rgbValue] );
      return new Color( rgbValue );

   } // end rgbToColor()


   /**
    * Rotate the turtle clockwise by the specified angle,
    * measured in degrees.
    *
    * @param degrees Angle to change turtle heading.
    * @see #rt
    */
   public void right( double degrees ) { rt( degrees ); }
   public void right( float degrees ) { rt( (double) degrees ); }
   public void right( int degrees ) { rt( (double) degrees ); }

   /**
    * Rotate the turtle clockwise by the specified angle,
    * measured in degrees.
    *
    * Abbreviation for right().  Both spellings need
    * to provided for compatibility.
    *
    * @param degrees Angle to change turtle's heading by.
    * @see #right
    */
   // Logo degrees increase in the clockwise direction.
   // Since this does not match the mathematical convention
   // of measuring angles counterclockwise, we must convert
   // the parameter.
   public void rt( float degrees ) { rt( (double) degrees ); }
   public void rt( int degrees ) { rt( (double) degrees ); }
   public void rt( double degrees )
   {
      float radians = (float) (degrees * (Math.PI/180.0));
      curHeading -= radians;
      if ( curHeading < 0.0F )
         curHeading += Math.PI * 2.0;
      if ( showTurtle )
         if ( curTurtlePixels.setTurtleHeading(curHeading) )
         {
            turtleImage = null;
            tgc.repaint();
         }

   } // end rt()


   /**
    * Set the size of the text displayed in the graphics area.
    * @see #label
    */
   public void setlabelheight( int size )
   {
      if ( curFont.getSize() != size )
         curFont = new Font(INITIAL_FONT_NAME, INITIAL_FONT_STYLE, size);

   } // end setlabelheight()


   /**
    * Turns the turtle to the specified absolute heading.
    * The heading is specified in degrees (units of 1/360th
    * of a circle) with 0 being North (+Y axis), increasing
    * clockwise.  So, East is 90 degrees, South is 180 degrees,
    * and West is 270 degrees.
    *
    * Abbreviation for setheading().  Both spellings
    * need to provided for compatibility.
    *
    * @param degrees number of 1/360ths increments clockwise
    * from the positive Y axis.
    * @see #setheading
    */
   // Logo defines North (+Y axis) as 0 degrees and degrees
   // increase in the clockwise direction, so East is 90
   // degrees, South is 180 degrees and West is 270 degrees.
   // Since this does not match the mathematical convention
   // of measuring angles counterclockwise from the positive
   // X axis, we must convert the parameter.  The module-wide
   // variable (curHeading) contains the mathematically-
   // correct heading, not Logo's point of view.
   //
   public void seth( int turtleSpaceDegrees )
   {
      int degrees = (360 - (turtleSpaceDegrees % 360));
      degrees += 90;
      degrees %= 360;
      float newHeading = (float) (degrees * (Math.PI/180.0));
      curHeading = newHeading;
      if ( showTurtle )
         if ( curTurtlePixels.setTurtleHeading(curHeading) )
         {
            turtleImage = null;
            tgc.repaint();
         }

   } // end seth()


   /**
    * Turns the turtle to the specified absolute heading.
    * The heading is specified in degrees (units of 1/360th
    * of a circle) with 0 being North (+Y axis), increasing
    * clockwise.  So, East is 90 degrees, South is 180 degrees,
    * and West is 270 degrees.
    * @param degrees number of 1/360ths increments clockwise
    * from the positive Y axis.
    * @see #seth
    */
   public void setheading(int degrees) { seth(degrees); }
  

   /**
    * Sets the color of the turtle's pen to the supplied number.
    * @param colorNum  numbers 0-15 are:
    *
    * Number  Color       Number  Color
    * ------  -------     ------  -------
    *    0    black          8    brown
    *    1    blue           9    tan
    *    2    green         10    forest
    *    3    cyan          11    aqua
    *    4    red           12    salmon
    *    5    magenta       13    violet
    *    6    yellow        14    orange
    *    7    white         15    grey
    *
    * Color numbers greater than 15 will be assumed to be RGB
    * values with the red component in bits 16-23, the green
    * component in bits 8-15, and the blue component in bits
    * 0-7. The actual color used in rendering will depend on
    * finding the best match given the color space available
    * for a given display.
    * @see #getpencolor
    * @see #setpencolor
    */
   public void setpc( int colorNum )
   {
      Color color;

      if ( colorNum >= 0 && colorNum <= 15 )
         color = COLORS[colorNum];
      else
      {
         colorNum &= 0xFFFFFF;
         color = new Color( colorNum );
      }
      if ( curColor.getRGB() != color.getRGB() )
      {
         curColor = color;
         if ( showTurtle )
            if ( curTurtlePixels.setTurtleColor(color) )
            {
               turtleImage = null;
               tgc.repaint();
            }
      }

  } // end setpc()


   /**
    * Sets the color of the turtle's pen to the supplied number.
    * @param colorNum  numbers 0-15 are:
    *
    * Number  Color       Number  Color
    * ------  -------     ------  -------
    *    0    black          8    brown
    *    1    blue           9    tan
    *    2    green         10    forest
    *    3    cyan          11    aqua
    *    4    red           12    salmon
    *    5    magenta       13    violet
    *    6    yellow        14    orange
    *    7    white         15    grey
    *
    * Color numbers greater than 15 will be assumed to be RGB
    * values with the red component in bits 16-23, the green
    * component in bits 8-15, and the blue component in bits
    * 0-7. The actual color used in rendering will depend on
    * finding the best match given the color space available
    * for a given display.
    * @see #getpencolor
    * @see #setpc
    */
   public void setpencolor(int colorNum) { setpc(colorNum); }
  

   /**
    * Sets the width of the turtle's pen to the supplied number.
    * @param width  small positive number; 1 (or less) results
    * in a single pixel line.
    */
   public void setpensize( int width )
   {
      if ( width == curPenSize )
         return;
      if ( width < 1 )
         curPenSize = 1;
      else
         curPenSize = width;

   } // end setpensize()
  

   /**
    * Sets the shape of a turtle - its pixel image.
    * @param shapeNum  small positive number; 0 for default
    * turtle; see constants (e.g., BALL, BOX, etc...) for
    * other turtle image shapes...
    * @param params  an optional int array containing sizing
    * information hints, e.g. radius of a ball, ...
    */
   public void setshape( int shapeNum, int[] params )
   {
      TurtlePixels newTurtlePixels = null;
      int width = 0;
      if ( params != null )
         width = params[0];
      int height = width;
      if ( (params != null) && (params.length >= 2))
         height = params[1];
      switch ( shapeNum )
      {
         case TURTLE:
            newTurtlePixels = new TurtleTurtle( curColor, curHeading );
            break;
         case ARROW:
            if ( params != null )
               newTurtlePixels = new ArrowTurtle( width, height, curColor, curHeading );
            else
               newTurtlePixels = new ArrowTurtle( curColor, curHeading );
            break;
         case BALL:
            if ( params != null )
               newTurtlePixels = new BallTurtle( width, curColor, curHeading );
            else
               newTurtlePixels = new BallTurtle( curColor, curHeading );
            break;
         case BOX:
            if ( params != null )
               newTurtlePixels = new BoxTurtle( width, height, curColor, curHeading );
            else
               newTurtlePixels = new BoxTurtle( curColor, curHeading );
            break;
         case CROSS:
            if ( params != null )
               newTurtlePixels = new CrossTurtle( width, height, curColor, curHeading );
            else
               newTurtlePixels = new CrossTurtle( curColor, curHeading );
            break;
         case TRIANGLE:
            newTurtlePixels = new TriangleTurtle( curColor, curHeading );
            break;
      }
      if ( newTurtlePixels != null )
      {
         curTurtlePixels = newTurtlePixels;
         turtleImage = null;
         turtleImageProducer = null;
         tgc.repaint();
      }

   } // end setshape()


   /**
    * Move the turtle to an absolute display position.
    *
    * Move the turtle horizontally to a new location
    * specified as an X coordinate argument.
    * @param newX the X-coordinate of destination.
    * @see #home
    * @see #setxy
    * @see #sety
    */
   public void setx( int newX )
   { setx((float) newX); }

   public void setx( float newX )
   {
      TGPoint p2 = new TGPoint( (float) newX, curPoint.yFloatValue() );
      double heading = 0;
      if ( newX < curPoint.xFloatValue() )
         heading += Math.PI;
      if ( penDown )
         tgc.drawLine( curPoint, p2, heading, curPenSize, curColor );
      curPoint = p2;
      if ( penDown  || showTurtle )
         tgc.repaint();

   } // end setx()


   /**
    * Move the turtle to an absolute display position.
    *
    * Move the turtle to the x and y coordinates provided
    * as arguments.
    * @param newX the X-coordinate of destination.
    * @param newY the Y-coordinate of destination.
    * @see #home
    * @see #setx
    * @see #sety
    */
   public void setxy( int newX, int newY )
   { setxy( new TGPoint(newX, newY) ); }

   public void setxy( float newX, float newY )
   { setxy( new TGPoint(newX, newY) ); }


   /**
    * Move the turtle to an absolute display position.
    *
    * Move the turtle to the x and y coordinates provided in the
    * TGPoint parameter.
    * @param newPt a TGPoint objext containing the X-coordinate
    *              and Y-coordinate of destination.
    * @see #home
    * @see #setx
    * @see #sety
    */
   public void setxy( TGPoint newPt )
   {
      if ( penDown )
      {
         double heading = getRadiansTwds( curPoint, newPt );
         tgc.drawLine( curPoint, newPt, heading, curPenSize, curColor );
      }
      curPoint = newPt;
      if ( penDown  || showTurtle )
         tgc.repaint();

   } // end setxy()


   /**
    * Move the turtle to an absolute display position.
    *
    * Move the turtle vertically to a new location
    * specified as an Y coordinate argument.
    * @param newY the Y-coordinate of destination.
    * @see #home
    * @see #setx
    * @see #setxy
    */
   public void sety( int newY )
   { sety((float) newY); }

   public void sety( float newY )
   {
      TGPoint p2 = new TGPoint( curPoint.xFloatValue(), newY );
      double heading = Math.PI/2.0;
      if ( newY < curPoint.yFloatValue() )
         heading += Math.PI;
      if ( penDown )
         tgc.drawLine( curPoint, p2, heading, curPenSize, curColor );
      curPoint = p2;
      if ( penDown  || showTurtle )
         tgc.repaint();

   } // end sety()


   /**
    * Show the turtle; make it visible.
    *
    * Long name for st().  Both spellings need to provided for
    * compatibility.
    *
    * @see #hideturtle
    * @see #ht
    * @see #st
    */
   public void showturtle() { st(); }


   /**
    * Show the turtle; make it visible.
    *
    * Abbreviation for showturtle().  Both spellings need to
    * provided for compatibility.
    *
    * @see #hideturtle
    * @see #ht
    * @see #showturtle
    */
   public void st()
   {
      if ( ! showTurtle )
      {
         if ( curTurtlePixels.setTurtleColor(curColor) )
            turtleImage = null;
         if ( curTurtlePixels.setTurtleHeading(curHeading) )
            turtleImage = null;
         tgc.addTurtle( this );
         tgc.repaint();
         showTurtle = true;
      }

   } // end st()


   /**
    * Return the Turtle's X-coordinate 
    * @see #setxy
    * @see #setx
    * @see #sety
    * @see #ycor
    */
   public float xcor()
   { return curPoint.xFloatValue(); }

   /**
    * Return the Turtle's Y-coordinate
    * @see #setxy
    * @see #setx
    * @see #sety
    * @see #xcor
    */
   public float ycor()
   { return curPoint.yFloatValue(); }

} // end class Turtle
