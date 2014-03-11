class Cross extends TurtleGraphicsWindow    {

    public void myTurtleCmds( ) {
        int centerx = this.xcor();
        int centery = this.ycor();

        forward( 100 );
        back( 200 );
        forward( 100 );
        right( 90 );
        forward( 100 );
        back( 200 );
        left(90);

        setxy(centerx, centery);
    }

    public void drawSquare(int sidelength)    {
        int centerx = this.xcor();
        int centery = this.ycor();

        System.out.println(centerx + " " + centery);
        penup();
        setxy(centerx - sidelength/2, centery - sidelength/2);
        pendown();
        forward(sidelength);
        right(90);
        forward(sidelength);
        right(90);
        forward(sidelength);
        right(90);
        forward(sidelength);
        right(90);
        penup();
        setxy(centerx, centery);
    }

    public void drawCircle(int radius)   {
        int centerx = this.xcor();
        int centery = this.ycor();

        penup();
        for(int i=0; i<360; i++)    {
            double radians = Math.toRadians((double)i);
            setxy((int)(centerx + radius*Math.cos(radians)), (int)(centery + radius*Math.sin(radians)));
            if(i == 0)
                pendown();
        }
        penup();
        setxy(centerx, centery);
    }

    public void drawUPSymbol(int size)  {
        int centerx = this.xcor();
        int centery = this.ycor();

        seth(90);
        penup();
        for(int i=0; i<360; i++)    {
            double radians = Math.toRadians((double)i);
            int value = (int)(size*(Math.sin(2*radians) + 0.25*Math.sin(6*radians)));
            forward(value);
            pendown();
            penup();
            setxy(centerx, centery);
            seth(-1);
        }
    }

    // program starts here
    public static void main (String[] args)      {
        Cross obj = new Cross();
        obj.myTurtleCmds();
        obj.drawSquare(300);
        obj.drawCircle(125);
        obj.drawUPSymbol(50);
    }

} // end class Cross

