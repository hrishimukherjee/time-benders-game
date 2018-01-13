abstract class Obj // Creates a new base type for all objects
{
  protected float x, y;
  protected float wid, hei;
  
  public Obj(float x, float y, float wid, float hei)
  {
    // x and y are the top left coordinates
    this.wid = wid;
    this.hei = hei;
    this.x = x;
    this.y = y;
  }
  
  abstract public void move();
  abstract public void display();  
  abstract public void handleCollision();
  abstract public String getColour(); // Forcing this makes colour checking simpler
  abstract public String getEverything(); // Returns a string of every local variable
} 
