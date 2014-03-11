import java.awt.Image;
import java.awt.Rectangle;

/**
 * TGGraphicsOp is an interface that a class implements when
 * it provides TGCanvas with support for a graphics operation.
 *
 * @author Guy Haas
 */

public interface TGGraphicsOp
{
   public Rectangle doIt( Image image );  // make whatever change the implementor
                                          // is responsible for doing to "image"
                                          // and return a cliprect for the
                                          // *rectangular* area of image that was
                                          // changed

} // end interface TGGraphicsOp
