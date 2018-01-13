class Mineral extends Obj
{
  //the currency/(Minerals) class
  
  private int amount;
  private color colour;
  
  public Mineral(float x, float y, float w, float h, int amount, color colour)
  {
    super(x, y, w, h);
    this.amount = amount;
    this.colour = colour;
  }
  
  public String getEverything()
  {
    return "" + x + y + wid + hei + amount + colour;
  }
  
  public void display()
  {
    fill(this.colour);
    stroke(this.colour);
    strokeWeight(8);
    ellipse(x+(wid/2), y+(hei/2), wid, hei);
  }
    
  public void handleCollision() 
  {
    if(isCollision(player, this))
    {
      collectedMineralSound.play();
      collidable.del(getEverything());
      player.addCurrency(this.amount);
      // Only increases the charge if time isn't frozen
      if(isTimeFrozen == false)
      {
        player.addFreezeCharge(5);
      }
    }
  }
  
  public void move() {} //will not move
  public String getColour() {return "error";} //no reason to use this
}

