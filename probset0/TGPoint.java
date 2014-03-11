import java.lang.Math;

// TurtleGraphics now works entirely with TGPoints - points in
// TurtleSpace.
//
// Early versions of TG used int primitive values for points.
// This approach led to too many visual problems due to
// propagation of round offs.  Class TGPoint isolates actual
// implementation of points to one place which can easily be
// changed.
//
// A TGPoint is not bound to the current TGCanvas display. It
// is in TurtleSpace, which can be out of the bounds of what
// is currently viewable in the graphics sub-window of TG.


public class TGPoint implements Cloneable
{

   private float x, y;

   //
   // constructors
   //
   TGPoint() { x = 0.0F; y = 0.0F; }
   TGPoint( double x, double y ) { this.x = (float)x; this.y = (float)y; }
   TGPoint( float x, float y ) { this.x = x; this.y = y; }
   TGPoint( int x, int y ) { this.x = (float)x; this.y = (float)y; }


   public int imageX( int imageWidth )
   {
      int xCenter = imageWidth / 2;
      return xCenter + xIntValue();

   } // end imageX()

   public int imageX( float adj, int imageWidth )
   {
      int xCenter = imageWidth / 2;
      int xInt = Math.round(x + adj);
      return xCenter + xInt;

   } // end imageX()


   public int imageY( int imageHeight )
   {
      int yCenter = imageHeight / 2;
      int yIV = yIntValue();
      int retVal = yCenter - yIV;
      //System.out.println("TGPoint.imageY: yC="+yCenter+", yIV="+yIV+", rV="+retVal);
      return retVal;
      //return yCenter - yIntValue();

   } // end imageY()

   public int imageY( float adj, int imageHeight )
   {
      int yCenter = imageHeight / 2;
      int yInt = Math.round(y + adj);
      return yCenter - yInt;

   } // end imageY()


   public Object clone()
   {
      try { return super.clone(); }
      catch (CloneNotSupportedException e) { /* impossible error */ }
      return new TGPoint( x, y );
   }


   // given one end point of a line, its length and heading (in radians),
   // return its other end point
   // the idea of rounding deltaX and deltaY to zero when close came from
   // Berkeley Logo - absolutely necessary to make graphics look pretty
   //
   public TGPoint otherEndPoint( double radians, double length )
   {
      //System.out.print("otherEndPoint: p1=" + toString() );
      //System.out.println(", radians=" + radians + ", len=" + length);
      double sine = Math.sin(radians);
      double cosine = Math.cos(radians);
      double deltaX = cosine * length;
      if ( (deltaX < 0 && deltaX > -0.00001) || (deltaX > 0 && deltaX < 0.00001) )
         deltaX = 0;
      double deltaY = sine * length;
      if ( (deltaY < 0 && deltaY > -0.00001) || (deltaY > 0 && deltaY < 0.00001) )
         deltaY = 0;
      TGPoint p2 = new TGPoint( x + deltaX, y + deltaY );
      //System.out.println("               p2=" + p2);
      return p2;

   } // end otherEndPoint()


   public void setX( float fNum )
   { x = fNum; }


   public void setY( float fNum )
   { y = fNum; }


   public float xFloatValue()
   { return x; }


   public int xIntValue()
   { return Math.round(x); }


   public float yFloatValue()
   { return y; }


   public int yIntValue()
   { return Math.round(y); }


   public String toString()
   { return "{" + x + "," + y + "}"; }

} // end class TGPoint
