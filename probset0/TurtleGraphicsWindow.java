import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Frame;

public class TurtleGraphicsWindow extends Frame
                                  implements TGKeyHandler, TGMouseHandler,
                                             WindowListener
{
   //
   // constants
   //
   private final static int CANVAS_HEIGHT = 400;
   private final static int CANVAS_WIDTH = 600;


   // variables with class-wide visibility (scope)
   //
   private int mouseX, mouseY;        // position of mouse when it
                                      // was last clicked
   private TGCanvas canvas;
   private Turtle turtle;


   // Constructors

   public TurtleGraphicsWindow()
   {
      addWindowListener( this );
      setLayout( new BorderLayout() );
      canvas = new TGCanvas( CANVAS_WIDTH, CANVAS_HEIGHT );
      canvas.setBackground( Color.white );
      canvas.addKeyHandler( this );
      canvas.addMouseHandler( this );
      add( "Center", canvas );
      turtle = new Turtle( canvas );
      pack();
      setVisible(true);
      return;

   } // end TurtleGraphicsWindow()


   //
   // TGKeyHandler interface methods
   //
   // TGCanvas will invoke this method since it has
   // been registered via canvas.addKeyHandler(this)
   // in the constructor. 
   public void keyPressed( int keyNum )
   { keypressed(keyNum); }

   // dummy declaration which child programs override
   public void keypressed(int keyNum) {}


   //
   // TGMouseHandler interface methods
   //
   // TGCanvas will invoke this method since it has
   // been registered via canvas.addMouseHandler(this)
   // in the constructor. 
   public void mouseClicked() {}
   public void mouseMoved() {}


   // WindowListener interface methods
   //
   public void windowActivated(WindowEvent we) {}
   public void windowClosed(WindowEvent we) {}
   public void windowClosing(WindowEvent we) { System.exit(0); }
   public void windowDeactivated(WindowEvent we) {}
   public void windowDeiconified(WindowEvent we) {}
   public void windowIconified(WindowEvent we) {}
   public void windowOpened(WindowEvent we) {}


  // TurtleGraphics Methods
  // Provided to allow the class extending TurtleGraphicsWindow
  // to invoke them without being forced to obtain and use the
  // Turtle object to invoke them.

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
  public void bk( int steps ) { turtle.bk( steps ); }

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
   public void back( int steps ) { turtle.bk( steps ); }

  /**
   * Clears the graphics area of the TurtleGraphics window.
   *
   * Note: Clean does not change the current position of
   * the turtle, its heading, the size of the pen it is
   * drawing with and/or the color of the pen it is
   * drawing with.
   */
   public void clean() { canvas.clean(); }


   /**
    * Return the color the Turtle is sitting on
    *
    * @see #pencolor
    * @see #setpc
    * @see #setpencolor
    */
   public int colorunder()
   { return turtle.colorunder(); }


   /**
    * Fill a bounded area in the graphics image.
    *
    * The current pixel, and any of its neighbors that are the
    * same color as it (and any of their neighbors that are the
    * same color as it, etc...) are changed to the current color.
    */
   public void fill() { turtle.fill(); }


  /**
   * Move the turtle forward along its current heading.  If the
   * pen is currently in the DOWN position, a line is drawn.
   *
   * Abbreviation for forward().  Both spellings need to
   * provided for compatibility.
   *
   * @param steps Number of pixels (in this implementation) to take.
   * @see #forward
   */
  public void fd( int steps ) { turtle.fd( steps ); }

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
  public void forward( int steps ) { turtle.fd( steps ); }


  /**
   * Return the Turtle's heading
   *
   * @see #seth
   * @see #setheading
   */
  public int heading() { return( turtle.heading() ); }


  /**
   * Move the turtle to the center of the display.  If the
   * pen is currently in the DOWN position, a line is drawn.
   *
   * Home is equivilent to setxy( 0, 0 )
   *
   * @see #setxy
   */
  public void home() { turtle.home(); }


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
  public void ht() { turtle.ht(); }

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
  public void hideturtle() { turtle.ht(); }


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
  public boolean ispendown() { return( turtle.ispendown() ); }


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
  public void label( String text ) { turtle.label( text ); }


  /**
   * Rotate the turtle counterclockwise by the specified
   * angle, measured in degrees.
   *
   * @param degrees Angle to change turtle's heading by.
   * @see #lt
   */
  public void left( int degrees ) { turtle.lt( degrees ); }

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
  public void lt( int degrees ) { turtle.lt( degrees ); }


  /**
   * Return the x-coordinate where last mouse click occured.
   *
   * @see #mousey
   */
  public int mousex()
  { return( canvas.mousex() ); }

  /**
   * Return the y-coordinate where last mouse click occured.
   *
   * @see #mousex
   */
  public int mousey()
  { return( canvas.mousey() ); }


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
  public void pd() { turtle.pd(); }

  /**
   * Put the turtle's pen in the down position.
   *
   * When the turtle moves, it will leave a trace from its
   * current position to its destination (its new position).
   * @see #ispendown
   * @see #pd
   * @see #penup
   * @see #pu
   */
  public void pendown() { turtle.pd(); }

  /**
   * Put the turtle's pen in the up position.
   *
   * When the turtle moves, it will leave no trace.
   * @see #ispendown
   * @see #pd
   * @see #pendown
   * @see #pu
   */
  public void penup() { turtle.pu(); }

  /**
   * Put the turtle's pen in the up position.
   *
   * When the turtle moves, it will leave no trace.
   * @see #ispendown
   * @see #pd
   * @see #pendown
   * @see #penup
   */
  public void pu() { turtle.pu(); }

  /**
   * Set the size of the text displayed in the graphics area.
   * @see #label
   */
  public void setlabelheight( int size )
  { turtle.setlabelheight( size ); }

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
  public void st() { turtle.st(); }

  /**
   * Show the turtle; make it visible.
   *
   * Long name for st().  Both spellings need to
   * provided for compatibility.
   *
   * @see #hideturtle
   * @see #ht
   * @see #st
   */
  public void showturtle() { turtle.st(); }

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
  public void setx( int new_X ) { turtle.setx( new_X ); }

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
  public void setxy( int new_X, int new_Y ) { turtle.setxy( new_X, new_Y ); }

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
  public void sety( int new_Y ) { turtle.sety( new_Y ); }

  /**
   * Rotate the turtle clockwise by the specified angle,
   * measured in degrees.
   *
   * @param degrees Angle to change turtle's heading by.
   * @see #rt
   */
  public void right( int degrees ) { turtle.rt( degrees ); }

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
  public void rt( int degrees ) { turtle.rt( degrees ); }

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
  public void seth( int degrees ) { turtle.seth( degrees ); }

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
  public void setheading( int degrees ) { turtle.seth( degrees ); }

  /**
   * Sets the color of the turtle's pen to the supplied number.
   * @param color_Num  numbers 0-15 are:
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
   * @see #setpencolor
   */
  public void setpc( int color_Num ) { turtle.setpc( color_Num ); }

  /**
   * Sets the color of the turtle's pen to the supplied number.
   * @param color_Num  numbers 0-15 are:
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
   * @see #setpencolor
   */
  public void setpencolor( int color_Num ) { turtle.setpc( color_Num ); }

  /**
   * Sets the width of the turtle's pen to the supplied number.
   * @param width  small positive number; 1 (or less) results
   * in a single pixel line. increasing numbers incrementally
   * add approximately 2 extra pixels in width
   */
  public void setpensize( int width ) { turtle.setpensize( width ); }


  /**
   * Return the Turtle's X-coordinate 
   * @see #setxy
   * @see #setx
   * @see #sety
   * @see #ycor
   */
  public int xcor() { return( (int) turtle.xcor() ); }

  /**
   * Return the Turtle's Y-coordinate
   * @see #setxy
   * @see #setx
   * @see #sety
   * @see #xcor
   */
  public int ycor() { return( (int) turtle.ycor() ); }

} // end class TurtleGraphicsWindow
