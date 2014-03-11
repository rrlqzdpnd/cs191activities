import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Rectangle;

/*
 * This class implements a TurtleGraphics Line graphics Operation.
 * A line of a specified penWidth is drawn between two TurtleSpace
 * points.
 *
 * *NOTE* The points supplied to a TGLineOp constructor are arranged
 * such that lines are always drawn left to right (increasing X
 * values). This consistency is necessary to insure identical pixels 
 * are painted for fat lines that should be identical lines, say
 * erasing a fat black line by drawing an identical white line over
 * it, e.g., (SETPS 2 FORWARD 100 WAIT 1000 SETC 0 BACK 100). For an
 * angular line, rounding differences can cause the BACK to generate
 * a line that has a different width.  The above sequence with the
 * turtle heading at 45 degrees is a good example.
 *
 * @author Guy Haas
 */

class TGLineOp implements ImageObserver, TGGraphicsOp
{

   private Color color;
   private double heading;
   private int penWidth;
   private int[] xPoints;
   private int[] yPoints;
   private TGPoint p1, p2;


   //
   // constructor
   //
   public TGLineOp(TGPoint pt1, TGPoint pt2, double hd, Color color, int wid)
   {
      if ( pt1.xFloatValue() <= pt2.xFloatValue() )
      {
         this.p1 = pt1;
         this.p2 = pt2;
         this.heading = hd;
      }
      else
      {
         this.p1 = pt2;
         this.p2 = pt1;
         this.heading = hd - Math.PI;
         if ( this.heading < 0 )
            this.heading += (2 * Math.PI);
      }
      this.color = color;
      this.penWidth = wid;
   }


   //
   // ImageObserver interface methods
   //
   public boolean imageUpdate(Image img, int flags, int x, int y, int wd, int ht)
   {
      System.err.println( "TGLineOp.imageUpdate: got here!" );
      return true;
   }


   private Rectangle drawHorizontalFatLine(Graphics g, int canvasHeight, int canvasWidth)
   {
      float hafWid =  ((float)penWidth) / 2.0F;
      int y = p1.imageY( hafWid, canvasHeight );
      int p1X = p1.imageX(canvasWidth);
      int p2X = p2.imageX(canvasWidth);
      int x = (p1X < p2X) ? p1X : p2X;
      int lineWidth = Math.abs( p1X - p2X );
      g.setClip( x, y, lineWidth, penWidth );
      g.fillRect( x, y, lineWidth, penWidth );
      return new Rectangle(x, y, lineWidth, penWidth);

   } // end drawHorizontalFatLine()


   // drawFatLine - the turtle's pen is wider than 1 pixel
   // but... the special case of movement of only a single pixel (e.g. "fd 1")
   //        must be handled
   private Rectangle drawFatLine( Graphics g, int canvasHeight, int canvasWidth )
   {
      double qtrDegRads = Math.PI / 720.0;
      if ( heading < qtrDegRads || heading > (Math.PI*2.0 - qtrDegRads) )
         return drawHorizontalFatLine( g, canvasHeight, canvasWidth );
      if ( heading > (Math.PI/2.0 - qtrDegRads) && heading < (Math.PI + Math.PI/2.0 + qtrDegRads) )
         return drawVerticalFatLine( g, canvasHeight, canvasWidth );
      float xDf = Math.abs( p1.xFloatValue() - p2.xFloatValue() );
      float yDf = Math.abs( p1.yFloatValue() - p2.yFloatValue() );
      if ( xDf < 1.5F && yDf < 1.5F )
         return drawOnePixelFatLine(g, canvasHeight, canvasWidth);
      int p1X = p1.imageX( canvasWidth );
      int p1Y = p1.imageY( canvasHeight );
      int p2X = p2.imageX( canvasWidth );
      int p2Y = p2.imageY( canvasHeight );
      // compute the width end points of perpendicular line at p1
      double hafWid = ((double) penWidth) / 2.0;
      double perpLineHead = heading + (Math.PI / 2.0);
      if ( perpLineHead > 2.0 * Math.PI )
         perpLineHead -= 2.0 * Math.PI;
      TGPoint point = p1.otherEndPoint( perpLineHead, hafWid );
      int p1LeftX = point.imageX( canvasWidth );
      int p1LeftDX = p1LeftX - p1X;
      int p1LeftY = point.imageY( canvasHeight );
      int p1LeftDY = p1LeftY - p1Y;
      perpLineHead = heading - (Math.PI / 2.0);
      if ( perpLineHead < 0.0 )
         perpLineHead += 2.0 * Math.PI;
      point = p1.otherEndPoint( perpLineHead, hafWid );
      int p1RightX = point.imageX( canvasWidth );
      int p1RightDX = p1RightX - p1X;
      int p1RightY = point.imageY( canvasHeight );
      int p1RightDY = p1RightY - p1Y;
      if ( xPoints == null )
      {
         xPoints = new int[4];
         yPoints = new int[4];
      }
      xPoints[0] = p1LeftX;
      yPoints[0] = p1LeftY;
      xPoints[1] = p1RightX;
      yPoints[1] = p1RightY;
      xPoints[2] = p2X + p1RightDX;
      yPoints[2] = p2Y + p1RightDY;
      xPoints[3] = p2X + p1LeftDX;
      yPoints[3] = p2Y + p1LeftDY;
      int crX = min( xPoints );
      int crWidth = Math.abs( crX - max(xPoints) ) + 1;
      int crY = min( yPoints );
      int crHeight = Math.abs( crY - max(yPoints) ) + 1;
      g.setClip( crX, crY, crWidth, crHeight );
      g.fillPolygon( xPoints, yPoints, 4 );
      return new Rectangle( crX, crY, crWidth, crHeight );

   } // end drawFatLine()


