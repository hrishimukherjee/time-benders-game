class TimeFreeze extends Obj
{
  color outerColor;
  color midColor;
  color innerColor;
  int visibility;
 
  public TimeFreeze(float xPos, float yPos)
  {
    super(xPos, yPos, 0, 0);
    visibility = 60;
    player.freezeTimer.restart();
    outerColor = color(196, 61, 224, visibility);
    midColor = color(240, 44, 158, visibility);
    innerColor = color(237, 24, 37, visibility);
  }

  public void display()
  {
    outerColor = color(196, 61, 224, visibility);
    midColor = color(240, 44, 158, visibility);
    innerColor = color(237, 24, 37, visibility);
    fill(outerColor);
    stroke(144, 2, 173, visibility + 50);
    strokeWeight(5);
    ellipse(this.x, this.y, this.wid, this.hei);
    fill(midColor);
    stroke(165, 15, 103, visibility + 50);
    ellipse(this.x, this.y, this.wid/2, this.hei/2);
    fill(innerColor);
    stroke(160, 13, 15, visibility + 50);
    ellipse(this.x, this.y, this.wid/3, this.hei/3);
  }
  
  // Expands the blast radius constantly
  public void move()
  {
    if(this.wid < 4000)
    {
      this.wid = this.wid + 125;
      this.hei = this.hei + 125;
    }
    if(this.wid > 1500)
    {
      visibility = visibility - 4;
    }
    if(isFinished())
    {
      playerObjects.del(getEverything());
    }
  }
  
  // Checks if the blast has reached its maximum radius
  public boolean isFinished()
  {
    if(this.wid >= 4000)
    {
      return true;
    }
    else
    {
     return false;
    }
  }
  
  // Returns all the information of the object 
  public String getEverything()
  { 
    return "" + x + y + wid + hei; 
  }
  
  public void handleCollision(){}
  public String getColour(){ return " "; } 
}

