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
  player = new Spaceship(screenW/2, screenH/2, #B73907);
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
  
  player.health = int(loadConfig[0]);
  player.freezeCharge = float(loadConfig[1]);
  player.powerup = new PowerUp(loadConfig[2], int(loadConfig[3]), Long.valueOf(loadConfig[4]));
  player.currency = int(loadConfig[5]);
  player.score = int(loadConfig[6]);
  wave.waveCount = int(loadConfig[7])-1;  //to avoid new wave error
  wave.enemiesPerWave = float(loadConfig[8]);
  
  return true;
}
