import processing.core.*; 
import processing.xml.*; 

import java.awt.event.*; 
import java.sql.Time; 
import pitaru.sonia_v2_9.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Object extends PApplet {

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
class Asteroid extends Obj
{      
  private float speedX, speedY; //a speed for each axis
  private int colour; //outline
  
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
 
  public Asteroid(float x, float y, float wid, float hei, float speedX, float speedY, int colour)
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
  
  private int convertColour(String colour)
  {
    //converts string to color type    
    if (colour == "red")
      return color(242, 7, 66);
    else if (colour == "blue")
      return color(15, 89, 214);
    else if (colour == "green")
      return color(59, 214, 17);
    else
      return 0xffCCD60F; //defaults to yellow
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
    destroyedAsteroidSound.setVolume(1.2f);
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
      collidable.add(new Mineral(this.x+(this.wid/2), this.y+(this.hei/2), PApplet.parseInt(random(2, 4)), PApplet.parseInt(random(2, 4)), 10, 0xffD7DE2B)); 
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
class AsteroidSeekingBullet extends Bullet
{
  private float destinationX, destinationY;
  private boolean found = false;
  
  public AsteroidSeekingBullet(float x, float y, float w, float h, float v, float ang)
  {
    super(x, y, w, h, v, ang);
    
    col = determineColor();
    //restarts count
    player.rateOfFire.restart();    
    findAsteroid();
  }
  
  private void findAsteroid()
  {
    float closestX = width+5;
    float closestY = height+5;
    
    for (int i = 0; i < asteroids.len(); i++)
    {
      if ((asteroids.grab(i).x < closestX && asteroids.grab(i).y < closestY) && asteroids.grab(i).getColour() == this.getColour())
      {
        closestX = asteroids.grab(i).x;
        closestY = asteroids.grab(i).y;
        found = true;
      }
    }
    
    //the location to move towards
    destinationX = closestX;
    destinationY = closestY;
  }    
  
  public void move()
  {
    if (found)
    {
      float tempX = x;
      float tempY = y;
      
      if (destinationX >= x+speed)
        x += speed;
      else if (destinationX <= x-speed)
        x -= speed;
      else if (destinationX > x && destinationX < x+speed)
        x += destinationX-x;
      else if (destinationX < x && destinationX > x-speed)
        x -= x-destinationX; 
      
      if (destinationY >= y+speed)
        y += speed;
      else if (destinationY <= y-speed)
        y -= speed;  
      else if (destinationY > y && destinationY < y+speed)
        y += destinationY-y;
      else if (destinationY < y && destinationY > y-speed)
        y -= y-destinationY;   
  
      if (isOffScreen())
      {
        playerObjects.del(getEverything());
      }
      
      //if the bullet has stopped moving (eg. the target wasn't the proper colour)
      //delete itself
      if (tempX == x && tempY == y)
        handleCollision();
        
      findAsteroid();
    }
    else
    {
      super.move();
    }
  }
}
class Bullet extends Obj
{
  protected float speed;
  private float angle;
  protected int col;
  
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
      fill(0xffDBE60B);
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
  public int determineColor()
  {
    int temp = color(255);
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
// Keeps track of which text to display
int sequenceNumber = 0;

/* Animates the title and the subtitle in the credits to 
 come quickly into the screen, slow down through a 
 specific range, and then exit the screen. */
public void creditsAnimation(String title, String subTitle)
{
  if (creditsTitleX < (width/2 - 500))
  {
    creditsTitleX = creditsTitleX + 20;
    creditsSubTitleX = creditsSubTitleX - 20;
  }
  else if (creditsTitleX >= (width/2 - 500) && creditsTitleX <= (width/2 - 100))
  {
    creditsTitleX = creditsTitleX + 2;
    creditsSubTitleX = creditsSubTitleX - 2;
  }
  else if (creditsTitleX > (width/2 - 100) && creditsTitleX <= (width + 10))
  {
    creditsTitleX = creditsTitleX + 100;
    creditsSubTitleX = creditsSubTitleX - 100;
  }
  else
  {
    creditsTitleX = -500;
    creditsSubTitleX = width + 300;
    sequenceNumber++;
  }
  textFont(creditsFont);
  fill(175, 18, 18);
  text(title, creditsTitleX, (height/2) - 80);
  fill(245, 209, 163);
  text(subTitle, creditsSubTitleX, (height/2) + 80);
}

// Uses credits animation to display ALL the credits in order.    
public void creditsSequence()
{
  if(!creditsMusic.isPlaying())
  {
    creditsMusic.play();
  }
  if (sequenceNumber == 0)
  {
    creditsAnimation("Game Developers", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 1)
  {
    creditsAnimation("Game Developers", "Scott Andrechek");
  }
  else if (sequenceNumber == 2)
  {
    creditsAnimation("Course", "Intro to Game Dev I");
  }
  else if (sequenceNumber == 3)
  {
    creditsAnimation("Professor", "Dr. David Mould");
  }
  else if (sequenceNumber == 4)
  {
    creditsAnimation("Art Work", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 5)
  {
    creditsAnimation("Artificial Intelligence", "Scott Andrechek");
  }
  else if (sequenceNumber == 6)
  {
    creditsAnimation("Music", "Multiple Sources");
  }
  else if (sequenceNumber == 7)
  {
    creditsAnimation("Menu Design/Implementation", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 8)
  {
    creditsAnimation("Collision Detection", "Scott Andrechek");
  }
  else if (sequenceNumber == 9)
  {
    creditsAnimation("Graphics", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 10)
  {
    creditsAnimation("Wave System", "Scott Andrechek");
  }
  else if (sequenceNumber == 11)
  {
    creditsAnimation("Spaceship Development", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 12)
  {
    creditsAnimation("Game Engine Development", "Scott Andrechek");
  }
  else if (sequenceNumber == 13)
  {
    creditsAnimation("Heads Up Display", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 14)
  {
    creditsAnimation("Power Ups", "Scott Andrechek");
  }
  else if (sequenceNumber == 15)
  {
    creditsAnimation("Particle Physics", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 16)
  {
    creditsAnimation("Store", "Scott Andrechek");
  }
  else if (sequenceNumber == 17)
  {
    creditsAnimation("Music Player", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 18)
  {
    creditsAnimation("Game Testers", "Brad Martin");
  }
  else if (sequenceNumber == 19)
  {
    creditsAnimation("Game Testers", "Marko Arizanovic");
  }
  else if (sequenceNumber == 20)
  {
    creditsAnimation("Game Testers", "Haamed Sultani");
  }
  else if (sequenceNumber == 21)
  {
    creditsAnimation("Game Testers", "Moe Abushawish");
  }
  else if (sequenceNumber == 22)
  {
    creditsAnimation("Game Testers", "Andrew Abdalla");
  }
  else if (sequenceNumber == 23)
  {
    creditsAnimation("Game Testers", "Sahel Farah");
  }
  else if (sequenceNumber == 24)
  {
    creditsAnimation("Debugging", "Scott Andrechek");
  }
  else if (sequenceNumber == 25)
  {
    creditsAnimation("Debugging", "Hrishi Mukherjee");
  }
  else if (sequenceNumber == 26)
  {
    creditsAnimation("Special Thanks To", "Our Friends and Families");
  }
  else if (sequenceNumber == 27)
  {
    creditsAnimation("Thank You For Playing", "TIME BENDERS");
  }
  else if (sequenceNumber == 28)
  {
    isCreditsPlaying = false;
    creditsReset = true;
    sequenceNumber = 0;
  }
}
public boolean isCollision(Obj obj1, Obj obj2)
{
  //Calculate the sides of this
  float leftThis = obj1.x;
  float rightThis = obj1.x + obj1.wid;
  float topThis = obj1.y;
  float bottomThis = obj1.y + obj1.hei;

  //Calculate the sides of obj
  float leftObj = obj2.x;
  float rightObj = obj2.x + obj2.wid;
  float topObj = obj2.y;
  float bottomObj = obj2.y + obj2.hei;   
 
  //these return false if there is NOT a collision  
  if (bottomThis <= topObj || topThis >= bottomObj 
      || rightThis <= leftObj || leftThis >= rightObj)
  {        
    return false;
  }
  
  //if none of the sides from A are outside B
  return true;
}    

// Loads the config file from the folder
public void loadConfig()
{
  try
  {    
    //loads file
    String[] inConfig = loadStrings("config.txt");
    //loads width and height; 
    currentResolutionChoice = Character.getNumericValue(inConfig[0].charAt(inConfig[0].length()-1));
    screenW = widthResolutions[currentResolutionChoice];
    screenH = heightResolutions[currentResolutionChoice];
    
    isFullScreen = Boolean.valueOf(inConfig[1].substring(inConfig[1].indexOf(':')+2, inConfig[1].length()));
    
    println("Resolution Choice:\t" + currentResolutionChoice + "\tScreen W:\t" + screenW + "\tScreen H:\t" + screenH);
    println("Full Screen:\t" + isFullScreen);    

    println("Success. File read.");
  }
  catch (Exception fe) //in case the file does not exist
  {
    println("Error. File config.txt not found.");
    //create file, set resolution and add them to the file
    PrintWriter outConfig = createWriter("config.txt");
    
    currentResolutionChoice = 0;
    isFullScreen = false;
    
    screenW = widthResolutions[currentResolutionChoice];
    screenH = heightResolutions[currentResolutionChoice];
    
    outConfig.print("Current Resolution Choice: ");
    outConfig.println(currentResolutionChoice);
    
    outConfig.print("Full Screen: ");
    outConfig.println(isFullScreen);
    
    outConfig.flush();
    outConfig.close();
  }
}

// Resets all the game variables when called (Used when 'new game' is selected)
public void resetGameVariables()
{
  // Reset Dynamic lists
  asteroids = new dyList(); 
  collidable = new dyList(); 
  playerObjects = new dyList();
  explosions = new dyList();
  // Reset Spaceship
  player = new Spaceship(screenW/2, screenH/2, 0xffB73907);
  playerObjects.add(player);
  // Reset Game booleans
  isGameOver = false;
  isPaused = false;
  isTimeFrozen = false;
  // Reset the Wave System
  wave = new WaveSystem();
  // Reset Freeze Bar attributes
  freezeBarBrightness = 250;
  freezeBrightnessInc = 20;
  freezeBarWidth = 0;
}

// Saves the relevant game variables
public void saveGame()
{
  String[] saveConfig = new String[9];
  
  saveConfig[0] = (Integer.toString(player.health));
  saveConfig[1] = (Float.toString(player.freezeCharge));
  saveConfig[2] = (player.getPowerUp().getName());
  saveConfig[3] = (Integer.toString(player.getPowerUp().getPrice()));
  saveConfig[4] = (Long.toString(player.getPowerUp().getDuration()));
  saveConfig[5] = (Integer.toString(player.currency));
  saveConfig[6] = (Integer.toString(player.score));
  saveConfig[7] = (Integer.toString(wave.waveCount));
  saveConfig[8] = (Float.toString(wave.enemiesPerWave));
  
  saveStrings("saveConfig.txt", saveConfig);
}

// Loads the relevant game variables
public boolean loadGame()
{
  String[] loadConfig = loadStrings("saveConfig.txt");
  
  if (loadConfig == null)
    return false;
  
  player.health = PApplet.parseInt(loadConfig[0]);
  player.freezeCharge = PApplet.parseFloat(loadConfig[1]);
  player.powerup = new PowerUp(loadConfig[2], PApplet.parseInt(loadConfig[3]), Long.valueOf(loadConfig[4]));
  player.currency = PApplet.parseInt(loadConfig[5]);
  player.score = PApplet.parseInt(loadConfig[6]);
  wave.waveCount = PApplet.parseInt(loadConfig[7])-1;  //to avoid new wave error
  wave.enemiesPerWave = PApplet.parseFloat(loadConfig[8]);
  
  return true;
}
 //for scoll wheel usage

public String randColour()
{
  int rand = PApplet.parseInt(random(3));

  switch (rand)
  {
  case 0:
    return "red";
  case 1:
    return "green";
  case 2:
    return "blue";
  }

  return "error";
}

public void keyPressed()
{         
  if (!(keys.contains(key) || keys.contains(keyCode)))
  {    
    keys.add(keyCode); //int allows easier deletion
  }
  // Checks if the player wants to skip game intro
  if(isGameIntro)
  {
    if(keys.contains(10))
    {
      isGameIntro = false;
      introReset = true;
    }
  }
  // Checks if the player wants to skip the credits
  if(isCreditsPlaying)
  {
    if(keys.contains(10))
    {
      isCreditsPlaying = false;
      creditsReset = true;
    }
  }
  // Only checks for these keys when the game is running 
  if (isGameRunning && !isGameOver)
  {
    // Shoots a bullet whenever the spacebar is pressed
    if (keys.contains(32))
    {      
      if (player.rateOfFire.isOver())
      {
        player.playShotSound(); 
        if (player.getBulletType().equals("std"))
          playerObjects.add(new Bullet(player.centerX-7, player.centerY, 6, 6, player.speed + 10, player.angle)); 
        else if (player.getBulletType().equals("seek"))
          playerObjects.add(new AsteroidSeekingBullet(player.centerX-7, player.centerY, 6, 6, player.speed + 8, player.angle));
        else if (player.getBulletType().equals("spread"))
        {
          playerObjects.add(new Bullet(player.centerX-7, player.centerY, 6, 6, player.speed + 10, player.angle)); 
          playerObjects.add(new Bullet(player.centerX-14, player.centerY - 10, 6, 6, player.speed + 10, player.angle + 30)); 
          playerObjects.add(new Bullet(player.centerX, player.centerY - 10, 6, 6, player.speed + 10, player.angle - 30));
        }
      }
    }  

    // Changes the type of the bullet shot
    if (keys.contains(90)) // 'z'
    {
      player.changeType();
    }
    // Quickly switch to a specific bullet colour
    if (keys.contains(65)) // 'a'
    {
      player.bulletType = 1;
    }
    if (keys.contains(87)) // 'w'
    {
      player.bulletType = 2;
    }
    if (keys.contains(68)) // 'd'
    {
      player.bulletType = 3;
    }  

    if (keys.contains(83)) //'s' for store
    {
      if (startGameTimer.isOver())
      {
        if (store.isStoreOpened())
        {
          isPaused = false;
          store.closeStore();
        }
        else
        {
          isPaused = true;
          store.openStore();
        }
      }
    }
    if (keys.contains(16)) //right shift
    {
      if (!player.getPowerUp().getName().equals("Empty") && !player.getPowerUp().getIsActivated())
      {
        player.usePowerUp();
      }
    }
    // 'e' for TimeFreeze
    if (keys.contains(69))
    {
      // Only enables TimeFreeze if the max charge is accumulated
      if (player.freezeCharge >= player.maxCharge)
      {
        playerObjects.add(new TimeFreeze(player.centerX, player.centerY));
        isTimeFrozen = true;
        if (!timeFreezeSound.isPlaying())
        {
          timeFreezeSound.setVolume(2);
          timeFreezeSound.play();
        }
        player.freezeCharge = 0;
      }
    }

    if (keys.contains(86) && isGameRunning) // 'v'
    {      
      saveGame();
      // To display the indicator on the screen
      gameSavedIndicatorBrightness = 255;
    }

    if (store.isStoreOpened() && !isGameOver)
    {
      //"quick buy" buttons
      if (keys.contains(49)) //49-56 is 1-8 on the top row of number keys
      {
        store.purchaseSlot(0);
      }
      if (keys.contains(50)) 
      {
        store.purchaseSlot(1);
      }
      if (keys.contains(51)) 
      {
        store.purchaseSlot(2);
      }
      if (keys.contains(52)) 
      {
        store.purchaseSlot(3);
      }
      if (keys.contains(53)) 
      {
        store.purchaseSlot(4);
      }
      if (keys.contains(54)) 
      {
        store.purchaseSlot(5);
      }
      if (keys.contains(55)) 
      {
        store.purchaseSlot(6);
      }
      if (keys.contains(56)) 
      {
        store.purchaseSlot(7);
      }
    }
  }
}

public boolean getPlayerName() // Returns true if done
{
  if (debounce.getEnd() == 0)
    debounce = new Timer(200);

  if (debounce.isOver())
  {
    if (keys.contains(8)) // Backspace
    {      
      if (playerName.length() > 0)
        playerName = playerName.substring(0, playerName.length()-1);

      debounce.restart();
    }
    else if (keys.contains(10) && playerName.length() > 1) // Return
    {      
      String[] outConfig = loadStrings("highscores.txt");

      for (int i = 0; i < outConfig.length; i++)
      {
        if (player.score > Integer.parseInt(outConfig[i].substring(outConfig[i].indexOf(' ')+1, outConfig[i].length())))
        {
          for (int j = outConfig.length-1; j > i; j--)          
            outConfig[j] = outConfig[j-1];          

          outConfig[i] = playerName + " " + Integer.toString(player.score);   
          saveStrings("highscores.txt", outConfig);   
          break;
        }
      }

      return true;
    }
    else if (keyPressed && key != ' ' && playerName.length() < 10) // Check for no spacebar
    {
      playerName += PApplet.parseChar(key);      
      debounce.restart();
    }
  }

  return false;
}

public void keyReleased()
{
  //clears the released key
  int i = keys.indexOf(keyCode);
  keys.remove(i);
}

public void mouseReleased()
{
  if (store.getIsClicked() && store.isStoreOpened()) 
    store.changeIsClickedState(); //when the mouse is released it will set isClicked back to false
}

public boolean isMouseOver(Obj o) {
  return isCollision(new Button(mouseX, mouseY, 0.05f, 0.05f), o);
}

 //for rate of fire
 // For Sonia (Sound Class)

// GLOBALS:
// ========
// Width and Height are read from the config file
int screenW = 0; 
int screenH = 0;
// Lists of all objects
dyList asteroids = new dyList(); //all enemies
dyList collidable = new dyList(); //currency and powerups
dyList playerObjects = new dyList(); //all player owned objects (ship, bullets, etc)
dyList explosions = new dyList(); // Keeps track of all explosions 
// Creating the player 
// Note: MUST be global for use in collision detection
Spaceship player;
// Heads Up Display Font:
PFont jediFont;
PFont introFontOne;
PFont creditsFont;
// Game specific booleans
boolean isGameOver = false;
boolean isPaused = false;
boolean isTimeFrozen = false;
boolean isGameRunning = false;
boolean isFirstRun = true;
boolean isGameIntro = true;
boolean isCreditsPlaying = false;
// Countdown Timer for the beginning of the game
Timer startGameTimer = new Timer(0);
// List of character for keys
ArrayList keys = new ArrayList();
// Wave system
WaveSystem wave = new WaveSystem();  
// The Store
Store store;
// Freeze Bar attributes
int freezeBarBrightness = 250;
int freezeBrightnessInc = 20;
float freezeBarWidth = 0;
// The Main Menu
MainMenu mainMenu;
// Resolution
int[] widthResolutions = {1366, 1600, 1920, 2048};
int[] heightResolutions = {768, 900, 1080, 1152};
int currentResolutionChoice = 1; //default
boolean isFullScreen = false;
// Main game background
ScrollingBackground scrollingBack;
// Timer for displaying Game Over for a length of time
Timer gameOverDisplayTimer = new Timer(0);
// Player name
String playerName = "";
// Debounce Timer
Timer debounce = new Timer(0);
// Game Saved Indicator Brightness
int gameSavedIndicatorBrightness;
// Music Player for the game
MusicPlayer musicPlayer;
// Sound Effects used for the game
Sample mouseClickedSound;
Sample collectedMineralSound;
Sample destroyedAsteroidSound;
Sample shipClangSound;
Sample shipShotSound;
Sample shipEngineSound;
Sample timeFreezeSound;
Sample powerupActivateSound;
Sample epic;
Sample introMusic;
Sample creditsMusic;
// Intro sequence:
float hrishiX;
float mukherjeeX;
float scottX;
float andrecheckX;
int namesB;
int titleUpB; 
int titleDownB;
boolean introReset = true;
boolean epicPlayedOnce = false;
boolean bendersPlayedOnce = false;
// Credits Sequence
float creditsTitleX; 
float creditsSubTitleX;
boolean creditsReset = true;
// Applies the fade in effect to a specific brightness.
public int fadeIn(int b, int speed)
{
  if (b < 255)
  {
    b = b + speed;
  }
  return b;
}

// Applies the fade out effect to a specific brightness.
public int fadeOut(int b, int speed)
{
  if (b > 0)
  {
    b = b - speed;
  }
  return b;
}

// Cues the name sequence (Game Intro).
public void nameSequence()
{
  if(!introMusic.isPlaying())
  {
    introMusic.play();
  }
  textFont(introFontOne);
  if (frameCount > 180 && frameCount < 900)
  {
    moveNames();
    if (frameCount < 600)
    {
      namesB = fadeIn(namesB, 1);
    }
    else if (frameCount > 600)
    {
      namesB = fadeOut(namesB, 1);
    }
    fill(175, 18, 18, namesB);
    text("hrishi", hrishiX, 100);
    text("scott", scottX, height - 150);
    fill(245, 209, 163, namesB); 
    text("andrechek", andrecheckX, height - 100);
    text("mukherjee", mukherjeeX, 150);
  }
  else if (frameCount > 900 && frameCount < 1500)
  {
    if (frameCount < 1200)
    {
      namesB = fadeIn(namesB, 2);
    }
    else
    {
      namesB = fadeOut(namesB, 2);
    }
    fill(55, 201, 122, namesB);
    text("present", 60, 100);
  }
}

// Moves all the names towards each other (Game Intro).
public void moveNames()
{
  hrishiX = hrishiX + 0.5f;
  andrecheckX = andrecheckX - 0.5f;
  mukherjeeX = mukherjeeX - 0.5f;
  scottX = scottX + 0.5f;
}

// Cues the title sequence (Game Intro).
public void titleSequence()
{
  if (frameCount > 1400 && frameCount < 2400)
  {
    if (frameCount > 1400 && frameCount < 2100)
    {
      if(!epic.isPlaying() && epicPlayedOnce == false)
      {
        epic.play();
        epicPlayedOnce = true;
      }
      titleUpB = fadeIn(titleUpB, 5);
    }
    if (frameCount > 1520 && frameCount < 2100) 
    {
      if(!epic.isPlaying() && bendersPlayedOnce == false)
      {
        epic.play();
        bendersPlayedOnce = true;
      }
      titleDownB = fadeIn(titleDownB, 5);
    }
    if (frameCount > 1700)
    {
      titleUpB = fadeOut(titleUpB, 6);
      titleDownB = fadeOut(titleDownB, 6);
    }
    if(frameCount > 2200)
    {
      isGameIntro = false;
      introReset = true;
    }
    textFont(mainMenu.titleFont);
    fill(160, 10, 125, titleUpB);
    text("time", (width/2) - 250, (height/2) - 20);
    fill(165, 158, 12, titleDownB);
    text("benders", (width/2) - 200, (height/2) + 80);
  }
}


class MainMenu
{
  // Create a menu using the menu class
  Menu main;
  Menu startGameMenu;
  Menu extrasMenu;
  Menu optionsMenu;
  // Background Image for the main menu
  PImage backImage;
  float imageX;
  float imageY;
  int imageW;
  int imageH;
  int imageIncX;
  int imageIncY;
  // Text for the options
  String[] menuText = {"Start Game", "High Scores", "Extras", "options", "Exit"};
  String[] startGameMenuText = {"Continue", "New Game"};
  String[] extrasMenuText = {"intro Sequence", "Credits"};
  String[] optionsMenuText = new String[2]; //optionsMenuText is in MainMenu()  
  // Title font
  PFont titleFont;
  Timer fadeTimer = new Timer(0);
  float noSaveTextX;
  
  //for options menu
  PFont arrowFont = createFont("space age.ttf", 16);
  int leftArrowColour = color(36, 224, 103);
  int rightArrowColour = color(36, 224, 103);
  Button leftArrow;
  Button rightArrow;
  Button acceptRes;
  Timer buttonDebounce = new Timer(0);
  boolean displayOk = false;
  boolean insideOptionsMenu = false;
  int resIndex = currentResolutionChoice;
  
  
  public MainMenu()
  {
    main = new Menu(50, (screenH*2)/3, 200, 50, 5);
    main.initialize(menuText);
    backImage = new PImage();
    backImage = loadImage("mainMenuBackground.jpg");
    imageX = 0;
    imageY = 0;
    imageW = screenW + 600;
    imageH = screenH + 375;
    imageIncX = -1;
    imageIncY = -1;
    backImage.resize(imageW, imageH);
    titleFont = createFont("ultimate MIDNIGHT.ttf", 128);   
    
    
    optionsMenuText[0] = "Resolution";
    if (!isFullScreen)
      optionsMenuText[1] = "Full Screen";
    else
      optionsMenuText[1] = "Windowed";    
  }
  
  // Displays the main menu 
  public void display()
  {
    // Display the background image
    panImage();
    image(backImage, imageX, imageY);
    // Display the main tab
    main.display();
    // Determine if sub menu needs to be created
    createStartGameMenu();
    createExtrasMenu();
    createOptionsMenu();
    // Display the sub menu
    displayStartGameMenu();
    displayExtrasMenu();
    displayOptionsMenu();
    // Display the title
    displayTitle();
    
    if (isFullScreen)     
    {
      frame.setExtendedState(Frame.MAXIMIZED_BOTH); //maximizes     
      frame.setLocation(0, 0); //sets the position to the top left hand corner of the monitor
    }
    else      
    {
      frame.setExtendedState(Frame.NORMAL);  
      frame.setLocationRelativeTo(null); //centres the game to the monitor
    }
  }
  
  // Handles the events for all options in the main tab
  public void handleEvents()
  {
    handleStartGameMenuEvents();
    handleOptionsMenuEvents();
    // Handles events for the high scores option
    if (main.selectedOption == 1)
    {
      insideOptionsMenu = false;
      
      String[] inConfig = loadStrings("highscores.txt");
      
      textFont(jediFont);         
      textSize(20);
      
      fill(165, 158, 12, 100);
      rect(((width*2)/3) - 25, (height/3) - 50, 300, 350);
      
      for (int i = 0; i < inConfig.length; i++)
      {
        if (i == 0)
          fill(0xffE5EB36);
        else
          fill(0xffE3E3DC);
          
        text((i+1) + ". " + inConfig[i].substring(0, inConfig[i].indexOf(' ')), (width*2)/3, (height/3)+(i*50)); // Name
        text("  " + inConfig[i].substring(inConfig[i].indexOf(' ')+1, inConfig[i].length()), (width*2)/3+textWidth((i+1) + ". " + inConfig[i].substring(0, inConfig[i].indexOf(' '))), (height/3)+(i*50)); // Score
      }
    }    
    // Handles the extras menu events
    handleExtrasMenuEvents();
    if(main.selectedOption == 4)
    {
      // Close the program
      exit();
    }
  }
  
  // Displays the extras sub menu 
  public void displayExtrasMenu()
  {
    if(main.selectedOption == 2)
    {
      extrasMenu.display();
      // Draw the expanding lines
      drawSubMenuLines(2, color(255));
    }
  }
  
  // Displays the start game sub menu
  public void displayStartGameMenu()
  {
    if(main.selectedOption == 0)
    {
       startGameMenu.display();
       // Draw the expanding lines
       drawSubMenuLines(0, color(255));
    }
  }
  
  // Displays the Options Menu
  public void displayOptionsMenu()
  {
    if (main.selectedOption == 3)
    {
      optionsMenu.display();
      // Draw the expanding lines
      drawSubMenuLines(3, color(255));
    }
  }
  
  // Draws the title at the top left
  public void displayTitle()
  {
    textFont(titleFont);
    fill(160, 10, 125);
    text("time", 40, 150);
    fill(165, 158, 12);
    text("benders", 90, 250);
  }
  
  // Creates the extras sub menu
  public void createExtrasMenu()
  {
    if(isMouseOver(main.menuButtons[2]) && mousePressed)
    {
      insideOptionsMenu = false;
      extrasMenu = new Menu(250, (screenH*2/3) + 1.5f*main.buttonHeight, 200, 50, 2);
      extrasMenu.initialize(extrasMenuText);
    }
  }
  
  // Creates the start game sub menu
  public void createStartGameMenu()
  {
    if(isMouseOver(main.menuButtons[0]) && mousePressed)
    {
      insideOptionsMenu = false;
      startGameMenu = new Menu(250, (screenH*2/3) + (-0.5f*main.buttonHeight), 200, 50, 2);
      startGameMenu.initialize(startGameMenuText);
    }
  }
  
  // Creates the options menu
  public void createOptionsMenu()
  {
    if (isMouseOver(main.menuButtons[3]) && mousePressed)
    {
      insideOptionsMenu = true;
      optionsMenu = new Menu(250, (screenH*2/3) + (2.5f*main.buttonHeight), 200, 50, 2);
      optionsMenu.initialize(optionsMenuText);
      //for options menu
      leftArrow = new Button(optionsMenu.menuButtons[0].x + 215, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("<-")+10, 30);   
      rightArrow = new Button(optionsMenu.menuButtons[0].x + 355, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("->")+10, 30);
      acceptRes = new Button(optionsMenu.menuButtons[0].x + 270, (optionsMenu.menuButtons[0].y+60 + (optionsMenu.buttonHeight/2))-20, textWidth("ok")+10, 20);
    }
  }
  
  // Move the background image
  public void panImage()
  {
    if(imageX + imageW < screenW || imageX > 0)
    {
      imageIncX = -imageIncX;
    }
    if(imageY + imageH < screenH || imageY > 0)
    {
      imageIncY = -imageIncY;
    }
    imageX = imageX + imageIncX;
    imageY = imageY + imageIncY;
  }
  
  // Takes in the button number and draws expanding sub menu lines from it
  public void drawSubMenuLines(int bN, int border)
  {
    stroke(border);
    strokeWeight(2);
    line(main.menuButtons[bN].x + 125, main.menuButtons[bN].y + 17, 
           main.menuButtons[bN].x + 150, main.menuButtons[bN].y + 17);
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (-0.5f*main.buttonHeight), 
           main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (1.25f*main.buttonHeight));
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (-0.5f*main.buttonHeight), 
           main.menuButtons[bN].x + 175, main.menuButtons[bN].y + (-0.5f*main.buttonHeight));
    line(main.menuButtons[bN].x + 150, main.menuButtons[bN].y + (1.25f*main.buttonHeight), 
           main.menuButtons[bN].x + 175, main.menuButtons[bN].y + (1.25f*main.buttonHeight));
  }
  
  // Overloaded to include x shift
  public void drawSubMenuLines(int bN, int border, float xPad)
  {
    stroke(border);
    strokeWeight(2);
    line(main.menuButtons[bN].x + xPad+125, main.menuButtons[bN].y + 17, 
           main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + 17);
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (-0.5f*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (1.25f*main.buttonHeight));
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (-0.5f*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+175, main.menuButtons[bN].y + (-0.5f*main.buttonHeight));
    line(main.menuButtons[bN].x + xPad+150, main.menuButtons[bN].y + (1.25f*main.buttonHeight), 
           main.menuButtons[bN].x + xPad+175, main.menuButtons[bN].y + (1.25f*main.buttonHeight));
  }
  
  // Handles the Start Game sub menu events
  public void handleStartGameMenuEvents()
  {
    // Only handles events if the menu exists at that moment
    if(startGameMenu != null)
    {
      if (startGameMenu.selectedOption == 0)
      { 
        if (loadGame())
        {
          isGameRunning = true;
        }
        else if (main.selectedOption == 0)
        {         
          textFont(jediFont);
          textSize(20);
          fill(0xff1BBFE0);
          text("No Save Found", (screenW - textWidth("No Save Found")) - 10, 25);      
        }          
      }
      if(startGameMenu.selectedOption == 1)
      {
        resetGameVariables();
        isGameRunning = true;
      }
    }
  }
  
  // Handles the Options sub menu events
  public void handleOptionsMenuEvents()
  {    
    if (optionsMenu != null)
    {
      //resets choice
      if (!insideOptionsMenu)
      {
        resIndex = currentResolutionChoice;
        displayOk = false;
      }
        
      if (optionsMenu.selectedOption == 0 && main.selectedOption == 3)
      {
        drawSubMenuLines(3, color(255), 225);    
        
        fill(leftArrowColour);
        textFont(arrowFont);        
        text("<-", optionsMenu.menuButtons[0].x + 215, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2));
        
        fill(36, 224, 103);
        textFont(jediFont);        
        text(widthResolutions[resIndex] + "x" + heightResolutions[resIndex], optionsMenu.menuButtons[0].x + 250, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2));
        
        fill(rightArrowColour);
        textFont(arrowFont);  
        text("->", optionsMenu.menuButtons[0].x + 355, optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2)); 
        
        if (displayOk)
        {
          if (isMouseOver(acceptRes))
          {
            fill(255);
            if (mousePressed)
            {
              displayOk = false;
              currentResolutionChoice = resIndex;
              
              //change Processing height and width variables
              size(widthResolutions[currentResolutionChoice], heightResolutions[currentResolutionChoice]);
              //change global variables
              screenW = widthResolutions[currentResolutionChoice];
              screenH = heightResolutions[currentResolutionChoice];
              //change actual window size
              frame.setSize(widthResolutions[currentResolutionChoice], heightResolutions[currentResolutionChoice]);
              //refresh menu to reflect size changes
              refreshMenu();      
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();   
            }
          }
          else
            fill(36, 224, 103);
            
          textFont(jediFont);
          text("ok", optionsMenu.menuButtons[0].x + 275, optionsMenu.menuButtons[0].y+55 + (optionsMenu.buttonHeight/2));
        }
        
        if (isMouseOver(leftArrow))
        {
          leftArrowColour = color(255);
          if (buttonDebounce.isOver())
          {  
            if (mousePressed)
            {
              buttonDebounce = new Timer(200);
              if (resIndex <= 0)
                resIndex = widthResolutions.length-1;
              else
                resIndex--;
              
              if (widthResolutions[resIndex] != screenW)
                displayOk = true;
              else
                displayOk = false;
            }     
          }     
        }
        else        
          leftArrowColour = color(36, 224, 103);         
        
        if (isMouseOver(rightArrow))
        {
          rightArrowColour = color(255);  
          if (buttonDebounce.isOver())
          { 
            if (mousePressed)
            {
              buttonDebounce = new Timer(200);
              if (resIndex >= widthResolutions.length-1)
                resIndex = 0;
              else
                resIndex++;
                
              if (widthResolutions[resIndex] != screenW)
                displayOk = true;
              else
                displayOk = false;
            }       
          }   
        }
        else        
          rightArrowColour = color(36, 224, 103);         
      } 

      if (optionsMenu.selectedOption == 1 && main.selectedOption == 3)
      {      
        if (mousePressed)
        {
          if (buttonDebounce.isOver())
          { 
            buttonDebounce = new Timer(200);
            if (!isFullScreen)
            {
              isFullScreen = true;
              optionsMenuText[1] = "Windowed";
              frame.setExtendedState(Frame.MAXIMIZED_BOTH);
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();
              
              outConfig.flush();
              outConfig.close(); 
            }
            else
            {
              isFullScreen = false;
              
              optionsMenuText[1] = "Full Screen";
              frame.setExtendedState(Frame.NORMAL);
              
              //write new data to file
              PrintWriter outConfig = createWriter("config.txt");  
              outConfig.print("Current Resolution Choice: ");
              outConfig.println(currentResolutionChoice);
              
              outConfig.print("Full Screen: ");
              outConfig.println(isFullScreen);
              
              outConfig.flush();
              outConfig.close();
              
              outConfig.flush();
              outConfig.close(); 
            }
            
            optionsMenu.initialize(optionsMenuText);
          }
        }
      }
    }
  }
  
  // Handles the Extras sub menu events
  public void handleExtrasMenuEvents()
  {
    // Put extras sub menu events in here:
    if(extrasMenu != null)
    {
      if(extrasMenu.selectedOption == 0)
      {
        extrasMenu.selectedOption = -1;
        musicPlayer.currentSong.track.stop();
        isGameIntro = true;
      }
      if(extrasMenu.selectedOption == 1)
      {
        extrasMenu.selectedOption = -1;
        musicPlayer.currentSong.track.stop();
        isCreditsPlaying = true;
      }
    }
  }
  
  // Refreshes the menu
  private void refreshMenu()
  {
    main = new Menu(50, (screenH*2)/3, 200, 50, 5);
    main.initialize(menuText);
    backImage = new PImage();
    backImage = loadImage("mainMenuBackground.jpg");
    imageX = 0;
    imageY = 0;
    imageW = screenW + 600;
    imageH = screenH + 375;
    imageIncX = -1;
    imageIncY = -1;
    backImage.resize(imageW, imageH);
    titleFont = createFont("ultimate MIDNIGHT.ttf", 128);   
    main.selectedOption = 3;
    
    insideOptionsMenu = true;
    optionsMenu = new Menu(250, (screenH*2/3) + (2.5f*main.buttonHeight), 200, 50, 2);
    optionsMenu.initialize(optionsMenuText);
    //for options menu
    leftArrow = new Button(optionsMenu.menuButtons[0].x + 215, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("<-")+10, 30);   
    rightArrow = new Button(optionsMenu.menuButtons[0].x + 355, (optionsMenu.menuButtons[0].y+25 + (optionsMenu.buttonHeight/2))-20, textWidth("->")+10, 30);
    acceptRes = new Button(optionsMenu.menuButtons[0].x + 270, (optionsMenu.menuButtons[0].y+60 + (optionsMenu.buttonHeight/2))-20, textWidth("ok")+10, 20);
    
    optionsMenu.selectedOption = 0;
  }
}
class Menu
{
  // Physical Attrributes
  float x;
  float y;
  float buttonWidth;
  float buttonHeight;
  // Parallel Arrays for the menu options
  Button[] menuButtons;
  String[] optionText;
  int[] optionColor;
  // Other Attributes
  int numOptions;
  int selectedOption;
  
  public Menu(float xPos, float yPos, float bWid, float bHei, int numElements)
  {
    x = xPos;
    y = yPos;
    buttonWidth = bWid;
    buttonHeight = bHei;
    numOptions = numElements;
    menuButtons = new Button[numOptions];
    optionText = new String[numOptions];
    optionColor = new int[numOptions];
    // Nothing selected yet
    selectedOption = -1;
  }
  
  // Initializes the buttons with the text provided in the parameter
  public void initialize(String[] textArray)
  {
    for(int i = 0; i < numOptions; i++)
    {
      menuButtons[i] = new Button(x, y + i*buttonHeight, buttonWidth, buttonHeight);
      optionText[i] = textArray[i];
      optionColor[i] = color(36, 224, 103);
    }
  }
  
  // Displays the buttons/text on the screen 
  public void display()
  {
    for(int i = 0; i < numOptions; i++)
    {
      textFont(jediFont);
      determineOptionColor(i);
      fill(optionColor[i]);
      text(optionText[i], menuButtons[i].x + 5, menuButtons[i].y + (buttonHeight/2));
    }
    determineSelectedOption();
  }
  
  /* Privately used to determine the color depending on the 
     mouse's position */
  private void determineOptionColor(int i)
  {
    if(isMouseOver(menuButtons[i]))
    {
      optionColor[i] = color(255);
    }
    else
    {
      optionColor[i] = color(36, 224, 103);
    }
  }
  
  // Determine the option the user has chosen    
  public void determineSelectedOption()
  {
    for(int i = 0; i < numOptions; i++)
    {
      if(isMouseOver(menuButtons[i]) && mousePressed)
      {
        selectedOption = i;
      }
    }
  }
}
class Mine extends Obj
{
  protected float speed;
  private float angle;
  protected int col;
  
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
    fill(0xffB409D6);
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

class Mineral extends Obj
{
  //the currency/(Minerals) class
  
  private int amount;
  private int colour;
  
  public Mineral(float x, float y, float w, float h, int amount, int colour)
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

class MusicPlayer
{
  // List of all songs
  Song[] trackList;
  Sample[] samples;
  // Songs already played
  boolean[] tracksPlayed;
  // Current song playing
  Song currentSong;
  // Timer for the duration of the current song
  Timer currentSongDuration;
  // Number of songs in the music player
  int numSongs;
  // Pop Up Box Attributes
  float pointOneX;
  float pointOneY;
  float pointTwoX;
  float pointTwoY;
  Timer popUpTimer;
  boolean boxOpen, popOut;
  boolean startTimer;
  PFont popUpFont;

  public MusicPlayer()
  {
    numSongs = 8;
    trackList = new Song[numSongs];
    samples = new Sample[numSongs];
    initializeTrackList();
    tracksPlayed = new boolean[numSongs];
    currentSong = nextSong();
    // Pop Up Box Attributes
    pointOneX = screenW;
    pointTwoX = screenW;
    pointOneY = screenH - 100;
    pointTwoY = screenH - 100;
    boxOpen = true;
    startTimer = true;
    popOut = true;
    popUpFont = createFont("Star_Jedi_Rounded.ttf", 14);
  }

  // Puts the songs files into the arrays
  public void initializeTrackList()
  {
    samples[0] = new Sample("/sounds/Rusko - Everyday (Netsky VIP Remix).wav");
    trackList[0] = new Song(samples[0], "everyday (netsky vip remix)", "rusko", "Single", 154000);
    samples[1] = new Sample("/sounds/Mord Fustang - Lick the Rainbow.wav");
    trackList[1] = new Song(samples[1], "lick the rainbow", "mord fustang", "Single", 142000);
    samples[2] = new Sample("/sounds/Adam K & Frederik Mooij - In A Mirror.wav");
    trackList[2] = new Song(samples[2], "in a mirror", "adam k and frederik m", "Single", 112000);
    samples[3] = new Sample("/sounds/Al Bizzare - Fire Breazze (Original Mix).wav");
    trackList[3] = new Song(samples[3], "fire breazze", "al bizzare", "Single", 132000);
    samples[4] = new Sample("/sounds/Sander Van Doorn - Chasin'(Original Mix).wav");
    trackList[4] = new Song(samples[4], "chasin", "sander van doorn", "Single", 150000);
    samples[5] = new Sample("/sounds/Dreamscape - 009 Sound System.wav");
    trackList[5] = new Song(samples[5], "dreamscape", "009 sound system", "Single", 127000);
    samples[6] = new Sample("/sounds/Porter Robinson - Language.wav");
    trackList[6] = new Song(samples[6], "language", "porter robinson", "Single", 132000);
    samples[7] = new Sample("/sounds/Techno Mix.wav");
    trackList[7] = new Song(samples[7], "techno mix", "unknown", "Single", 150000);
  }

  /* Plays the songs from the track list infinitely 
   in a random order (without repeating any tracks 
   until each track has been played at least once) */
  public void play()
  {
    if (!currentSong.track.isPlaying())
    {
      currentSongDuration = new Timer(currentSong.duration);
      currentSong.track.play();
    }
    if (currentSongDuration.isOver())
    {
      currentSong.track.stop();
      if (checkAllTracksPlayed())
      {
        resetTracksPlayed();
      }
      currentSong = nextSong();
      // Reset the pop up display's attributes
      popOut = true;
      boxOpen = true;
      startTimer = true;
      popUpTimer = null;
    }
  }

  // Returns a random song that has not been played
  public Song nextSong()
  {
    int randomSong;
    do
    {
      randomSong = (int)random(0, numSongs);
    }
    while (tracksPlayed[randomSong]);
    tracksPlayed[randomSong] = true;
    return trackList[randomSong];
  }

  // Resets the tracks played already
  public void resetTracksPlayed()
  {
    for (int i = 0; i < numSongs; i++)
    {
      tracksPlayed[i] = false;
    }
  }

  // Checks if all the tracks have been played
  public boolean checkAllTracksPlayed()
  {
    int numSongsPlayed = 0;
    for (int i = 0; i < numSongs; i++)
    {
      if (tracksPlayed[i] == true)
      {
        numSongsPlayed++;
      }
    }
    if (numSongsPlayed >= numSongs)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  // Displays the details of the current song in the bottom left for 
  public void displayPopUp()
  {
    if (boxOpen == true)
    {
      // Points move vertically to visualize the vertical expansion of the pop up
      if (pointOneY > (screenH - 150))
      {
        pointOneY = pointOneY - 2;
      }
      if (pointTwoY < (screenH - 50))
      {
        pointTwoY = pointTwoY + 2;
      }
      // Points move horizontally to visualize the horizontal expansion of the pop up
      if (pointOneY <= (screenH - 150))
      {
        if (pointOneX > (screenW - 250))
        {
          pointOneX = pointOneX - 4;
          pointTwoX = pointTwoX - 4;
        }
      }
    }
    else
    {
      // Points move horizontally to visualize the horizontal closure of the pop up
      if (pointOneX < screenW)
      {
        pointOneX = pointOneX + 4;
        pointTwoX = pointTwoX + 4;
      }
      else
      {
        // Points move vertically to visualize the vertical closure of the pop up 
        if (pointOneY < (screenH - 100))
        {
          pointOneY = pointOneY + 2;
        }
        if (pointTwoY > (screenH - 100))
        {
          pointTwoY = pointTwoY - 2;
        }
        else
        {
          popOut = false;
        }
      }
    } 
    // Timer starts when the box is completed
    if (pointOneX <= (screenW - 250) && startTimer == true)
    {
      // Keep the box opened for 5 seconds
      popUpTimer = new Timer(5000);
      startTimer = false;
    }
    if (popUpTimer != null && boxOpen)
    {
      // If the timer's over, initiate box closing animation
      if (popUpTimer.isOver())
      {
        boxOpen = false;
      }
    }
    // Draw the two points
    stroke(255, 253, 196);
    fill(255, 253, 196);
    strokeWeight(3);
    ellipse(pointOneX, pointOneY, 5, 5);
    ellipse(pointTwoX, pointTwoY, 5, 5);
    // Draw the box
    fill(57, 237, 226, 100);
    stroke(255, 253, 196);
    strokeWeight(3);
    rect(pointOneX, pointOneY, (screenW - pointOneX), (pointTwoY - pointOneY));
    // Fill in the details of the current song
    fill(255, 255, 255, 200);
    textFont(popUpFont);
    text(currentSong.name, pointOneX + 15, pointOneY + 25);
    text(currentSong.artist, pointOneX + 15, pointOneY + 55);
    text(currentSong.album, pointOneX + 15, pointOneY + 85);
  }
}
// Class used for particle physics
class Particle
{
  float x;
  float y;
  float speed;
  float angle;
  String col;
  int particleSize;
  int visibility;
  int deteriorationRate;
  
  public Particle(float xPos, float yPos, String c)
  {
    x = xPos;
    y = yPos;
    speed = random(1, 5);
    angle = random(0, 359);
    col = c;
    particleSize = 1;
    visibility = 255;
    deteriorationRate = (int)random(3, 7);
  }
  
  // Draws the particle
  public void display()
  {
    fill(determineColor());
    stroke(determineColor());
    strokeWeight(1);
    ellipse(x, y, particleSize, particleSize);
  }
  
  // Moves the particle  
  public void move() 
  {
    x = x + speed*cos(angle*PI/180);
    y = y + speed*sin(angle*PI/180);
    deteriorate();
  }
  
  public void deteriorate()
  {
    if(visibility > 0)
    {
      visibility = visibility - deteriorationRate;
    }
  }
  
  // Translates the string into the actual fill color and returns it
  public int determineColor()
  {
    int result = color(255);
    if(col == "red")
    {
      result = color(242, 7, 66, visibility);
    }
    else if(col == "blue")
    {
      result = color(15, 89, 214, visibility);
    }
    else if(col == "green")
    {
      result = color(59, 214, 17, visibility);
    }
    return result;
  }
}  
// Particle Physics simulating explosions
class ParticlePhysics extends Obj
{
  Particle[] particles;
  int numParticles;
  String col;

  public ParticlePhysics(float spawnX, float spawnY, int num, String c)
  {
    super(spawnX, spawnY, 0, 0);
    numParticles = num;
    particles = new Particle[numParticles];
    col = c;
    initializeParticles();
  }
  
  // Initializes the array of particles
  public void initializeParticles()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i] = new Particle(x, y, col);   
    }
  }
  
  // Displays all the particles
  public void display()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i].display();
    }
  }
  
  // Moves all the particles
  public void move()
  {
    for(int i = 0; i < numParticles; i++)
    {
      particles[i].move();
    }
  }
  
  // Checks if the explosion's over or not
  public boolean isFinished()
  {
    boolean result = true;
    for(int i = 0; i < numParticles; i++)
    {
      if(particles[i].visibility > 0)
      {
        result = false;
      }
    }
    return result;
  }
 
  public void handleCollision(){}
  public String getColour(){ return col; } 
  public String getEverything(){ return "error"; } 
}
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
      fill(0xffDBE60B);
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
      fill(0xffF06E2E);
      ellipse(x+20, y+20, 20, 20); 
    }
    else if (name.equals("Floating Mine"))
    {
      noStroke();
      fill(0xffB409D6);
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
    fill(0xff05E322, 102);
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
class ScrollingBackground
{
  private float x, y;
  private int w, h;
  private Timer scrollTimer;
  private PImage backImg;
  private String backFilename;
  
  public ScrollingBackground(String backFilename, long speed)
  {
    this.backFilename = backFilename;
    backImg = loadImage(this.backFilename);
    w = width;
    h = height;
    backImg.resize(w, h);
    scrollTimer = new Timer(speed);
    x = 0;
    y = 0;    
  }
  
  public void display()
  {
    if (scrollTimer.isOver())
    {     
      x -= width/1000;
      scrollTimer.restart();
    }
    
    if (x < -width)
        x = 0;
        
    if (w != width || h != height)
    {
      println("Reloaded Image");
      w = width;
      h = height;
      backImg = loadImage(backFilename);
      backImg.resize(w, h);
    }
    
    tint(255, 90);  
    image(backImg, x, y);
    image(backImg, x+width, y);    
    noTint();    
  }
}
// Used for Music Player
class Song
{
  // The actual .wav sound file 
  Sample track;
  // Details of the song
  String name;
  String artist;
  String album;
  // Duration in milliseconds
  long duration;
  
  public Song(Sample audioFile, String n, String art, String alb, long dur)
  {
    track = audioFile;
    name = n;
    artist = art;
    album = alb;
    duration = dur;
  }
}
class Spaceship extends Obj
{
  // Physical Attributes
  private float centerX, centerY;
  private float w, h;
  private float accel, speed;
  private float angle;
  private int c;
  
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
  private int healthIndicator;
  private int healthBrightness;
  private int incBrightness;
  
  // Charge for using Freeze Time
  float freezeCharge;
  float maxCharge;
  
  Spaceship(float x, float y, int col)
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
    accel = 0.05f;
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
  public void display()
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
    rect(w - 3, (h/3) - 1.6f, w - 1, h/10);
    triangle(w + 2, h/3, w + 8, (h/3) - (h*3)/4, w + 9, h/3);
    rect(-(w - 3), (h/3) - 1.6f, w - 1, h/10);
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
    shipShotSound.setVolume(1.5f);
    //shipShotSound.setSpeed(int(random(1, 4)));
    shipShotSound.play();
  }
  
  public void playClangSound()
  {
    shipClangSound.play();
  }

  // Move the spaceship
  public void move()
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
  public void moveStraight()
  {
    centerX = centerX - speed*cos((angle + 90)*PI/180);
    centerY = centerY - speed*sin((angle + 90)*PI/180);
  }

  /* Decreases the angle of the ship, as a result 
   rotating it to the left. */
  public void rotateLeft()
  {
    angle = angle - 4;
  }

  /* Increases the angle of the ship, as a result 
   rotating it to the right. */
  public void rotateRight()
  {
    angle = angle + 4;
  }

  // Accelerates the ship by 0.15 units.
  public void accelerate()
  {
    if (speed < 8)
    {
      speed = speed + 0.15f;
    }
  }

  // Deaccelerates the ship by 0.04 units.
  public void deaccelerate()
  {
    if (speed > 0)
    {
      speed = speed - 0.06f;
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
  public int determineTurretColor()
  {
    if (player.getPowerUp().getName().equals("RGB Bullet") && player.getPowerUp().getIsActivated())
      return color(255);
      
    int temp = color(255);
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
  public int determineLaserColor()
  {
    int temp = color(255);
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

class Store 
{  
  private float x, y, w, h; 
  private Button closeButton;
  private Button[] powerupButtons;
  private PowerUp[] powerups;
  private boolean isStoreOpened;
  private boolean isClicked;
  
  private Sample clickSound;
  
  public Store()
  {
    x = width/20;
    y = height-(height*0.85f);
    w = width*0.9f;
    h = height*0.8f;
    
    isStoreOpened = false;
    isClicked = false;
    
    closeButton = null; 
    powerupButtons = new Button[8];
    powerups = new PowerUp[8];
    
    powerups[0] = new PowerUp("Rapid Fire", 150, 30000);
    powerups[1] = new PowerUp("Rock Seekers", 250, 10000);
    powerups[2] = new PowerUp("RGB Bullet", 350, 10000);
    powerups[3] = new PowerUp("Laser Pointer", 150, 30000);
    powerups[4] = new PowerUp("Insta-Brake", 100, 20000);
    powerups[5] = new PowerUp("Supernova", 750, 0);
    powerups[6] = new PowerUp("Floating Mine", 100, 0);
    powerups[7] = new PowerUp("Spreader", 200, 20000);
  }
  
  public void display()
  {
    fill(0);
    stroke(36, 224, 103);
    rect(x, y, w, h, 10, 10); //background
    
    textSize(20);
    fill(255);
    text("Store", x+(w/60), y+(h/20));
    
    if (closeButton == null) //initialize here so the textwidth will be correct
      closeButton = new Button(w-textWidth("close x")+w/26, y+(h/35), textWidth("close x")+2, 15);
    //closeButton.display();
    fill(255);
    text("close", w-textWidth("x")+w/25-textWidth("close "), y+(h/20));
    fill(0xffFF0000); //red 
    text("x", w-textWidth("x")+w/25, y+(h/20));    
    
    fill(0);
    //top row
    rect(x+(w/8), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[0] == null)
      powerupButtons[0] = new Button(x+(w/8), y+(h/6), w/8, h/3);
    if (powerups[0] != null)    
      powerups[0].iconDisplay(x+(w/6), y+(h/3.5f));
      
    rect(x+(w/8)+(w/5), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[1] == null)
      powerupButtons[1] = new Button(x+(w/8)+(w/5), y+(h/6), w/8, h/3);
    if (powerups[1] != null)   
      powerups[1].iconDisplay(x+((w/6)*2.25f), y+(h/3.5f));
      
    rect(x+(w/8)+((w/5)*2), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[2] == null)
      powerupButtons[2] = new Button(x+(w/8)+((w/5)*2), y+(h/6), w/8, h/3);
    if (powerups[2] != null)        
      powerups[2].iconDisplay(x+((w/6)*3.35f), y+(h/3.5f));    
      
    rect(x+(w/8)+((w/5)*3), y+(h/6), w/8, h/3, 10, 10);
    if (powerupButtons[3] == null)
      powerupButtons[3] = new Button(x+(w/8)+((w/5)*3), y+(h/6), w/8, h/3);
    if (powerups[3] != null)    
      powerups[3].iconDisplay(x+((w/6)*4.5f), y+(h/3.5f));
      
    //bottom row
    rect(x+(w/8), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[4] == null)
      powerupButtons[4] = new Button(x+(w/8), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[4] != null)    
      powerups[4].iconDisplay(x+((w/6)), y+(h/3.5f)*2.3f);
      
    rect(x+(w/8)+(w/5), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[5] == null)
      powerupButtons[5] = new Button(x+(w/8)+(w/5), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[5] != null)    
      powerups[5].iconDisplay(x+((w/6)*2.25f), y+(h/3.5f)*2.3f);
      
    rect(x+(w/8)+((w/5)*2), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[6] == null)
      powerupButtons[6] = new Button(x+(w/8)+((w/5)*2), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[6] != null)    
      powerups[6].iconDisplay(x+((w/6)*3.35f), y+(h/3.5f)*2.3f);
      
    rect(x+(w/8)+((w/5)*3), y+(h/5)+(h/3), w/8, h/3, 10, 10);
    if (powerupButtons[7] == null)
      powerupButtons[7] = new Button(x+(w/8)+((w/5)*3), y+(h/5)+(h/3), w/8, h/3);
    if (powerups[7] != null)    
      powerups[7].iconDisplay(x+((w/6)*4.5f), y+(h/3.5f)*2.3f);
      
      // Display the quick buy buttons
      for(int i = 0; i < 8; i++)
      {
        fill(255, 255, 255, 150);
        if(i < 4)
        {
          text(i + 1, (x + ((3*w)/16)) + i*(w/5), y + h/8);
        }
        else
        {
          text(i + 1, (x + ((3*w)/16)) + (i - 4)*(w/5), (y+(h/5)+(2.2f*(h/3))));
        }
      }  
  }
  
  public void handleEvents()
  {
    if (isMouseOver(closeButton))
    {
      //draws a roll over effect on top of the button
      fill(0, 0, 0, 100);
      noStroke();
      rect(w-textWidth("close x")+w/26, y+(h/37), textWidth("close x")+4, 25);
      
      if (mousePressed)
      {
        mouseClickedSound.play();
        closeStore();
        isPaused = !isPaused;        
      }
    }
    
    for (int i = 0; i < powerupButtons.length; i++)
    {      
      if (isMouseOver(powerupButtons[i]))
      {                
        fill(0, 0, 0, 100);
        noStroke();         
        rect(powerupButtons[i].x, powerupButtons[i].y, powerupButtons[i].wid, powerupButtons[i].hei, 10, 10);
        fill(255);
        textSize(12);
        if (powerups[i] != null)
        {
          text("Name: " + powerups[i].getName(), powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2);
          text("Price: " + powerups[i].getPrice() + " Minerals", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+15);
          if (powerups[i].getDuration() != 0)
            text("Duration: " + powerups[i].getDuration() + " ms", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+30);
          else
            text("one time use", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+30);
          text("quick Buy: " + (i+1), powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2+45);        
        }
        else
          text("Name: Testing\nPrice: 100 Minerals", powerupButtons[i].x+5, powerupButtons[i].y+powerupButtons[i].hei/2);     
       
        if (mousePressed && !isClicked) //isClicked stops from mouse holding error
        {
          purchaseSlot(i); //handles all purchasing code
        }          
      }
    }
  }
  
  public void purchaseSlot(int storeSlot)
  {
    if (storeSlot >= 0 && storeSlot <= 7) //range available (0-7)
    {
      if (powerups[storeSlot] != null) 
      {
        if (player.currency < powerups[storeSlot].getPrice() || !player.getPowerUp().getName().equals("Empty")) //not enough currency or already have a powerup equipped           
        {
          fill(255, 0, 0); //red error fill         
          clickSound = new Sample("/sounds/failed_purchase.aiff");
          clickSound.setVolume(2);
        } 
        else
        {
          clickSound = new Sample("/sounds/successful_purchase.wav");
          clickSound.setVolume(1);
          fill(0xffD7DE2B); //yellow success fill
          player.currency -= powerups[storeSlot].getPrice();
          player.setPowerUp(powerups[storeSlot]);                        
        }
          
        rect(powerupButtons[storeSlot].x, powerupButtons[storeSlot].y, powerupButtons[storeSlot].wid, powerupButtons[storeSlot].hei);
        clickSound.play();
        changeIsClickedState(); //invert isClicked     
      }     
    }    
  }
  
  public void changeIsClickedState() {isClicked = !isClicked;}
  public boolean getIsClicked() {return isClicked;}
  private void closeStore() {isStoreOpened = false;}
  private void openStore() {isStoreOpened = true;}
  private void changeStoreState() {isStoreOpened = !isStoreOpened;}
  public boolean isStoreOpened() {return isStoreOpened;}
}
class TimeFreeze extends Obj
{
  int outerColor;
  int midColor;
  int innerColor;
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

class Timer
{
  //end and start point
  private long end;
  private long start;
  
  public Timer(long milliseconds)
  {
    end = milliseconds; 
    start = System.currentTimeMillis(); //current time
  }
  
  public boolean isOver()
  {
    if (System.currentTimeMillis() - start > end)
      return true;
    
    return false;
  }  
  
  public void restart() //used in Bullet()
  {
    start = System.currentTimeMillis();
  }
    
  public long getTimeRemaining() {return end-(System.currentTimeMillis()-start);}
  public long getStart() {return start;}
  public long getEnd() {return end;}
}
class WaveSystem
{
  // All counters 
  private float enemiesPerWave;
  private int waveCount;  
  // Difficulty modifier
  private float difficulty;
  // The percentile of increase the number of enemies by each round
  private float waveIncrease = 1.15f;
  
  public WaveSystem()
  {
    difficulty = 1; //initial default difficulty
    enemiesPerWave = 2; //initial default amount
    waveCount = 0;
  }
  
  public WaveSystem(int diff)
  {
    difficulty = diff;
    enemiesPerWave = 2; //initial default amount
    waveCount = 0;
  }
  
  public WaveSystem(int diff, int enemies)
  {
    difficulty = diff;
    enemiesPerWave = enemies; //initial amount
    waveCount = 0;
  }
  
  public void newWave()
  {
    spawn(PApplet.parseInt(enemiesPerWave*difficulty));
    enemiesPerWave *= (waveIncrease*difficulty);
    waveCount++;
  }
  
  private void spawn(int enemies)
  {
    //for randomizing
    float x, y, speedX, speedY;
    
    for (int i = 0; i < enemies; i++)
    {
      int side = PApplet.parseInt(random(4)); //randomize spawn side
      switch (side)
      {
        case 0: //top side
          y = 0;
          x = random(screenW);
          break;
        case 1: //right side
          y = random(screenH);
          x = screenW;
          break;
        case 2: //bottom side
          y = screenH;
          x = random(screenW);
          break;
        case 3: //left side
          y = random(screenH);
          x = 0;
          break;
        default: //default to top in case of error
          y = 0;
          x = random(screenW);
      }
      
      asteroids.add(new Asteroid(x, y, random(20, 100), random(20, 100), random(-3, 3), random(-3, 3), randColour()));
    }    
  }
  
  public boolean isOver()
  {
    if (!asteroids.containsInstance("Asteroid"))
    {      
      return true;
    }
    return false;
  }
    
  private String randColour()
  {
    int rand = PApplet.parseInt(random(3));
    
    switch (rand)
    {
      case 0:
        return "red";
      case 1:
        return "green";
      case 2:
        return "blue";
    } 
    return "error";
  }
}
// Custom implementation of a dynamic list (Similar to Array List)
class dyList
{
  private Obj[] objs = new Obj[0];
  
  public void add(Obj obj)
  {
    Obj[] n_objs = new Obj[this.objs.length+1];    
    for (int i = 0; i < this.objs.length; i++)
    {
      n_objs[i] = this.objs[i];
    }
    
    n_objs[n_objs.length-1] = obj;     
    this.objs = new Obj[this.objs.length+1];    
    
    for (int i = 0; i < this.objs.length; i++)
    {
      this.objs[i] = n_objs[i];      
    }
  }
  
  public void del(int index)
  {      
    Obj[] n_objs = new Obj[this.objs.length-1];
    for (int i = 0, j = 0; i < this.objs.length; i++)
    {
      if (i != index)
      {
        n_objs[j] = this.objs[i];
        j++;
      }         
    }
    
    this.objs = new Obj[this.objs.length-1];    
    for (int i = 0; i < this.objs.length; i++)
    {
      this.objs[i] = n_objs[i];      
    }
  }
  
  public void del(String everything)
  {
    for (int i = 0; i < objs.length; i++)
    {
      if (objs[i].getEverything().equals(everything))      
        del(i);
    }
  }
  
  public int len()
  {    
    return this.objs.length;
  }
  
  public Obj grab(int i)
  {
    return this.objs[i];
  } 
  
  public boolean indexExists(int i)
  {
    try
    {
      Obj t = objs[i];
    }
    catch (Exception e)
    {
      return false;
    }
   return true;
  } 
  
  public boolean contains(Obj o)
  {
    for (int i = 0; i < objs.length; i++)
    {
      if (objs[0] == o)
        return true;
    }
    
    return false;
  }
  
  public boolean containsInstance(String inst)
  {
    if (inst == "Asteroid")
    {
      for (int i = 0; i < objs.length; i++)
      {
        if (objs[i] instanceof Asteroid)
        {
          return true;
        }
      }
    }
    
    return false;
  }  
}
/*
It is HIGHLY RECOMMENDED to read the Game Manual provided with Time Benders before lauching
the game. It contains build instructions, controls, strategies and other features such as 
how to make the game run faster if it lags. The Game Manual can be found in the main folder. 
*/
public void setup()
{
  loadConfig();  

  size(screenW, screenH);  
  smooth();
  // Initialize all fonts
  jediFont = createFont("Star_Jedi_Rounded.ttf", 16);
  introFontOne = createFont("firefightbb_reg.ttf", 50);
  creditsFont = loadFont("BerlinSansFBDemi-Bold-48.vlw");

  // Must now be declared here due to resolution config
  player = new Spaceship(screenW/2, screenH/2, 0xffB73907);
  playerObjects.add(player);  

  // Create the store
  store = new Store();
  
  // Start the sound engine
  Sonia.start(this);
  
  // Initialize all sounds used in the game
  initializeSoundEffects();  
  
  // Create the music player
  musicPlayer = new MusicPlayer();
  
  // Create the main menu
  mainMenu = new MainMenu();
 
  // Allows us to remove the title bar
  frame.removeNotify(); 
  frame.setUndecorated(true); //removes title bar  
  
  // Create the scrolling game background
  scrollingBack = new ScrollingBackground("mainGameBack.JPG", 100);
  
  if (isFullScreen)     
  {
    frame.setExtendedState(Frame.MAXIMIZED_BOTH); //maximizes     
    frame.setLocation(0, 0); //sets the position to the top left hand corner of the monitor
  }
  else      
  {
    frame.setExtendedState(Frame.NORMAL);  
    frame.setLocationRelativeTo(null); //centres the game to the monitor
  }
}

public void draw()
{
 background(0);
 // Play Game Intro
 if(isGameIntro == true)
 {
   playIntroSequence();
 }
 // Play Credits
 else if(isCreditsPlaying == true)
 {
   playCreditsSequence();
 }
 // Display the main menu otherwise
 else
 {
  // Play the music player regardless if in the main menu or the game
  musicPlayer.play();
  // Stop all other sounds
  if(introMusic.isPlaying())
  {
    introMusic.stop();
  }
  if(creditsMusic.isPlaying())
  {
    creditsMusic.stop();
  }
  // Display the main menu
  if (!isGameRunning)
  {
    mainMenu.display();
    mainMenu.handleEvents();
    musicPlayer.currentSong.track.setVolume(1.5f);
    isGameOver = false;
  }
  else // Start the game once new game/continue is pressed
  {
    musicPlayer.currentSong.track.setVolume(0.8f);
    if (wave.isOver())
    {
      startNewWave();
    }   
    
    if (!isGameOver)
    {     
      scrollingBack.display();
      
      displayAndMoveLoops();
      
      if (!isPaused)
      {
        collisionDetectionLoops();
      }
        
      // Ends powerup if applicable
      player.endPowerUp(); 
      // Ends TimeFreeze if applicable
      player.checkTimeFreeze();
      // Displays the Heads Up Display    
      displayHUD();
      // Draws the timer if it's the first run through the game (from Continue or New Game)
      drawGameStartTimer();
    }
    else
    {
      if (gameOverDisplayTimer.getEnd() == 0)
        gameOverDisplayTimer = new Timer(4000);
        
      if (!gameOverDisplayTimer.isOver())
      {
        fill(255);
        textFont(jediFont);
        textSize(32);
        text("Game over", (screenW/2)-(textWidth("game over")/2), screenH/2);
      }
      else if (isNewHighScore() && !getPlayerName())
      {        
        textFont(jediFont);     
        textSize(32);
        fill(255);        
        text("Enter your name", (screenW/2)-(textWidth("Enter your name")/2), screenH/2);                
        
        fill(0xffDE095E);
        textSize(26);
        text(playerName, (screenW/2)-(textWidth(playerName)/2), screenH/2 + 80);      
      }
      else
      {
        // Need to create a new menu again to reset the values of the options
        mainMenu = new MainMenu();
        // Go back to the main menu
        isGameRunning = false;
      }
    }
  }
  // Pops up the song detail box if the song switched
  if(musicPlayer.popOut == true)
  {
    musicPlayer.displayPopUp();
  }
 }
}

public boolean isNewHighScore()
{
  String[] inConfig = loadStrings("highscores.txt");
  
  if (player.score > Integer.parseInt(inConfig[inConfig.length-1].substring(inConfig[inConfig.length-1].indexOf(' ')+1, inConfig[inConfig.length-1].length())))  
    return true;  
  
  return false;
}

// Starts a new wave of asteroids 
public void startNewWave()
{
  // Start new wave
  wave.newWave();
  // If the wave ends and time is frozen, the freezeBar is reset
  if (isTimeFrozen == true)
  {
    freezeBarWidth = 0;
  }
  // Time unfreezes when a new wave starts
  isTimeFrozen = false;
}

// Displays and moves the game objects
public void displayAndMoveLoops()
{
  // Display and Move loops (Player Objects)
  for (int i = 0; i < playerObjects.len(); i++)
  {
    playerObjects.grab(i).display();
    if (!isPaused) //stops movement on pause
      playerObjects.grab(i).move();
  }
  // Display and Move loops (Asteroids)
  for (int i = 0; i < asteroids.len(); i++)
  {
    asteroids.grab(i).display();
    if (!isPaused && isTimeFrozen == false) //stops movement on pause
        asteroids.grab(i).move();
  }
  // Display and Move Loops (Collidables)
  for (int i = 0; i < collidable.len(); i++)
  {      
    collidable.grab(i).display();
    if (!isPaused) //stops movement on pause
      collidable.grab(i).move();
  }
  // Display and Move Loops (Explosions)
  for(int i = 0; i < explosions.len(); i++)
  {
    explosions.grab(i).display();
    if(!isPaused)
    {
      explosions.grab(i).move();
    }
    // Delete the explosion if it's finished
    if(((ParticlePhysics)explosions.grab(i)).isFinished())
    {
      explosions.del(i);
    }
  }
}

// Checks collision detection for all objects
public void collisionDetectionLoops()
{
  // Collision Detection Loop    
  for (int i = 0; i < playerObjects.len(); i++)
  { 
    for (int j = 0; j < asteroids.len(); j++)
    {                          
      if (playerObjects.indexExists(i) && asteroids.indexExists(j))
      {                 
        if ((playerObjects.grab(i) instanceof Bullet || playerObjects.grab(i) instanceof Mine) && asteroids.grab(j) instanceof Asteroid)
        { 
          if (isCollision(playerObjects.grab(i), asteroids.grab(j)))
          { 
            if (playerObjects.grab(i).getColour() == asteroids.grab(j).getColour() || player.getPowerUp().getName().equals("RGB Bullet") || playerObjects.grab(i) instanceof Mine)
            {
              // Handle collision separately for each object
              playerObjects.grab(i).handleCollision();
              asteroids.grab(j).handleCollision();
            }         
          }
        }
      }

      if (asteroids.indexExists(j))
      {
        if (isCollision(player, asteroids.grab(j)))
        {   
          // Handle collision separately for each object
          player.handleCollision();
          asteroids.grab(j).handleCollision();
        }
      }
    }       

    for (int j = 0; j < collidable.len(); j++)
    {
      if (isCollision(player, collidable.grab(j)))
      {
        playerObjects.grab(i).handleCollision();
        collidable.grab(j).handleCollision();
      }
    }
  }
}

// Creates and displays the Heads Up Display of the game
public void displayHUD()
{
  textSize(20);
  fill(255);
  // Sets a transparent frame for the HUD.
  rectMode(CORNER);
  fill(50, 52, 49, 100);
  stroke(36, 224, 103);
  strokeWeight(2);
  rect(0, 0, 280, 40, 10, 10);
  rect(width - 291, 0, 290, 63, 10, 10);
  rect(0, 40, 280, 30, 10, 10);
  rect(0, 70, 280, 30, 10, 10);
  rect(width - 291, 63, 290, 30, 10, 10);
  // Displays the minerals
  textFont(jediFont);
  fill(255, 255, 255, 180);
  text("Acquired Minerals: " + player.getCurrency(), 5, 25);
  text("Score: " + player.score, 5, 60);  
  text("Wave: " + wave.waveCount, 5, 90);
  // Displays the saved game indicator
  updateSavedGameIndicator();
  fill(255, 255, 255, gameSavedIndicatorBrightness);
  text("Game Saved.", 5, 120);
  /* Displays the current stored powerup(ability)
  inside the box */
  textSize(25);
  fill(255, 255, 255, 180);
  text("Ability", width - 270, 40);  
  line(width - 150, 32, width - 100, 32);
  line(width - 100, 32, width - 110, 25);
  line(width - 100, 32, width - 110, 39);
  noFill();
  rect(width - 75, 10, 45, 45, 10, 10);  
  if (!player.getPowerUp().getName().equals("Empty"))
    player.getPowerUp().iconDisplay(width - 75, 10);
    
  if (player.getPowerUp().getName().equals("Supernova") && player.getPowerUp().getIsActivated())
    player.getPowerUp().drawSupernova();

  /* Displays the Freeze Charge Bar as a percentage of the amount of charge
   accumulated */
  float freezePercent = (player.freezeCharge/player.maxCharge);
  float newBarWidth = freezePercent * 290;
  fill(196, 61, 224, 150);
  if (freezePercent < 1.00f)
  {
    stroke(238, 245, 146);
  }
  else
  {
    maxChargeEffect();
    strokeWeight(5);
    stroke(238, 245, 146, freezeBarBrightness);
  }
  // Increase the bar width to what it's new width
  if (newBarWidth > freezeBarWidth)
  {
    freezeBarWidth++;
    if (abs(freezeBarWidth - newBarWidth) < 1)
    {
      freezeBarWidth = newBarWidth;
    }
  }
  // Decrease the bar width showing the time left for TimeFreeze (when it's enabled)
  if (newBarWidth < freezeBarWidth)
  {
    float decreaseSpeed = (290/(player.freezeDuration/1000))/frameRate;
    freezeBarWidth = freezeBarWidth - decreaseSpeed;
  }
  // Only display the bar if the width is more than 0
  if (freezeBarWidth > 0)
  { 
    rect(width - 291, 63, freezeBarWidth, 30, 10, 10);
  }

  // Draws timer for powerup
  if (!player.getPowerUp().getName().equals("Empty"))
  {
    if (player.getPowerUp().getIsActivated())    
      player.getPowerUp().drawTimer();
  }

  // Displays the store
  if (store.isStoreOpened())
  {
    store.display();
    store.handleEvents();
  }
}

// Flashes the Freeze Bar (Used when max charge is reached)
public void maxChargeEffect()
{
  if (freezeBarBrightness < 0 || freezeBarBrightness > 250)
  {
    freezeBrightnessInc = -freezeBrightnessInc;
  }
  freezeBarBrightness = freezeBarBrightness - freezeBrightnessInc;
}

// Draws the countdown for the game if it's the first run
public void drawGameStartTimer()
{
  if (isFirstRun)
  {    
    if (startGameTimer.getEnd() == 0)      
      startGameTimer = new Timer(3000);
       
    isPaused = true;
    
    textFont(jediFont);
    textSize(48);
    fill(255);
    text(PApplet.parseInt(startGameTimer.getTimeRemaining()/1000)+1, (width/2)-(textWidth(Integer.toString((PApplet.parseInt(startGameTimer.getTimeRemaining()/1000)+1)/2))), (height/3));  
      
    if (startGameTimer.isOver())
    {
      isFirstRun = false;
      isPaused = false;
    }
  }
}

// Decreases the brightness of the saved game indicator if it is more than 0
public void updateSavedGameIndicator()
{
  if(gameSavedIndicatorBrightness > 0)
  {
    gameSavedIndicatorBrightness = gameSavedIndicatorBrightness - 2;
  }
}

// Plays the intro sequence
public void playIntroSequence()
{
  if(introReset == true)
  {
    frameCount = 0;
    introReset = false;
    // Initialize the intro sequence
    hrishiX = width/20;
    mukherjeeX = width/2;
    scottX = width/3; 
    andrecheckX = (width/3)*2;
    namesB = 0;
    titleUpB = 0; 
    titleDownB = 0; 
    epicPlayedOnce = false;
    bendersPlayedOnce = false;
  }
  nameSequence();
  titleSequence();
}

// Plays the credits
public void playCreditsSequence()
{
  if(creditsReset == true)
  {
    sequenceNumber = 0;
    creditsTitleX = -500;
    creditsSubTitleX = width + 300;
    creditsReset = false;
  }
  creditsSequence();
}

// Initializes the sound effects used in the game
public void initializeSoundEffects()
{
  mouseClickedSound = new Sample("/sounds/mouse_click.wav");
  destroyedAsteroidSound = new Sample("/sounds/asteroid_destroyed" + (PApplet.parseInt(random(1, 4))) + ".wav");
  collectedMineralSound = new Sample("/sounds/collect_mineral.wav");
  shipClangSound = new Sample("/sounds/clang" + ((int)(random(1, 4))) + ".wav");
  shipEngineSound = new Sample("/sounds/engine.wav");
  shipShotSound = new Sample("/sounds/basic_laser.wav");
  timeFreezeSound = new Sample("/sounds/Time Freeze Sound.wav");
  powerupActivateSound = new Sample("/sounds/activate_powerup.wav");
  epic = new Sample("/sounds/inceptionbutton.wav");
  introMusic = new Sample("/sounds/Intro Ambience.wav");  
  creditsMusic = new Sample("/sounds/Credits Soundtrack.wav");
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "Object" });
  }
}
