class Bullet extends Obj
{
  protected float speed;
  private float angle;
  protected color col;
  
  // Local x, y for drawing
  // Super x, y for collision
  // Protected float x, y;
  private float xPad = 4, yPad = 43; // Padding for collision box

  public Bullet(float x, float y, float w, float h, float v, float ang)
  {
    // Must update xPad and yPad if this changes
    super(x, y, w, h);
    
    this.x = x;
    this.y = y;
        
    speed = v;
    angle = ang;
    col = determineColor();
    // Restarts count
    player.rateOfFire.restart();
  }
  
  public String getEverything()
  {
    return "" + x + y + wid + hei + speed + angle;
  }

  public void move()
  {
    x = x - speed*cos((angle + 90)*PI/180);
    y = y - speed*sin((angle + 90)*PI/180);

    if (isOffScreen())
    {
      playerObjects.del(getEverything());
    }
    // Updates super coordinates
    super.x = this.x;
    super.y = this.y;
  }

  /* Displays the bullet taking into account the ROTATION OF THE SHIP.
   The bullet is shot from the turret no matter what the orientation
   of the ship is (Only if the x and y of the bullet are set to the 
   centerX and centerY of the ship). */
  public void display()
  {    
    pushMatrix();
    translate(x, y);
    rotate(radians(angle));
    if (!player.getPowerUp().getName().equals("RGB Bullet") || !player.getPowerUp().getIsActivated())
    {
      fill(col);
      noStroke();
      ellipse(0, (-player.hei)/2, wid, hei);
    }
    else
    {
      noStroke();
      fill(#DBE60B);
      ellipse(0, (-player.hei)/2, wid+2, hei+2);
      fill(255);
      ellipse(0, (-player.hei)/2, wid, hei);
    }
    
    popMatrix();    
  }

  /* Displays the bullet at the specified x and y location.
   Can be overidden for the previous display method. */
  public void display(float x, float y)
  {
    fill(col);
    noStroke();
    ellipse(x + wid/2, y + hei/2, wid, hei);   
  }

  public void handleCollision()
  {
    playerObjects.del(getEverything());
  }

  // To see if the bullet is still on screen
  public boolean isOffScreen()
  {
    if (x > screenW)         
    {      
      return true;
    }   

    if ((x + wid) < 0)
    {
      return true;
    }

    if (y > screenH)
    {
      return true;
    }

    if ((y + hei) < 0)
    {
      return true;
    }

    return false;
  }

  // Determines the color of the bullet shot depending on the type.
  public color determineColor()
  {
    color temp = color(255);
    // Shoots red
    if(player.bulletType == 1)
    {
      temp = color(242, 7, 66);
    }
    // Shoots green
    else if(player.bulletType == 2)
    {
      temp = color(59, 214, 17);
    }
    // Shoots blue
    else if(player.bulletType == 3)
    {
      temp = color(15, 89, 214);
    }
    return temp;
  }

  public String getColour() 
  {
    if (col == color(242, 7, 66))
      return "red";
    else if (col == color(15, 89, 214))
      return "blue";
    else if (col == color(59, 214, 17))
      return "green";
      
    return "error";
  }
} 

