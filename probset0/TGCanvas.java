import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.awt.Rectangle;
import java.lang.Math;

/**
 * This class is an implementation of a graphics window, a Java
 * AWT Canvas.  All graphics is saved for re-display in the event
 * of resizing. Other features are:
 *
 * - the coordinate space is the traditional mathematics; [0,0] is
 *   in the middle of the graphics window.
 *
 * - KeyEvents; keyPressed() is captured and can be passed on.
 *
 * - MouseEvents; mouseReleased() is captured and can be passed on.
 *
 * - higher-level functionality (i.e., flood-fill provided). 
 *
 * - in-memory Image maintained with buffered operations to improve
 *   graphics display performance. 
 *
 * @author Guy Haas
 */
public class TGCanvas extends Component
                      implements FocusListener, KeyListener,
                                 MouseListener, MouseMotionListener
{

   // global constants
   //
   public static final int MAX_TURTLES = 64;
   public static final int MINIMUM_HEIGHT = 40;
   public static final int MINIMUM_WIDTH = 40;


   // constants
   //
   private static final String CLASS_NAME = "TGCanvas";

   private static final Color INITIAL_BACKGROUND = Color.white;
   private static final Color INITIAL_PEN_COLOR = Color.black;
   private static final int GI_HEIGHT = 1201;  // needs to be odd to allow for
   private static final int GI_WIDTH = 1601;   // zero at center and equal num
                                               // of neg/pos ints above/below

   private final static int INIT_NUM_GRAFOPS = 500;  // initial size of pending
                                                     // graphics operations
                                                     // buffer (an array)
   private final static int INCR_NUM_GRAFOPS = 200;  // incremental growth amount
                                                     // when need to expand the
                                                     // graphics operations array

   private static final int INITIAL_FONT_SIZE = 14;
   private static final int INITIAL_FONT_STYLE = Font.PLAIN;
   private static final int INITIAL_PEN_SIZE = 2;
   private static final String INITIAL_FONT_NAME = "Courier";

   private final static int NUM_KEYHANDLERS = 3;     // number of TGKeyHandler
                                                     // objects supported. as of
                                                     // v.9.29 (09/17/08) only
                                                     // one object (TGDriver)
                                                     // registers for callbacks
   private final static int NUM_MOUSEHANDLERS = 3;   // number of TGMouseHandler
                                                     // objects supported. as of
                                                     // v.9.29 (09/17/08) only
                                                     // one object (TGDriver)
                                                     // registers for callbacks
   
   
   // the following STATES are needed by paint() due to the
   // async model provided by drawImage()
   private static final int PAINT_REFRESH = 0;
   private static final int PAINT_DRAW_GRAPHICS = 1;
   private static final int PAINT_ERASE_TURTLES = 2;
   private static final int PAINT_DRAW_TURTLES = 3;



   // ----- ------
   // Class Fields
   // ----- ------

   private boolean gotFocus;            // used to determine when to pass on
                                        // mouseMoved Events to TGDriver
   private int canvasHeight;
   private int canvasWidth;
   private int numGraphicsOps;          // number of graphics operations that
                                        // are buffered in graphicsOps array
   private int mouseX, mouseY;          // position of mouse when last clicked
   private int paintState;              // used in paint() to determine what
                                        // needs to be done. drawImage() does
                                        // not necessarily complete and thus
                                        // forces follow-up passes of paint()
   private int paintTurtleNum;          // used in paint() to determine which
                                        // turtle is being erased/painted
   private int xCenter, yCenter;        // these AWT graphics coordinates will
                                        // be [0,0] for the TGCanvas
                                        // coordinate space. *NOTE* i tried
                                        // making these "float" but when they
                                        // had a .5 fractional part, pixel
                                        // choice by AWT was poorer. i ended
                                        // up with lines with endpoints one
                                        // pixel apart instead of straight
   private Color background;
   private Image graphicsImage;         // in-memory Image for the composite
                                        // graphics - all the stuff on the
                                        // display except for the turtle(s)
   private Rectangle[] turtleClipRect;  // clipRects used to draw turtle images
   private Turtle[] turtles;            // array of turtles that want to be
                                        // displayed 
   private TGGraphicsOp[] graphicsOps;  // a list/queue of Graphics operations
                                        // pending processing
   private TGKeyHandler[] keyHandlers;  // Objects that want their keyPressed()
                                        // method invoked when our KeyListener
                                        // interface: keyPressed() method
                                        // is invoked, propagating key stuff
   private TGMouseHandler[] mouseHandlers; // Objects that want their
                                        // mouseClicked() method invoked when
                                        // our MouseListener interface:
                                        // mouseReleased() method is invoked,
                                        // propagating mouse stuff


   //
   // TGCanvas Constructors
   //
   public TGCanvas() { this( 700, 400 ); }

   public TGCanvas( int width, int height )
   {
      if ( width > MINIMUM_WIDTH )
         canvasWidth = width;
      else
         canvasWidth = MINIMUM_WIDTH;
      if ( height > MINIMUM_HEIGHT )
         canvasHeight = height;
      else
         canvasHeight = MINIMUM_HEIGHT;
      super.setSize( canvasWidth, canvasHeight );
      addFocusListener(this);
      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener(this);
      xCenter = canvasWidth / 2;
      yCenter = canvasHeight / 2;
      Font font =  new Font( INITIAL_FONT_NAME,
                             INITIAL_FONT_STYLE,
                             INITIAL_FONT_SIZE
                           );
      setFont( font );
      background = INITIAL_BACKGROUND;
      graphicsOps = new TGGraphicsOp[ INIT_NUM_GRAFOPS ];
      numGraphicsOps = 0;
      keyHandlers = new TGKeyHandler[ NUM_KEYHANDLERS ];
      mouseHandlers = new TGMouseHandler[ NUM_MOUSEHANDLERS ];
      turtleClipRect = new Rectangle[ MAX_TURTLES ];
      turtles = new Turtle[ MAX_TURTLES ];
      paintState = PAINT_REFRESH;
      gotFocus = false;

   } // end TGCanvas()


   //
   // FocusListener methods
   //
   public void focusGained(FocusEvent e)
   { gotFocus = true; }

   public void focusLost(FocusEvent e)
   { gotFocus = false; }


   //
   // KeyListener  Interface Support
   //
   public void keyPressed(KeyEvent ke)
   {
      int keyCode = ke.getKeyCode();
      int tgKeyNum = 0;
      switch ( keyCode )
      {
         case KeyEvent.VK_DOWN:
            tgKeyNum = TGKeyHandler.DOWN;
            break;
         case KeyEvent.VK_ENTER:
            tgKeyNum = TGKeyHandler.ENTER;
            break;
         case KeyEvent.VK_LEFT:
            tgKeyNum = TGKeyHandler.LEFT;
            break;
         case KeyEvent.VK_RIGHT:
            tgKeyNum = TGKeyHandler.RIGHT;
            break;
         case KeyEvent.VK_SPACE:
            tgKeyNum = TGKeyHandler.SPACE;
            break;
         case KeyEvent.VK_UP:
            tgKeyNum = TGKeyHandler.UP;
            break;
      }
      if ( tgKeyNum == 0 )
         return;
      for (int idx=0; idx < keyHandlers.length; idx++)
         if ( keyHandlers[idx] != null )
            keyHandlers[idx].keyPressed( tgKeyNum );

   } // end keyPressed()


   /**
    * Invoked when a key has been released.
    */
   public void keyReleased(KeyEvent ke) {}


   /**
    * Invoked when a key has been typed.
    */
   public void keyTyped(KeyEvent ke)
   {
      char ch = ke.getKeyChar();
      //System.out.println( "keyTyped: " + ch + ", " + (int) ch );
      //System.out.println( "          " + ke.getKeyCode() );
      for (int idx=0; idx < keyHandlers.length; idx++)
         if ( keyHandlers[idx] != null )
            keyHandlers[idx].keyPressed( ch );

   } // end keyTyped()


   // MouseListener Interface Support
   //
   // I intended to only support mouseClicked(), but too many clicks
   // of the mouse never got to the program.  Searching Sun's Java
   // Developer web site turned up an explanation: if the mouse is
   // moved at all between the press and the release, it is considered
   // a mouse-stroke instead of a mouse-click.  i was not able to find
   // any details on how one could adjust a threshold differentiating
   // the two, so the solution is to use mouseReleased() instead of
   // mouseClicked()
   //
   // Currently, TGCanvas only supports capturing and propagating a
   // left-button release (with no modifiers, e.g. [Shift] key down).
   // So, if a MouseEvent is received that isn't for TGCanvas, the
   // AWT tree is walked and if a MouseListener is found in a parent
   // component - the MouseEvent is given to it.
   //
   private void fwdMouseEvent( int id, MouseEvent me )
   {
      boolean popupTrigger = me.isPopupTrigger();
      long when = me.getWhen();
      int clickCount = me.getClickCount();
      int modifiers = me.getModifiers();
      int x = me.getX();
      int y = me.getY();
      Rectangle r = getBounds();
      x += r.x;
      y += r.y;
      Container parent = getParent();
      while ( parent != null )
      {
         Class c = parent.getClass();
         Class[] interfaces = c.getInterfaces();
         for ( int i=0; i < interfaces.length; i++)
         {
            String name = interfaces[i].getName();
            if ( name.equals("java.awt.event.MouseListener") )
            {
               MouseEvent nme = new MouseEvent(parent, id, when, modifiers,
                                               x, y, clickCount, popupTrigger);
               switch ( id )
               {
                 case MouseEvent.MOUSE_PRESSED:
                     ((MouseListener)parent).mousePressed( nme );
                     return;
                 case MouseEvent.MOUSE_RELEASED:
                     ((MouseListener)parent).mouseReleased( nme );
                     return;
                 case MouseEvent.MOUSE_CLICKED:
                     ((MouseListener)parent).mouseClicked( nme );
                     return;
                 case MouseEvent.MOUSE_ENTERED:
                     ((MouseListener)parent).mouseEntered( nme );
                     return;
                 case MouseEvent.MOUSE_EXITED:
                     ((MouseListener)parent).mouseExited( nme );
                     return;
                 case MouseEvent.MOUSE_MOVED:
                     ((MouseMotionListener)parent).mouseMoved( nme );
                     return;
                 case MouseEvent.MOUSE_DRAGGED:
                     ((MouseMotionListener)parent).mouseDragged( nme );
                     return;
                 default:
                     System.err.println(CLASS_NAME+".fwdMouseEvent: bad id");
                     return;
               }
            }
         }
         r = parent.getBounds();
         x += r.x;
         y += r.y;
         parent = parent.getParent();
      }

   } // end fwdMouseEvent()


   // MouseListener interface methods 
   //

   public void mouseClicked(MouseEvent me)
   { fwdMouseEvent( MouseEvent.MOUSE_CLICKED, me ); }
   public void mouseEntered(MouseEvent me)
   { fwdMouseEvent( MouseEvent.MOUSE_ENTERED, me ); }
   public void mouseExited(MouseEvent me)
   { fwdMouseEvent( MouseEvent.MOUSE_EXITED, me ); }
   public void mousePressed(MouseEvent me)
   { fwdMouseEvent( MouseEvent.MOUSE_PRESSED, me ); }

   public void mouseReleased(MouseEvent me)
   {
      int modifiersMask = me.getModifiers();
      if ( modifiersMask == InputEvent.BUTTON1_MASK )
      {
         mouseX = me.getX();
         mouseY = me.getY();
         for (int idx=0; idx < mouseHandlers.length; idx++)
            if ( mouseHandlers[idx] != null )
               mouseHandlers[idx].mouseClicked();
         this.requestFocus();
      }
      else
         fwdMouseEvent( MouseEvent.MOUSE_RELEASED, me );

   } // end mouseReleased()


   // MouseMotionListener interface methods 



   // ------------------- --------- -------
   // MouseMotionListener interface methods 
   // ------------------- --------- -------

   /**
    * Invoked when a mouse button is pressed on TGCanvas and
    * the mouse is dragged.
    */
   public void mouseDragged(MouseEvent me) { }


   /**
    * Invoked when the mouse is moved around on the TGCanvas
    * but no buttons have been pushed.
    */
   public void mouseMoved(MouseEvent me)
   {
      if ( gotFocus )
      {
         mouseX = me.getX();
         mouseY = me.getY();
         for (int idx=0; idx < mouseHandlers.length; idx++)
            if ( mouseHandlers[idx] != null )
               mouseHandlers[idx].mouseMoved();
      }
   }



   //
   // support methods only used in this class
   //


   /*
    * An attempt has been made to append another graphics operation to
    * the queue of outstanding ones and there is no room for it. Allocate
    * a new larger queue and transfer entries from old queue into it.
    */
   private void expandGraphicsOps()
   {
      TGGraphicsOp[] oldGraphicsOps = graphicsOps;
      graphicsOps = new TGGraphicsOp[ oldGraphicsOps.length + INCR_NUM_GRAFOPS ];
      System.arraycopy( oldGraphicsOps, 0, graphicsOps, 0, numGraphicsOps );
      //System.out.println( "Expanded graphicsOps to " + graphicsOps.length );
   }


   private void clearGraphicsImage()
   {
      if ( graphicsImage != null )
      {
         Graphics giGraphics = graphicsImage.getGraphics();
         giGraphics.setClip( 0, 0, GI_WIDTH-1, GI_HEIGHT-1 );
         giGraphics.setColor( background );
         giGraphics.fillRect( 0, 0, GI_WIDTH-1, GI_HEIGHT-1 );
         giGraphics.setColor( INITIAL_PEN_COLOR );
         giGraphics.dispose();
      }

   } // end clearGraphicsImage()


   private void initGraphicsImage()
   {
      graphicsImage = createImage( GI_WIDTH, GI_HEIGHT );
      clearGraphicsImage();

   } // end initGraphicsImage()


   // apply all outstanding graphics operations to graphicsImage.
   // return a clipRect for area of me (TGCanvas extends Component)
   // that is to be painted into, based on bits changed in the
   // graphicsImage
   private Rectangle renderGraphics()
   {
      int giLeftX = GI_WIDTH;
      int giRightX = -1;
      int giUpperY = GI_HEIGHT;
      int giLowerY = -1;
      if ( graphicsImage == null )
      {
         initGraphicsImage();
         giLeftX = 0;
         giRightX = GI_WIDTH-1;
         giUpperY = 0;
         giLowerY = GI_HEIGHT-1;
      }
      synchronized ( graphicsOps )
      {
         for ( int opIdx=0; opIdx < numGraphicsOps; opIdx++ )
         {
            TGGraphicsOp op = graphicsOps[opIdx];
            graphicsOps[opIdx] = null;
            Rectangle clipRect = null;
            try { clipRect = op.doIt( graphicsImage ); }
            catch ( NullPointerException npe )
	    { sysErr(".renderGraphics(): " + npe + " performing " + op); }
            if ( clipRect != null )
            {
               if ( clipRect.x < giLeftX )
                  giLeftX = clipRect.x;
               if ( clipRect.y < giUpperY )
                  giUpperY = clipRect.y;
               int coord = clipRect.x + clipRect.width - 1;
               if ( coord > giRightX )
                  giRightX = coord;
               coord = clipRect.y + clipRect.height - 1;
               if ( coord > giLowerY )
                  giLowerY = coord;
            }
         }
         numGraphicsOps = 0;
         graphicsOps.notifyAll();
      }
      int width = (giRightX + 1) - giLeftX;
      int widthInset = (GI_WIDTH - canvasWidth) / 2;
      int canvasLeftX = giLeftX - widthInset;
      if ( canvasLeftX < 0 )    // if negative, at least some of the
      {                         // painted pixels are to the left of
         width += canvasLeftX;  // the canvas, so adjust width
         canvasLeftX = 0;       // appropriately and reset left-most
      }                         // pixel number
      if ( width > canvasWidth )
         width = canvasWidth;
      int height = (giLowerY + 1) - giUpperY;
      int heightInset = (GI_HEIGHT - canvasHeight) / 2;
      int canvasUpperY = giUpperY - heightInset;
      if ( canvasUpperY < 0 )
      {
         height += canvasUpperY;
         canvasUpperY = 0;
      }
      if ( height > canvasHeight )
         height = canvasHeight;
      if ( height > 0 && width > 0 )
         return new Rectangle( canvasLeftX, canvasUpperY, width, height );
      return null;

   } // end renderGraphics()


   /*
    * print an error message to console tying it to this class
    */
   private void sysErr( String errTxt )
   { System.err.println( CLASS_NAME + errTxt ); }


   //
   // Overridden Canvas/Component Methods
   //

   public Dimension getMinimumSize()
   { return new Dimension ( MINIMUM_WIDTH, MINIMUM_HEIGHT ); }

   public Dimension getPreferredSize()
   { return new Dimension ( canvasWidth, canvasHeight ); }

   public Dimension getSize()
   { return new Dimension ( canvasWidth, canvasHeight ); }


   // either TGGraphicsOps have been queued to be performed or the
   // AWT has decided we need to redraw at least some part of the
   // Canvas, e.g., it was partially covered by some other window
   // that has moved/gone away.
   //
   public void paint(Graphics g)
   {
      //System.out.println("TGCanvas.paint: got here!");
      Rectangle rect = g.getClipBounds();
      int heightDiff = (GI_HEIGHT - canvasHeight) / 2;
      int widthDiff = (GI_WIDTH - canvasWidth) / 2;
      switch ( paintState )
      {
         case PAINT_REFRESH:
            if ( graphicsImage == null )
            {
               g.setColor( background );
               g.fillRect( 0, 0, canvasWidth, canvasHeight );
            }
            else
               if ( ! g.drawImage(graphicsImage, -widthDiff, -heightDiff, this) )
                  return;
            paintState = PAINT_DRAW_GRAPHICS;
         case PAINT_DRAW_GRAPHICS:
            rect = renderGraphics();
            if ( rect != null )
            {
               g.setClip( rect );
               if ( ! g.drawImage(graphicsImage, -widthDiff, -heightDiff, this) )
                  return;
            }
            paintState = PAINT_ERASE_TURTLES;
            paintTurtleNum = 0;
         case PAINT_ERASE_TURTLES:
            while ( paintTurtleNum < turtleClipRect.length )
            {
               if ( (rect = turtleClipRect[paintTurtleNum]) != null )
               {
                  g.setClip( rect );
                  if ( ! g.drawImage(graphicsImage, -widthDiff, -heightDiff, this) )
                     return;
                  turtleClipRect[paintTurtleNum] = null;
               }
               paintTurtleNum++;
            }
            paintState = PAINT_DRAW_TURTLES;
            paintTurtleNum = 0;
         case PAINT_DRAW_TURTLES:
            while ( paintTurtleNum < turtles.length )
            {
               Turtle turtle = turtles[paintTurtleNum];
               if ( turtle != null )
               {
                  int turtleX = (int) Math.rint( turtle.xcor() + xCenter );
                  int turtleY = (int) Math.rint( yCenter - turtle.ycor() );
                  int imgSz = turtle.getImageSideSize();
                  int imgLeftX = turtleX - imgSz/2;
                  int imgTopY = turtleY - imgSz/2;
                  g.setClip( imgLeftX, imgTopY, imgSz, imgSz);
                  if ( ! g.drawImage (turtle.getImage(), imgLeftX, imgTopY, this) )
                     return;
                  turtleClipRect[paintTurtleNum] = new Rectangle(imgLeftX, imgTopY, imgSz, imgSz);
               }
               paintTurtleNum++;
            }
            paintState = PAINT_REFRESH;
      }

   } //end paint()


   public void setBounds( int x, int y, int width, int height )
   {
      super.setBounds( x, y, width, height );
      canvasWidth = width;
      canvasHeight = height;
      xCenter = width / 2;
      yCenter = height / 2;
      repaint();

   } // end setBounds()


   public void setSize( int width, int height )
   {
      super.setSize( width, height );
      canvasWidth = width;
      canvasHeight = height;
      xCenter = width / 2;
      yCenter = height / 2;
      repaint();

   } // end setSize()


   // override update() to eliminate its invocation of clear()
   //
   public void update(Graphics g) { paint(g); }


   
   // ------ -------
   // Public methods
   // ------ -------


   /**
    * Append a graphics operation, a TGGraphicsOp object, to
    * the queue of outstanding operations.
    */
   public void addGraphOp( TGGraphicsOp grafOp )
   {
      synchronized (graphicsOps)
      {
         if ( numGraphicsOps == graphicsOps.length )
            expandGraphicsOps();
         graphicsOps[ numGraphicsOps++ ] = grafOp;
      }
   }


   /**
    * Add an object which implements TGKeyHandler to the list of
    * those that want a callback when a key is pressed while the
    * graphics canvas has focus. The object's keyPressed() method
    * is invoked.
    */
   public void addKeyHandler( TGKeyHandler kh )
   {
      for (int idx=0; idx < keyHandlers.length; idx++)
         if ( keyHandlers[idx] == kh )
            return;
      for (int idx=0; idx < keyHandlers.length; idx++)
         if ( keyHandlers[idx] == null )
         {
            keyHandlers[idx] = kh;
            return;
         }
      sysErr( ".addKeyHandler: no space" );

   } // end addKeyHandler


   public void addMouseHandler( TGMouseHandler mh )
   {
      for (int idx=0; idx < mouseHandlers.length; idx++)
         if ( mouseHandlers[idx] == mh )
            return;
      for (int idx=0; idx < mouseHandlers.length; idx++)
         if ( mouseHandlers[idx] == null )
         {
            mouseHandlers[idx] = mh;
            return;
         }
      sysErr( ".addMouseHandler: no space" );

   } // end addMouseHandler()


   public void addTurtle( Turtle turtle )
   {
      int openIdx = -1;
      for (int i=turtles.length-1; i >= 0; i--)
      {
         if ( turtles[i] != null )
         {
            if ( turtles[i] == turtle )
               return;
         }
         else
            openIdx = i;
      }
      if ( openIdx < 0 )
      {
         System.err.println(CLASS_NAME+".addTurtle: no room!");
         return;
      }
      turtles[openIdx] = turtle;
      repaint();

   } // end addTurtle()

   public int canvasHeight()
   { return canvasHeight; }

   public int canvasWidth()
   { return canvasWidth; }


   /**
    * Clean graphics off of the display.
    */
   public void clean()
   {
      synchronized ( graphicsOps )
      {
         while ( numGraphicsOps > 0 )
            graphicsOps[--numGraphicsOps] = null;
      }
      clearGraphicsImage();
      repaint();

   } // end clean()


   public int colorunder( TGPoint curXY )
   {
      String me = CLASS_NAME + ".colorunder: ";
      int imageX = curXY.imageX( GI_WIDTH );
      if ( imageX < 0 || imageX > GI_WIDTH )
         return background.getRGB() & 0xffffff;
      int imageY = curXY.imageY( GI_HEIGHT );
      if ( imageY < 0 || imageY > GI_HEIGHT )
         return background.getRGB() & 0xffffff;
      int[] pixel = new int[1];
      if ( graphicsImage == null )
         return background.getRGB() & 0xffffff;
      PixelGrabber pg = new PixelGrabber( graphicsImage,
                                          imageX, imageY,
                                          1, 1,
                                          pixel,
                                          0,
                                          GI_WIDTH
                                        );
      try { pg.grabPixels(); }
      catch (InterruptedException e)
      {
         System.err.println( me + "grabPixels interrupted" );
         return background.getRGB() & 0xffffff;
      }
      int status = pg.getStatus();
      //if ( (status & ImageObserver.ALLBITS ) == 0)
      //{
      //   System.err.print( me + "grabPixels ALLBITS not set" );
      //   System.err.println( ", status=" + status );
      //   //return background.getRGB() & 0xffffff;
      //}
      return pixel[0] & 0xFFFFFF;

   } // end colorunder()


   public TGPoint drawLine(TGPoint p1, double steps, double hd, int wd, Color cl )
   {
      if ( steps < 0 )
      {
         hd -= Math.PI;
         if ( hd < 0 )
            hd += (2 * Math.PI);
         steps = -steps;
      }
      TGPoint p2 = p1.otherEndPoint( hd, steps );
      addGraphOp( new TGLineOp(p1, p2, hd, cl, wd) );
      return p2;
   }

   public void drawLine( TGPoint p1, TGPoint p2, double hd, int wd, Color cl )
   { addGraphOp( new TGLineOp(p1, p2, hd, cl, wd) ); }


   public void fill( TGPoint point, Color color )
   { addGraphOp( new TGFillOp(point, color) ); }


   public void label( String text, TGPoint p, Font font, Color color )
   { addGraphOp( new TGLabelOp(text, p, font, color) ); }


   /**
    * Return a TurtleSpace x-coordinate of last mouse click
    *
    * @see #mousey
    */
   public int mousex()
   {
      return mouseX - xCenter;
   }

   /**
    * Return a TurtleSpace y-coordinate of last mouse click
    *
    * @see #mousex
    */
   public int mousey()
   {
      return -(mouseY - yCenter);
   }


   public void removeKeyHandler( TGKeyHandler kh )
   {
      for (int idx=0; idx < keyHandlers.length; idx++)
         if ( keyHandlers[idx] == kh )
            keyHandlers[idx] = null;
   }


   /**
    * Remove an object from TGCanvas' list of those wanting
    * their mouseClicked() and mouseMoved() methods invoked
    * when we receive these Events.
    */
   public void removeMouseHandler( TGMouseHandler mh )
   {
      for (int idx=0; idx < mouseHandlers.length; idx++)
         if ( mouseHandlers[idx] == mh )
            mouseHandlers[idx] = null;
   }


   public void removeTurtle( Turtle turtle )
   {
      for (int i=turtles.length-1; i >= 0; i--)
         if ( turtles[i] == turtle )
         {
            turtles[i] = null;
            return;
         }
      System.err.println(CLASS_NAME+".removeTurtle: turtle missing!");

   } // end removeTurtle()


   /**
    * Set the background color of the graphics canvas.
    * <p>
    * *NOTE* a side-effect of changing the background color is that
    * all current graphics is cleared.
    */
   public void setbg( int rgbValue )
   {
      synchronized ( graphicsOps )
      {
         while ( numGraphicsOps > 0 )
            graphicsOps[--numGraphicsOps] = null;
      }
      background = Turtle.rgbToColor( rgbValue );
      clearGraphicsImage();
      repaint();
   }

} // end class TGCanvas