   // draw a line perpendicular to current heading
   // with its midpoint at p1 (which equals p2)...
   private Rectangle drawOnePixelFatLine(Graphics g, int canvasHeight, int canvasWidth)
   {
      // compute the width end points of perpendicular line at p1
      double hafWid = ((double) penWidth) / 2.0;
      double perpLineHead = heading + (Math.PI / 2.0);
      if ( perpLineHead > 2.0 * Math.PI )
         perpLineHead -= 2.0 * Math.PI;
      TGPoint leftPt = p1.otherEndPoint( perpLineHead, hafWid );
      int ptLeftX = leftPt.imageX( canvasWidth );
      int ptLeftY = leftPt.imageY( canvasHeight );
      perpLineHead = heading - (Math.PI / 2.0);
      if ( perpLineHead < 0.0 )
         perpLineHead += 2.0 * Math.PI;
      TGPoint rightPt = leftPt.otherEndPoint( perpLineHead, (double)penWidth );
      int ptRightX = rightPt.imageX( canvasWidth );
      int ptRightY = rightPt.imageY( canvasHeight );
      int crX = ptLeftX < ptRightX ? ptLeftX : ptRightX;
      int crWidth = Math.abs(ptLeftX - ptRightX) + 1;
      int crY = ptLeftY < ptRightY ? ptLeftY : ptRightY;
      int crHeight = Math.abs(ptLeftY - ptRightY) + 1;
      g.setClip( crX, crY, crWidth, crHeight );
      g.drawLine( ptLeftX, ptLeftY, ptRightX, ptRightY );
      return new Rectangle( crX, crY, crWidth, crHeight );

   } // end drawOnePixelFatLine()


   private Rectangle drawVerticalFatLine( Graphics g, int canvasHeight, int canvasWidth )
   {
      float hafWid =  ((float)penWidth) / 2.0F;
      int x = p1.imageX( -hafWid, canvasWidth );
      int p1Y = p1.imageY(canvasHeight);
      int p2Y = p2.imageY(canvasHeight);
      int y = (p1Y < p2Y) ? p1Y : p2Y;
      int height = Math.abs( p1Y - p2Y );
      g.setClip( x, y, penWidth, height );
      g.fillRect( x, y, penWidth, height );
      return new Rectangle(x, y, penWidth, height);

   } // end drawVerticalFatLine()


   private int max( int[] ary )
   {
      int num = ary[0];
      for ( int i=1; i < ary.length; i++ )
         if ( ary[i] > num )
            num = ary[i];
      return num;

   } // end max()


   private int min( int[] ary )
   {
      int num = ary[0];
      for ( int i=1; i < ary.length; i++ )
         if ( ary[i] < num )
            num = ary[i];
      return num;

   } // end min()


   public Rectangle doIt( Image inMemoryImage )
   {
      int crX, crY, crHeight, crWidth;
      Rectangle clipRect;
      int imageWidth = inMemoryImage.getWidth( this );
      if ( imageWidth < 0 )
         return null;
      int imageHeight = inMemoryImage.getHeight( this );
      if ( imageHeight < 0 )
         return null;
      Graphics g = inMemoryImage.getGraphics();
      g.setColor(color);
      if ( penWidth == 1 )
      {
         int p1X = p1.imageX( imageWidth );
         int p1Y = p1.imageY( imageHeight );
         int p2X = p2.imageX( imageWidth );
         int p2Y = p2.imageY( imageHeight );
         crX = p1X < p2X ? p1X : p2X;
         crWidth = Math.abs( p1X - p2X ) + 1;
         crY = p1Y < p2Y ? p1Y : p2Y;
         crHeight = Math.abs( p1Y - p2Y ) + 1;
         g.setClip( crX, crY, crWidth, crHeight );
         g.drawLine( p1X, p1Y, p2X, p2Y );
         clipRect = new Rectangle( crX, crY, crWidth, crHeight );
      }
      else
         clipRect = drawFatLine( g, imageHeight, imageWidth );
      g.dispose();
      return clipRect;

   } // end doIt()


   public Color getColor()
   { return color; }


   public String toString()
   {
      return "TGLineOp[color="+color+",width="+penWidth+",p1="+p1+",p2="+p2+"]";
   }

} // end class TGLineOp
