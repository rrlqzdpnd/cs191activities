/**
 * TGKeyHandler is an interface that a class implements when
 * it would like to receive key events from TGCanvas.
 *
 * @author Guy Haas
 */

public interface TGKeyHandler
{
   public final int ENTER =  10;
   public final int SPACE =  32;
   public final int DOWN =  128;
   public final int LEFT =  129;
   public final int RIGHT = 130;
   public final int UP =    131;

   public void keyPressed( int keyNum );
}
