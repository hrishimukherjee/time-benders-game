class Button extends Obj 
{  
  public Button(float x, float y, float w, float h)
  {
    super(x, y, w, h);
  }
  
  public void display()
  {
    stroke(255);
    noFill();
    rect(x, y, wid, hei);
  }
  
  public void move(){;}
  public void handleCollision(){;}
  public String getColour(){return "error";} 
  public String getEverything(){return "error";} 
} 
