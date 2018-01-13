class Asteroid extends Obj
{      
  private float speedX, speedY; //a speed for each axis
  private color colour; //outline
  
  public Asteroid(float x, float y, float wid, float hei, float speedX, float speedY, String colour)
  {   
    //this constructor is used for sending colour strings
    //uses convertColour()
    
    //sent up for collision detection
    //used for numerous other purposes in Asteroid()
    super(x, y, wid, hei);    
    
    //sets speed
    this.speedX = speedX;
    this.speedY = speedY;
    
    //sets outline
    this.colour = convertColour(colour);
  } 
 
  public Asteroid(float x, float y, float wid, float hei, float speedX, float speedY, color colour)
  {   
    //used for specific colouration
    
    //sent up for collision detection
    //used for numerous other purposes in Asteroid()
    super(x, y, wid, hei);    
    
    //sets speed
    this.speedX = speedX;
    this.speedY = speedY;
    
    //sets outline
    this.colour = colour;    
  } 
  
  public String getEverything()
  {
    return "" + x + y + wid + hei + speedX + speedY + colour;
  }
  
  private color convertColour(String colour)
  {
    //converts string to color type    
    if (colour == "red")
      return color(242, 7, 66);
    else if (colour == "blue")
      return color(15, 89, 214);
    else if (colour == "green")
      return color(59, 214, 17);
    else
      return #CCD60F; //defaults to yellow
  }
  
  public void display()
  {
    //outline and thickness
    stroke(this.colour);
    strokeWeight(4);
    //to compensate for (x,y)
    float w = this.wid+this.x;
    float h = this.hei+this.y;
  
    //top line
    line(this.x+(this.wid/4), this.y, (this.x+this.wid)-this.wid/4, this.y);  
    //top right line
    line((this.x+this.wid)-this.wid/4, this.y, this.x+this.wid, this.y+(this.hei/2));
    //bottom right line
    line(this.x+this.wid, this.y+(this.hei/2), (this.x+this.wid)-this.wid/4, this.y+this.hei);    
    //bottom line
    line((this.x+this.wid)-this.wid/4, this.y+this.hei, this.x+(this.wid/4), this.y+this.hei);
    //bottom left line
    line(this.x+(this.wid/4), this.y+this.hei, this.x, this.y+(this.hei/2));
    //top left line
    line(this.x, this.y+(this.hei/2), this.x+(this.wid/4), this.y);
    
    /* collision box
    noFill();
    stroke(255);
    strokeWeight(1);
    line(x, y, x+wid, y);
    line(x+wid, y, x+wid, y+hei);
    line(x+wid, y+hei, x, y+hei);
    line(x, y+hei, x, y);
    */
  }    
  
  public void move()
  {    
    //shifts coordinates
    this.x += this.speedX;
    this.y += this.speedY;  
    
    //checks to see if "wrapping" is necessary and wraps
    if (this.x > screenW)         
      this.x = -this.wid;     
    
    if ((this.x + this.wid) < 0)
    {
      float temp = screenW;
      this.x = temp;
    }
    
    if (this.y > screenH)
      this.y = -this.hei;
    
    if ((this.y + this.hei) < 0)
      this.y = screenH;
      
    //end of checking for "wrapping"
  } 
  
  public void handleCollision()
  {
    destroyedAsteroidSound.setVolume(1.2);
    destroyedAsteroidSound.play();
    
    // Destroyed
    asteroids.del(getEverything());
    
    // Add an explosion at the point of impact
    explosions.add(new ParticlePhysics(x + wid/2, y + hei/2, 75, this.getColour()));
    
    // Decrease player health if collision with ship
    if (isCollision(player, this))
    {
      player.playClangSound();
      player.decreaseHealth(5);
    } 
    else
    {
      // Drops minerals
      collidable.add(new Mineral(this.x+(this.wid/2), this.y+(this.hei/2), int(random(2, 4)), int(random(2, 4)), 10, #D7DE2B)); 
      player.addScore(15);
      // Only increases the charge if time isn't frozen
      if(isTimeFrozen == false)
      {
        player.addFreezeCharge(10);
      }
    }
  }
  
  public String getColour() 
  {
    if (colour == color(242, 7, 66))
      return "red";
    else if (colour == color(15, 89, 214))
      return "blue";
    else if (colour == color(59, 214, 17))
      return "green";
    return "error";
  }
  
  public float getSpeedX()
  {
    return speedX;
  }
  
  public float getSpeedY()
  {
    return speedY;
  }
  
  public void setSpeedX(float newSpeed)
  {
    speedX = newSpeed;
  }
  
  public void setSpeedY(float newSpeed)
  {
    speedY = newSpeed;
  }
}
