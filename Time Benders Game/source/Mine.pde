class Mine extends Obj
{
  protected float speed;
  private float angle;
  protected color col;
  
  //local x, y for drawing
  //super x, y for collision
  //protected float x, y;
  private float xPad = 4, yPad = 43; //padding for collision box

  public Mine(float x, float y, float w, float h)
  {
    //must updates xPad and yPad if this changes
    super(x, y, w, h);
  }
  
  public String getEverything()
  {
    return "" + x + y + wid + hei;
  }

  public void move() {}

  public void display()
  {        
    noStroke();
    fill(#B409D6);
    ellipse(x, y, wid+5, hei+5);
    fill(204);
    ellipse(x, y, wid, hei); 
  }
  
  public void handleCollision()
  {
    playerObjects.del(getEverything());
  }
  
  public String getColour() {return "error";}
} 

