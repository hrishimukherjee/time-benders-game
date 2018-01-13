class Spaceship extends Obj
{
  // Physical Attributes
  private float centerX, centerY;
  private float w, h;
  private float accel, speed;
  private float angle;
  private color c;
  
  // Variable to switch between different bullets
  private int bulletType;    
  //bullet equipped
  String currentBullet;

  // Padding used for collision box
  private float xPad = 37;
  private float yPad = 40;
  
  // Rate of Fire (in milliseconds)
  private long rof = 750;
  
  // Duration of time freeze (in milliseconds)
  private long freezeDuration = 20000;
  
  // Timer for rof & duration
  Timer rateOfFire = new Timer(rof);
  Timer freezeTimer = new Timer(freezeDuration);

  // Currency
  private int currency;
  
  // Score
  private int score;
  
  // Power Up
  PowerUp powerup;

  // Health
  private int health;
  private color healthIndicator;
  private int healthBrightness;
  private int incBrightness;
  
  // Charge for using Freeze Time
  float freezeCharge;
  float maxCharge;
  
  Spaceship(float x, float y, color col)
  {    
    //creates Obj
    //these coordinates were found via trial-and-error
    //create a box arround the ship regardless of orientation
    //+/- 37 and 40 will be used periodically
    //remember to update class variables xPad and yPad if they change
    super(x-37, y-40, 75, 80); 
    //the height and width should not change
    w = 25;
    h = 40; 
    // Keeps a copy for the center point
    centerX = x;
    centerY = y;           
    // Movement variables
    angle = 0;
    speed = 0;
    accel = 0.05;
    // Implicit/Explicit health variables
    health = 100;
    healthBrightness = 250; 
    incBrightness = 15;
    // Freeze Charge
    freezeCharge = 0;
    maxCharge = 250;
    // Misc variables
    c = col;
    bulletType = 1;    
    currentBullet = "std";
    
    powerup = new PowerUp();
  }
  
  public String getEverything()
  {
    return "Spaceship";
  }

  // Draw the spaceship.
  void display()
  {
    updateHealthIndicator();
    pushMatrix();
    rectMode(CENTER);
    translate(centerX, centerY);
    rotate(radians(angle));
    // Draw the laser pointers from the turrets (if activated)
    if (player.getPowerUp().getName().equals("Laser Pointer") && player.getPowerUp().getIsActivated())
    {
      stroke(determineLaserColor());
      strokeWeight(3);
      line((-w/4), (-h + 25) - h/2, (-w/4), ((-h + 25) - h/2) - screenW); //x - speed*cos((angle + 90)*PI/180);
      line((w/4), (-h + 25) - h/2, (w/4), ((-h + 25) - h/2) - screenW); 
      strokeWeight(1);
      stroke(0);
    }
    // Draw the turrets.
    stroke(0);
    strokeWeight(1);
    fill(c);
    rect(-w/4, -h + 25, w/4, h);
    fill(determineTurretColor());
    triangle((-w/4) - (w/8), (-h + 25) - h/2, -w/4, (-h + 25) - (h/2 + 5), 
    (-w/4) + (w/8), (-h + 25) - h/2);
    fill(c);
    rect(w/4, -h + 25, w/4, h);
    fill(determineTurretColor());
    triangle((w/4) - (w/8), (-h + 25) - h/2, w/4, (-h + 25) - (h/2 + 5), 
    (w/4) + (w/8), (-h + 25) - h/2);    
    // Draw the side wings.
    noStroke();
    fill(67, 62, 30);
    rect(w - 5, (h/3), w - 5, h/3);
    triangle(w - 2, h/2, w + 8, (h/3) - h, w + 13, h/2);
    rect(-(w - 5), (h/3), w - 5, h/3);
    triangle(-(w - 2), h/2, -(w + 8), (h/3) - h, -(w + 13), h/2);
    // Draw the damage-taken indicator
    fill(healthIndicator);
    rect(w - 3, (h/3) - 1.6, w - 1, h/10);
    triangle(w + 2, h/3, w + 8, (h/3) - (h*3)/4, w + 9, h/3);
    rect(-(w - 3), (h/3) - 1.6, w - 1, h/10);
    triangle(-(w + 2), h/3, -(w + 8), (h/3) - (h*3)/4, -(w + 9), h/3);
    // Draw the body.
    stroke(0);
    fill(c);
    rect(0, 0, w, h);
    // Draw the cockpit.
    fill(255, 255, 255, 200);
    quad(-w/4, -h/2, (-w/4) - (w/8), 0, (w/4) + (w/8), 0, w/4, -h/2);
    fill(71, 169, 206, 200);
    quad((-w/4) + 3, -h/2, ((-w/4) - (w/8)) + 3, -3, ((w/4) + (w/8)) - 3, -3, (w/4) - 3, -h/2);
    // Draw the flames (Depending on the ship's speed)
    noStroke();
    fill(222, 138, 29);
    triangle(-w/4 - speed, h/2, 0, h/2 + speed*5, w/4 + speed, h/2);
    fill(247, 205, 15);
    triangle((-w/4 - speed) + 3, h/2, 0, h/2 + speed*3, (w/4 + speed) - 3, h/2);
    fill(21, 107, 209);
    triangle((-w/4 - speed) + 6, h/2, 0, h/2 + speed, (w/4 + speed) - 6, h/2);    
    popMatrix();
    
    /* collision box
    noFill();
    stroke(255);
    line(x, y, x+wid, y);
    line(x+wid, y, x+wid, y+hei);
    line(x+wid, y+hei, x, y+hei);
    line(x, y+hei, x, y);
    strokeWeight(10);
    point(x, y);
    */

    if (!isPaused) //so the ship doesn't slow down on pause
      this.deaccelerate();
  }

  public void playShotSound()
  {
    shipShotSound.setVolume(1.5);
    //shipShotSound.setSpeed(int(random(1, 4)));
    shipShotSound.play();
  }
  
  public void playClangSound()
  {
    shipClangSound.play();
  }

  // Move the spaceship
  void move()
  {
    shipEngineSound.setVolume(speed/35);
    if (!shipEngineSound.isPlaying())
      shipEngineSound.play();      
      
    moveStraight();
    
    // Accelerates the ship when up is pressed
    if (keys.contains(38))
    {        
      accelerate();
    }
    // Rotates the ship left when left is pressed 
    if (keys.contains(37))
    {
      rotateLeft();
    }
    // Rotates the ship right when right is pressed
    if (keys.contains(39))
    {
      rotateRight();
    }
    // Brakes the ship when down is pressed 
    if(keys.contains(40))
    {
      if (powerup.getName().equals("Insta-Brake"))
        speed = 0;
      else
        this.deaccelerate();
    }
    
    // Updates x and y
    x = centerX-xPad;
    y = centerY-yPad;
    checkWrapping();
  }


  // Checks to see if "wrapping" is necessary and wraps 
  public void checkWrapping()
  {
    if (x > screenW)         
    {      
      x = -wid;  
      centerX = x+xPad;
    }   

    if ((x + wid) < 0)
    {
      float temp = screenW;
      x = temp;
      centerX = x+xPad;
    }

    if (y > screenH)
    {
      y = -hei;
      centerY = y+yPad;
    }

    if ((y + hei) < 0)
    {
      y = screenH;  
      centerY = y+yPad;
    }
  }


  /* Moves the spaceship straight along the direction 
   it's facing. */
  void moveStraight()
  {
    centerX = centerX - speed*cos((angle + 90)*PI/180);
    centerY = centerY - speed*sin((angle + 90)*PI/180);
  }

  /* Decreases the angle of the ship, as a result 
   rotating it to the left. */
  void rotateLeft()
  {
    angle = angle - 4;
  }

  /* Increases the angle of the ship, as a result 
   rotating it to the right. */
  void rotateRight()
  {
    angle = angle + 4;
  }

  // Accelerates the ship by 0.15 units.
  void accelerate()
  {
    if (speed < 8)
    {
      speed = speed + 0.15;
    }
  }

  // Deaccelerates the ship by 0.04 units.
  void deaccelerate()
  {
    if (speed > 0)
    {
      speed = speed - 0.06;
    }
    if (speed < 0)
    {
      speed = 0;
    }

    //updates x and y
    x = centerX-37;
    y = centerY-40;
  }

  public void addCurrency(int newCurrency) 
  {
    this.currency += newCurrency;
  }

  public int getCurrency() 
  {
    return this.currency;
  }

  public void handleCollision() 
  {
    //health decrease is found in Asteroid.handleCollision()
    if (this.health <= 0)
    {
      isGameOver = true;
    }
  }

  // Decrease the health of the ship by the desired amount.
  public void decreaseHealth(int amount)
  {
    health = health - amount;
  }

  // Updates the color of the valves in the wings to give visual feedback on the player's health.
  public void updateHealthIndicator()
  {
    if(health > 50)
    {
      healthIndicator = color(255 - health*2, 255, 0);
    }
    else if(health <= 50 && health > 10)
    {
      healthIndicator = color(255, health*2, 0);
    }
    else 
    {
      blinkingEffect();
      healthIndicator = color(255, health*2, 0, healthBrightness);
    }
  }

  // Blinks the health valves when the health is critical (health < 10).
  public void blinkingEffect()
  {
    if(healthBrightness < 0 || healthBrightness > 250)
    {
      incBrightness = -incBrightness;
    }
    healthBrightness = healthBrightness - incBrightness;
  }

  // Switch the type of bullet.  
  public void changeType()
  {
    if(bulletType < 3)
    {
      bulletType++;
    }
    else
    {
      bulletType = 1;
    }
  }

  //for scroll wheel
  public void changeType(int delta)
  {
    if (delta > 0)
      bulletType++;
    else if (delta < 0)
      bulletType--;

    if (bulletType > 3 && delta > 0)    
      bulletType = 1; 

    if (bulletType < 1 && delta < 0)
      bulletType = 3;
  }
  
  public String getBulletType() 
  {
    return currentBullet;
  }

  // Determines the turret color according to the type of bullet.
  public color determineTurretColor()
  {
    if (player.getPowerUp().getName().equals("RGB Bullet") && player.getPowerUp().getIsActivated())
      return color(255);
      
    color temp = color(255);
    // Sets red
    if(player.bulletType == 1)
    {
      temp = color(242, 7, 66);
    }
    // Sets green
    else if(player.bulletType == 2)
    {
      temp = color(59, 214, 17);
    }
    // Sets blue
    else if(player.bulletType == 3)
    {
      temp = color(15, 89, 214);
    }
    return temp;
  }
  
  // Works the same as determineTurretColor but returns the color with a transparency factor
  public color determineLaserColor()
  {
    color temp = color(255);
    // Sets red
    if(player.bulletType == 1)
    {
      temp = color(242, 7, 66, 100);
    }
    // Sets green
    else if(player.bulletType == 2)
    {
      temp = color(59, 214, 17, 100);
    }
    // Sets blue
    else if(player.bulletType == 3)
    {
      temp = color(15, 89, 214, 100);
    }
    return temp;
  }
  
  public void usePowerUp()
  {
    powerup.activate();  
  }
  
  public void endPowerUp()
  {
    if (!powerup.getName().equals("Empty"))
    {
      if (powerup.getIsActivated())
      {        
        if (powerup.getTimer().isOver())
        {
          powerup.reset();
          powerup = new PowerUp();
        }
      }
    }
  }
  
  // Checks if the time frozen ability is still valid
  public void checkTimeFreeze()
  {
    if(freezeTimer.isOver())
    {
      isTimeFrozen = false;
    }
  }
  
  public void addFreezeCharge(float fC) 
  {
    if(freezeCharge < maxCharge)
    { 
      this.freezeCharge = this.freezeCharge + fC;
    }
    if(freezeCharge > maxCharge)
    {
      this.freezeCharge = this.maxCharge;
    }
  }
  
  public void addScore(int s) {this.score += s;}
  public String getColour() {return "error";} //no reason to use this
  public void setPowerUp(PowerUp pu) {powerup = pu;}
  public PowerUp getPowerUp() {return powerup;}
}

