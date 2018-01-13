class PowerUp extends Obj
{
  private int price;
  private String name;
  private long duration; //in milliseconds
  private Timer powerTime;
  private boolean isActivated;
  
 public PowerUp(String name, int price, long duration)
  {
    super(0, 0, 0, 0);
    
    this.name = name;
    this.price = price;
    this.duration = duration;
    isActivated = false;
  }
  
  public PowerUp() // An empty PowerUp
  {
    super(0, 0, 0, 0);
    
    this.name = "Empty";
    this.price = 0;
    this.duration = 0;
    isActivated = false;
  }
  
  public void display(int x, int y, int w, int h)
  {
    fill(255);
    rect(x, y, w, h);
  }
  
  public void iconDisplay(float x, float y) //width and height of icon are 45 at max
  {
    if (name.equals("Rapid Fire"))
    { 
      fill(255, 0, 0);
      noStroke();
      ellipse(x+7, y+7, 10, 10);
      ellipse(x+40, y+25, 10, 10);
      ellipse(x+32, y+35, 10, 10);
      fill(0, 255, 0);
      ellipse(x+15, y+28, 10, 10);
      fill(0, 0, 255);
      ellipse(x+38, y+10, 10, 10);
      ellipse(x+20, y+16, 10, 10);
    }    
    else if (name.equals("Rock Seekers"))
    {
      Asteroid icon = new Asteroid(x+3, y+7, 32, 32, 0, 0, "red");
      icon.display();
      fill(255, 0, 0);
      ellipse(x+36, y+7, 5, 5);
    }
    else if (name.equals("RGB Bullet")) 
    {         
      noStroke();
      fill(#DBE60B);
      ellipse(x+20, y+20, 22, 22);
      fill(255);
      ellipse(x+20, y+20, 20, 20);      
    }     
    else if (name.equals("Laser Pointer"))
    {
      strokeWeight(2);
      stroke(color(255, 0, 0));
      line(x+20, y+10, x+40, y+40);
      noStroke();
    }
    else if (name.equals("Insta-Brake"))
    {
      fill(204);
      arc(x+25, y+15, 25, 30, 0, PI+QUARTER_PI);
    }
    else if (name.equals("Supernova"))
    {
      noStroke();
      fill(color(255, 0, 0));
      ellipse(x+20, y+20, 30, 30);
      fill(#F06E2E);
      ellipse(x+20, y+20, 20, 20); 
    }
    else if (name.equals("Floating Mine"))
    {
      noStroke();
      fill(#B409D6);
      ellipse(x+20, y+20, 22, 22);
      fill(204);
      ellipse(x+20, y+20, 20, 20);  
    }
    else if (name.equals("Spreader"))
    {
      noStroke();
      fill(color(255, 0, 0));
      ellipse(x+15, y+15, 10, 10);
      ellipse(x+26, y+15, 10, 10);
      ellipse(x+20, y+25, 10, 10);
    }
    
    //changes back to previous fill/stroke
    fill(0);
    stroke(36, 224, 103);
  } 
  
  public void activate()
  {    
    if (name.equals("Rapid Fire"))
    {      
      powerupActivateSound.play();
    
      isActivated = true;
      powerTime = new Timer(duration); //half a minute
      player.rof = 200;
      player.rateOfFire = new Timer(player.rof);
    }    
    else if (name.equals("Rock Seekers"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      powerTime = new Timer(duration);
      player.currentBullet = "seek";
    }
    else if (name.equals("RGB Bullet")) 
    {      
      powerupActivateSound.play();
      
      isActivated = true;
      powerTime = new Timer(duration);
    }
    else if (name.equals("Laser Pointer"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      powerTime = new Timer(duration);
    }
    else if (name.equals("Insta-Brake"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      powerTime = new Timer(duration);
    }
    else if (name.equals("Supernova"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      powerTime = new Timer(1000);
    }
    else if (name.equals("Floating Mine"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      
      playerObjects.add(new Mine(player.x, player.y, 20, 20));
      powerTime = new Timer(0);
    }
    else if (name.equals("Spreader"))
    {
      powerupActivateSound.play();
      
      isActivated = true;
      
      powerTime = new Timer(duration);
      player.currentBullet = "spread";
    }      
  }
  
  public void drawSupernova()
  {    
    stroke(255, 255, 113);
    fill(255, 255, 113, 150);
    ellipse(player.x, player.y, x+100, y+100);
    stroke(250, 13, 88);
    fill(250, 13, 88, 200);
    ellipse(player.x, player.y, x, y); 
        
    try
    {
      x += width/powerTime.getTimeRemaining()+20;
      y += height/powerTime.getTimeRemaining()+20;
    }
    catch (ArithmeticException e)
    {
      x = width*3;
      y = height*3;
    }
  }  
  
  public void reset()
  {
    isActivated = false;
    
    if (name.equals("Rapid Fire"))
    {
      player.rof = 750;
      player.rateOfFire = new Timer(player.rof);
    }
    else if (name.equals("Rock Seekers") || name.equals("Spreader"))
    {
      player.currentBullet = "std";
    }
    else if (name.equals("Supernova"))
    {          
      while (asteroids.len() > 0)      
        asteroids.grab(0).handleCollision();      
        
      x = 0;
      y = 0;
    }
  }
  
  public void drawTimer()
  {      
    // Overlays a green tint to powerup
    rectMode(CORNERS);
    double percentageLeft = ((double)powerTime.getTimeRemaining()/(double)powerTime.getEnd()); 
    fill(#05E322, 102);
    noStroke();
    rect(width - 75, (float)(10+(43-(float)43f*percentageLeft)), (width - 75)+45 , 55, 10, 10); //43 makes the timer visible for a bit longer opposed to 45
    rectMode(CORNER);    
  }
  
  public boolean getIsActivated() {return isActivated;}
  public String getName() {return name;}
  public int getPrice() {return price;}  
  public long getDuration() {return duration;}
  public Timer getTimer() {return powerTime;}
  
  public String getEverything()
  {
    return "" + x + y + wid + hei + name + price;
  } 
  
  public void display(){;}  
  public void handleCollision(){;}   
  public void move(){;}
  public String getColour() {return "error";} 
}
